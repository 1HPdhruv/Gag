package com.srmfood.gag.feature.notifications

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagTopBar

@Composable
fun NotificationsScreen(
    onBack: () -> Unit,
    onOrderClick: (String) -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    Scaffold(topBar = { GagTopBar("Notifications", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "No notifications", modifier = Modifier.padding(padding))
    }
}
