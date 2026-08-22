package com.hooshkar.materialcolorpickercompose

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
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
    val ContentSpacing: Dp = 12.dp
    val SectionSpacing: Dp = 4.dp

    /** Tighter spacing used in the landscape layout, which has much less room to spare. */
    val LandscapeContentSpacing: Dp = 12.dp

    /**
     * Fixed width of the Swatches grid / Spectrum pad in landscape, where the picking area sits
     * beside the rest of the controls rather than above them. A weight-based width (half the
     * dialog) would make it far larger than the controls next to it; this instead matches the
     * View-based picker's landscape sizing (~194dp).
     */
    val LandscapePickerAreaWidth: Dp = 200.dp

    /** The Swatches grid is 11 columns x 10 rows, so it's very slightly wider than tall. */
    const val SwatchGridAspectRatio: Float = 11f / 10f

    /**
     * The Spectrum pad matches the reference UI's proportions: for the same width, it's exactly
     * 9/10 as tall as the Swatches grid (11 / (10 * 10/9) = 11/9).
     */
    const val SpectrumPadAspectRatio: Float = 11f / 9f

    val SliderTrackHeight: Dp = 20.dp
    val SliderThumbSize: Dp = 18.dp

    val SelectedColorPreviewWidth: Dp = 52.dp
    val SelectedColorPreviewHeight: Dp = 40.dp
    val SelectedColorPreviewCornerRadius: Dp = 12.dp
    val HexFieldWidth: Dp = 72.dp
    val RgbFieldWidth: Dp = 34.dp

    /**
     * Below [CompactWidthBreakpoint] of *actually available* width — not just narrow screens,
     * also the landscape details column or a squeezed dialog — [SelectedColorSection] switches
     * to these smaller sizes instead of relying solely on [FlowRow][androidx.compose.foundation.layout.FlowRow]
     * wrapping, since a wrapped "Blue" field by itself still looks broken.
     */
    val CompactWidthBreakpoint: Dp = 320.dp
    val HexFieldWidthCompact: Dp = 64.dp
    val RgbFieldWidthCompact: Dp = 32.dp

    val RecentColorItemSize: Dp = 40.dp

    val TabRowHeight: Dp = 48.dp

    @Composable
    @ReadOnlyComposable
    internal fun labelTextStyle() = MaterialTheme.typography.labelLarge

    @Composable
    @ReadOnlyComposable
    internal fun valueTextStyle() = MaterialTheme.typography.bodyLarge

    /** Used for the Hex/RGB value fields when [SelectedColorSection] is in its compact layout. */
    @Composable
    @ReadOnlyComposable
    internal fun valueTextStyleCompact() = MaterialTheme.typography.bodyMedium

    /** Used for the Hex/RGB labels when [SelectedColorSection] is in its compact layout. */
    @Composable
    @ReadOnlyComposable
    internal fun labelTextStyleCompact() = MaterialTheme.typography.labelMedium
}

/** A neutral gray shown in empty recent-color slots. */
internal val EmptyRecentColorSlot: Color = Color(0xFFBDBDBD)

/**
 * Freezes the system font-scale (accessibility "large text") setting to 1x for [content], while
 * still respecting the display's density. The picker's Hex/RGB/percent fields are only a few dp
 * wide by design; letting their `sp` text keep growing with the user's font-scale setting is what
 * causes fields like "Blue" to overflow and wrap onto the recent-colors row on narrow screens —
 * freezing scale keeps the fixed-width layout stable instead of fighting it field by field.
 */
@Composable
internal fun FixedFontScale(content: @Composable () -> Unit) {
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density = density.density, fontScale = 1f),
        content = content
    )
}
