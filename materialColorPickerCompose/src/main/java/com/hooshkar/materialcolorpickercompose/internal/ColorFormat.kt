package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.ui.graphics.Color
import kotlin.math.roundToInt

internal fun Color.redInt(): Int = (red * 255f).roundToInt().coerceIn(0, 255)
internal fun Color.greenInt(): Int = (green * 255f).roundToInt().coerceIn(0, 255)
internal fun Color.blueInt(): Int = (blue * 255f).roundToInt().coerceIn(0, 255)

internal fun Color.toHexRgb(): String =
    "%02X%02X%02X".format(redInt(), greenInt(), blueInt())

internal fun colorFromHex(hex: String, alpha: Int): Color? {
    if (hex.length != 6) return null
    val rgb = hex.toIntOrNull(16) ?: return null
    val argb = (alpha and 0xFF shl 24) or (rgb and 0xFFFFFF)
    return Color(argb)
}

internal fun colorFromRgb(red: Int, green: Int, blue: Int, alpha: Int): Color = Color(
    red = red.coerceIn(0, 255) / 255f,
    green = green.coerceIn(0, 255) / 255f,
    blue = blue.coerceIn(0, 255) / 255f,
    alpha = alpha.coerceIn(0, 255) / 255f
)
