package com.hooshkar.materialcolorpicker.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.hooshkar.materialcolorpicker.R

internal class MaterialOpacitySeekBar : AppCompatSeekBar {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private val colors: IntArray = intArrayOf(-1, MEASURED_STATE_MASK)
    private var seekBarProgressDrawable: GradientDrawable = ResourcesCompat.getDrawable(
        resources,
        R.drawable.material_color_picker_opacity_seekbar,
        null
    ) as GradientDrawable

    fun init(num: Int?) {
        setMax(255)
        if (num != null) {
            initColor(num)
            seekBarProgressDrawable =
                ContextCompat.getDrawable(
                    context,
                    R.drawable.material_color_picker_opacity_seekbar
                ) as GradientDrawable
        }
        progressDrawable = seekBarProgressDrawable
        setThumb(
            ContextCompat.getDrawable(context, R.drawable.material_color_picker_seekbar_cursor)
        )
        setThumbOffset(0)
        setSplitTrack(false)
    }

    private fun initColor(i: Int) {
        val fArr = FloatArray(3)
        Color.colorToHSV(i, fArr)
        val alpha = Color.alpha(i)
        colors[0] = Color.HSVToColor(0, fArr)
        colors[1] = Color.HSVToColor(255, fArr)
        progress = alpha
    }

    fun restoreColor(i: Int) {
        initColor(i)
        seekBarProgressDrawable.colors = colors
        progressDrawable = seekBarProgressDrawable
    }

    fun changeColorBase(i: Int, i2: Int) {
        if (progressDrawable != null) {
            val iArr: IntArray = colors
            iArr[1] = i
            seekBarProgressDrawable.colors = iArr
            progressDrawable = seekBarProgressDrawable
            val fArr = FloatArray(3)
            Color.colorToHSV(i, fArr)
            colors[0] = Color.HSVToColor(0, fArr)
            colors[1] = Color.HSVToColor(255, fArr)
            progress = i2
        }
    }
}