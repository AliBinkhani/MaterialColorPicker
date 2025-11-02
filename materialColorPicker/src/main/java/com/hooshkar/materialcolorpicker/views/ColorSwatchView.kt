package com.hooshkar.materialcolorpicker.views

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.BlurMaskFilter
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Point
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import androidx.core.view.ViewCompat
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import androidx.customview.widget.ExploreByTouchHelper
import com.hooshkar.materialcolorpicker.R
import com.hooshkar.materialcolorpicker.dpToPx

internal class ColorSwatchView : View {

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

    companion object {
        private const val MAX_SWATCH_VIEW_ID: Int = 110

    }

    private val roundedCornerRadius: Float = resources.dpToPx(4f).toFloat()
    private val strokeWidth: Float = 0.25f
    private val swatchItemSizeRoundingCorner: Float = 4.5f

    //    private var corners: FloatArray = TODO()
    private var currentCursorColor: Int = 0
    private val backgroundPaint: Paint
    private val colorBrightness: Array<IntArray>
    private val colorSwatch: Array<IntArray>
    private val colorSwatchDescription: Array<Array<StringBuilder>>
    private var cursorDrawable: GradientDrawable
    private val cursorIndex: Point = Point(-1, -1)
    private val cursorRect: Rect = Rect()
    private var fromUser = false
    private var isColorInSwatch = false
    var onColorSwatchChangedListener: OnColorSwatchChangedListener? = null
    private var selectedVirtualViewId = 0
    private val shadowRect: Rect = Rect()
    val strokePaint: Paint
    private val swatchItemHeight: Float
    private val swatchItemWidth: Float
    private val swatchRect: RectF
    private val swatchRectBackground: RectF
    private var touchHelper: TouchHelper = TouchHelper(this)
    val shadow: Paint

    init {
        selectedVirtualViewId = -1
        fromUser = false
        isColorInSwatch = true

        colorSwatch = arrayOf<IntArray>(
            intArrayOf(
                -1,
                -3355444,
                -5000269,
                -6710887,
                -8224126,
                -10066330,
                -11711155,
                -13421773,
                -15066598,
                -16777216
            ),
            intArrayOf(
                -22360,
                -38037,
                -49859,
                -60396,
                -65536,
                -393216,
                -2424832,
                -5767168,
                -10747904,
                -13434880
            ),
            intArrayOf(
                -11096,
                -19093,
                -25544,
                -30705,
                -32768,
                -361216,
                -2396672,
                -5745664,
                -10736128,
                -13428224
            ),
            intArrayOf(
                -88,
                -154,
                -200,
                -256,
                -328704,
                -329216,
                -2368768,
                -6053120,
                -10724352,
                -13421824
            ),
            intArrayOf(
                -5701720,
                -10027162,
                -13041864,
                -16056566,
                -16711936,
                -16713216,
                -16721152,
                -16735488,
                -16753664,
                -16764160
            ),
            intArrayOf(
                -5701685,
                -10027101,
                -13041784,
                -15728785,
                -16711834,
                -16714398,
                -16721064,
                -16735423,
                -16753627,
                -16764140
            ),
            intArrayOf(
                -5701633,
                -10027009,
                -12713985,
                -16056321,
                -16711681,
                -16714251,
                -16720933,
                -16735325,
                -16753572,
                -16764109
            ),
            intArrayOf(
                -5712641,
                -9718273,
                -13067009,
                -15430913,
                -16744193,
                -16744966,
                -16748837,
                -16755544,
                -16764575,
                -16770509
            ),
            intArrayOf(
                -5723905,
                -9737217,
                -13092609,
                -16119041,
                -16776961,
                -16776966,
                -16776997,
                -16777048,
                -16777119,
                -16777165
            ),
            intArrayOf(
                -3430145,
                -5870593,
                -7849729,
                -9498625,
                -10092289,
                -10223366,
                -11009829,
                -12386136,
                -14352292,
                -15466445
            ),
            intArrayOf(
                -22273,
                -39169,
                -50945,
                -61441,
                -65281,
                -392966,
                -2424613,
                -5767000,
                -10420127,
                -13434829
            )
        )
        colorBrightness = arrayOf<IntArray>(
            intArrayOf(100, 80, 70, 60, 51, 40, 30, 20, 10, 0),
            intArrayOf(83, 71, 62, 54, 50, 49, 43, 33, 18, 10),
            intArrayOf(83, 71, 61, 53, 50, 49, 43, 33, 18, 10),
            intArrayOf(83, 70, 61, 50, 51, 49, 43, 32, 18, 10),
            intArrayOf(83, 70, 61, 52, 50, 49, 43, 32, 18, 10),
            intArrayOf(83, 70, 61, 53, 50, 48, 43, 32, 18, 10),
            intArrayOf(83, 70, 62, 52, 50, 48, 43, 32, 18, 10),
            intArrayOf(83, 71, 61, 54, 50, 49, 43, 33, 19, 10),
            intArrayOf(83, 71, 61, 52, 50, 49, 43, 33, 19, 10),
            intArrayOf(83, 71, 61, 53, 50, 49, 43, 33, 18, 10),
            intArrayOf(83, 70, 61, 53, 50, 49, 43, 33, 19, 10)
        )
        colorSwatchDescription = Array<Array<StringBuilder>>(11) {
            Array<StringBuilder>(10) {
                StringBuilder()
            }
        }

        // initCursorDrawable
        cursorDrawable = ResourcesCompat.getDrawable(
            resources,
            R.drawable.material_color_swatch_view_cursor,
            null
        ) as GradientDrawable
        shadow = Paint()
        shadow.style = Paint.Style.FILL
        shadow.setColor(resources.getColor(R.color.material_color_picker_shadow, null))
        shadow.setMaskFilter(BlurMaskFilter(10.0f, BlurMaskFilter.Blur.NORMAL))

        initAccessibility()

        swatchItemHeight =
            resources.getDimension(R.dimen.material_color_picker_ui_color_swatch_view_height) / 10.0f
        swatchItemWidth =
            resources.getDimension(R.dimen.material_color_picker_ui_color_swatch_view_width) / 11.0f
        swatchRect = RectF(
            swatchItemSizeRoundingCorner,
            swatchItemSizeRoundingCorner,
            (resources.getDimensionPixelSize(R.dimen.material_color_picker_color_swatch_view_width)
                .toFloat()) + swatchItemSizeRoundingCorner,
            (resources.getDimensionPixelSize(R.dimen.material_color_picker_ui_color_swatch_view_height)
                .toFloat()) + swatchItemSizeRoundingCorner
        )
        swatchRectBackground = RectF(
            0.0f,
            0.0f,
            resources.getDimensionPixelSize(R.dimen.material_color_picker_ui_color_swatch_view_width_background)
                .toFloat(),
            resources.getDimensionPixelSize(R.dimen.material_color_picker_ui_color_swatch_view_height_background)
                .toFloat()
        )

        strokePaint = Paint()
        strokePaint.style = Paint.Style.STROKE
        strokePaint.setColor(
            resources.getColor(
                R.color.material_color_picker_stroke_color_swatchview,
                null
            )
        )
        strokePaint.strokeWidth = strokeWidth


        backgroundPaint = Paint()
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.setColor(resources.getColor(android.R.color.transparent, null))
    }

