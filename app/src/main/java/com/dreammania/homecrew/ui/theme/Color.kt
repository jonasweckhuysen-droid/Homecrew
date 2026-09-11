package com.dreammania.homecrew.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Soft, calm, and colorful palette
val CalmPeach = Color(0xFFFFF0E1)
val CalmMint = Color(0xFFE0F7FA)
val CalmSky = Color(0xFFE1F5FE)
val CalmPink = Color(0xFFFCE4EC)

// Slightly darker grey background as requested
val BackgroundColor = Color(0xFFE0E0E0)
val TextColor = Color(0xFF4E342E)

// Gradients for a softer feel - Adjusted for better contrast with white text
val HeaderBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF0288D1), Color(0xFF01579B)) // Darker blue shades
)

val CardBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFFFFFFFF), CalmMint)
)

// Soft button and event colors
val ButtonRed = Color(0xFFF48FB1)
val ButtonGreen = Color(0xFFA5D6A7)
val ButtonBlue = Color(0xFF90CAF9)
val ButtonYellow = Color(0xFFFFF59D)
val ButtonPurple = Color(0xFFCE93D8)
val ButtonOrange = Color(0xFFFFCC80)
val ButtonCyan = Color(0xFF80DEEA)
val ButtonTeal = Color(0xFF80CBC4)

val EventColors = listOf(
    ButtonRed, ButtonGreen, ButtonBlue, ButtonYellow, 
    ButtonPurple, ButtonOrange, ButtonCyan, ButtonTeal
)
