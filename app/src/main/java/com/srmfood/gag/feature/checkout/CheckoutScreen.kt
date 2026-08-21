package com.srmfood.gag.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srmfood.gag.core.common.UiState
import com.srmfood.gag.core.ui.component.GagLoadingScreen
import com.srmfood.gag.core.ui.component.GagPrimaryButton
import com.srmfood.gag.core.ui.component.GagTopBar
import com.srmfood.gag.core.ui.theme.*
import com.srmfood.gag.domain.model.Cart
import com.srmfood.gag.domain.model.Order
import com.srmfood.gag.domain.model.PaymentMethod
import com.srmfood.gag.domain.model.PickupSlot
import com.srmfood.gag.domain.usecase.cart.GetCartUseCase
import com.srmfood.gag.domain.usecase.order.PlaceOrderUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────

data class CheckoutUiState(
    val cart: Cart? = null,
    val selectedSlot: PickupSlot? = null,
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.PAY_AT_COUNTER,
    val specialInstructions: String = "",
    val orderState: UiState<Order> = UiState.Idle
)

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val getCartUseCase: GetCartUseCase,
    private val placeOrderUseCase: PlaceOrderUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState: StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getCartUseCase().collectLatest { cart ->
                _uiState.value = _uiState.value.copy(cart = cart)
            }
        }
    }

    fun onSlotSelected(slot: PickupSlot) {
        _uiState.value = _uiState.value.copy(selectedSlot = slot)
    }

    fun onPaymentMethodSelected(method: PaymentMethod) {
        _uiState.value = _uiState.value.copy(selectedPaymentMethod = method)
    }

    fun onInstructionsChanged(text: String) {
        _uiState.value = _uiState.value.copy(specialInstructions = text)
    }

    fun placeOrder() {
        val state = _uiState.value
        val cart = state.cart ?: return
        val slot = state.selectedSlot ?: return

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(orderState = UiState.Loading)
            val result = placeOrderUseCase(
                outletId = cart.outletId,
                pickupSlotId = slot.id,
                paymentMethod = state.selectedPaymentMethod,
                specialInstructions = state.specialInstructions.ifBlank { null }
            )
            _uiState.value = _uiState.value.copy(
                orderState = result.fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Failed to place order") }
                )
            )
        }
    }

    fun resetOrderState() { _uiState.value = _uiState.value.copy(orderState = UiState.Idle) }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun CheckoutScreen(
    onBack: () -> Unit,
    onSelectPickupSlot: (String) -> Unit,
    onOrderPlaced: (String) -> Unit,
    viewModel: CheckoutViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.orderState) {
        if (uiState.orderState is UiState.Success) {
            val order = (uiState.orderState as UiState.Success<Order>).data
            viewModel.resetOrderState()
            onOrderPlaced(order.id)
        }
    }

    Scaffold(
        topBar = { GagTopBar(title = "Checkout", onBack = onBack) },
        containerColor = GagBackground,
        bottomBar = {
            Surface(color = GagBackground, shadowElevation = 8.dp) {
                GagPrimaryButton(
                    text = if (uiState.selectedSlot == null) "Select a Pickup Slot First" else "Place Order",
                    onClick = viewModel::placeOrder,
                    enabled = uiState.selectedSlot != null && uiState.cart != null,
                    isLoading = uiState.orderState is UiState.Loading,
                    modifier = Modifier.padding(16.dp).navigationBarsPadding()
                )
            }
        }
    ) { padding ->
        val cart = uiState.cart
        if (cart == null) {
            GagLoadingScreen(modifier = Modifier.padding(padding))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Order summary
                item {
                    CheckoutSection(title = "Order Summary") {
                        cart.items.forEach { item ->
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("${item.foodName} × ${item.quantity}", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                Text("₹${item.itemTotal.toInt()}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = GagOutlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", style = MaterialTheme.typography.bodyMedium, color = GagOnSurfaceVariant)
                            Text("₹${cart.subtotal.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("GST (5%)", style = MaterialTheme.typography.bodyMedium, color = GagOnSurfaceVariant)
                            Text("₹${cart.tax.toInt()}", style = MaterialTheme.typography.bodyMedium)
                        }
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = GagOutlineVariant)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                            Text("₹${cart.total.toInt()}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = GagOrange)
                        }
                    }
                }

                // Pickup slot
                item {
                    CheckoutSection(title = "Pickup Slot") {
                        if (uiState.selectedSlot != null) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Column {
                                    Text(uiState.selectedSlot!!.displayTime, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                                    Text("${uiState.selectedSlot!!.availableCount} slots left", style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant)
                                }
                                TextButton(onClick = { onSelectPickupSlot(cart.outletId) }) {
                                    Text("Change", color = GagOrange)
                                }
                            }
                        } else {
                            OutlinedButton(
                                onClick = { onSelectPickupSlot(cart.outletId) },
                                modifier = Modifier.fillMaxWidth(),
                                border = androidx.compose.foundation.BorderStroke(1.dp, GagOrange),
                                shape = RoundedCornerShape(10.dp)
                            ) { Text("Select Pickup Time Slot", color = GagOrange) }
                        }
                    }
                }

                // Payment method
                item {
                    CheckoutSection(title = "Payment Method") {
                        PaymentMethod.values().forEach { method ->
                            Row(
                                modifier = Modifier.fillMaxWidth().clickable { viewModel.onPaymentMethodSelected(method) }.padding(vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (uiState.selectedPaymentMethod == method) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked,
                                    contentDescription = null,
                                    tint = if (uiState.selectedPaymentMethod == method) GagOrange else GagOnSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(method.displayName, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
                                    if (method == PaymentMethod.ONLINE) {
                                        Text("Coming soon", style = MaterialTheme.typography.labelSmall, color = GagOnSurfaceVariant)
                                    }
                                }
                            }
                        }
                    }
                }

                // Special instructions
                item {
                    CheckoutSection(title = "Special Instructions (Optional)") {
                        OutlinedTextField(
                            value = uiState.specialInstructions,
                            onValueChange = viewModel::onInstructionsChanged,
                            placeholder = { Text("e.g. Less spice, extra sauce…", color = GagOnSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GagOrange,
                                unfocusedBorderColor = GagOutline,
                                cursorColor = GagOrange
                            )
                        )
                    }
                }

                if (uiState.orderState is UiState.Error) {
                    item {
                        Text((uiState.orderState as UiState.Error).message, color = GagError, style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Surface(shape = RoundedCornerShape(16.dp), color = GagSurface, modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}
