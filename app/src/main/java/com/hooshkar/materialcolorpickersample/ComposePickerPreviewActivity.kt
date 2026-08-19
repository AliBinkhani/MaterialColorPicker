package com.hooshkar.materialcolorpickersample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hooshkar.materialcolorpickercompose.MaterialColorPicker
import com.hooshkar.materialcolorpickercompose.SpectrumColorPicker
import com.hooshkar.materialcolorpickercompose.SwatchesColorPicker
import com.hooshkar.materialcolorpickercompose.rememberMaterialColorPickerState

private enum class Demo { Swatches, Spectrum, Both }

class ComposePickerPreviewActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    var demo by remember { mutableStateOf(Demo.Swatches) }
                    val recentColors = remember {
                        listOf(
                            Color(0xFF6200EE),
                            Color(0xFFBB86FC),
                            Color(0xFF03DAC5),
                            Color(0xFF018786),
                            Color(0xFF3700B3),
                            Color(0xFF3F51B5)
                        )
                    }
                    val state = rememberMaterialColorPickerState(
                        initialColor = Color(0xFF03DAC5),
                        previousColor = Color(0xFF6200EE),
                        recentColors = recentColors
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                    ) {
                        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxSize()) {
                            Demo.entries.forEachIndexed { index, d ->
                                SegmentedButton(
                                    selected = demo == d,
                                    onClick = { demo = d },
                                    shape = SegmentedButtonDefaults.itemShape(index, Demo.entries.size)
                                ) { Text(d.name) }
                            }
                        }

                        when (demo) {
                            Demo.Swatches -> SwatchesColorPicker(
                                state = state,
                                onColorChanged = { }
                            )

                            Demo.Spectrum -> SpectrumColorPicker(
                                state = state,
                                onColorChanged = { }
                            )

                            Demo.Both -> MaterialColorPicker(
                                state = state,
                                onColorChanged = { }
                            )
                        }
                    }
                }
            }
        }
    }
}
