package com.hooshkar.materialcolorpickercompose

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.hooshkar.materialcolorpickercompose.internal.ColorSwatchGrid
import com.hooshkar.materialcolorpickercompose.internal.OpacityTrack
import com.hooshkar.materialcolorpickercompose.internal.PercentSliderRow
import com.hooshkar.materialcolorpickercompose.internal.PickerScaffold
import com.hooshkar.materialcolorpickercompose.internal.RecentColorsRow
import com.hooshkar.materialcolorpickercompose.internal.SelectedColorSection
import kotlin.math.roundToInt

/**
 * A Material 3 color picker showing only the fixed-palette Swatches grid: an 11x10 grid, an
 * optional Opacity slider, the selected-color preview with Hex/RGB fields, and an optional
 * recent-colors row.
 *
 * @param state the color being edited; create one with [rememberMaterialColorPickerState].
 * @param opacityBarEnabled whether the Opacity slider is shown.
 * @param recentColorsEnabled whether the recent-colors row is shown.
 * @param onColorChanged called whenever [state]'s color changes, with the new value.
 */
@Composable
fun SwatchesColorPicker(
    state: MaterialColorPickerState,
    modifier: Modifier = Modifier,
    opacityBarEnabled: Boolean = true,
    recentColorsEnabled: Boolean = true,
    onColorChanged: (Color) -> Unit = {}
) {
    LaunchedEffect(state.color) { onColorChanged(state.color) }

    val isLandscape =
        LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE

    FixedFontScale {
        PickerScaffold(
            modifier = modifier,
            topContent = { areaModifier ->
                ColorSwatchGrid(
                    color = state.color,
                    onColorSelected = { newColor -> state.updateRgbKeepAlpha(newColor) },
                    modifier = areaModifier
                )
            },
            opacitySlider = if (opacityBarEnabled) {
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
                        onColorSelected = { newColor -> state.color = newColor },
                        showDivider = !isLandscape
                    )
                }
            } else null
        )
    }
}
