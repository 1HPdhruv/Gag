package com.srmfood.gag.feature.food

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import coil.compose.AsyncImage
import com.srmfood.gag.core.common.UiState
import com.srmfood.gag.core.ui.component.GagErrorScreen
import com.srmfood.gag.core.ui.component.GagLoadingScreen
import com.srmfood.gag.core.ui.component.GagPrimaryButton
import com.srmfood.gag.core.ui.component.GagTopBar
import com.srmfood.gag.core.ui.component.QuantitySelector
import com.srmfood.gag.core.ui.theme.*
import com.srmfood.gag.domain.model.FoodItem
import com.srmfood.gag.domain.usecase.cart.AddToCartUseCase
import com.srmfood.gag.domain.usecase.food.GetFoodItemUseCase
import com.srmfood.gag.domain.usecase.food.ToggleFavoriteUseCase
import com.srmfood.gag.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// ─── ViewModel ────────────────────────────────────────────────────────────────

data class FoodDetailUiState(
    val food: UiState<FoodItem> = UiState.Loading,
    val quantity: Int = 1,
    val isFavorite: Boolean = false,
    val addedToCart: Boolean = false
)

@HiltViewModel
class FoodDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getFoodItemUseCase: GetFoodItemUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val foodId: String = savedStateHandle[Screen.FoodDetail.ARG_FOOD_ID] ?: ""
    private val _uiState = MutableStateFlow(FoodDetailUiState())
    val uiState: StateFlow<FoodDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val result = getFoodItemUseCase(foodId)
            _uiState.value = _uiState.value.copy(
                food = result.fold(onSuccess = { UiState.Success(it) }, onFailure = { UiState.Error(it.message ?: "Failed") })
            )
        }
    }

    fun increaseQuantity() {
        if (_uiState.value.quantity < 10) _uiState.value = _uiState.value.copy(quantity = _uiState.value.quantity + 1)
    }

    fun decreaseQuantity() {
        if (_uiState.value.quantity > 1) _uiState.value = _uiState.value.copy(quantity = _uiState.value.quantity - 1)
    }

    fun addToCart() {
        val food = (_uiState.value.food as? UiState.Success)?.data ?: return
        viewModelScope.launch {
            addToCartUseCase(food, _uiState.value.quantity)
            _uiState.value = _uiState.value.copy(addedToCart = true)
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            val result = toggleFavoriteUseCase(foodId)
            result.onSuccess { isNow ->
                _uiState.value = _uiState.value.copy(isFavorite = isNow)
            }
        }
    }

    fun resetAddedToCart() { _uiState.value = _uiState.value.copy(addedToCart = false) }
}

// ─── Screen ───────────────────────────────────────────────────────────────────

@Composable
fun FoodDetailScreen(
    onBack: () -> Unit,
    onCartClick: () -> Unit,
    viewModel: FoodDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.addedToCart) {
        if (uiState.addedToCart) { viewModel.resetAddedToCart(); onCartClick() }
    }

    when (val foodState = uiState.food) {
        is UiState.Loading -> GagLoadingScreen()
        is UiState.Error -> GagErrorScreen(message = foodState.message, onRetry = {})
        is UiState.Success -> {
            val food = foodState.data
            Scaffold(
                containerColor = GagBackground,
                topBar = {
                    GagTopBar(
                        title = "",
                        onBack = onBack,
                        actions = {
                            IconButton(onClick = viewModel::toggleFavorite) {
                                Icon(
                                    imageVector = if (uiState.isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                    contentDescription = "Favourite",
                                    tint = if (uiState.isFavorite) GagOrange else GagOnBackground
                                )
                            }
                        }
                    )
                },
                bottomBar = {
                    Surface(color = GagBackground, shadowElevation = 8.dp) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            QuantitySelector(
                                quantity = uiState.quantity,
                                onDecrease = viewModel::decreaseQuantity,
                                onIncrease = viewModel::increaseQuantity
                            )
                            GagPrimaryButton(
                                text = "Add to Cart  ₹${(food.price * uiState.quantity).let { if (it % 1 == 0.0) it.toInt() else it }}",
                                onClick = viewModel::addToCart,
                                enabled = food.isAvailable,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            ) { padding ->
                LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                    // Hero image
                    item {
                        Box(modifier = Modifier.fillMaxWidth().height(280.dp).background(GagSurfaceVariant)) {
                            AsyncImage(
                                model = food.imageUrl,
                                contentDescription = food.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            // Veg indicator
                            Box(
                                modifier = Modifier.padding(16.dp).size(22.dp).background(Color.White, CircleShape).align(Alignment.TopEnd),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.FiberManualRecord,
                                    contentDescription = if (food.isVeg) "Veg" else "Non-veg",
                                    tint = if (food.isVeg) VegGreen else NonVegRed,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }

                    // Food info
                    item {
                        Column(modifier = Modifier.padding(20.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(food.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                    Text(food.outletName, style = MaterialTheme.typography.bodyMedium, color = GagOrange)
                                }
                                Text("₹${food.price.toInt()}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = GagOrange)
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.Schedule, null, tint = GagOnSurfaceVariant, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("${food.prepTimeMinutes} min", style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant)
                                }
                                if (food.rating > 0) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Filled.Star, null, tint = GagAmber, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("${food.rating} (${food.totalReviews} reviews)", style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Description", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(food.description, style = MaterialTheme.typography.bodyMedium, color = GagOnSurfaceVariant)

                            if (food.ingredients.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Ingredients", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(food.ingredients.joinToString(", "), style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant)
                            }

                            if (food.calories != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("~${food.calories} cal", style = MaterialTheme.typography.labelSmall, color = GagOnSurfaceVariant)
                            }

                            if (!food.isAvailable) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Surface(shape = RoundedCornerShape(10.dp), color = GagErrorContainer, modifier = Modifier.fillMaxWidth()) {
                                    Text("Currently Unavailable", color = GagError, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(12.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
        else -> {}
    }
}
