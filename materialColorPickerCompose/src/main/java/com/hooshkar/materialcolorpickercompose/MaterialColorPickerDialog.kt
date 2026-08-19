package com.hooshkar.materialcolorpickercompose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

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
    Dialog(onDismissRequest = onDismissRequest, properties = properties) {
        // usePlatformDefaultWidth = false hands us the whole window, so this Box's padding is
        // what keeps the dialog off the screen edges (the platform's own default-width dialogs
        // get that margin for free; a custom-width one like this has to reserve it itself).
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(MaterialColorPickerDialogDefaults.MinEdgeMargin),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = modifier.widthIn(
                    min = MaterialColorPickerDialogDefaults.MinWidth,
                    max = MaterialColorPickerDialogDefaults.MaxWidth
                ),
                shape = shape,
                color = containerColor,
                tonalElevation = tonalElevation
            ) {
                Column {
                    // No explicit max-height here: the Surface is already capped by the padded
                    // Box above to whatever the real screen actually leaves available, so this
                    // only scrolls when the content genuinely doesn't fit — never before that.
                    Box(
                        modifier = Modifier
                            .weight(weight = 1f, fill = false)
                            .verticalScroll(rememberScrollState())
                            .padding(MaterialColorPickerDialogDefaults.ContentPadding)
                    ) {
                        content()
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
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
    val TonalElevation: Dp = 6.dp
    val ContentPadding = PaddingValues(horizontal = 24.dp, vertical = 20.dp)

    val shape: Shape
        @Composable get() = MaterialTheme.shapes.extraLarge

    val containerColor: Color
        @Composable get() = MaterialTheme.colorScheme.surfaceContainerHigh
}
