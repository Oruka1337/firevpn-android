package com.v2ray.ang.ui.compose

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.v2ray.ang.AppConfig
import com.v2ray.ang.handler.MmkvManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// FireVPN: огненная палитра сайта fire-vpn.online
private val LightColor = lightColorScheme(
    primary = Color(0xFFD9381A), // Fire Red-Orange
    onPrimary = Color(0xFFFFFFFF), // White
    primaryContainer = Color(0xFFFFDBCF), // Pale Flame
    onPrimaryContainer = Color(0xFF3A0B00), // Deep Ember
    secondary = Color(0xFFE8203C), // Fire Red
    onSecondary = Color(0xFFFFFFFF), // White
    secondaryContainer = Color(0xFFFFDADC), // Pale Red
    onSecondaryContainer = Color(0xFF40000B), // Dark Red
    tertiary = Color(0xFFB07A00), // Gold
    onTertiary = Color(0xFFFFFFFF), // White
    tertiaryContainer = Color(0xFFFFE08A), // Light Gold
    onTertiaryContainer = Color(0xFF2A1C00), // Dark Gold
    error = Color(0xFFBA1A1A), // Red
    errorContainer = Color(0xFFFFDAD6), // Light Red
    onError = Color(0xFFFFFFFF), // White
    onErrorContainer = Color(0xFF410002), // Dark Red
    background = Color(0xFFFFF8F5), // Warm White
    onBackground = Color(0xFF231917), // Near Black
    surface = Color(0xFFFFF8F5), // Warm White
    onSurface = Color(0xFF231917), // Near Black
    surfaceVariant = Color(0xFFF5DED8), // Warm Gray
    onSurfaceVariant = Color(0xFF53433F), // Dark Warm Gray
    outline = Color(0xFF85736E), // Medium Warm Gray
    outlineVariant = Color(0xFFD8C2BC), // Light Warm Gray
    inverseSurface = Color(0xFF392E2B), // Dark Warm Gray
    inverseOnSurface = Color(0xFFFFEDE8), // Very Light Warm
    inversePrimary = Color(0xFFFFB59F), // Light Flame
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFFD9381A), // Fire Red-Orange
    surfaceContainerLowest = Color(0xFFFFFFFF), // White
    surfaceContainerLow = Color(0xFFFFF1EC), // Very Light Warm
    surfaceContainer = Color(0xFFFCEAE4), // Light Warm
    surfaceContainerHigh = Color(0xFFF6E4DE), // Light Warm
    surfaceContainerHighest = Color(0xFFF1DFD9), // Light Warm
)

private val DarkColor = darkColorScheme(
    primary = Color(0xFFFF6A33), // Flame Orange
    onPrimary = Color(0xFF3A0B00), // Deep Ember
    primaryContainer = Color(0xFF5C1A08), // Ember
    onPrimaryContainer = Color(0xFFFFDBCF), // Pale Flame
    secondary = Color(0xFFFF5A6E), // Fire Red
    onSecondary = Color(0xFF67001A), // Dark Red
    secondaryContainer = Color(0xFF8C1027), // Deep Red
    onSecondaryContainer = Color(0xFFFFDADC), // Pale Red
    tertiary = Color(0xFFFFCC33), // Gold
    onTertiary = Color(0xFF3F2E00), // Dark Gold
    tertiaryContainer = Color(0xFF5B4300), // Bronze
    onTertiaryContainer = Color(0xFFFFE08A), // Light Gold
    error = Color(0xFFFFB4AB), // Light Red
    errorContainer = Color(0xFF93000A), // Dark Red
    onError = Color(0xFF690005), // Deep Red
    onErrorContainer = Color(0xFFFFDAD6), // Light Red
    background = Color(0xFF120305), // Night Ember (site background)
    onBackground = Color(0xFFFFF3EC), // Warm White
    surface = Color(0xFF120305), // Night Ember
    onSurface = Color(0xFFFFF3EC), // Warm White
    surfaceVariant = Color(0xFF3A1A1C), // Dark Ember Gray
    onSurfaceVariant = Color(0xFFC9A59A), // Muted Peach (site text)
    outline = Color(0xFF8F6E66), // Dim Peach
    outlineVariant = Color(0xFF3A1A1C), // Dark Ember Gray
    inverseSurface = Color(0xFFFFF3EC), // Warm White
    inverseOnSurface = Color(0xFF120305), // Night Ember
    inversePrimary = Color(0xFFD9381A), // Fire Red-Orange
    scrim = Color(0xFF000000), // Black
    surfaceTint = Color(0xFFFF6A33), // Flame Orange
    surfaceContainerLowest = Color(0xFF0C0203), // Near Black
    surfaceContainerLow = Color(0xFF1A0608), // Dark Ember
    surfaceContainer = Color(0xFF200A0C), // Dark Ember
    surfaceContainerHigh = Color(0xFF2B1013), // Ember Gray
    surfaceContainerHighest = Color(0xFF36161A), // Ember Gray
)

