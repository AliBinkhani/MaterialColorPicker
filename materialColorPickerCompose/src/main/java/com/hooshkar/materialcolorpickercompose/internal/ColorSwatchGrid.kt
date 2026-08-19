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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults

private val ShadowColor = Color.Black.copy(alpha = 0.4f)
private val DarkRowCursorColor = Color.White.copy(alpha = 0.3f)

/** The 11x10 fixed-palette grid shown on the Swatches page. */
@Composable
internal fun ColorSwatchGrid(
    color: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val selected = findSwatchIndex(color)

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ColorPickerDefaults.SwatchAspectRatio)
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
                        val column = (change.position.x / (size.width / SwatchPaletteColumns))
                            .toInt()
                            .coerceIn(0, SwatchPaletteColumns - 1)
                        val row = (change.position.y / (size.height / SwatchPaletteRows))
                            .toInt()
                            .coerceIn(0, SwatchPaletteRows - 1)
                        onColorSelected(swatchColorAt(column, row))
                        change.consume()
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        val canvasSize = size
        val cellWidth = canvasSize.width / SwatchPaletteColumns
        val cellHeight = canvasSize.height / SwatchPaletteRows
        val cornerRadius = CornerRadius(4.dp.toPx())

        val clip = Path().apply {
            addRoundRect(
                RoundRect(
                    left = 0f,
                    top = 0f,
                    right = canvasSize.width,
                    bottom = canvasSize.height,
                    cornerRadius = cornerRadius
                )
            )
        }

        clipPath(clip) {
            for (column in 0 until SwatchPaletteColumns) {
                for (row in 0 until SwatchPaletteRows) {
                    val topLeft = Offset(column * cellWidth, row * cellHeight)
                    val isSelected = selected != null && selected.first == column && selected.second == row

                    if (isSelected) {
                        drawRect(
                            color = ShadowColor,
                            topLeft = topLeft - Offset(1.dp.toPx(), 1.dp.toPx()),
                            size = Size(cellWidth + 2.dp.toPx(), cellHeight + 2.dp.toPx())
                        )
                    }

                    drawRect(
                        color = swatchColorAt(column, row),
                        topLeft = topLeft,
                        size = Size(cellWidth, cellHeight)
                    )

                    if (isSelected && isDarkSwatchRow(row)) {
                        drawRect(
                            color = DarkRowCursorColor,
                            topLeft = topLeft,
                            size = Size(cellWidth, cellHeight),
                            style = Stroke(width = 1.5.dp.toPx())
                        )
                    }
                }
            }
        }

        drawRoundRect(
            color = Color(0xFFD1D0D0),
            cornerRadius = cornerRadius,
            style = Stroke(width = 1.dp.toPx())
        )
    }
}

private fun findSwatchIndex(color: Color): Pair<Int, Int>? {
    val argb = color.copy(alpha = 1f).let {
        (0xFF shl 24) or ((it.red * 255).toInt() shl 16) or ((it.green * 255).toInt() shl 8) or (it.blue * 255).toInt()
    }
    for (column in 0 until SwatchPaletteColumns) {
        for (row in 0 until SwatchPaletteRows) {
            if (swatchPalette[column][row] == argb) return column to row
        }
    }
    return null
}
