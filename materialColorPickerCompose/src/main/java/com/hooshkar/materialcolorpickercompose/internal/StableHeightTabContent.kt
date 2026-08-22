package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.animation.Crossfade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.unit.Constraints
import com.hooshkar.materialcolorpickercompose.MaterialColorPickerTab

/**
 * Renders [swatchesBlock] or [spectrumBlock] (crossfading between them) depending on
 * [selectedTab], inside a container as tall as the taller of the two — measured for real, not
 * guessed — so switching tabs never shifts whatever [MaterialColorPicker] places below this.
 *
 * [swatchesBlock] and [spectrumBlock] intentionally may have different natural heights (e.g. in
 * portrait, where the Spectrum block also carries the Saturation slider); rather than forcing
 * them to match — which would either stretch the shorter one or leave an oversized gap under it
 * — both are measured once per pass and the shorter one simply leaves blank space at the bottom
 * of the shared, fixed-height container. [modifier] must give this a concrete width to measure
 * both blocks with, the same as [ColorSwatchGrid]/[ColorSpectrumPad].
 */
@Composable
internal fun StableHeightTabContent(
    selectedTab: MaterialColorPickerTab,
    swatchesBlock: @Composable () -> Unit,
    spectrumBlock: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    SubcomposeLayout(modifier) { constraints ->
        val measuringConstraints = constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity)

        val swatchesHeight = subcompose(MeasureSlot.Swatches, swatchesBlock)
            .first()
            .measure(measuringConstraints)
            .height
        val spectrumHeight = subcompose(MeasureSlot.Spectrum, spectrumBlock)
            .first()
            .measure(measuringConstraints)
            .height

        val height = maxOf(swatchesHeight, spectrumHeight)
        val width = constraints.maxWidth

        val activePlaceable = subcompose(MeasureSlot.Active) {
            Crossfade(targetState = selectedTab, label = "MaterialColorPickerTopContent") { tab ->
                when (tab) {
                    MaterialColorPickerTab.Swatches -> swatchesBlock()
                    MaterialColorPickerTab.Spectrum -> spectrumBlock()
                }
            }
        }.first().measure(Constraints.fixed(width, height))

        layout(width, height) {
            activePlaceable.placeRelative(0, 0)
        }
    }
}

private enum class MeasureSlot { Swatches, Spectrum, Active }
