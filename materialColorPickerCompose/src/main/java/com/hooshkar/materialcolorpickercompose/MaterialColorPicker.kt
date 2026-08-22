package com.hooshkar.materialcolorpickercompose

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.hooshkar.materialcolorpickercompose.internal.BrightnessTrack
import com.hooshkar.materialcolorpickercompose.internal.ColorPickerTabRow
import com.hooshkar.materialcolorpickercompose.internal.ColorSpectrumPad
import com.hooshkar.materialcolorpickercompose.internal.ColorSwatchGrid
import com.hooshkar.materialcolorpickercompose.internal.LandscapeDetailsColumn
import com.hooshkar.materialcolorpickercompose.internal.LandscapePickerArea
import com.hooshkar.materialcolorpickercompose.internal.OpacityTrack
import com.hooshkar.materialcolorpickercompose.internal.PercentSliderRow
import com.hooshkar.materialcolorpickercompose.internal.PickerScaffold
import com.hooshkar.materialcolorpickercompose.internal.RecentColorsRow
import com.hooshkar.materialcolorpickercompose.internal.SelectedColorSection
import com.hooshkar.materialcolorpickercompose.internal.StableHeightTabContent
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
    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(state.color) { onColorChanged(state.color) }

    @Composable
    fun SaturationSlider(labelModifier: Modifier = Modifier) {
        val saturationLabel = stringResource(R.string.material_color_picker_compose_saturation)
        val fullColor = Color(
            android.graphics.Color.HSVToColor(floatArrayOf(state.hue, state.saturation, 1f))
        )
        PercentSliderRow(
            label = saturationLabel,
            percent = (state.brightness * 100f).roundToInt(),
            onPercentChange = { percent -> state.updateBrightness(percent / 100f) },
            modifier = labelModifier,
            showLabel = !isLandscape,
            contentDescription = saturationLabel
        ) { BrightnessTrack(fullColor = fullColor) }
    }

    val opacitySlider: (@Composable () -> Unit)? = if (opacityBarEnabled) {
        {
            val opacityLabel = stringResource(R.string.material_color_picker_compose_opacity)
            PercentSliderRow(
                label = opacityLabel,
                percent = (state.alpha * 100f / 255f).roundToInt(),
                onPercentChange = { percent -> state.updateAlpha((percent * 255f / 100f).roundToInt()) },
                showLabel = !isLandscape,
                contentDescription = opacityLabel
            ) { OpacityTrack(baseColor = state.color.copy(alpha = 1f)) }
        }
    } else null

    val selectedColorSection: @Composable () -> Unit = {
        SelectedColorSection(
            color = state.color,
            previousColor = state.previousColor,
            alpha = state.alpha,
            onColorChange = { newColor -> state.color = newColor }
        )
    }

    val recentColorsRow: (@Composable () -> Unit)? = if (recentColorsEnabled) {
        {
            RecentColorsRow(
                recentColors = state.recentColors,
                selectedColor = state.color,
                onColorSelected = { newColor -> state.color = newColor },
                showDivider = !isLandscape
            )
        }
    } else null

    FixedFontScale {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(
                if (isLandscape) ColorPickerDefaults.LandscapeContentSpacing else ColorPickerDefaults.ContentSpacing
            )
        ) {
            ColorPickerTabRow(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
    
            if (isLandscape) {
                // A dedicated layout rather than PickerScaffold's generic one: the picking area
                // renders the Swatches grid and Spectrum pad at the same fixed width *and* the
                // same aspect ratio (the grid's), so they're pixel-identical in size and neither
                // jumps nor overflows when the tab changes. The details column reserves the
                // Saturation slider's height up front and bottom-aligns its content, so switching
                // tabs only grows or shrinks the empty space above Opacity, never moves it.
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(ColorPickerDefaults.LandscapeContentSpacing)
                ) {
                    LandscapePickerArea(
                        selectedTab = selectedTab,
                        swatchGrid = { areaModifier ->
                            ColorSwatchGrid(
                                color = state.color,
                                onColorSelected = { newColor -> state.updateRgbKeepAlpha(newColor) },
                                modifier = areaModifier
                            )
                        },
                        spectrumPad = { areaModifier ->
                            ColorSpectrumPad(
                                hue = state.hue,
                                saturation = state.saturation,
                                onHueSaturationChanged = { hue, saturation ->
                                    state.updateHueSaturation(hue, saturation)
                                },
                                modifier = areaModifier,
                                aspectRatio = ColorPickerDefaults.SwatchGridAspectRatio
                            )
                        }
                    )
    
                    LandscapeDetailsColumn(
                        modifier = Modifier.weight(1f),
                        includeSaturation = selectedTab == MaterialColorPickerTab.Spectrum,
                        saturationSlider = { SaturationSlider() },
                        opacitySlider = opacitySlider,
                        selectedColorSection = selectedColorSection,
                        recentColorsRow = recentColorsRow
                    )
                }
            } else {
                PickerScaffold(
                    topContent = { areaModifier ->
                        // Everything after this shares one Column, so the Saturation slider is
                        // folded into the Spectrum block and both blocks are measured for real —
                        // otherwise the Opacity row and everything below it would jump up and down
                        // when the tab changes.
                        StableHeightTabContent(
                            selectedTab = selectedTab,
                            modifier = areaModifier,
                            swatchesBlock = {
                                ColorSwatchGrid(
                                    color = state.color,
                                    onColorSelected = { newColor -> state.updateRgbKeepAlpha(newColor) },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            },
                            spectrumBlock = {
                                Column {
                                    ColorSpectrumPad(
                                        hue = state.hue,
                                        saturation = state.saturation,
                                        onHueSaturationChanged = { hue, saturation ->
                                            state.updateHueSaturation(hue, saturation)
                                        },
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(Modifier.height(ColorPickerDefaults.ContentSpacing))
                                    SaturationSlider()
                                }
                            }
                        )
                    },
                    opacitySlider = opacitySlider,
                    selectedColorSection = selectedColorSection,
                    recentColorsRow = recentColorsRow
                )
            }
        }
    }
}
