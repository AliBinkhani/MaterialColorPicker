package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import com.hooshkar.materialcolorpickercompose.MaterialColorPickerTab

/**
 * The landscape picking area for [MaterialColorPicker]: the Swatches grid and Spectrum pad
 * crossfade in place at one fixed width — [ColorPickerDefaults.LandscapePickerAreaWidth] — with
 * *both* rendered at [ColorPickerDefaults.SwatchGridAspectRatio] (the caller must pass that same
 * ratio to [spectrumPad], overriding its own default [ColorPickerDefaults.SpectrumPadAspectRatio])
 * so the two are pixel-identical in size and neither jumps or overflows when the tab changes —
 * matching only the height while letting each keep its own aspect ratio would still leave their
 * widths different, and letting the taller one's width win risks overflowing this fixed slot.
 */
@Composable
internal fun LandscapePickerArea(
    selectedTab: MaterialColorPickerTab,
    swatchGrid: @Composable (modifier: Modifier) -> Unit,
    spectrumPad: @Composable (modifier: Modifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Crossfade(
        targetState = selectedTab,
        modifier = modifier.width(ColorPickerDefaults.LandscapePickerAreaWidth),
        label = "LandscapePickerArea"
    ) { tab ->
        when (tab) {
            MaterialColorPickerTab.Swatches -> swatchGrid(Modifier.fillMaxWidth())
            MaterialColorPickerTab.Spectrum -> spectrumPad(Modifier.fillMaxWidth())
        }
    }
}
