package com.srmfood.gag.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srmfood.gag.core.common.UiState
import com.srmfood.gag.domain.model.FoodCategory
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.model.Order
import com.srmfood.gag.domain.model.Outlet
import com.srmfood.gag.domain.model.User
import com.srmfood.gag.domain.usecase.auth.GetCurrentUserUseCase
import com.srmfood.gag.domain.usecase.food.GetCategoriesUseCase
import com.srmfood.gag.domain.usecase.food.GetPopularFoodUseCase
import com.srmfood.gag.domain.usecase.food.GetRecommendedFoodUseCase
import com.srmfood.gag.domain.usecase.order.GetOrdersUseCase
import com.srmfood.gag.domain.usecase.outlet.GetOutletsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val user: User? = null,
    val outlets: UiState<List<Outlet>> = UiState.Loading,
    val popularFood: UiState<List<FoodItem>> = UiState.Loading,
    val recommendedFood: UiState<List<FoodItem>> = UiState.Loading,
    val categories: UiState<List<FoodCategory>> = UiState.Loading,
    val activeOrder: Order? = null,
    val cartItemCount: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getOutletsUseCase: GetOutletsUseCase,
    private val getPopularFoodUseCase: GetPopularFoodUseCase,
    private val getRecommendedFoodUseCase: GetRecommendedFoodUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getOrdersUseCase: GetOrdersUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
        observeUser()
        observeActiveOrders()
    }

    private fun observeUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { user ->
                _uiState.value = _uiState.value.copy(user = user)
            }
        }
    }

    private fun observeActiveOrders() {
        viewModelScope.launch {
            getOrdersUseCase().collectLatest { orders ->
                _uiState.value = _uiState.value.copy(
                    activeOrder = orders.firstOrNull { it.status.isActive }
                )
            }
        }
    }

    fun loadData() {
        viewModelScope.launch {
            // Load outlets
            getOutletsUseCase().collectLatest { outlets ->
                _uiState.value = _uiState.value.copy(outlets = UiState.Success(outlets))
            }
        }
        viewModelScope.launch {
            val popularResult = getPopularFoodUseCase()
            _uiState.value = _uiState.value.copy(
                popularFood = popularResult.fold(
                    onSuccess = { if (it.isEmpty()) UiState.Empty else UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Failed") }
                )
            )
        }
        viewModelScope.launch {
            val recommendedResult = getRecommendedFoodUseCase()
            _uiState.value = _uiState.value.copy(
                recommendedFood = recommendedResult.fold(
                    onSuccess = { if (it.isEmpty()) UiState.Empty else UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Failed") }
                )
            )
        }
        viewModelScope.launch {
            val catResult = getCategoriesUseCase()
            _uiState.value = _uiState.value.copy(
                categories = catResult.fold(
                    onSuccess = { UiState.Success(it) },
                    onFailure = { UiState.Error(it.message ?: "Failed") }
                )
            )
        }
    }
}
