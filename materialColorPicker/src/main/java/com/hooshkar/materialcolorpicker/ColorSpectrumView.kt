package com.hooshkar.materialcolorpicker

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Shader
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.SoundEffectConstants
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRectF
import kotlin.math.max

internal class ColorSpectrumView : View {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes)

    private val hueColors = intArrayOf(-65281, -16776961, -16711681, -16711936, -256, -65536)
    private val roundedCornerRadius: Float = resources.dpToPx(4f).toFloat()
    private val strokeWidth: Int = resources.dpToPx(2f)
    private val cursorDrawable: Drawable
    private var cursorPaint: Paint = Paint()
    private val cursorPaintSize: Int = resources.dpToPx(25f)
    private var cursorPosX: Float = 0f
    private var cursorPosY: Float = 0f
    private val huePaint: Paint = Paint(1)
    private val saturationPaint: Paint = Paint(1)
    private var colorChangedListener: Listener? = null
    private val strokePaint: Paint = Paint()
    private val spectrumRect: Rect = Rect(
        0,
        0,
        resources.getDimensionPixelSize(R.dimen.material_color_picker_color_swatch_view_width),
        resources.getDimensionPixelSize(R.dimen.material_color_picker_color_swatch_view_height)
    )

    init {
        strokePaint.style = Paint.Style.STROKE
        strokePaint.setColor(
            resources.getColor(
                R.color.material_color_picker_stroke_color_spectrum_view,
                null
            )
        )
        strokePaint.strokeWidth = strokeWidth.toFloat()
        cursorDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.material_color_picker_gradient_wheel_cursor,
            null
        )!!
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        //Math.sqrt(Math.pow((double) event.getX(), 2.0d) + Math.pow((double) event.getY(), 2.0d));
        val action = event.action
        if (action == MotionEvent.ACTION_DOWN) {
            playSoundEffect(SoundEffectConstants.CLICK)
        } else if (action == MotionEvent.ACTION_MOVE && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }

        var x = event.x
        var y = event.y
        if (x > (spectrumRect.width().toFloat())) {
            x = spectrumRect.width().toFloat()
        }
        if (y > (spectrumRect.height().toFloat())) {
            y = spectrumRect.height().toFloat()
        }
        if (x < 0.0f) {
            x = 0.0f
        }
        if (y <= 7.0f) {
            y = 7.0f
        }

        cursorPosX = x
        cursorPosY = y
        val width =
            ((x - (spectrumRect.left.toFloat())) / (spectrumRect.width().toFloat())) * 300.0f
        val height: Float =
            (cursorPosY - (spectrumRect.top.toFloat())) / (spectrumRect.height().toFloat())
        val fArr = FloatArray(3)
        fArr[0] = max(width, 0.0f)
        fArr[1] = height

        colorChangedListener?.onSpectrumColorChanged(fArr[0], fArr[1])
            ?: Log.d(javaClass.simpleName, "Listener is not set.")

        invalidate()
        return true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.getClipBounds(spectrumRect)
        val spectrumRectF = spectrumRect.toRectF()
        huePaint.setShader(
            LinearGradient(
                spectrumRectF.right,
                spectrumRectF.top,
                spectrumRectF.left,
                spectrumRectF.top,
                hueColors,
                null,
                Shader.TileMode.CLAMP
            )
        )

        huePaint.style = Paint.Style.FILL

        saturationPaint.setShader(
            LinearGradient(
                spectrumRectF.left,
                spectrumRectF.top,
                spectrumRectF.left,
                spectrumRectF.bottom,
                -1,
                0,
                Shader.TileMode.CLAMP
            )
        )

        canvas.drawRoundRect(
            spectrumRectF.left,
            spectrumRectF.top,
            spectrumRectF.right,
            spectrumRectF.bottom,
            roundedCornerRadius,
            roundedCornerRadius,
            huePaint
        )
        canvas.drawRoundRect(
            spectrumRectF.left,
            spectrumRectF.top,
            spectrumRectF.right,
            spectrumRectF.bottom,
            roundedCornerRadius,
            roundedCornerRadius,
            saturationPaint
        )
        canvas.drawRoundRect(
            spectrumRectF.left,
            spectrumRectF.top,
            spectrumRectF.right,
            spectrumRectF.bottom,
            roundedCornerRadius,
            roundedCornerRadius,
            strokePaint
        )

        if (cursorPosX < (spectrumRect.left.toFloat())) {
            cursorPosX = spectrumRect.left.toFloat()
        }
        if (cursorPosY <= ((spectrumRect.top + 7).toFloat())) {
            cursorPosY = (spectrumRect.top + 7).toFloat()
        }
        if (cursorPosX > (spectrumRect.right.toFloat())) {
            cursorPosX = spectrumRect.right.toFloat()
        }
        if (cursorPosY > (spectrumRect.bottom.toFloat())) {
            cursorPosY = spectrumRect.bottom.toFloat()
        }

        canvas.drawCircle(
            cursorPosX,
            cursorPosY,
            (cursorPaintSize.toFloat()) / strokeWidth,
            cursorPaint
        )

        cursorDrawable.setBounds(
            (cursorPosX.toInt()) - (cursorPaintSize / 2),
            (cursorPosY.toInt()) - (cursorPaintSize / 2),
            (cursorPosX.toInt()) + (cursorPaintSize / 2),
            (cursorPosY.toInt()) + (cursorPaintSize / 2)
        )
        cursorDrawable.draw(canvas)

        setDrawingCacheEnabled(true)
    }

    internal fun setColor(color: Int) {
        val fArr = FloatArray(3)
        Color.colorToHSV(color, fArr)
        updateCursorPosition(color, fArr)
    }

    internal fun updateCursorColor(i: Int) {
        cursorPaint.setColor(i)
    }

    fun updateCursorPosition(color: Int, fArr: FloatArray) {
        cursorPosX =
            (spectrumRect.left.toFloat()) + (((spectrumRect.width().toFloat()) * fArr[0]) / 300.0f)
        cursorPosY = (spectrumRect.top.toFloat()) + ((spectrumRect.height().toFloat()) * fArr[1])
        invalidate()
    }

    fun interface Listener {
        fun onSpectrumColorChanged(f: Float, f2: Float)
    }
}