// Semantic Colors
val colorPing = Color(0xFF009966) // Green
val colorPingRed = Color(0xFFFF0099) // Pink Red
val colorConfigType = Color(0xFFFF6A33) // Flame Orange
val colorFabActive = Color(0xFFFF5A1F) // Flame Orange
val colorFabInactiveLight = Color(0xFF9C9C9C) // Gray
val colorFabInactiveDark = Color(0xFF646464) // Dark Gray
val dividerColorLight = Color(0xFFE0E0E0) // Light Gray
val dividerColorDark = Color(0xFF424242) // Dark Gray

// Toast Colors 85%
val toastNormalBgLight = Color(0xD9353A3E) // Dark Gray
val toastNormalBgDark = Color(0xD94A4F54) // Darker Gray
val toastSuccessBg = Color(0xD9388E3C) // Green
val toastErrorBg = Color(0xD9D50000) // Red
val toastInfoBg = Color(0xD93F51B5) // Indigo Blue
val toastIconCircleBg = Color(0x33FFFFFF) // Semi-transparent White
val toastTextColor = Color.White // White

object ThemeManager {
    private val _themeMode = MutableStateFlow(
        MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2"
    )
    val themeMode: StateFlow<String> = _themeMode.asStateFlow()

    private val _dynamicColorEnabled = MutableStateFlow(
        MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    )
    val dynamicColorEnabled: StateFlow<Boolean> = _dynamicColorEnabled.asStateFlow()

    fun setThemeMode(mode: String) {
        MmkvManager.encodeSettings(AppConfig.PREF_UI_MODE_NIGHT, mode)
        _themeMode.value = mode
    }

    fun setDynamicColorEnabled(enabled: Boolean) {
        MmkvManager.encodeSettings(AppConfig.PREF_DYNAMIC_COLOR, enabled)
        _dynamicColorEnabled.value = enabled
    }

    fun refresh() {
        _themeMode.value =
            MmkvManager.decodeSettingsString(AppConfig.PREF_UI_MODE_NIGHT, "2") ?: "2"
        _dynamicColorEnabled.value =
            MmkvManager.decodeSettingsBool(AppConfig.PREF_DYNAMIC_COLOR, false)
    }
}

@Composable
fun resolveDarkTheme(): Boolean {
    val mode by ThemeManager.themeMode.collectAsState()
    return when (mode) {
        "1" -> false
        "2" -> true
        else -> isSystemInDarkTheme()
    }
}

val LocalDarkTheme = compositionLocalOf { false }

@Composable
fun AppTheme(
    darkTheme: Boolean = resolveDarkTheme(),
    content: @Composable () -> Unit
) {
    val dynamicColor by ThemeManager.dynamicColorEnabled.collectAsState()
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColor
        else -> LightColor
    }
    val snackbarController = rememberAppSnackbarController()

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val activity = view.context as? Activity ?: return@SideEffect
            val window = activity.window
            WindowCompat.getInsetsController(window, view).apply {
                isAppearanceLightStatusBars = !darkTheme
                isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    CompositionLocalProvider(
        LocalDarkTheme provides darkTheme,
        LocalAppSnackbar provides snackbarController
    ) {
        MaterialTheme(
            colorScheme = colorScheme
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AppSnackbarBridge(controller = snackbarController)
                content()
                AppSnackbarHost(hostState = snackbarController.hostState)
            }
        }
    }
}
