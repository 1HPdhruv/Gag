package com.srmfood.gag.feature.vendor.menu

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagTopBar

@Composable
fun VendorMenuScreen(onBack: () -> Unit) {
    Scaffold(topBar = { GagTopBar("Menu", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "Menu management coming soon", modifier = Modifier.padding(padding))
    }
}
