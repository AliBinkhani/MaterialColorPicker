package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.hooshkar.materialcolorpickercompose.ColorPickerDefaults
import com.hooshkar.materialcolorpickercompose.MaterialColorPickerTab
import com.hooshkar.materialcolorpickercompose.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun ColorPickerTabRow(
    selectedTab: MaterialColorPickerTab,
    onTabSelected: (MaterialColorPickerTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = MaterialColorPickerTab.entries
    SingleChoiceSegmentedButtonRow(
        modifier = modifier
            .fillMaxWidth()
            .height(ColorPickerDefaults.TabRowHeight)
    ) {
        tabs.forEachIndexed { index, tab ->
            SegmentedButton(
                selected = selectedTab == tab,
                onClick = { onTabSelected(tab) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = tabs.size)
            ) {
                Text(
                    text = stringResource(
                        if (tab == MaterialColorPickerTab.Swatches) {
                            R.string.material_color_picker_compose_swatches
                        } else {
                            R.string.material_color_picker_compose_spectrum
                        }
                    )
                )
            }
        }
    }
}
