package com.srmfood.gag.data.mock

import com.srmfood.gag.domain.model.Cart
import com.srmfood.gag.domain.model.CartItem
import com.srmfood.gag.domain.model.SelectedCustomization
import com.srmfood.gag.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

private const val TAX_RATE = 0.05 // 5%

@Singleton
class MockCartRepository @Inject constructor() : CartRepository {

    private val cartItems = MutableStateFlow<List<CartItem>>(emptyList())

    private fun buildCart(items: List<CartItem>): Cart? {
        if (items.isEmpty()) return null
        val outletId = items.first().outletId
        val outletName = items.first().outletId.let { id ->
            MockData.outlets.find { it.id == id }?.name ?: "Unknown Outlet"
        }
        val subtotal = items.sumOf { it.itemTotal }
        val tax = subtotal * TAX_RATE
        val estimatedPrepMinutes = MockData.foodItems
            .filter { food -> items.any { it.foodItemId == food.id } }
            .maxOfOrNull { it.prepTimeMinutes } ?: 10
        return Cart(outletId, outletName, items, subtotal, tax, subtotal + tax, estimatedPrepMinutes)
    }

    override fun getCart(): Flow<Cart?> = cartItems.map { buildCart(it) }

    override suspend fun addToCart(
        foodItemId: String, foodName: String, foodImageUrl: String?,
        outletId: String, outletName: String, price: Double, quantity: Int,
        isVeg: Boolean, selectedCustomizations: List<SelectedCustomization>, specialInstructions: String?
    ): Result<Cart> {
        val current = cartItems.value.toMutableList()
        val existing = current.find { it.foodItemId == foodItemId &&
                it.selectedCustomizations == selectedCustomizations }
        if (existing != null) {
            val updated = existing.copy(quantity = existing.quantity + quantity)
            current[current.indexOf(existing)] = updated
        } else {
            current.add(CartItem(
                id = UUID.randomUUID().toString(), foodItemId = foodItemId,
                foodName = foodName, foodImageUrl = foodImageUrl, outletId = outletId,
                price = price, quantity = quantity, selectedCustomizations = selectedCustomizations,
                isVeg = isVeg, specialInstructions = specialInstructions
            ))
        }
        cartItems.value = current
        return Result.success(buildCart(current)!!)
    }

    override suspend fun updateQuantity(cartItemId: String, quantity: Int): Result<Cart> {
        val current = cartItems.value.toMutableList()
        val idx = current.indexOfFirst { it.id == cartItemId }
        if (idx != -1) current[idx] = current[idx].copy(quantity = quantity)
        cartItems.value = current
        return Result.success(buildCart(current) ?: Cart("","", emptyList(), 0.0, 0.0, 0.0, 0))
    }

    override suspend fun removeItem(cartItemId: String): Result<Cart> {
        val current = cartItems.value.filter { it.id != cartItemId }
        cartItems.value = current
        return Result.success(buildCart(current) ?: Cart("","", emptyList(), 0.0, 0.0, 0.0, 0))
    }

    override suspend fun clearCart(): Result<Unit> {
        cartItems.value = emptyList()
        return Result.success(Unit)
    }

    override suspend fun getCartOutletId(): String? = cartItems.value.firstOrNull()?.outletId
}
