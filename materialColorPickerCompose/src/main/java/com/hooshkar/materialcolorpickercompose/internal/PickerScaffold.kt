package com.hooshkar.materialcolorpickercompose.internal

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults

/**
 * Arranges the picker's pieces vertically in portrait (each section below the previous one) and
 * as two side-by-side columns in landscape (the Swatches/Spectrum area on the left, everything
 * else stacked on the right) — mirroring how the View-based picker re-lays itself out based on
 * [Configuration.orientation].
 *
 * [topContent] receives the modifier it should size itself with: [Modifier.fillMaxWidth] in
 * portrait, or a fixed [ColorPickerDefaults.LandscapePickerAreaWidth] in landscape (a weight-based
 * width there would make the picking area far larger than the controls beside it).
 */
@Composable
internal fun PickerScaffold(
    topContent: @Composable (Modifier) -> Unit,
    selectedColorSection: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    brightnessSlider: (@Composable () -> Unit)? = null,
    opacitySlider: (@Composable () -> Unit)? = null,
    recentColorsRow: (@Composable () -> Unit)? = null
) {
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.spacedBy(ColorPickerDefaults.LandscapeContentSpacing)
        ) {
            topContent(Modifier.width(ColorPickerDefaults.LandscapePickerAreaWidth))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(ColorPickerDefaults.LandscapeContentSpacing)
            ) {
                brightnessSlider?.invoke()
                opacitySlider?.invoke()
                selectedColorSection()
                recentColorsRow?.invoke()
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(ColorPickerDefaults.ContentSpacing)
        ) {
            topContent(Modifier.fillMaxWidth())
            brightnessSlider?.invoke()
            opacitySlider?.invoke()
            selectedColorSection()
            recentColorsRow?.invoke()
        }
    }
}
