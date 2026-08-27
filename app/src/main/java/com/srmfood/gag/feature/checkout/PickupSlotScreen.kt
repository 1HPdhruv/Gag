package com.srmfood.gag.feature.checkout

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srmfood.gag.core.common.UiState
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagLoadingScreen
import com.srmfood.gag.core.ui.component.GagTopBar
import com.srmfood.gag.core.ui.theme.*
import com.srmfood.gag.domain.model.PickupSlot
import com.srmfood.gag.domain.model.SlotStatus
import com.srmfood.gag.domain.usecase.order.GetPickupSlotsUseCase
import com.srmfood.gag.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────

data class PickupSlotUiState(
    val slots: UiState<List<PickupSlot>> = UiState.Loading,
    val selectedSlot: PickupSlot? = null
)

@HiltViewModel
class PickupSlotViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getPickupSlotsUseCase: GetPickupSlotsUseCase
) : ViewModel() {

    private val outletId: String = savedStateHandle[Screen.PickupSlotSelection.ARG_OUTLET_ID] ?: ""
    private val _uiState = MutableStateFlow(PickupSlotUiState())
    val uiState: StateFlow<PickupSlotUiState> = _uiState.asStateFlow()

    init { loadSlots() }

    private fun loadSlots() {
        viewModelScope.launch {
            val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
            val result = getPickupSlotsUseCase(outletId, today)
            _uiState.value = _uiState.value.copy(
                slots = result.fold(
                    onSuccess = { if (it.isEmpty()) UiState.Empty else UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Failed to load slots") }
                )
            )
        }
    }

    fun selectSlot(slot: PickupSlot) {
        if (slot.status != SlotStatus.FULL) {
            _uiState.value = _uiState.value.copy(selectedSlot = slot)
        }
    }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun PickupSlotScreen(
    onBack: () -> Unit,
    onSlotSelected: (PickupSlot) -> Unit,
    viewModel: PickupSlotViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { GagTopBar(title = "Select Pickup Slot", onBack = onBack) },
        containerColor = GagBackground,
        bottomBar = {
            if (uiState.selectedSlot != null) {
                Surface(color = GagBackground, shadowElevation = 8.dp) {
                    Button(
                        onClick = { onSlotSelected(uiState.selectedSlot!!) },
                        modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                        colors = ButtonDefaults.buttonColors(containerColor = GagOrange),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Confirm: ${uiState.selectedSlot!!.displayTime}", fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    ) { padding ->
        when (val slots = uiState.slots) {
            is UiState.Loading -> GagLoadingScreen(modifier = Modifier.padding(padding))
            is UiState.Empty -> GagEmptyScreen(title = "No slots available", message = "Please try again later", modifier = Modifier.padding(padding))
            is UiState.Error -> com.srmfood.gag.core.ui.component.GagErrorScreen(message = slots.message, onRetry = {}, modifier = Modifier.padding(padding))
            is UiState.Success -> {
                Column(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
                    Text(
                        "Choose a pickup window. Backend controls capacity — slots marked FULL cannot be selected.",
                        style = MaterialTheme.typography.bodySmall,
                        color = GagOnSurfaceVariant,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    slots.data.forEach { slot ->
                        PickupSlotCard(
                            slot = slot,
                            isSelected = uiState.selectedSlot?.id == slot.id,
                            onClick = { viewModel.selectSlot(slot) }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
            else -> {}
        }
    }
}

@Composable
private fun PickupSlotCard(slot: PickupSlot, isSelected: Boolean, onClick: () -> Unit) {
    val slotColor = when (slot.status) {
        SlotStatus.AVAILABLE -> SlotAvailable
        SlotStatus.LIMITED -> SlotLimited
        SlotStatus.FULL -> SlotFull
    }
    val bgColor = when {
        isSelected -> GagOrange.copy(alpha = 0.15f)
        slot.status == SlotStatus.FULL -> GagSurface.copy(alpha = 0.5f)
        else -> GagSurface
    }
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = bgColor,
        border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, GagOrange) else null,
        modifier = Modifier.fillMaxWidth().clickable(enabled = slot.status != SlotStatus.FULL, onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(slot.displayTime, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,
                    color = if (slot.status == SlotStatus.FULL) GagOnSurfaceVariant else GagOnBackground)
                Text("${slot.availableCount}/${slot.capacity} available", style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant)
            }
            Surface(shape = RoundedCornerShape(8.dp), color = slotColor.copy(alpha = 0.15f)) {
                Text(
                    slot.status.name,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = slotColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                )
            }
        }
    }
}
