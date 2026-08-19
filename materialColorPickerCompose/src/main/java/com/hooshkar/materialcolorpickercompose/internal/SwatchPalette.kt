package com.hooshkar.materialcolorpickercompose.internal

import androidx.compose.ui.graphics.Color

/**
 * The fixed 11x10 swatch palette (columns x rows), shown on the Swatches page.
 * Column 0 runs white -> gray -> black; the remaining columns each run a hue
 * from light to dark. Values are an independent copy of MaterialColorPicker's
 * palette, kept separate on purpose so the Compose module has no dependency
 * on the View-based module.
 */
internal val SwatchPaletteColumns = 11
internal val SwatchPaletteRows = 10

internal val swatchPalette: Array<IntArray> = arrayOf(
    intArrayOf(-1, -3355444, -5000269, -6710887, -8224126, -10066330, -11711155, -13421773, -15066598, -16777216),
    intArrayOf(-22360, -38037, -49859, -60396, -65536, -393216, -2424832, -5767168, -10747904, -13434880),
    intArrayOf(-11096, -19093, -25544, -30705, -32768, -361216, -2396672, -5745664, -10736128, -13428224),
    intArrayOf(-88, -154, -200, -256, -328704, -329216, -2368768, -6053120, -10724352, -13421824),
    intArrayOf(-5701720, -10027162, -13041864, -16056566, -16711936, -16713216, -16721152, -16735488, -16753664, -16764160),
    intArrayOf(-5701685, -10027101, -13041784, -15728785, -16711834, -16714398, -16721064, -16735423, -16753627, -16764140),
    intArrayOf(-5701633, -10027009, -12713985, -16056321, -16711681, -16714251, -16720933, -16735325, -16753572, -16764109),
    intArrayOf(-5712641, -9718273, -13067009, -15430913, -16744193, -16744966, -16748837, -16755544, -16764575, -16770509),
    intArrayOf(-5723905, -9737217, -13092609, -16119041, -16776961, -16776966, -16776997, -16777048, -16777119, -16777165),
    intArrayOf(-3430145, -5870593, -7849729, -9498625, -10092289, -10223366, -11009829, -12386136, -14352292, -15466445),
    intArrayOf(-22273, -39169, -50945, -61441, -65281, -392966, -2424613, -5767000, -10420127, -13434829)
)

internal fun swatchColorAt(column: Int, row: Int): Color = Color(swatchPalette[column][row])

/** True for the two bottom-most rows (very dark colors), which need a lighter cursor outline. */
internal fun isDarkSwatchRow(row: Int): Boolean = row >= SwatchPaletteRows - 2
