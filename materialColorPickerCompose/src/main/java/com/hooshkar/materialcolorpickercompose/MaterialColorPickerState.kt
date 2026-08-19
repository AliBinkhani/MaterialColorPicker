package com.hooshkar.materialcolorpickercompose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.roundToInt

/** Maximum hue reachable on the [SpectrumColorPicker] pad, see [MaterialColorPickerState.hue]. */
internal const val MaxSpectrumHue = 300f

/** Number of recent-color slots shown by [SwatchesColorPicker], [SpectrumColorPicker] and [MaterialColorPicker]. */
internal const val RecentColorSlotCount = 6

/**
 * Holds the color currently being edited plus everything the picker UI needs to stay in sync:
 * hue/saturation/brightness/alpha components, the previously-committed color, and recent colors.
 *
 * Create one with [rememberMaterialColorPickerState] and pass it to [SwatchesColorPicker],
 * [SpectrumColorPicker] or [MaterialColorPicker].
 */
@Stable
class MaterialColorPickerState(
    initialColor: Color = Color.White,
    previousColor: Color? = null,
    recentColors: List<Color> = emptyList()
) {
    // Kept in sync with `color` on every change so the Spectrum pad, the brightness slider and
    // the opacity slider can each edit a single component without disturbing the others.
    internal var hue by mutableFloatStateOf(0f)
        private set
    internal var saturation by mutableFloatStateOf(0f)
        private set
    internal var brightness by mutableFloatStateOf(1f)
        private set
    internal var alpha by mutableIntStateOf(255)
        private set

    private var colorState by mutableStateOf(initialColor)

    /** The color currently being edited, including its alpha component. Assigning re-derives hue/saturation/brightness/alpha. */
    var color: Color
        get() = colorState
        set(value) {
            colorState = value
            decompose(value)
        }

    /**
     * The color the picker was opened with (e.g. the value before the user started editing),
     * shown side by side with [color] in the selected-color preview. Null hides the split and
     * shows [color] alone.
     */
    var previousColor: Color? by mutableStateOf(previousColor)

    /** Up to [RecentColorSlotCount] recently used colors, newest first. */
    val recentColors = mutableStateListOf<Color>().apply { addAll(recentColors.take(RecentColorSlotCount)) }

    init {
        decompose(initialColor)
    }

    /** Applies a new hue+saturation pair (from the Spectrum pad) while keeping brightness/alpha. */
    internal fun updateHueSaturation(newHue: Float, newSaturation: Float) {
        hue = newHue.coerceIn(0f, MaxSpectrumHue)
        saturation = newSaturation.coerceIn(0f, 1f)
        recompose()
    }

    /** Applies a new brightness (the "Saturation"-labeled slider) while keeping hue/saturation/alpha. */
    internal fun updateBrightness(newBrightness: Float) {
        brightness = newBrightness.coerceIn(0f, 1f)
        recompose()
    }

    /** Applies a new alpha (the Opacity slider) while keeping hue/saturation/brightness. */
    internal fun updateAlpha(newAlpha: Int) {
        alpha = newAlpha.coerceIn(0, 255)
        recompose()
    }

    /** Applies an RGB color from the Swatches grid or a hex/RGB text field, keeping the current alpha. */
    internal fun updateRgbKeepAlpha(rgb: Color) {
        color = rgb.copy(alpha = alpha / 255f)
    }

    /** Pushes [color] to the front of [recentColors], capped at [RecentColorSlotCount], dropping duplicates. */
    fun commitToRecentColors(color: Color = this.color) {
        recentColors.remove(color)
        recentColors.add(0, color)
        while (recentColors.size > RecentColorSlotCount) recentColors.removeAt(recentColors.lastIndex)
    }

    private fun decompose(value: Color) {
        val hsv = FloatArray(3)
        android.graphics.Color.colorToHSV(value.toArgb(), hsv)
        hue = hsv[0].coerceIn(0f, MaxSpectrumHue)
        saturation = hsv[1]
        brightness = hsv[2]
        alpha = (value.alpha * 255f).roundToInt()
    }

    private fun recompose() {
        val argb = android.graphics.Color.HSVToColor(alpha, floatArrayOf(hue, saturation, brightness))
        colorState = Color(argb)
    }

    companion object {
        val Saver: Saver<MaterialColorPickerState, *> = listSaver(
            save = { state ->
                buildList {
                    add(state.color.toArgb())
                    add(state.previousColor?.toArgb() ?: Int.MIN_VALUE)
                    add(state.recentColors.size)
                    state.recentColors.forEach { add(it.toArgb()) }
                }
            },
            restore = { saved ->
                val color = Color(saved[0])
                val previous = saved[1].let { if (it == Int.MIN_VALUE) null else Color(it) }
                val count = saved[2]
                val recent = (0 until count).map { index -> Color(saved[3 + index]) }
                MaterialColorPickerState(color, previous, recent)
            }
        )
    }
}

/** Creates and remembers a [MaterialColorPickerState], surviving configuration changes. */
@Composable
fun rememberMaterialColorPickerState(
    initialColor: Color = Color.White,
    previousColor: Color? = null,
    recentColors: List<Color> = emptyList()
): MaterialColorPickerState = rememberSaveable(saver = MaterialColorPickerState.Saver) {
    MaterialColorPickerState(initialColor, previousColor, recentColors)
}
