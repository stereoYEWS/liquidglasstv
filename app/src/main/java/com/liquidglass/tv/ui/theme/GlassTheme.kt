package com.liquidglass.tv.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Палитра в духе "Liquid Glass": тёмный фон + мягкие цветные переливы
// под полупрозрачным стеклом, как в последних дизайн-системах Apple.

val DeepSpace = Color(0xFF05060A)
val NebulaBlue = Color(0xFF1B2A4A)
val NebulaPurple = Color(0xFF3A1B4A)
val NebulaTeal = Color(0xFF123A3A)

val GlassSurface = Color(0x33FFFFFF)      // полупрозрачная белая "стеклянная" подложка
val GlassSurfaceFocused = Color(0x55FFFFFF)
val GlassBorder = Color(0x40FFFFFF)
val GlassHighlight = Color(0x80FFFFFF)

val TextPrimary = Color(0xFFF5F6FA)
val TextSecondary = Color(0xB3F5F6FA)

val backgroundGradient = Brush.radialGradient(
    colors = listOf(NebulaBlue, NebulaPurple, DeepSpace, DeepSpace)
)

val glassCardGradient = Brush.linearGradient(
    colors = listOf(Color(0x40FFFFFF), Color(0x10FFFFFF))
)
