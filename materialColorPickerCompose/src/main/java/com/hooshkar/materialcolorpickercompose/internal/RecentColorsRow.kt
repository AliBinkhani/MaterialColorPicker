package com.hooshkar.materialcolorpickercompose.internal

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

/** A divider followed by up to [RecentColorSlotCount] circular recent-color buttons. */
@Composable
internal fun RecentColorsRow(
    recentColors: List<Color>,
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(ColorPickerDefaults.SectionSpacing))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(
                ColorPickerDefaults.RecentColorItemSpacing,
                Alignment.CenterHorizontally
            )
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

@Composable
private fun RecentColorItem(
    color: Color?,
    isSelected: Boolean,
    contentDescription: String,
    onClick: (() -> Unit)?
) {
    val borderColor = MaterialTheme.colorScheme.primary
    Box(
        modifier = Modifier
            .size(ColorPickerDefaults.RecentColorItemSize)
            .clip(CircleShape)
            .background(color ?: EmptyRecentColorSlot)
            .then(
                if (isSelected) {
                    Modifier.border(width = 2.dp, color = borderColor, shape = CircleShape)
                } else Modifier
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(onClick = onClick)
                } else Modifier
            )
            .semantics {
                this.contentDescription = contentDescription
                if (onClick != null) role = Role.Button
            }
    )
}
