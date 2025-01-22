package com.nsicyber.notificatipsum.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = DarkYellow,
    onPrimary = White,
    primaryContainer = LightYellow,
    onPrimaryContainer = DarkText,
    secondary = DarkYellow,
    onSecondary = White,
    secondaryContainer = LightYellow,
    onSecondaryContainer = DarkText,
    tertiary = DarkYellow,
    onTertiary = White,
    tertiaryContainer = LightYellow,
    onTertiaryContainer = DarkText,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = White,
    onErrorContainer = Color(0xFF410002),
    background = OffWhite,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumText,
    outline = LightText
)

private val DarkColorScheme = lightColorScheme(
    primary = DarkYellow,
    onPrimary = White,
    primaryContainer = LightYellow,
    onPrimaryContainer = DarkText,
    secondary = DarkYellow,
    onSecondary = White,
    secondaryContainer = LightYellow,
    onSecondaryContainer = DarkText,
    tertiary = DarkYellow,
    onTertiary = White,
    tertiaryContainer = LightYellow,
    onTertiaryContainer = DarkText,
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    onError = White,
    onErrorContainer = Color(0xFF410002),
    background = White,
    onBackground = DarkText,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = LightGray,
    onSurfaceVariant = MediumText,
    outline = LightText
)

@Composable
fun NotificatipsumTheme(
    darkTheme: Boolean = false, // Always use light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme // Always use light color scheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}