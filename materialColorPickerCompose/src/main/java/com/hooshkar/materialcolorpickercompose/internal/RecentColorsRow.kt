package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import com.hooshkar.materialcolorpickercompose.EmptyRecentColorSlot
import com.hooshkar.materialcolorpickercompose.R
import com.hooshkar.materialcolorpickercompose.RecentColorSlotCount

/**
 * An optional divider followed by up to [RecentColorSlotCount] circular recent-color buttons.
 * [showDivider] is turned off in the landscape layout to save vertical room.
 */
@Composable
internal fun RecentColorsRow(
    recentColors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
    showDivider: Boolean = true
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showDivider) {
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            Spacer(Modifier.height(ColorPickerDefaults.ContentSpacing))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            repeat(RecentColorSlotCount) { index ->
                val color = recentColors.getOrNull(index)
                RecentColorItem(
                    color = color,
                    isSelected = color != null && color == selectedColor,
                    contentDescription = if (color != null) {
                        stringResource(R.string.material_color_picker_compose_recent_color, index + 1)
                    } else {
                        stringResource(R.string.material_color_picker_compose_empty_slot)
                    },
                    onClick = if (color != null) ({ onColorSelected(color) }) else null
                )
            }
        }
    }
}

/**
 * A selected item shrinks slightly so a neutral outline ring shows around it, inside the same
 * fixed footprint (so the row never reflows). A border drawn in the app's accent color was tried
 * first but reads poorly against whichever recent color happens to be selected; the theme's
 * outline color stays legible against any swatch.
 */
@Composable
private fun RecentColorItem(
    color: Color?,
    isSelected: Boolean,
    contentDescription: String,
    onClick: (() -> Unit)?
) {
    val circleSize by animateDpAsState(
        targetValue = if (isSelected) {
            ColorPickerDefaults.RecentColorItemSize - 8.dp
        } else {
            ColorPickerDefaults.RecentColorItemSize
        },
        animationSpec = tween(150),
        label = "RecentColorItemSize"
    )

    Box(
        modifier = Modifier
            .size(ColorPickerDefaults.RecentColorItemSize)
            .clip(CircleShape)
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .semantics {
                this.contentDescription = contentDescription
                if (onClick != null) role = Role.Button
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier
                .size(circleSize)
                .clip(CircleShape)
                .background(color ?: EmptyRecentColorSlot)
        )
        if (isSelected) {
            Box(
                Modifier
                    .matchParentSize()
                    .border(width = 1.5.dp, color = MaterialTheme.colorScheme.outline, shape = CircleShape)
            )
        }
    }
}
