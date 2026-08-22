package com.hooshkar.materialcolorpickercompose

import android.content.res.Configuration
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider

/**
 * A Material 3 dialog shell for [SwatchesColorPicker], [SpectrumColorPicker] or
 * [MaterialColorPicker], built the same way as Compose's own `DatePickerDialog`: a rounded
 * surface with [content] on top and a bottom-aligned [dismissButton]/[confirmButton] row.
 *
 * Unlike `DatePickerDialog` (which always sizes its single fixed calendar layout), this dialog
 * uses [widthIn] rather than a single fixed width, since its three possible contents range from
 * a compact single-column picker to [MaterialColorPicker]'s wider side-by-side landscape layout.
 * [content] is placed in a vertically scrolling area, but only actually scrolls once it no
 * longer fits the real screen — a fixed max height isn't guessed up front.
 *
 * The window is forced to fill the display (`FLAG_LAYOUT_NO_LIMITS` + `MATCH_PARENT`) and centers
 * a `Box` inside itself with a single, symmetric [edgeMargin] on every side — deliberately *not*
 * reading the status/navigation bar sizes, since that reserves equal space from the true top and
 * bottom of the screen rather than equal space from the visible (bar-excluded) area, which isn't
 * the same thing whenever those two bars differ in height (true on most devices). The platform's
 * own default wrap-content dialog gravity was tried first and measured inconsistently off-center
 * on a real device (Samsung One UI); this gets much closer but isn't necessarily pixel-exact on
 * every OEM skin, since the window is still ultimately positioned by the platform.
 *
 * ```
 * MaterialColorPickerDialog(
 *     onDismissRequest = { dialogVisible = false },
 *     confirmButton = { TextButton(onClick = { dialogVisible = false }) { Text("OK") } },
 *     dismissButton = { TextButton(onClick = { dialogVisible = false }) { Text("Cancel") } },
 * ) {
 *     MaterialColorPicker(state = state)
 * }
 * ```
 *
 * @param onDismissRequest called when the user taps outside the dialog or presses back.
 * @param confirmButton the primary action, typically a `TextButton` that commits the color.
 * @param dismissButton the secondary action, typically a `TextButton` that discards changes.
 * @param content one of [SwatchesColorPicker], [SpectrumColorPicker] or [MaterialColorPicker].
 */
@Composable
fun MaterialColorPickerDialog(
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    dismissButton: (@Composable () -> Unit)? = null,
    shape: Shape = MaterialColorPickerDialogDefaults.shape,
    containerColor: Color = MaterialColorPickerDialogDefaults.containerColor,
    tonalElevation: Dp = MaterialColorPickerDialogDefaults.TonalElevation,
    properties: DialogProperties = DialogProperties(usePlatformDefaultWidth = false),
    content: @Composable () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val edgeMargin = if (isLandscape) {
        MaterialColorPickerDialogDefaults.MinEdgeMarginLandscape
    } else {
        MaterialColorPickerDialogDefaults.MinEdgeMargin
    }
    val contentPadding = if (isLandscape) {
        MaterialColorPickerDialogDefaults.ContentPaddingLandscape
    } else {
        MaterialColorPickerDialogDefaults.ContentPadding
    }
    val buttonRowVerticalPadding = if (isLandscape) 6.dp else 12.dp

    // The raw display size (configuration.screenWidthDp/screenHeightDp are the physical size in
    // this edge-to-edge app, not the bar-excluded size), so the Surface never needs more room than
    // is actually available once edgeMargin is reserved on both sides.
    val availableWidth = configuration.screenWidthDp.dp
    val availableHeight = configuration.screenHeightDp.dp

    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        // usePlatformDefaultWidth = false leaves the window at its default wrap-content size,
        // whose platform gravity centering is what came out asymmetric; FLAG_LAYOUT_NO_LIMITS +
        // MATCH_PARENT instead gives the Box below real, full-screen bounds (ignoring the system
        // bars entirely) to center within using plain Compose alignment.
        val dialogWindowProvider = LocalView.current.parent as? DialogWindowProvider
        SideEffect {
            dialogWindowProvider?.window?.apply {
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                setGravity(Gravity.CENTER)
                setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT)
                attributes = attributes.apply {
                    x = 0
                    y = 0
                    horizontalMargin = 0f
                    verticalMargin = 0f
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(edgeMargin),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = modifier.widthIn(
                    min = MaterialColorPickerDialogDefaults.MinWidth,
                    max = (availableWidth - edgeMargin * 2).coerceAtMost(MaterialColorPickerDialogDefaults.MaxWidth)
                ).heightIn(max = availableHeight - edgeMargin * 2),
                shape = shape,
                color = containerColor,
                tonalElevation = tonalElevation
            ) {
                Column {
                    // No explicit max-height here: the Surface above already carries one, so this
                    // only scrolls when the content genuinely doesn't fit — never before that.
                    Box(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(contentPadding)
                    ) {
                        content()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = buttonRowVerticalPadding),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
                    ) {
                        dismissButton?.invoke()
                        confirmButton()
                    }
                }
            }
        }
    }
}

object MaterialColorPickerDialogDefaults {
    val MinWidth: Dp = 300.dp
    val MaxWidth: Dp = 560.dp
    val MinEdgeMargin: Dp = 24.dp
    val MinEdgeMarginLandscape: Dp = 12.dp
    val TonalElevation: Dp = 6.dp
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)
    val ContentPaddingLandscape = PaddingValues(horizontal = 20.dp, vertical = 10.dp)

    val shape: Shape
        @Composable get() = MaterialTheme.shapes.extraLarge

    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
}
