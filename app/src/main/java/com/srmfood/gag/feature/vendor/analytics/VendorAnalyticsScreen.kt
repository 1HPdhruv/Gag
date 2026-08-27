package com.srmfood.gag.feature.vendor.analytics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagTopBar

@Composable
fun VendorAnalyticsScreen(onBack: () -> Unit) {
    Scaffold(topBar = { GagTopBar("Analytics", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "Analytics coming soon", modifier = Modifier.padding(padding))
    }
}
