package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import kotlin.math.ceil
import kotlin.math.roundToInt

/** Label + draggable percent slider + editable "NN%" field, shared by the Opacity and Saturation rows. */
@Composable
internal fun PercentSliderRow(
    label: String,
    percent: Int,
    onPercentChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentDescription: String? = null,
    track: @Composable (fraction: Float) -> Unit
) {
    Column(modifier) {
        Text(text = label, style = ColorPickerDefaults.labelTextStyle())
        Spacer(Modifier.height(ColorPickerDefaults.SectionSpacing))
        Row(verticalAlignment = Alignment.CenterVertically) {
            GradientSlider(
                fraction = percent / 100f,
                onFractionChange = { onPercentChange((it * 100f).roundToInt().coerceIn(0, 100)) },
                modifier = Modifier.weight(1f),
                enabled = enabled,
                contentDescription = contentDescription,
                track = track
            )
            Spacer(Modifier.width(12.dp))
            Row(
                modifier = Modifier.width(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SyncedTextField(
                    displayValue = percent.toString(),
                    onValueChange = { raw ->
                        val value = raw.toIntOrNull()?.coerceIn(0, 100) ?: 0
                        onPercentChange(value)
                    },
                    modifier = Modifier.width(32.dp),
                    enabled = enabled,
                    maxLength = 3,
                    keyboardType = KeyboardType.Number,
                    textStyle = ColorPickerDefaults.valueTextStyle().copy(
                        color = LocalContentColor.current,
                        textAlign = TextAlign.End
                    ),
                    filter = ::digitsOnly
                )
                Text(text = "%", style = ColorPickerDefaults.valueTextStyle())
            }
        }
    }
}

/** Checkerboard + a transparent-to-opaque [baseColor] gradient overlay, used by the Opacity slider. */
@Composable
internal fun OpacityTrack(baseColor: Color, modifier: Modifier = Modifier) {
    Canvas(modifier.fillMaxSize()) {
        val cellPx = 6.dp.toPx()
        val columns = ceil(size.width / cellPx).toInt()
        val rows = ceil(size.height / cellPx).toInt()
        for (row in 0 until rows) {
            for (column in 0 until columns) {
                val isLight = (row + column) % 2 == 0
                drawRect(
                    color = if (isLight) Color(0xFFEEEEEE) else Color(0xFFBDBDBD),
                    topLeft = Offset(column * cellPx, row * cellPx),
                    size = Size(cellPx, cellPx)
                )
            }
        }
        drawRect(
            brush = Brush.horizontalGradient(
                listOf(baseColor.copy(alpha = 0f), baseColor.copy(alpha = 1f))
            )
        )
    }
}

/** A black-to-[fullColor] gradient, used by the Saturation (brightness) slider. */
@Composable
internal fun BrightnessTrack(fullColor: Color, modifier: Modifier = Modifier) {
    Box(
        modifier
            .fillMaxSize()
            .background(Brush.horizontalGradient(listOf(Color.Black, fullColor)))
    )
}
