package com.srmfood.gag.data.repository.supabase

import com.srmfood.gag.data.local.dao.CartDao
import com.srmfood.gag.data.local.dao.OrderDao
import com.srmfood.gag.data.mapper.toDomain
import com.srmfood.gag.data.remote.dto.OrderDto
import com.srmfood.gag.data.remote.dto.OrderItemDto
import com.srmfood.gag.data.remote.dto.PickupSlotDto
import com.srmfood.gag.domain.model.Order
import com.srmfood.gag.domain.model.OrderStatus
import com.srmfood.gag.domain.model.PaymentMethod
import com.srmfood.gag.domain.model.PickupSlot
import com.srmfood.gag.domain.repository.OrderRepository
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.channels.awaitClose

@Serializable
data class SupabaseOrderInsert(
    val id: String,
    @SerialName("order_number") val orderNumber: String,
    @SerialName("user_id") val userId: String,
    @SerialName("vendor_id") val vendorId: String,
    @SerialName("outlet_id") val outletId: String,
    @SerialName("pickup_slot_id") val pickupSlotId: String,
    val subtotal: Double,
    val tax: Double,
    val total: Double,
    val status: String,
    @SerialName("payment_status") val paymentStatus: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("special_instructions") val specialInstructions: String?
)

@Serializable
data class SupabaseOrderItemInsert(
    val id: String,
    @SerialName("order_id") val orderId: String,
    @SerialName("food_item_id") val foodItemId: String,
    @SerialName("food_name") val foodName: String,
    @SerialName("food_image_url") val foodImageUrl: String?,
    val quantity: Int,
    @SerialName("unit_price") val unitPrice: Double,
    @SerialName("total_price") val totalPrice: Double,
    @SerialName("is_veg") val isVeg: Boolean
)

@Serializable
data class OutletVendorResponse(
    @SerialName("vendor_id") val vendorId: String
)

