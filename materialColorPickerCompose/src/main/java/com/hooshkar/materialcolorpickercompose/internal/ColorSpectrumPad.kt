package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import com.hooshkar.materialcolorpickercompose.MaxSpectrumHue

/**
 * The Hue (horizontal) x Saturation (vertical) picking pad shown on the Spectrum page.
 * Brightness and opacity are controlled by separate sliders, so this pad always renders at
 * full brightness/full alpha regardless of the currently selected color.
 */
@Composable
internal fun ColorSpectrumPad(
    hue: Float,
    saturation: Float,
    onHueSaturationChanged: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val cursorColor = Color(android.graphics.Color.HSVToColor(floatArrayOf(hue, saturation, 1f)))

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ColorPickerDefaults.SpectrumAspectRatio)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            )
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed } ?: continue
                        val xFraction = (change.position.x / size.width).coerceIn(0f, 1f)
                        val yFraction = (change.position.y / size.height).coerceIn(0f, 1f)
                        onHueSaturationChanged(xFraction * MaxSpectrumHue, yFraction)
                        change.consume()
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        val cornerRadius = CornerRadius(4.dp.toPx())
        val clip = Path().apply {
            addRoundRect(RoundRect(0f, 0f, size.width, size.height, cornerRadius))
        }

        val hueColors = listOf(
            Color.Red,
            Color.Yellow,
            Color.Green,
            Color.Cyan,
            Color.Blue,
            Color(0xFFFF00FF) // magenta
        )

        clipPath(clip) {
            drawRect(brush = Brush.horizontalGradient(hueColors))
            drawRect(
                brush = Brush.verticalGradient(
                    listOf(Color.White, Color.White.copy(alpha = 0f))
                )
            )
        }

        drawRoundRect(
            color = Color(0xFFD6D6D6),
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.dp.toPx())
        )

        val cursorCenter = Offset(
            x = (hue / MaxSpectrumHue) * size.width,
            y = saturation * size.height
        )
        drawCircle(color = cursorColor, radius = 9.dp.toPx(), center = cursorCenter)
        drawCircle(
            color = Color.White,
            radius = 9.dp.toPx(),
            center = cursorCenter,
            style = Stroke(width = 2.5.dp.toPx())
        )
    }
}
