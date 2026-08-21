package com.srmfood.gag.data.mock

import com.srmfood.gag.domain.model.Order
import com.srmfood.gag.domain.model.OrderItem
import com.srmfood.gag.domain.model.OrderStatus
import com.srmfood.gag.domain.model.PaymentMethod
import com.srmfood.gag.domain.model.PaymentStatus
import com.srmfood.gag.domain.model.PickupSlot
import com.srmfood.gag.domain.repository.CartRepository
import com.srmfood.gag.domain.repository.OrderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MockOrderRepository @Inject constructor(
    private val cartRepository: CartRepository
) : OrderRepository {

    private val _orders = MutableStateFlow(MockData.mockOrders.toList())

    override fun getOrders(): Flow<List<Order>> = _orders.asStateFlow()

    override suspend fun getOrderById(orderId: String): Result<Order> {
        delay(200)
        return _orders.value.find { it.id == orderId }
            ?.let { Result.success(it) }
            ?: Result.failure(Exception("Order not found"))
    }

    override suspend fun placeOrder(
        outletId: String, pickupSlotId: String,
        paymentMethod: PaymentMethod, specialInstructions: String?
    ): Result<Order> {
        delay(800)
        // Get cart items synchronously by collecting once
        val cart = cartRepository.getCart().let {
            var cartResult: com.srmfood.gag.domain.model.Cart? = null
            // simplified for mock
            MockData.outlets.find { o -> o.id == outletId }
            null
        }

        val outlet = MockData.outlets.find { it.id == outletId }
            ?: return Result.failure(Exception("Outlet not found"))
        val slot = MockData.generatePickupSlots(outletId, today()).find { it.id == pickupSlotId }

        val orderNumber = "#${('A'..'Z').random()}${(100..999).random()}"
        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = orderNumber,
            userId = "student-001",
            vendorId = outlet.vendorId,
            outletId = outletId,
            outletName = outlet.name,
            items = listOf(
                OrderItem(UUID.randomUUID().toString(), "food-001", "Chicken Biryani", null, 1, 80.0, 80.0, emptyList(), false)
            ),
            subtotal = 80.0, tax = 4.0, total = 84.0,
            status = OrderStatus.PLACED,
            pickupSlot = slot,
            estimatedPrepMinutes = 12, actualPrepMinutes = null,
            createdAt = now(), placedAt = now(),
            acceptedAt = null, preparingAt = null, readyAt = null, pickedUpAt = null,
            cancelledAt = null, cancellationReason = null,
            paymentStatus = PaymentStatus.PENDING, paymentMethod = paymentMethod,
            specialInstructions = specialInstructions, qrToken = null
        )
        _orders.value = _orders.value + order
        cartRepository.clearCart()
        return Result.success(order)
    }

    override suspend fun cancelOrder(orderId: String, reason: String): Result<Order> {
        delay(400)
        val updated = _orders.value.map { order ->
            if (order.id == orderId) order.copy(status = OrderStatus.CANCELLED, cancellationReason = reason, cancelledAt = now())
            else order
        }
        _orders.value = updated
        return updated.find { it.id == orderId }?.let { Result.success(it) }
            ?: Result.failure(Exception("Order not found"))
    }

    override suspend fun getPickupSlots(outletId: String, date: String): Result<List<PickupSlot>> {
        delay(300)
        return Result.success(MockData.generatePickupSlots(outletId, date))
    }

    override suspend fun getQrToken(orderId: String): Result<String> {
        delay(200)
        return Result.success("MOCK_QR_TOKEN_${orderId}_${System.currentTimeMillis()}")
    }

    override fun observeOrderStatus(orderId: String): Flow<OrderStatus> =
        _orders.map { orders -> orders.find { it.id == orderId }?.status ?: OrderStatus.CREATED }

    // Vendor operations
    override suspend fun acceptOrder(orderId: String): Result<Order> = updateOrderStatus(orderId, OrderStatus.ACCEPTED)
    override suspend fun rejectOrder(orderId: String, reason: String): Result<Order> = updateOrderStatus(orderId, OrderStatus.REJECTED)
    override suspend fun startPreparing(orderId: String): Result<Order> = updateOrderStatus(orderId, OrderStatus.PREPARING)
    override suspend fun markReady(orderId: String): Result<Order> = updateOrderStatus(orderId, OrderStatus.READY)

    override suspend fun confirmPickup(qrToken: String): Result<Order> {
        delay(400)
        val order = _orders.value.find { it.status == OrderStatus.READY }
            ?: return Result.failure(Exception("No ready order found for this QR"))
        return updateOrderStatus(order.id, OrderStatus.PICKED_UP)
    }

    override suspend fun getVendorOrders(status: OrderStatus?): Result<List<Order>> {
        delay(300)
        val orders = if (status != null) _orders.value.filter { it.status == status }
        else _orders.value
        return Result.success(orders)
    }

    private suspend fun updateOrderStatus(orderId: String, newStatus: OrderStatus): Result<Order> {
        delay(300)
        val updated = _orders.value.map { if (it.id == orderId) it.copy(status = newStatus) else it }
        _orders.value = updated
        return updated.find { it.id == orderId }?.let { Result.success(it) }
            ?: Result.failure(Exception("Order not found"))
    }

    private fun now(): String = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).format(Date())
    private fun today(): String = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
}
