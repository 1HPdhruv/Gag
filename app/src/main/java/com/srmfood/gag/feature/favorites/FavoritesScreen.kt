package com.srmfood.gag.feature.favorites

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagTopBar

@Composable
fun FavoritesScreen(
    onBack: () -> Unit,
    onFoodClick: (String) -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    Scaffold(topBar = { GagTopBar("Favorites", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "No favorites yet", modifier = Modifier.padding(padding))
    }
}
