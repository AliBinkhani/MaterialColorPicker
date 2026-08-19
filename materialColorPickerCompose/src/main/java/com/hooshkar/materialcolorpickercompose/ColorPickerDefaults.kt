package com.hooshkar.materialcolorpickercompose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Layout constants for [SwatchesColorPicker], [SpectrumColorPicker] and [MaterialColorPicker].
 *
 * Sizing intentionally follows Material 3 defaults (spacing, touch targets, typography)
 * rather than the dense set of per-screen-size dimensions the View-based picker ships,
 * since Compose's dp units already scale correctly across densities and window sizes.
 */
object ColorPickerDefaults {
    val ContentSpacing: Dp = 20.dp
    val SectionSpacing: Dp = 8.dp

    val PickerCornerRadius: Dp = 8.dp
    val SwatchAspectRatio: Float = 11f / 10f
    val SpectrumAspectRatio: Float = 6f / 5f

    val SliderTrackHeight: Dp = 24.dp
    val SliderThumbSize: Dp = 22.dp

    val SelectedColorPreviewWidth: Dp = 96.dp
    val SelectedColorPreviewHeight: Dp = 56.dp
    val SelectedColorPreviewCornerRadius: Dp = 16.dp
    val ColorFieldSpacing: Dp = 16.dp
    val HexFieldWidth: Dp = 76.dp
    val RgbFieldWidth: Dp = 40.dp
    val RecentColorItemSize: Dp = 40.dp
    val RecentColorItemSpacing: Dp = 12.dp

    val TabRowHeight: Dp = 48.dp

    @Composable
    @ReadOnlyComposable
    internal fun labelTextStyle() = MaterialTheme.typography.labelLarge

    @Composable
    @ReadOnlyComposable
    internal fun valueTextStyle() = MaterialTheme.typography.bodyLarge
}

/** A neutral gray shown in empty recent-color slots. */
internal val EmptyRecentColorSlot: Color = Color(0xFFBDBDBD)
