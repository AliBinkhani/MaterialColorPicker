package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import kotlin.math.roundToInt

/**
 * A draggable [fraction] (0f..1f) slider with a fully custom, drawn track, used for the
 * Opacity and Saturation(brightness) rows whose backgrounds aren't plain colors.
 */
@Composable
internal fun GradientSlider(
    fraction: Float,
    onFractionChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    track: @Composable (fraction: Float) -> Unit
) {
    var widthPx by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current
    val trackHeight = ColorPickerDefaults.SliderTrackHeight
    val thumbSize = ColorPickerDefaults.SliderThumbSize

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(trackHeight)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else Modifier
            )
            .onSizeChanged { widthPx = it.width.toFloat() }
            .pointerInput(Unit) {
                awaitEachGesture {
                    do {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.pressed } ?: continue
                        val x = change.position.x.coerceIn(0f, widthPx)
                        onFractionChange(if (widthPx > 0f) x / widthPx else 0f)
                        change.consume()
                    } while (event.changes.any { it.pressed })
                }
            }
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .clip(RoundedCornerShape(trackHeight / 2))
        ) {
            track(fraction)
        }

        val thumbOffsetPx = with(density) {
            (fraction * (widthPx - thumbSize.toPx())).coerceIn(0f, (widthPx - thumbSize.toPx()).coerceAtLeast(0f))
        }
        Box(
            Modifier
                .align(Alignment.CenterStart)
                .offset { IntOffset(thumbOffsetPx.roundToInt(), 0) }
                .size(thumbSize)
                .shadow(elevation = 2.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(Color.White)
        )
    }
}
