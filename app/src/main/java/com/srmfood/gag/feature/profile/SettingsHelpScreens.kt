package com.srmfood.gag.feature.profile

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.srmfood.gag.core.ui.component.GagEmptyScreen
import com.srmfood.gag.core.ui.component.GagTopBar

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(topBar = { GagTopBar("Settings", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "Settings coming soon", modifier = Modifier.padding(padding))
    }
}

@Composable
fun HelpScreen(onBack: () -> Unit) {
    Scaffold(topBar = { GagTopBar("Help & Support", onBack = onBack) }) { padding ->
        GagEmptyScreen(title = "Help & Support coming soon", modifier = Modifier.padding(padding))
    }
}
