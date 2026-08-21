package com.srmfood.gag.core.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val GagDarkColorScheme = darkColorScheme(
    primary = GagOrange,
    onPrimary = GagOnBackground,
    primaryContainer = GagOrangeContainer,
    onPrimaryContainer = GagOnOrangeContainer,
    secondary = GagAmber,
    onSecondary = GagOnBackground,
    secondaryContainer = GagWarningContainer,
    onSecondaryContainer = GagAmberLight,
    tertiary = GagInfo,
    onTertiary = GagOnBackground,
    background = GagBackground,
    onBackground = GagOnBackground,
    surface = GagSurface,
    onSurface = GagOnSurface,
    surfaceVariant = GagSurfaceVariant,
    onSurfaceVariant = GagOnSurfaceVariant,
    outline = GagOutline,
    outlineVariant = GagOutlineVariant,
    error = GagError,
    onError = GagOnBackground,
    errorContainer = GagErrorContainer,
    onErrorContainer = GagError
)

@Composable
fun GagTheme(
    content: @Composable () -> Unit
) {
    val colorScheme = GagDarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = GagBackground.toArgb()
            window.navigationBarColor = BottomNavBackground.toArgb()
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = false
                isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GagTypography,
        shapes = GagShapes,
        content = content
    )
}
