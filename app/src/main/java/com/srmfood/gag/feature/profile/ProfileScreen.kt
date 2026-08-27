package com.srmfood.gag.feature.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HelpOutline
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.srmfood.gag.core.ui.component.GagBottomNavBar
import com.srmfood.gag.core.ui.component.GagTopBar
import com.srmfood.gag.core.ui.component.studentBottomNavItems
import com.srmfood.gag.core.ui.theme.*
import com.srmfood.gag.domain.model.User
import com.srmfood.gag.domain.usecase.auth.GetCurrentUserUseCase
import com.srmfood.gag.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    init {
        viewModelScope.launch {
            getCurrentUserUseCase().collectLatest { _user.value = it }
        }
    }

    fun logout() { viewModelScope.launch { logoutUseCase() } }
}

@Composable
fun ProfileScreen(
    onNavigateBottom: (String) -> Unit,
    onLogoutSuccess: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsState()

    Scaffold(
        topBar = { GagTopBar(title = "Profile", onBack = null) },
        bottomBar = { GagBottomNavBar(items = studentBottomNavItems, currentRoute = "profile", onItemSelected = onNavigateBottom) },
        containerColor = GagBackground
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
            item {
                Surface(shape = RoundedCornerShape(16.dp), color = GagSurface, modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(shape = RoundedCornerShape(40.dp), color = GagOrange.copy(0.15f), modifier = Modifier.size(64.dp)) {
                            Icon(Icons.Outlined.Person, null, tint = GagOrange, modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(user?.name ?: "Student", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(user?.email ?: "student@srmist.edu.in", style = MaterialTheme.typography.bodyMedium, color = GagOnSurfaceVariant)
                            user?.registrationNumber?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = GagOnSurfaceVariant) }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }

            item { Text("General", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp)) }
            item { ProfileMenuItem(icon = Icons.Outlined.FavoriteBorder, title = "Favorites", onClick = { /* TODO */ }) }
            item { ProfileMenuItem(icon = Icons.Outlined.Settings, title = "Settings", onClick = { /* TODO */ }) }
            item { ProfileMenuItem(icon = Icons.Outlined.HelpOutline, title = "Help & Support", onClick = { /* TODO */ }) }
            
            item { Spacer(modifier = Modifier.height(24.dp)) }
            item {
                ProfileMenuItem(
                    icon = Icons.Outlined.ExitToApp,
                    title = "Logout",
                    tint = GagError,
                    onClick = { viewModel.logout(); onLogoutSuccess() }
                )
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(icon: ImageVector, title: String, tint: androidx.compose.ui.graphics.Color = GagOnBackground, onClick: () -> Unit) {
    Surface(shape = RoundedCornerShape(12.dp), color = GagBackground, modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(modifier = Modifier.padding(vertical = 12.dp, horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = tint)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, style = MaterialTheme.typography.bodyLarge, color = tint)
        }
    }
}
