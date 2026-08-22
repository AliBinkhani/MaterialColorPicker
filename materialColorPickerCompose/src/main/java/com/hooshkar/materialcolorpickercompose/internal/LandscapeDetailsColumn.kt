package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults

/**
 * The landscape details column for [MaterialColorPicker]: Saturation (only on the Spectrum tab),
 * Opacity, the selected-color section and the recent-colors row.
 *
 * Reserves enough height for the Saturation-slider variant (measured for real, the same way
 * [StableHeightTabContent] does for the picking area) and bottom-aligns its content within that
 * fixed height, so toggling [includeSaturation] only grows or shrinks the empty space above it —
 * Opacity, the color section and recent colors never move.
 */
@Composable
internal fun LandscapeDetailsColumn(
    includeSaturation: Boolean,
    saturationSlider: @Composable () -> Unit,
    opacitySlider: (@Composable () -> Unit)?,
    selectedColorSection: @Composable () -> Unit,
    recentColorsRow: (@Composable () -> Unit)?,
    modifier: Modifier = Modifier
) {
    SubcomposeLayout(modifier.fillMaxWidth()) { constraints ->
        val measuringConstraints = constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)

        val tallHeight = subcompose(DetailsSlot.Measure) {
            DetailsContent(
                includeSaturation = true,
                saturationSlider = saturationSlider,
                opacitySlider = opacitySlider,
                selectedColorSection = selectedColorSection,
                recentColorsRow = recentColorsRow
            )
        }.first().measure(measuringConstraints).height

        val activePlaceable = subcompose(DetailsSlot.Active) {
            DetailsContent(
                includeSaturation = includeSaturation,
                saturationSlider = saturationSlider,
                opacitySlider = opacitySlider,
                selectedColorSection = selectedColorSection,
                recentColorsRow = recentColorsRow
            )
        }.first().measure(Constraints.fixed(constraints.maxWidth, tallHeight))

        layout(constraints.maxWidth, tallHeight) {
            activePlaceable.placeRelative(0, 0)
        }
    }
}

@Composable
private fun DetailsContent(
    includeSaturation: Boolean,
    saturationSlider: @Composable () -> Unit,
    opacitySlider: (@Composable () -> Unit)?,
    selectedColorSection: @Composable () -> Unit,
    recentColorsRow: (@Composable () -> Unit)?
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(
            ColorPickerDefaults.LandscapeContentSpacing,
            Alignment.Bottom
        )
    ) {
        if (includeSaturation) saturationSlider()
        opacitySlider?.invoke()
        selectedColorSection()
        recentColorsRow?.invoke()
    }
}

private enum class DetailsSlot { Measure, Active }
