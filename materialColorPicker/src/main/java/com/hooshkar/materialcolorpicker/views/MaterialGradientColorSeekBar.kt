package com.hooshkar.materialcolorpicker.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.hooshkar.materialcolorpicker.R
import kotlin.math.roundToInt

internal class MaterialGradientColorSeekBar : AppCompatSeekBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val colors: IntArray = intArrayOf(ViewCompat.MEASURED_STATE_MASK, -1)
    private val seekBarProgressDrawable: GradientDrawable = ResourcesCompat.getDrawable(
        resources,
        R.drawable.material_color_picker_gradient_seekbar_drawable,
        null
    ) as GradientDrawable

    fun init(num: Int?) {
        setMax(100)
        if (num != null) {
            initColor(num)
        }
        initProgressDrawable()
        initThumb()
    }

    private fun initColor(i: Int) {
        val fArr = FloatArray(3)
        Color.colorToHSV(i, fArr)
        val f = fArr[2]
        fArr[2] = 1.0f
        colors[1] = Color.HSVToColor(fArr)
        progress = (f * (max.toFloat())).roundToInt()
    }

    fun restoreColor(i: Int) {
        initColor(i)
        seekBarProgressDrawable.colors = colors
        progressDrawable = seekBarProgressDrawable
    }

    fun changeColorBase(i: Int) {
        val iArr: IntArray = colors
        iArr[1] = i
        seekBarProgressDrawable.colors = iArr
        progressDrawable = seekBarProgressDrawable
        val fArr = FloatArray(3)
        Color.colorToHSV(i, fArr)
        val f = fArr[2]
        fArr[2] = 1.0f
        colors[1] = Color.HSVToColor(fArr)
        progress = (f * (max.toFloat())).roundToInt()
    }

    private fun initProgressDrawable() {
        progressDrawable = seekBarProgressDrawable
    }

    private fun initThumb() {
        setThumb(
            ResourcesCompat.getDrawable(
                resources,
                R.drawable.material_color_picker_seekbar_cursor,
                null
            )
        )
        setThumbOffset(0)
        setSplitTrack(false)
    }
}