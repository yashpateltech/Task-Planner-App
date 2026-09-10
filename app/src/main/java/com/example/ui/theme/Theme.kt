package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.data.ThemeMode

private val LightColorScheme = lightColorScheme(
    primary = PinkPrimary,
    onPrimary = PinkOnPrimary,
    primaryContainer = PinkPrimaryContainer,
    onPrimaryContainer = PinkOnPrimaryContainer,
    secondary = PinkSecondary,
    onSecondary = PinkOnSecondary,
    secondaryContainer = PinkSecondaryContainer,
    onSecondaryContainer = PinkOnSecondaryContainer,
    tertiary = PinkTertiary,
    onTertiary = PinkOnTertiary,
    tertiaryContainer = PinkTertiaryContainer,
    onTertiaryContainer = PinkOnTertiaryContainer,
    background = WhiteBackground,
    onBackground = OnSurfacePink,
    surface = WhiteSurface,
    onSurface = OnSurfacePink,
    surfaceVariant = SurfaceContainerWhite,
    onSurfaceVariant = OnSurfaceVariantPink,
    surfaceContainer = WhiteSurface,
    surfaceContainerLow = WhiteSurface,
    surfaceContainerHigh = SurfaceContainerWhite,
    surfaceContainerHighest = SurfaceContainerHighestWhite,
    outline = OutlinePink,
    outlineVariant = OutlineVariantPink
)

private val DarkColorScheme = darkColorScheme(
    primary = PinkPrimaryDark,
    onPrimary = PinkOnPrimaryDark,
    primaryContainer = PinkPrimaryContainerDark,
    onPrimaryContainer = PinkOnPrimaryContainerDark,
    secondary = PinkSecondaryDark,
    onSecondary = PinkOnSecondaryDark,
    secondaryContainer = PinkSecondaryContainerDark,
    onSecondaryContainer = PinkOnSecondaryContainerDark,
    tertiary = PinkTertiaryDark,
    onTertiary = PinkOnTertiaryDark,
    tertiaryContainer = PinkTertiaryContainerDark,
    onTertiaryContainer = PinkOnTertiaryContainerDark,
    background = BlackBackground,
    onBackground = OnSurfaceDark,
    surface = BlackSurface,
    onSurface = OnSurfaceDark,
    surfaceVariant = BlackSurfaceVariant,
    onSurfaceVariant = OnSurfaceVariantDark,
    surfaceContainer = BlackSurfaceContainer,
    surfaceContainerLow = Color(0xFF141418),
    surfaceContainerHigh = BlackSurfaceContainerHigh,
    surfaceContainerHighest = Color(0xFF32323E),
    outline = BlackOutline,
    outlineVariant = BlackOutlineVariant
)

@Composable
fun TaskPlannerTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }

    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (isDark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        isDark -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
