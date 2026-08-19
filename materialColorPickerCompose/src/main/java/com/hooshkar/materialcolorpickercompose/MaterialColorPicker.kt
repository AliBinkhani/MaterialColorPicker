package com.hooshkar.materialcolorpickercompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import com.hooshkar.materialcolorpickercompose.internal.BrightnessTrack
import com.hooshkar.materialcolorpickercompose.internal.ColorPickerTabRow
import com.hooshkar.materialcolorpickercompose.internal.ColorSpectrumPad
import com.hooshkar.materialcolorpickercompose.internal.ColorSwatchGrid
import com.hooshkar.materialcolorpickercompose.internal.OpacityTrack
import com.hooshkar.materialcolorpickercompose.internal.PercentSliderRow
import com.hooshkar.materialcolorpickercompose.internal.PickerScaffold
import com.hooshkar.materialcolorpickercompose.internal.RecentColorsRow
import com.hooshkar.materialcolorpickercompose.internal.SelectedColorSection
import kotlin.math.roundToInt

/** Which page [MaterialColorPicker] is currently showing. */
enum class MaterialColorPickerTab { Swatches, Spectrum }

/**
 * A Material 3 color picker offering both the Swatches grid and the Spectrum pad behind a
 * segmented-button switch at the top, plus an optional Opacity slider, the selected-color
 * preview with Hex/RGB fields, and an optional recent-colors row.
 *
 * @param state the color being edited; create one with [rememberMaterialColorPickerState].
 * @param initialTab which page is shown first.
 * @param opacityBarEnabled whether the Opacity slider is shown.
 * @param recentColorsEnabled whether the recent-colors row is shown.
 * @param onColorChanged called whenever [state]'s color changes, with the new value.
 */
@Composable
fun MaterialColorPicker(
    state: MaterialColorPickerState,
    modifier: Modifier = Modifier,
    initialTab: MaterialColorPickerTab = MaterialColorPickerTab.Swatches,
    opacityBarEnabled: Boolean = true,
    recentColorsEnabled: Boolean = true,
    onColorChanged: (Color) -> Unit = {}
) {
    var selectedTab by rememberSaveable { mutableStateOf(initialTab) }

    LaunchedEffect(state.color) { onColorChanged(state.color) }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(ColorPickerDefaults.ContentSpacing)
    ) {
        ColorPickerTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })

        PickerScaffold(
            topContent = {
                when (selectedTab) {
                    MaterialColorPickerTab.Swatches -> ColorSwatchGrid(
                        color = state.color,
                        onColorSelected = { newColor -> state.updateRgbKeepAlpha(newColor) }
                    )

                    MaterialColorPickerTab.Spectrum -> ColorSpectrumPad(
                        hue = state.hue,
                        saturation = state.saturation,
                        onHueSaturationChanged = { hue, saturation ->
                            state.updateHueSaturation(hue, saturation)
                        }
                    )
                }
            },
            brightnessSlider = if (selectedTab == MaterialColorPickerTab.Spectrum) {
                {
                    val saturationLabel = stringResource(R.string.material_color_picker_compose_saturation)
                    val fullColor = Color(
                        android.graphics.Color.HSVToColor(floatArrayOf(state.hue, state.saturation, 1f))
                    )
                    PercentSliderRow(
                        label = saturationLabel,
                        percent = (state.brightness * 100f).roundToInt(),
                        onPercentChange = { percent -> state.updateBrightness(percent / 100f) },
                        contentDescription = saturationLabel
                    ) { BrightnessTrack(fullColor = fullColor) }
                }
            } else null,
            opacitySlider = if (opacityBarEnabled) {
                {
                    val opacityLabel = stringResource(R.string.material_color_picker_compose_opacity)
                    PercentSliderRow(
                        label = opacityLabel,
                        percent = (state.alpha * 100f / 255f).roundToInt(),
                        onPercentChange = { percent -> state.updateAlpha((percent * 255f / 100f).roundToInt()) },
                        contentDescription = opacityLabel
                    ) { OpacityTrack(baseColor = state.color.copy(alpha = 1f)) }
                }
            } else null,
            selectedColorSection = {
                SelectedColorSection(
                    color = state.color,
                    previousColor = state.previousColor,
                    alpha = state.alpha,
                    onColorChange = { newColor -> state.color = newColor }
                )
            },
            recentColorsRow = if (recentColorsEnabled) {
                {
                    RecentColorsRow(
                        recentColors = state.recentColors,
                        selectedColor = state.color,
                        onColorSelected = { newColor -> state.color = newColor }
                    )
                }
            } else null
        )
    }
}
