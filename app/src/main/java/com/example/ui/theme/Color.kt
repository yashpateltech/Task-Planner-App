package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// White Background & Pink Theme Palette
val PinkPrimary = Color(0xFFE11D48) // Vivid Rose Pink
val PinkOnPrimary = Color(0xFFFFFFFF)
val PinkPrimaryContainer = Color(0xFFFDE8EF) // Soft Blush Pink
val PinkOnPrimaryContainer = Color(0xFF881337)

val PinkSecondary = Color(0xFFDB2777) // Bright Fuchsia Pink
val PinkOnSecondary = Color(0xFFFFFFFF)
val PinkSecondaryContainer = Color(0xFFFCE7F3)
val PinkOnSecondaryContainer = Color(0xFF701A75)

val PinkTertiary = Color(0xFFBE185D) // Deep Berry Pink
val PinkOnTertiary = Color(0xFFFFFFFF)
val PinkTertiaryContainer = Color(0xFFFFF0F5) // Lavender Blush
val PinkOnTertiaryContainer = Color(0xFF4C0519)

val WhiteBackground = Color(0xFFFFFFFF) // Pure White
val WhiteSurface = Color(0xFFFFFFFF)
val SurfaceContainerWhite = Color(0xFFFFF7F9)
val SurfaceContainerHighestWhite = Color(0xFFFDE8EF)

// Deep dark black colors for task text and typography
val DarkBlackText = Color(0xFF0F0F12) // Pure solid dark black for task text, titles & headers
val DarkBlackVariantText = Color(0xFF26262B) // High-contrast dark black-charcoal for task descriptions
val DarkBlackMutedText = Color(0xFF52525C) // Crisp readable dark muted tone for completed task text

val OnSurfacePink = DarkBlackText
val OnSurfaceVariantPink = DarkBlackVariantText
val OutlinePink = Color(0xFFE8CCD6)
val OutlineVariantPink = Color(0xFFF6E2EA)

// Black Theme Palette (High-contrast, sleek modern black)
val BlackBackground = Color(0xFF0F0F12) // Pure sleek black
val BlackSurface = Color(0xFF18181D)    // Dark card surface
val BlackSurfaceVariant = Color(0xFF23232B) // Elevated chip/card surface
val BlackSurfaceContainer = Color(0xFF1E1E24)
val BlackSurfaceContainerHigh = Color(0xFF282832)
val BlackOutline = Color(0xFF3A3A46)    // Clearly defined visible border
val BlackOutlineVariant = Color(0xFF2A2A34)

val TextWhite = Color(0xFFFFFFFF)       // Crisp white text
val TextSilver = Color(0xFFE2E8F0)      // High-contrast readable secondary text
val TextMuted = Color(0xFF94A3B8)       // Readable muted text

// Dark Theme Pink Palette
val PinkPrimaryDark = Color(0xFFFF4D6D)
val PinkOnPrimaryDark = Color(0xFFFFFFFF)
val PinkPrimaryContainerDark = Color(0xFF881337)
val PinkOnPrimaryContainerDark = Color(0xFFFDE8EF)

val PinkSecondaryDark = Color(0xFFF472B6)
val PinkOnSecondaryDark = Color(0xFFFFFFFF)
val PinkSecondaryContainerDark = Color(0xFF701A75)
val PinkOnSecondaryContainerDark = Color(0xFFFCE7F3)

val PinkTertiaryDark = Color(0xFFFDA4AF)
val PinkOnTertiaryDark = Color(0xFFFFFFFF)
val PinkTertiaryContainerDark = Color(0xFF9F1239)
val PinkOnTertiaryContainerDark = Color(0xFFFFF0F5)

val SurfaceDark = BlackSurface
val SurfaceContainerDark = BlackSurfaceContainer
val OnSurfaceDark = TextWhite
val OnSurfaceVariantDark = TextSilver

// Priority accent colors
val PriorityHigh = Color(0xFFE11D48) // High Priority: Vivid Pink
val PriorityMedium = Color(0xFFF59E0B) // Medium Priority: Warm Amber
val PriorityLow = Color(0xFF10B981) // Low Priority: Emerald Green

// Pink Gradients
object PinkGradients {
    val Primary = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFE11D48), // Rose pink
            Color(0xFFF43F5E), // Vibrant pink
            Color(0xFFFF6584)  // Coral pink
        )
    )

    val Header = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFBE185D), // Deep rose
            Color(0xFFE11D48), // Vivid pink
            Color(0xFFFB7185)  // Bright pink
        )
    )

    val Diagonal = Brush.linearGradient(
        colors = listOf(
            Color(0xFFD91B5C),
            Color(0xFFF02A71),
            Color(0xFFFF729F)
        )
    )

    val Secondary = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFDB2777),
            Color(0xFFE11D48)
        )
    )

    val Soft = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFFF0F5),
            Color(0xFFFCE7F3),
            Color(0xFFFFE4ED)
        )
    )

    val CardBorder = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFFFDA4AF),
            Color(0xFFF43F5E),
            Color(0xFFFB7185)
        )
    )

    val SubtlyGlowing = Brush.verticalGradient(
        colors = listOf(
            Color(0xFFFFF1F5),
            Color(0xFFFFFFFF)
        )
    )
}