    private fun initAccessibility() {
        ViewCompat.setAccessibilityDelegate(this, touchHelper)
        setImportantForAccessibility(1)
    }

    override fun onDraw(canvas: Canvas) {
        val paint = Paint()
        var corners: FloatArray
        canvas.drawRoundRect(
            swatchRectBackground,
            roundedCornerRadius,
            roundedCornerRadius,
            backgroundPaint
        )
        var i2 = 0
        while (true) {
            var i3 = 8
            if (i2 >= 11) {
                break
            }
            var i4 = 0
            while (i4 < 10) {
                paint.setColor(colorSwatch[i2][i4])
                if (i2 == 0 && i4 == 0) {
                    val fArr = FloatArray(i3)
                    val i5: Int = roundedCornerRadius.toInt()
                    fArr[0] = i5.toFloat()
                    fArr[1] = i5.toFloat()
                    fArr[2] = 0.0f
                    fArr[3] = 0.0f
                    fArr[4] = 0.0f
                    fArr[5] = 0.0f
                    fArr[6] = 0.0f
                    fArr[7] = 0.0f
                    corners = fArr
                    val path = Path()
                    path.addRoundRect(
                        ((((i2.toFloat()) * swatchItemWidth) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        ((((i4.toFloat()) * swatchItemHeight) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((swatchItemWidth * ((i2 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((swatchItemHeight * ((i4 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        corners,
                        Path.Direction.CW
                    )
                    canvas.drawPath(path, paint)
                } else if (i2 == 0 && i4 == 9) {
                    val i6: Int = roundedCornerRadius.toInt()
                    corners =
                        floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, i6.toFloat(), i6.toFloat())
                    val path2 = Path()
                    val f3: Float = swatchItemWidth
                    val f4: Float = swatchItemHeight
                    path2.addRoundRect(
                        ((((i2.toFloat()) * f3) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        ((((i4.toFloat()) * f4) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f3 * ((i2 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f4 * ((i4 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        corners,
                        Path.Direction.CW
                    )
                    canvas.drawPath(path2, paint)
                } else if (i2 == 10 && i4 == 0) {
                    val i7: Int = roundedCornerRadius.toInt()
                    corners =
                        floatArrayOf(0.0f, 0.0f, i7.toFloat(), i7.toFloat(), 0.0f, 0.0f, 0.0f, 0.0f)
                    val path3 = Path()
                    val f5: Float = swatchItemWidth
                    val f6: Float = swatchItemHeight
                    path3.addRoundRect(
                        ((((i2.toFloat()) * f5) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        ((((i4.toFloat()) * f6) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f5 * ((i2 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f6 * ((i4 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        corners,
                        Path.Direction.CW
                    )
                    canvas.drawPath(path3, paint)
                } else if (i2 == 10 && i4 == 9) {
                    val i8: Int = this.roundedCornerRadius.toInt()
                    corners =
                        floatArrayOf(0.0f, 0.0f, 0.0f, 0.0f, i8.toFloat(), i8.toFloat(), 0.0f, 0.0f)
                    val path4 = Path()
                    val f7: Float = swatchItemWidth
                    val f8: Float = swatchItemHeight
                    path4.addRoundRect(
                        ((((i2.toFloat()) * f7) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        ((((i4.toFloat()) * f8) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f7 * ((i2 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f8 * ((i4 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        corners,
                        Path.Direction.CW
                    )
                    canvas.drawPath(path4, paint)
                } else {
                    val f9: Float = swatchItemWidth
                    val f10: Float = swatchItemHeight
                    canvas.drawRect(
                        ((((i2.toFloat()) * f9) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        ((((i4.toFloat()) * f10) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f9 * ((i2 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        (((f10 * ((i4 + 1).toFloat())) + swatchItemSizeRoundingCorner).toInt()).toFloat(),
                        paint
                    )
                }
                i4++
                i3 = 8
            }
            i2++
        }
        val rectF2: RectF = swatchRect
        val i9: Int = this.roundedCornerRadius.toInt()
        canvas.drawRoundRect(rectF2, i9.toFloat(), i9.toFloat(), strokePaint)
        if (isColorInSwatch) {
            canvas.drawRect(this.shadowRect, this.shadow)
            if (this.cursorIndex.y == 8 || this.cursorIndex.y == 9) {
                this.cursorDrawable = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.material_color_swatch_view_cursor,
                    null
                ) as GradientDrawable
            } else {
                this.cursorDrawable = ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.material_color_swatch_view_cursor_gray,
                    null
                ) as GradientDrawable
            }
            this.cursorDrawable.setColor(this.currentCursorColor)
            this.cursorDrawable.bounds = this.cursorRect
            this.cursorDrawable.draw(canvas)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(motionEvent: MotionEvent): Boolean {
        if (motionEvent.action == 2 && parent != null) {
            parent.requestDisallowInterceptTouchEvent(true)
        }
        if (setCursorIndexAt(motionEvent.x, motionEvent.y) || !isColorInSwatch) {
            val i: Int = colorSwatch[cursorIndex.x][cursorIndex.y]
            this.currentCursorColor = i
            this.currentCursorColor = ColorUtils.setAlphaComponent(i, 255)
            setShadowRect(shadowRect)
            setCursorRect(cursorRect)
            setSelectedVirtualViewId()
            invalidate()

            onColorSwatchChangedListener?.onColorSwatchChanged(colorSwatch[cursorIndex.x][cursorIndex.y])
        }
        return true
    }

    internal fun getCursorIndexAt(i: Int): Point {
        val argb = Color.argb(255, (i shr 16) and 255, (i shr 8) and 255, i and 255)
        val point = Point(-1, -1)
        this.fromUser = false
        for (i2 in 0..10) {
            for (i3 in 0..9) {
                if (this.colorSwatch[i2][i3] == argb) {
                    point.set(i2, i3)
                    this.fromUser = true
                }
            }
        }
        this.isColorInSwatch = true
        if (!this.fromUser && !this.cursorIndex.equals(-1, -1)) {
            this.isColorInSwatch = false
            invalidate()
        }
        return point
    }

    private fun setCursorIndexAt(f: Float, f2: Float): Boolean {
        var f = f
        var f2 = f2
        val f3: Float = swatchItemWidth * 11.0f
        val f4: Float = swatchItemHeight * 10.0f
        if (f >= f3) {
            f = f3 - 1.0f
        } else if (f < 0.0f) {
            f = 0.0f
        }
        if (f2 >= f4) {
            f2 = f4 - 1.0f
        } else if (f2 < 0.0f) {
            f2 = 0.0f
        }
        val point = Point(this.cursorIndex.x, this.cursorIndex.y)
        this.cursorIndex.set(
            (f / this.swatchItemWidth).toInt(),
            (f2 / this.swatchItemHeight).toInt()
        )
        return point != this.cursorIndex
    }

    private fun setCursorIndexAt(i: Int) {
        val cursorIndexAt = getCursorIndexAt(i)
        if (this.fromUser) {
            this.cursorIndex.set(cursorIndexAt.x, cursorIndexAt.y)
        }
    }

    private fun setCursorRect(rect: Rect) {
        rect.set(
            ((((cursorIndex.x.toDouble()) - 0.05) * (swatchItemWidth.toDouble())) + 4.5).toInt(),
            ((((cursorIndex.y.toDouble()) - 0.05) * (swatchItemHeight.toDouble())) + 4.5).toInt(),
            (((((cursorIndex.x + 1).toDouble()) + 0.05) * (swatchItemWidth.toDouble())) + 4.5).toInt(),
            (((((cursorIndex.y + 1).toDouble()) + 0.05) * (swatchItemHeight.toDouble())) + 4.5).toInt()
        )
    }

    private fun setShadowRect(rect: Rect) {
        rect.set(
            (((cursorIndex.x.toFloat()) * swatchItemWidth) + swatchItemSizeRoundingCorner).toInt(),
            (((cursorIndex.y.toFloat()) * swatchItemHeight) + swatchItemSizeRoundingCorner).toInt(),
            (((((cursorIndex.x + 1).toDouble()) + 0.05) * (swatchItemWidth.toDouble())) + 4.5).toInt(),
            (((((cursorIndex.y + 1).toDouble()) + 0.1) * (swatchItemHeight.toDouble())) + 4.5).toInt()
        )
    }

    private fun setSelectedVirtualViewId() {
        selectedVirtualViewId = (cursorIndex.y * 11) + cursorIndex.x
    }

    fun updateCursorPosition(i: Int) {
        setCursorIndexAt(i)
        if (this.fromUser) {
            this.currentCursorColor = ColorUtils.setAlphaComponent(i, 255)
            setShadowRect(this.shadowRect)
            setCursorRect(this.cursorRect)
            invalidate()
            setSelectedVirtualViewId()
            return
        }
        this.selectedVirtualViewId = -1
    }

    fun getColorSwatchDescriptionAt(i: Int): StringBuilder? {
        val cursorIndexAt = getCursorIndexAt(i)
        if (!this.fromUser) {
            return null
        }
        if (this.colorSwatchDescription[cursorIndexAt.x][cursorIndexAt.y] == null) {
            return this.touchHelper.getItemDescription(cursorIndexAt.x + (cursorIndexAt.y * 11))
        }
        return this.colorSwatchDescription[cursorIndexAt.x][cursorIndexAt.y]
    }

    inner class TouchHelper(view: View) : ExploreByTouchHelper(view) {
        private val colorDescription: Array<Array<String>>
        private var virtualCursorIndexX = 0
        private var virtualCursorIndexY = 0
        private val virtualViewRect = Rect()

        init {
            colorDescription = arrayOf<Array<String>>(
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_white),
                    resources.getString(R.string.material_color_picker_light_gray),
                    resources.getString(R.string.material_color_picker_gray),
                    resources.getString(R.string.material_color_picker_dark_gray),
                    resources.getString(R.string.material_color_picker_black)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_red),
                    resources.getString(R.string.material_color_picker_red),
                    resources.getString(R.string.material_color_picker_dark_red)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_orange),
                    resources.getString(R.string.material_color_picker_orange),
                    resources.getString(R.string.material_color_picker_dark_orange)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_yellow),
                    resources.getString(R.string.material_color_picker_yellow),
                    resources.getString(R.string.material_color_picker_dark_yellow)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_green),
                    resources.getString(R.string.material_color_picker_green),
                    resources.getString(R.string.material_color_picker_dark_green)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_spring_green),
                    resources.getString(R.string.material_color_picker_spring_green),
                    resources.getString(R.string.material_color_picker_dark_spring_green)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_cyan),
                    resources.getString(R.string.material_color_picker_cyan),
                    resources.getString(R.string.material_color_picker_dark_cyan)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_azure),
                    resources.getString(R.string.material_color_picker_azure),
                    resources.getString(R.string.material_color_picker_dark_azure)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_blue),
                    resources.getString(R.string.material_color_picker_blue),
                    resources.getString(R.string.material_color_picker_dark_blue)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_violet),
                    resources.getString(R.string.material_color_picker_violet),
                    resources.getString(R.string.material_color_picker_dark_violet)
                ),
                arrayOf<String>(
                    resources.getString(R.string.material_color_picker_light_magenta),
                    resources.getString(R.string.material_color_picker_magenta),
                    resources.getString(R.string.material_color_picker_dark_magenta)
                )
            )

        }

        override fun getVirtualViewAt(x: Float, y: Float): Int {
            setVirtualCursorIndexAt(x, y)
            return getFocusedVirtualViewId()
        }

        override fun getVisibleVirtualViews(virtualViewIds: MutableList<Int>) {
            for (i in 0..<MAX_SWATCH_VIEW_ID) {
                virtualViewIds.add(i)
            }
        }

        override fun onPopulateNodeForVirtualView(
            virtualViewId: Int,
            node: AccessibilityNodeInfoCompat
        ) {
            node.setContentDescription(getItemDescription(virtualViewId))
        }

        override fun onPerformActionForVirtualView(
            virtualViewId: Int,
            action: Int,
            arguments: Bundle?
        ): Boolean {
            TODO("Not yet implemented")
        }

        fun getItemDescription(i: Int): java.lang.StringBuilder? {
            setVirtualCursorIndexAt(i)
            if (colorSwatchDescription[this.virtualCursorIndexX][this.virtualCursorIndexY] == null) {
                val sb = java.lang.StringBuilder()
                val i2: Int = this.virtualCursorIndexX
                if (i2 == 0) {
                    val i3: Int = this.virtualCursorIndexY
                    if (i3 == 0) {
                        sb.append(this.colorDescription[i2][0])
                    } else if (i3 < 3) {
                        sb.append(this.colorDescription[i2][1])
                    } else if (i3 < 6) {
                        sb.append(this.colorDescription[i2][2])
                    } else if (i3 < 9) {
                        sb.append(this.colorDescription[i2][3])
                    } else {
                        sb.append(this.colorDescription[i2][4])
                    }
                } else {
                    val i4: Int = this.virtualCursorIndexY
                    if (i4 < 3) {
                        sb.append(this.colorDescription[i2][0])
                    } else if (i4 < 6) {
                        sb.append(this.colorDescription[i2][1])
                    } else {
                        sb.append(this.colorDescription[i2][2])
                    }
                }
                val i5: Int = this.virtualCursorIndexX
                if (!(i5 == 3 && this.virtualCursorIndexY == 3)) {
                    if (i5 == 0 && this.virtualCursorIndexY == 4) {
                        sb.append(", ")
                            .append(colorBrightness[this.virtualCursorIndexX][this.virtualCursorIndexY])
                    } else if (this.virtualCursorIndexY != 4) {
                        sb.append(", ")
                            .append(colorBrightness[this.virtualCursorIndexX][this.virtualCursorIndexY])
                    }
                }
                colorSwatchDescription[this.virtualCursorIndexX][this.virtualCursorIndexY] =
                    sb
            }
            return colorSwatchDescription[this.virtualCursorIndexX][this.virtualCursorIndexY]
        }

        private fun onVirtualViewClick(i: Int) {
            onColorSwatchChangedListener?.onColorSwatchChanged(i)
            touchHelper?.sendEventForVirtualView(selectedVirtualViewId, 1)
        }

        private fun getFocusedVirtualViewId(): Int {
            return virtualCursorIndexX + (virtualCursorIndexY * 11)
        }

        private fun setVirtualCursorIndexAt(f: Float, f2: Float) {
            var f = f
            var f2 = f2
            val f3: Float = swatchItemWidth * 11.0f
            val f4: Float = swatchItemHeight * 10.0f
            if (f >= f3) {
                f = f3 - 1.0f
            } else if (f < 0.0f) {
                f = 0.0f
            }
            if (f2 >= f4) {
                f2 = f4 - 1.0f
            } else if (f2 < 0.0f) {
                f2 = 0.0f
            }
            virtualCursorIndexX = (f / swatchItemWidth).toInt()
            virtualCursorIndexY = (f2 / swatchItemHeight).toInt()
        }

        private fun setVirtualCursorIndexAt(i: Int) {
            virtualCursorIndexX = i % 11
            virtualCursorIndexY = i / 11
        }
    }

    fun interface OnColorSwatchChangedListener {
        fun onColorSwatchChanged(color: Int)
    }
}