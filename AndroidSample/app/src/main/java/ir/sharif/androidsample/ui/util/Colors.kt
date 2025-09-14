package ir.sharif.androidsample.ui.util

import androidx.compose.ui.graphics.Color

/** Compose Color -> packed ARGB Int (0xAARRGGBB). */
fun Color.toArgbInt(): Int {
  val a = (alpha * 255f + 0.5f).toInt().coerceIn(0, 255)
  val r = (red   * 255f + 0.5f).toInt().coerceIn(0, 255)
  val g = (green * 255f + 0.5f).toInt().coerceIn(0, 255)
  val b = (blue  * 255f + 0.5f).toInt().coerceIn(0, 255)
  return (a shl 24) or (r shl 16) or (g shl 8) or b
}

/** Packed ARGB Int -> Compose Color. */
fun argbIntToColor(argb: Int): Color {
  val a = (argb ushr 24) and 0xFF
  val r = (argb ushr 16) and 0xFF
  val g = (argb ushr  8) and 0xFF
  val b = (argb        ) and 0xFF
  return Color(r / 255f, g / 255f, b / 255f, a / 255f)
}

/** "#RRGGBB" or "#AARRGGBB" -> ARGB Int (null if invalid). */
fun hexToArgbInt(hex: String?): Int? {
  val h = hex?.trim()?.removePrefix("#") ?: return null
  val v = when (h.length) {
    6 -> "FF$h" // add full alpha
    8 -> h
    else -> return null
  }
  return v.toLong(16).toInt()
}

fun colorToHex(color: Color): String = argbIntToHex(color.toArgbInt())



fun colorFromHexOrNull(hex: String?): Color? {
  val h = hex?.trim()?.removePrefix("#") ?: return null
  val v = when (h.length) { 6 -> "FF$h"; 8 -> h; else -> return null }
  return try { Color(v.toLong(16).toInt()) } catch (_: Exception) { null }
}
