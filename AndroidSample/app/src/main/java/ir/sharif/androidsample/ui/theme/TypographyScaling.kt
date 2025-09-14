package ir.sharif.androidsample.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

private fun TextUnit.bump(delta: Float) = (this.value + delta).sp

fun Typography.scaled(deltaSp: Float): Typography =
  Typography(
    displayLarge = displayLarge.copy(fontSize = displayLarge.fontSize.bump(deltaSp)),
    displayMedium = displayMedium.copy(fontSize = displayMedium.fontSize.bump(deltaSp)),
    displaySmall = displaySmall.copy(fontSize = displaySmall.fontSize.bump(deltaSp)),
    headlineLarge = headlineLarge.copy(fontSize = headlineLarge.fontSize.bump(deltaSp)),
    headlineMedium = headlineMedium.copy(fontSize = headlineMedium.fontSize.bump(deltaSp)),
    headlineSmall = headlineSmall.copy(fontSize = headlineSmall.fontSize.bump(deltaSp)),
    titleLarge = titleLarge.copy(fontSize = titleLarge.fontSize.bump(deltaSp)),
    titleMedium = titleMedium.copy(fontSize = titleMedium.fontSize.bump(deltaSp)),
    titleSmall = titleSmall.copy(fontSize = titleSmall.fontSize.bump(deltaSp)),
    bodyLarge = bodyLarge.copy(fontSize = bodyLarge.fontSize.bump(deltaSp)),
    bodyMedium = bodyMedium.copy(fontSize = bodyMedium.fontSize.bump(deltaSp)),
    bodySmall = bodySmall.copy(fontSize = bodySmall.fontSize.bump(deltaSp)),
    labelLarge = labelLarge.copy(fontSize = labelLarge.fontSize.bump(deltaSp)),
    labelMedium = labelMedium.copy(fontSize = labelMedium.fontSize.bump(deltaSp)),
    labelSmall = labelSmall.copy(fontSize = labelSmall.fontSize.bump(deltaSp)),
  )
