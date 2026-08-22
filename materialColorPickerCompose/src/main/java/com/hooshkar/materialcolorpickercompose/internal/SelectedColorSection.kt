package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import com.hooshkar.materialcolorpickercompose.R

/**
 * The current-color preview (split against [previousColor] when present) plus the editable
 * Hex/Red/Green/Blue fields, all kept in sync with [color].
 *
 * Below [ColorPickerDefaults.CompactWidthBreakpoint] of available width, the Hex/RGB fields
 * shrink to their compact sizes.
 */
@Composable
internal fun SelectedColorSection(
    color: Color,
    previousColor: Color?,
    onColorChange: (Color) -> Unit,
    alpha: Int,
    modifier: Modifier = Modifier,
    editable: Boolean = true
) {
    BoxWithConstraints(modifier) {
        val compact = maxWidth < ColorPickerDefaults.CompactWidthBreakpoint
        val hexWidth = if (compact) ColorPickerDefaults.HexFieldWidthCompact else ColorPickerDefaults.HexFieldWidth
        val rgbWidth = if (compact) ColorPickerDefaults.RgbFieldWidthCompact else ColorPickerDefaults.RgbFieldWidth

        // Compact: pack fields with a minimal fixed gap, since the priority there is fitting at
        // all (wrapping onto a second line if even that isn't enough). Regular: spread them across
        // the full available width instead — with room to spare, a small fixed gap leaves the
        // fields bunched at the start with a large empty gap after Blue, which reads as cramped.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorPreviewSwatch(
                color = color,
                previousColor = previousColor,
                modifier = Modifier.size(
                    width = ColorPickerDefaults.SelectedColorPreviewWidth,
                    height = ColorPickerDefaults.SelectedColorPreviewHeight
                )
            )

            ColorValueField(
                label = stringResource(R.string.material_color_picker_compose_hex),
                displayValue = color.toHexRgb(),
                onValueChange = { raw -> colorFromHex(raw, alpha)?.let(onColorChange) },
                width = hexWidth,
                maxLength = 6,
                keyboardType = KeyboardType.Text,
                filter = ::hexDigitsOnly,
                enabled = editable,
                compact = compact
            )

            ColorValueField(
                label = stringResource(R.string.material_color_picker_compose_red),
                displayValue = color.redInt().toString(),
                onValueChange = { raw ->
                    val red = raw.toIntOrNull()?.coerceIn(0, 255) ?: 0
                    onColorChange(colorFromRgb(red, color.greenInt(), color.blueInt(), alpha))
                },
                width = rgbWidth,
                maxLength = 3,
                keyboardType = KeyboardType.Number,
                filter = ::digitsOnly,
                enabled = editable,
                compact = compact
            )

            ColorValueField(
                label = stringResource(R.string.material_color_picker_compose_green),
                displayValue = color.greenInt().toString(),
                onValueChange = { raw ->
                    val green = raw.toIntOrNull()?.coerceIn(0, 255) ?: 0
                    onColorChange(colorFromRgb(color.redInt(), green, color.blueInt(), alpha))
                },
                width = rgbWidth,
                maxLength = 3,
                keyboardType = KeyboardType.Number,
                filter = ::digitsOnly,
                enabled = editable,
                compact = compact
            )

            ColorValueField(
                label = stringResource(R.string.material_color_picker_compose_blue),
                displayValue = color.blueInt().toString(),
                onValueChange = { raw ->
                    val blue = raw.toIntOrNull()?.coerceIn(0, 255) ?: 0
                    onColorChange(colorFromRgb(color.redInt(), color.greenInt(), blue, alpha))
                },
                width = rgbWidth,
                maxLength = 3,
                keyboardType = KeyboardType.Number,
                filter = ::digitsOnly,
                enabled = editable,
                compact = compact
            )
        }
    }
}

@Composable
private fun ColorPreviewSwatch(
    color: Color,
    previousColor: Color?,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(ColorPickerDefaults.SelectedColorPreviewCornerRadius)
    val descriptionCurrent = stringResource(R.string.material_color_picker_compose_picked_color)
    val descriptionPrevious = stringResource(R.string.material_color_picker_compose_current_color)

    if (previousColor != null) {
        Row(modifier = modifier.clip(shape)) {
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(previousColor)
                    .semantics { contentDescription = descriptionPrevious }
            )
            Box(
                Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(color)
                    .semantics { contentDescription = descriptionCurrent }
            )
        }
    } else {
        Box(
            modifier
                .clip(shape)
                .background(color)
                .semantics { contentDescription = descriptionCurrent }
        )
    }
}

@Composable
private fun ColorValueField(
    label: String,
    displayValue: String,
    onValueChange: (String) -> Unit,
    width: androidx.compose.ui.unit.Dp,
    maxLength: Int,
    keyboardType: KeyboardType,
    filter: (String) -> String,
    enabled: Boolean,
    compact: Boolean
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = if (compact) ColorPickerDefaults.labelTextStyleCompact() else ColorPickerDefaults.labelTextStyle()
        )
        Spacer(Modifier.size(4.dp))
        SyncedTextField(
            displayValue = displayValue,
            onValueChange = onValueChange,
            modifier = Modifier.width(width),
            enabled = enabled,
            maxLength = maxLength,
            keyboardType = keyboardType,
            textStyle = (if (compact) ColorPickerDefaults.valueTextStyleCompact() else ColorPickerDefaults.valueTextStyle()).copy(
                color = LocalContentColor.current,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            ),
            filter = filter
        )
    }
}