@Singleton
class SupabaseOrderRepository @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val client: io.github.jan.supabase.SupabaseClient,
    private val orderDao: OrderDao,
    private val cartDao: CartDao
) : OrderRepository {

    override fun getOrders(): Flow<List<Order>> {
        return orderDao.observeOrders().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun getOrderById(orderId: String): Result<Order> = runCatching {
        val dto = postgrest["orders"].select(Columns.raw("*, pickup_slots(*)")) {
            filter { eq("id", orderId) }
        }.decodeSingle<OrderDto>()
        
        // Wait, order items need to be fetched too
        val itemsDto = postgrest["order_items"].select {
            filter { eq("order_id", orderId) }
        }.decodeList<OrderItemDto>()
        
        val fullDto = dto.copy(items = itemsDto)
        
        // Caching locally is handled via sync mechanism or here
        fullDto.toDomain()
    }

    override suspend fun placeOrder(
        outletId: String,
        pickupSlotId: String,
        paymentMethod: PaymentMethod,
        specialInstructions: String?
    ): Result<Order> = runCatching {
        val session = auth.currentSessionOrNull() ?: throw Exception("User not logged in")
        val userId = session.user?.id ?: throw Exception("Invalid user")
        
        // 1. Get cart
        val cartItems = cartDao.getCartItems()
        if (cartItems.isEmpty()) throw Exception("Cart is empty")
        
        // Calculate totals
        val subtotal = cartItems.sumOf { it.price * it.quantity }
        val tax = subtotal * 0.05
        val total = subtotal + tax
        
        // 2. Fetch vendor_id for outlet
        val outletVendor = postgrest["outlets"].select(Columns.raw("vendor_id")) {
            filter { eq("id", outletId) }
        }.decodeSingle<OutletVendorResponse>()
        
        val orderId = UUID.randomUUID().toString()
        val orderNumber = "GAG-${Random.nextInt(1000, 9999)}"
        
        // 3. Insert Order
        val orderInsert = SupabaseOrderInsert(
            id = orderId,
            orderNumber = orderNumber,
            userId = userId,
            vendorId = outletVendor.vendorId,
            outletId = outletId,
            pickupSlotId = pickupSlotId,
            subtotal = subtotal,
            tax = tax,
            total = total,
            status = "CREATED",
            paymentStatus = "PENDING",
            paymentMethod = paymentMethod.name,
            specialInstructions = specialInstructions
        )
        postgrest["orders"].insert(orderInsert)
        
        // 4. Insert Order Items
        val orderItemsInsert = cartItems.map { cartItem ->
            SupabaseOrderItemInsert(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                foodItemId = cartItem.foodItemId,
                foodName = cartItem.foodName,
                foodImageUrl = cartItem.foodImageUrl,
                quantity = cartItem.quantity,
                unitPrice = cartItem.price,
                totalPrice = cartItem.price * cartItem.quantity,
                isVeg = cartItem.isVeg
            )
        }
        postgrest["order_items"].insert(orderItemsInsert)
        
        // 5. Clear Carts
        cartDao.clearCart()
        try {
            postgrest["carts"].delete { filter { eq("user_id", userId) } }
        } catch (e: Exception) {
            // ignore
        }
        
        // Fetch full placed order to return
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun cancelOrder(orderId: String, reason: String): Result<Order> = runCatching {
        postgrest["orders"].update(
            {
                set("status", "CANCELLED")
                set("cancellation_reason", reason)
            }
        ) {
            filter { eq("id", orderId) }
        }
        orderDao.updateStatus(orderId, "CANCELLED")
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun getPickupSlots(outletId: String, date: String): Result<List<PickupSlot>> = runCatching {
        val dtos = postgrest["pickup_slots"].select {
            filter { 
                eq("outlet_id", outletId)
                eq("slot_date", date)
            }
        }.decodeList<PickupSlotDto>()
        
        dtos.map {
            PickupSlot(
                id = it.id,
                outletId = it.outletId,
                startTime = it.startTime,
                endTime = it.endTime,
                date = it.date,
                capacity = it.capacity,
                bookedCount = it.bookedCount,
                status = com.srmfood.gag.domain.model.SlotStatus.fromString(it.status)
            )
        }
    }

    override suspend fun getQrToken(orderId: String): Result<String> = runCatching {
        "MOCK_QR_TOKEN_$orderId"
    }

    override fun observeOrderStatus(orderId: String): Flow<OrderStatus> = kotlinx.coroutines.flow.callbackFlow {
        val local = orderDao.getOrderById(orderId)
        local?.let { trySend(OrderStatus.valueOf(it.status)) }

        val channel = client.realtime.channel("public:orders")
        val changes = channel.postgresChangeFlow<PostgresAction.Update>("public") {
            table = "orders"
            filter = "id=eq.$orderId"
        }

        val job = kotlinx.coroutines.launch {
            changes.collect { action ->
                val statusString = action.record["status"]?.toString()?.replace("\"", "")
                if (statusString != null) {
                    val newStatus = OrderStatus.valueOf(statusString)
                    orderDao.updateStatus(orderId, newStatus.name)
                    trySend(newStatus)
                }
            }
        }

        channel.subscribe()
        awaitClose { 
            channel.unsubscribe()
            job.cancel()
        }
    }

    // ─── Vendor Operations ────────────────────────────────────────────────────────
    
    override suspend fun getVendorOrders(status: OrderStatus?): Result<List<Order>> = runCatching {
        // Implement vendor order fetching
        emptyList()
    }

    override suspend fun acceptOrder(orderId: String): Result<Order> = runCatching {
        updateOrderStatus(orderId, "ACCEPTED")
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun rejectOrder(orderId: String, reason: String): Result<Order> = runCatching {
        postgrest["orders"].update(
            {
                set("status", "REJECTED")
                set("cancellation_reason", reason)
            }
        ) {
            filter { eq("id", orderId) }
        }
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun startPreparing(orderId: String): Result<Order> = runCatching {
        updateOrderStatus(orderId, "PREPARING")
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun markReady(orderId: String): Result<Order> = runCatching {
        updateOrderStatus(orderId, "READY")
        getOrderById(orderId).getOrThrow()
    }

    override suspend fun confirmPickup(qrToken: String): Result<Order> = runCatching {
        // Dummy implementation for confirm pickup
        val orderId = qrToken.replace("MOCK_QR_TOKEN_", "")
        updateOrderStatus(orderId, "PICKED_UP")
        getOrderById(orderId).getOrThrow()
    }
    
    private suspend fun updateOrderStatus(orderId: String, status: String) {
        postgrest["orders"].update(
            { set("status", status) }
        ) {
            filter { eq("id", orderId) }
        }
    }
}
