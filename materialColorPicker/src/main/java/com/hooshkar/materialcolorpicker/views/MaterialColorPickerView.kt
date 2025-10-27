package com.hooshkar.materialcolorpicker.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.TextView
import android.widget.TextView.OnEditorActionListener
import androidx.core.content.ContextCompat
import com.hooshkar.materialcolorpicker.R
import com.hooshkar.materialcolorpicker.isTablet
import java.util.Locale
import kotlin.math.ceil
import androidx.core.graphics.toColorInt

@SuppressLint("SetTextI18n")
class MaterialColorPickerView: LinearLayout, View.OnClickListener {
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
        private const val RIPPLE_EFFECT_OPACITY: Int = 61

    }

    private var RECENT_COLOR_SLOT_COUNT: Int = 6
    private var beforeValue: String? = null

//    private val binding: MaterialColorPickerOneui3LayoutBinding
    private val mColorPickerRedEditText: EditText
    private var mColorPickerGreenEditText: EditText //TODO: VAR???
    private val mColorPickerBlueEditText: EditText
    private val mColorPickerHexEditText: EditText
    private val mColorPickerOpacityEditText: EditText
    private val mColorPickerSaturationEditText: EditText
    private val mColorPickerTabSpectrumText: TextView
    private val mColorPickerTabSwatchesText: TextView
    private val mColorSpectrumView: ColorSpectrumView
    private val mColorSwatchView: ColorSwatchView
    private val mCurrentColorView: ImageView
    private val mGradientColorSeekBar: MaterialGradientColorSeekBar
    private val mGradientSeekBarContainer: LinearLayout
    private val mOpacityLayout: LinearLayout
    private val mOpacitySeekBar: MaterialOpacitySeekBar
    private val mOpacitySeekBarContainer: FrameLayout
    private val mPickedColorView: ImageView
    private val mRecentColorListLayout: LinearLayout
    private val mSpectrumViewContainer: FrameLayout
    private val mSwatchViewContainer: FrameLayout
    private val mTabLayoutContainer: LinearLayout
    private var mCurrentColorBackground: GradientDrawable? = null
    private var mFlagVar = false
    var mOnColorChangedListener: OnColorChangedListener? = null
    private val mPickedColor: PickedColor = PickedColor()
    private val mRecentColorInfo: RecentColorInfo = RecentColorInfo()
    private val mRecentColorValues: ArrayList<Int?> = mRecentColorInfo.getRecentColorInfo()
    private var mSelectedColorBackground: GradientDrawable? = null
    private val mSmallestWidthDp = intArrayOf(320, 360, 411)
    private var mIsInputFromUser = false
    private var mIsOpacityBarEnabled = false
    var mIsSpectrumSelected: Boolean = false
    var editTexts: ArrayList<EditText> = ArrayList()
    private var mColorDescription: Array<String?>? = null
    private var mfromEditText = false
    private var mfromSaturationSeekbar = false
    private var mfromSpectrumTouch = false
    private var mfromRGB = false
    private var mTextFromRGB = false

    private val mImageButtonClickListener: OnClickListener = OnClickListener { v ->
        var i = 0
        while (i < mRecentColorValues.size && i < RECENT_COLOR_SLOT_COUNT) {
            if (mRecentColorListLayout.getChildAt(i) == v) {
                mIsInputFromUser = true

                val intValue: Int = mRecentColorValues[i]!!
                mPickedColor.color = intValue
                mapColorOnColorWheel(intValue)
                updateHexAndRGBValues(intValue)

                if (mGradientColorSeekBar != null) {
                    val progress = mGradientColorSeekBar.progress
                    mColorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            progress
                        )
                    )
                    mColorPickerSaturationEditText.setSelection(progress.toString().length)
                }

                if (mOnColorChangedListener != null) {
                    mOnColorChangedListener!!.onColorChanged(intValue)
                }
            }
            i++
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.material_color_picker_oneui_3_layout, this)

        mColorPickerRedEditText = findViewById(R.id.material_color_red_edit_text)
        mColorPickerGreenEditText = findViewById(R.id.material_color_green_edit_text)
        mColorPickerBlueEditText = findViewById(R.id.material_color_blue_edit_text)
        mColorPickerHexEditText = findViewById(R.id.material_color_hex_edit_text)
        mColorPickerOpacityEditText = findViewById(R.id.material_color_seek_bar_opacity_value_edit_view)
        mColorPickerSaturationEditText = findViewById(R.id.material_color_seek_bar_saturation_value_edit_view)
        mColorPickerTabSpectrumText = findViewById(R.id.material_color_picker_spectrum_text_view)
        mColorPickerTabSwatchesText = findViewById(R.id.material_color_picker_swatches_text_view)
        mColorSpectrumView = findViewById(R.id.material_color_picker_color_spectrum_view)
        mColorSwatchView = findViewById(R.id.material_color_picker_color_swatch_view)
        mCurrentColorView = findViewById(R.id.material_color_picker_current_color_view)
        mGradientColorSeekBar = findViewById(R.id.material_color_picker_saturation_seekbar)
        mGradientSeekBarContainer = findViewById(R.id.material_color_picker_saturation_layout)
        mOpacityLayout = findViewById(R.id.material_color_picker_opacity_layout)
        mOpacitySeekBar = findViewById(R.id.material_color_picker_opacity_seekbar)
        mOpacitySeekBarContainer = findViewById(R.id.material_color_picker_opacity_seekbar_container)
        mPickedColorView = findViewById(R.id.material_color_picker_picked_color_view)
        mRecentColorListLayout = findViewById(R.id.material_color_picker_used_color_item_list_layout)
        mSpectrumViewContainer = findViewById(R.id.material_color_picker_color_spectrum_view_container)
        mSwatchViewContainer = findViewById(R.id.material_color_picker_color_swatch_view_container)
        mTabLayoutContainer = findViewById(R.id.material_color_picker_tab_layout)

        initDialogPadding()
        initCurrentColorView()
        initColorSwatchView()
        initGradientColorSeekBar()
        initColorSpectrumView()
        initOpacitySeekBar(false)
        initRecentColorLayout()
        updateCurrentColor()
        setInitialColors()
        initCurrentColorValuesLayout()
    }

    private fun initDialogPadding() {
        fun isContains(dp: Int): Boolean {
            for (i in mSmallestWidthDp) {
                if (dp == i) {
                    return true
                }
            }
            return false
        }

        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            val metrics: DisplayMetrics = resources.displayMetrics
            val density = metrics.density

            if (density % 1.0f != 0.0f) {
                val width = metrics.widthPixels.toFloat()
                if (isContains((width / density).toInt())) {
                    val seekBarWidth: Int =
                        resources.getDimensionPixelSize(R.dimen.material_color_picker_seekbar_width)
                    if (width < (((resources.getDimensionPixelSize(R.dimen.material_color_picker_oneui_3_dialog_padding_left) * 2) + seekBarWidth).toFloat())) {
                        val padding = ((width - (seekBarWidth.toFloat())) / 2.0f).toInt()
                        (findViewById<LinearLayout>(R.id.material_color_picker_main_content_container)).setPadding(
                            padding,
                            resources.getDimensionPixelSize(R.dimen.material_color_picker_oneui_3_dialog_padding_top),
                            padding,
                            resources.getDimensionPixelSize(R.dimen.material_color_picker_oneui_3_dialog_padding_bottom)
                        )
                    }
                }
            }
        }
    }

    private fun initCurrentColorView() {
        mColorPickerOpacityEditText.setPrivateImeOptions("disableDirectWriting=true;")
        mColorPickerSaturationEditText.setPrivateImeOptions("disableDirectWriting=true;")
        mColorPickerTabSwatchesText.setBackgroundResource(R.drawable.material_color_picker_tab_selector_bg)
        mColorPickerTabSwatchesText.setTextAppearance(R.style.TabTextSelected)
        mColorPickerTabSwatchesText.setTextColor(resources.getColor(R.color.material_tablayout_subtab_background_stroke_color, null))
        mColorPickerTabSpectrumText.setTextColor(resources.getColor(R.color.material_secondary_text_color, null))
        mColorPickerOpacityEditText.tag = 1
        mFlagVar = true
        mSelectedColorBackground = this.mPickedColorView.background as GradientDrawable?

        if (mPickedColor.color != null) {
            mSelectedColorBackground!!.setColor(mPickedColor.color!!)
        }
        mCurrentColorBackground = mCurrentColorView.background as GradientDrawable
        mColorPickerTabSwatchesText.setOnClickListener(this)
        mColorPickerTabSpectrumText.setOnClickListener(this)

        mColorPickerOpacityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val i = if (s.toString().isEmpty()) 0 else s.toString().toInt()
                if (mOpacitySeekBar != null && s.toString()
                        .trim { it <= ' ' }.isNotEmpty() && i <= 100
                ) {
                    mColorPickerOpacityEditText.tag = 0
                    mOpacitySeekBar.progress = (i * 255) / 100
                }
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    if (s.toString().toInt() > 100) {
                        mColorPickerOpacityEditText.setText(
                            "" + String.format(
                                Locale.getDefault(),
                                "%d",
                                100
                            )
                        )
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    mColorPickerOpacityEditText.setText("0")
                }
                mColorPickerOpacityEditText.setSelection(mColorPickerOpacityEditText.getText().length)
            }
        })
        mColorPickerOpacityEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!mColorPickerOpacityEditText.hasFocus() && mColorPickerOpacityEditText.getText()
                    .toString().isEmpty()
            ) {
                mColorPickerOpacityEditText.setText(
                    "" + String.format(
                        Locale.getDefault(),
                        "%d",
                        0
                    )
                )
            }
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.material_color_picker_swatches_text_view) {
            mColorPickerTabSwatchesText.setSelected(true)
            mColorPickerTabSwatchesText.setBackgroundResource(R.drawable.material_color_picker_tab_selector_bg)
            mColorPickerTabSpectrumText.setSelected(false)
            mColorPickerTabSpectrumText.setBackgroundResource(0)
            mColorPickerTabSwatchesText.setTextColor(resources.getColor(R.color.material_tablayout_subtab_background_stroke_color, null))
            mColorPickerTabSwatchesText.setTextAppearance(R.style.TabTextSelected)
            mColorPickerTabSpectrumText.setTextColor(resources.getColor(R.color.material_secondary_text_color, null))
            mColorPickerTabSpectrumText.setTypeface(Typeface.DEFAULT)

            mSwatchViewContainer.visibility = VISIBLE
            mSpectrumViewContainer.visibility = GONE

            if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || context.isTablet()) {
                mGradientSeekBarContainer.visibility = GONE
            } else {
                mGradientSeekBarContainer.visibility = INVISIBLE
            }

        } else if (v.id == R.id.material_color_picker_spectrum_text_view) {
            mColorPickerTabSwatchesText.setSelected(false)
            mColorPickerTabSpectrumText.setSelected(true)
            mColorPickerTabSpectrumText.setBackgroundResource(R.drawable.material_color_picker_tab_selector_bg)
            mColorPickerTabSwatchesText.setBackgroundResource(0)
            initColorSpectrumView()
            mColorPickerTabSpectrumText.setTextColor(resources.getColor(R.color.material_tablayout_subtab_background_stroke_color, null))
            mColorPickerTabSpectrumText.setTextAppearance(R.style.TabTextSelected)
            mColorPickerTabSwatchesText.setTextColor(resources.getColor(R.color.material_secondary_text_color, null))
            mColorPickerTabSwatchesText.setTypeface(Typeface.DEFAULT)

            mSwatchViewContainer.visibility = GONE
            mSpectrumViewContainer.visibility = VISIBLE

            mGradientSeekBarContainer.visibility = VISIBLE
        }
    }

    private fun initColorSpectrumView() {
        mColorPickerSaturationEditText.setText(
            "" + java.lang.String.format(
                Locale.getDefault(),
                "%d",
                mGradientColorSeekBar.progress
            )
        )

        mColorSpectrumView.colorChangedListener = ColorSpectrumView.Listener { newHue, newSaturation ->
            mIsInputFromUser = true
            try {
                (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                    windowToken,
                    InputMethodManager.RESULT_UNCHANGED_SHOWN
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mPickedColor.setHS(newHue, newSaturation, mOpacitySeekBar.progress)
            updateCurrentColor()
            updateHexAndRGBValues(mPickedColor.color!!)
        }

        mColorPickerSaturationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!mTextFromRGB) {
                    try {
                        if (mGradientColorSeekBar != null && s.toString()
                                .trim { it <= ' ' }.isNotEmpty()
                        ) {
                            val i = s.toString().toInt()
                            mfromEditText = true
                            mFlagVar = false
                            if (i <= 100) {
                                mColorPickerSaturationEditText.tag = 0
                                mGradientColorSeekBar.progress = i
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (!mTextFromRGB) {
                    try {
                        if (s.toString().toInt() > 100) {
                            mColorPickerSaturationEditText.setText(
                                "" + String.format(
                                    Locale.getDefault(),
                                    "%d",
                                    100
                                )
                            )
                        }
                    } catch (e: java.lang.NumberFormatException) {
                        e.printStackTrace()
                        mColorPickerSaturationEditText.setText("0")
                    }
                    mColorPickerSaturationEditText.setSelection(mColorPickerSaturationEditText.getText().length)
                }
            }
        })
        mColorPickerSaturationEditText.setOnFocusChangeListener(object : OnFocusChangeListener {
            override fun onFocusChange(v: View?, hasFocus: Boolean) {
                if (!mColorPickerSaturationEditText.hasFocus() && mColorPickerSaturationEditText.getText()
                        .toString().isEmpty()
                ) {
                    mColorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            0
                        )
                    )
                }
            }
        })
    }

    private fun initColorSwatchView() {
        mColorSwatchView.onColorSwatchChangedListener =
            ColorSwatchView.OnColorSwatchChangedListener { newColor ->
                mIsInputFromUser = true
                try {
                    (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(
                        windowToken,
                        InputMethodManager.RESULT_UNCHANGED_SHOWN
                    )
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                mPickedColor.setColorWithAlpha(newColor, mOpacitySeekBar.progress)
                updateCurrentColor()
                updateHexAndRGBValues(newColor)
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGradientColorSeekBar() {

        mGradientColorSeekBar.init(mPickedColor.color)
        mGradientColorSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mIsInputFromUser = true
                    mfromSaturationSeekbar = true
                }
                val f = (seekBar.progress.toFloat()) / (seekBar.max.toFloat())
                if (progress >= 0 && mFlagVar) {
                    mColorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            progress
                        )
                    )
                    mColorPickerSaturationEditText.setSelection(progress.toString().length)
                }
                if (mfromRGB) {
                    mTextFromRGB = true
                    mColorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            progress
                        )
                    )
                    mColorPickerSaturationEditText.setSelection(progress.toString().length)
                    mTextFromRGB = false
                }
                mPickedColor.v = f
                val i: Int = mPickedColor.color!!
                if (mfromEditText) {
                    updateHexAndRGBValues(i)
                    mfromEditText = false
                }
                mSelectedColorBackground?.setColor(i)
                mOpacitySeekBar?.changeColorBase(i, mPickedColor.alpha)
                mOnColorChangedListener?.onColorChanged(i)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                mfromSaturationSeekbar = false
            }
        })
        mGradientColorSeekBar!!.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                mFlagVar = true

                val action = event.getAction()
                when (action) {
                    MotionEvent.ACTION_DOWN -> {
                        mGradientColorSeekBar!!.setSelected(true)
                        return true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        mGradientColorSeekBar!!.setSelected(false)
                        return false
                    }

                    else -> return false
                }
            }
        })
        (findViewById<FrameLayout>(R.id.material_color_picker_saturation_seekbar_container)).setContentDescription(
            resources.getString(R.string.material_color_picker_hue_and_saturation) + ", " + resources.getString(R.string.material_color_picker_slider) + ", " + resources.getString(R.string.material_color_picker_double_tap_to_select)
        )
    }

    fun initOpacitySeekBar(enable: Boolean) {
        mOpacityLayout.visibility = if (enable) VISIBLE else GONE
        if (!mIsOpacityBarEnabled) {
            mOpacitySeekBar.visibility = GONE
            mOpacitySeekBarContainer.visibility = GONE
        }
        mOpacitySeekBar.init(mPickedColor.color)
        mOpacitySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    mIsInputFromUser = true
                }
                mPickedColor.alpha = progress
                if (progress >= 0 && mColorPickerOpacityEditText.tag.toString().toInt() == 1) {
                    mColorPickerOpacityEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            ceil((((progress * 100).toFloat()) / 255.0f).toDouble()).toInt()
                        )
                    )
                }
                if (mPickedColor.color != null) {
                    mSelectedColorBackground?.setColor(mPickedColor.color!!)
                    mOnColorChangedListener?.onColorChanged(mPickedColor.color!!)
                }
            }
        })
        mOpacitySeekBar!!.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                mColorPickerOpacityEditText.setTag(1)
                return event.getAction() == MotionEvent.ACTION_DOWN
            }
        })
        mOpacitySeekBarContainer.setContentDescription(
            resources.getString(R.string.material_color_picker_opacity) + ", " + resources.getString(R.string.material_color_picker_slider) + ", " + resources.getString(R.string.material_color_picker_double_tap_to_select)
        )
    }

    private fun initRecentColorLayout() {
        mColorDescription = arrayOf(
            resources.getString(R.string.material_color_picker_color_one),
            resources.getString(R.string.material_color_picker_color_two),
            resources.getString(R.string.material_color_picker_color_three),
            resources.getString(R.string.material_color_picker_color_four),
            resources.getString(R.string.material_color_picker_color_five),
            resources.getString(R.string.material_color_picker_color_six),
            resources.getString(R.string.material_color_picker_color_seven)
        )
        if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || context.isTablet()) {
            RECENT_COLOR_SLOT_COUNT = 6
        } else {
            RECENT_COLOR_SLOT_COUNT = 7
        }
        for (i in 0..<RECENT_COLOR_SLOT_COUNT) {
            val child = mRecentColorListLayout.getChildAt(i)
            setImageColor(
                child,
                ContextCompat.getColor(
                    context,
                    R.color.material_color_picker_used_color_item_empty_slot_color
                )
            )
            child.isFocusable = false
            child.isClickable = false
        }
    }

    private fun updateCurrentColor() {
        val color: Int? = mPickedColor.color
        if (color != null) {
            if (mOpacitySeekBar != null) {
                mOpacitySeekBar.changeColorBase(color, mPickedColor.alpha)
                mColorPickerOpacityEditText.setText(
                    "" + String.format(
                        Locale.getDefault(),
                        "%d",
                        Integer.valueOf(mOpacitySeekBar.progress)
                    )
                )
                mColorPickerOpacityEditText.setSelection(mColorPickerOpacityEditText.getText().length)
            }

            if (mSelectedColorBackground != null) {
                mSelectedColorBackground!!.setColor(color)
                setCurrentColorViewDescription(color, 1)
            }

            if (mOnColorChangedListener != null) {
                mOnColorChangedListener!!.onColorChanged(color)
            }

            if (mColorSpectrumView != null) {
                mColorSpectrumView.updateCursorColor(color)
                mColorSpectrumView.setColor(color)
            }

            if (mGradientColorSeekBar != null) {
                mGradientColorSeekBar.changeColorBase(color)
                mfromSpectrumTouch = true
                mColorPickerSaturationEditText.setText(
                    "" + String.format(
                        Locale.getDefault(),
                        "%d",
                        Integer.valueOf(mGradientColorSeekBar.progress)
                    )
                )
                mColorPickerSaturationEditText.setSelection(mGradientColorSeekBar.progress.toString().length)
                mfromSpectrumTouch = false
            }
        }
    }

    private fun setInitialColors() {
        if (mPickedColor.color != null) {
            mapColorOnColorWheel(mPickedColor.color!!)
        }
    }

    private fun initCurrentColorValuesLayout() {
        mColorPickerGreenEditText = findViewById(R.id.material_color_green_edit_text)
        mColorPickerRedEditText.setPrivateImeOptions("disableDirectWriting=true;")
        mColorPickerBlueEditText.setPrivateImeOptions("disableDirectWriting=true;")
        mColorPickerGreenEditText.setPrivateImeOptions("disableDirectWriting=true;")
        editTexts.add(mColorPickerRedEditText)
        editTexts.add(mColorPickerGreenEditText)
        editTexts.add(mColorPickerBlueEditText)
        setTextWatcher()
        mColorPickerBlueEditText.setOnEditorActionListener(object : OnEditorActionListener {
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mColorPickerBlueEditText.clearFocus()
                }
                return false
            }
        })
    }

    private fun setTextWatcher() {
        mColorPickerHexEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val length = s.toString().trim { it <= ' ' }.length
                if (length == 6) {
                    val color = "#$s".toColorInt()
                    if (!mColorPickerRedEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.red(color), ignoreCase = true)) {
                        mColorPickerRedEditText.setText("" + Color.red(color))
                    }
                    if (!mColorPickerGreenEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.green(color), ignoreCase = true)) {
                        mColorPickerGreenEditText.setText("" + Color.green(color))
                    }
                    if (!mColorPickerBlueEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.blue(color), ignoreCase = true)) {
                        mColorPickerBlueEditText.setText("" + Color.blue(color))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                mIsInputFromUser = true
            }
        })

        beforeValue = ""

        for (editText in editTexts) {
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    beforeValue = s.toString().trim { it <= ' ' }
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (!s.toString().equals(beforeValue, ignoreCase = true) && s.toString()
                            .trim { it <= ' ' }.length > 0
                    ) {
                        updateHexData()
                    }
                }

                // TODO: WTF!
                override fun afterTextChanged(s: Editable) {
                    try {
                        if (s.toString().toInt() > 255) {
                            if (editText === editTexts.get(0)) {
                                mColorPickerRedEditText.setText("255")
                            }
                            if (editText === editTexts.get(1)) {
                                mColorPickerGreenEditText.setText("255")
                            }
                            if (editText === editTexts.get(2)) {
                                mColorPickerBlueEditText.setText("255")
                            }
                        }
                    } catch (e: java.lang.NumberFormatException) {
                        e.printStackTrace()
                        if (editText === editTexts.get(0)) {
                            mColorPickerRedEditText.setText("0")
                        }
                        if (editText === editTexts.get(1)) {
                            mColorPickerGreenEditText.setText("0")
                        }
                        if (editText === editTexts.get(2)) {
                            mColorPickerBlueEditText.setText("0")
                        }
                    }
                    mIsInputFromUser = true
                    mfromRGB = true
                    mColorPickerRedEditText.setSelection(mColorPickerRedEditText.getText().length)
                    mColorPickerGreenEditText.setSelection(mColorPickerGreenEditText.getText().length)
                    mColorPickerBlueEditText.setSelection(mColorPickerBlueEditText.getText().length)
                }
            })
        }
    }

    private fun updateHexData() {
        val red = (if (mColorPickerRedEditText.getText().toString()
                .trim { it <= ' ' }.isNotEmpty()
        ) mColorPickerRedEditText.getText().toString().trim { it <= ' ' } else "0").toInt()
        val green = (if (mColorPickerGreenEditText.getText().toString()
                .trim { it <= ' ' }.isNotEmpty()
        ) mColorPickerGreenEditText.getText().toString().trim { it <= ' ' } else "0").toInt()
        val blue = (if (mColorPickerBlueEditText.getText().toString()
                .trim { it <= ' ' }.isNotEmpty()
        ) mColorPickerBlueEditText.getText().toString().trim { it <= ' ' } else "0").toInt()

        val color =
            ((red and 255) shl 16) or ((mOpacitySeekBar.progress and 255) shl 24) or ((green and 255) shl 8) or (blue and 255)
        val colorStr = String.format("%08x", color)

        mColorPickerHexEditText.setText(
            "" + colorStr.substring(2, colorStr.length).uppercase(Locale.getDefault())
        )
        mColorPickerHexEditText.setSelection(mColorPickerHexEditText.getText().length)
        if (!mfromSaturationSeekbar && !mfromSpectrumTouch) {
            mapColorOnColorWheel(color)
        }

        if (mOnColorChangedListener != null) {
            mOnColorChangedListener!!.onColorChanged(color)
        }
    }

    fun setOnlySpectrumMode() {
        mTabLayoutContainer.visibility = GONE

        initColorSpectrumView()
        if (!mIsSpectrumSelected) {
            mIsSpectrumSelected = true
        }
        mSwatchViewContainer.visibility = GONE
        mSpectrumViewContainer.visibility = VISIBLE
        mColorPickerHexEditText.setInputType(InputType.TYPE_NULL)
        mColorPickerRedEditText.setInputType(InputType.TYPE_NULL)
        mColorPickerBlueEditText.setInputType(InputType.TYPE_NULL)
        mColorPickerGreenEditText.setInputType(InputType.TYPE_NULL)
    }

    fun updateRecentColorLayout() {
        val recentColorValues = if (mRecentColorValues != null) mRecentColorValues.size else 0
        val description = ", " + resources.getString(R.string.material_color_picker_option)
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            RECENT_COLOR_SLOT_COUNT = 7
        } else {
            RECENT_COLOR_SLOT_COUNT = 6
        }
        for (i in 0..<RECENT_COLOR_SLOT_COUNT) {
            val child = mRecentColorListLayout.getChildAt(i)
            if (i < recentColorValues) {
                setImageColor(child, mRecentColorValues.get(i))

                val sb = java.lang.StringBuilder()
                sb.append(mColorSwatchView.getColorSwatchDescriptionAt(mRecentColorValues.get(i)!!) as CharSequence?)
                sb.insert(0, mColorDescription!![i] + description + ", ")
                child.setContentDescription(sb)

                child.isFocusable = true
                child.isClickable = true
            }
        }
        if (mRecentColorInfo.getCurrentColor() != null) {
            mCurrentColorBackground!!.setColor(mRecentColorInfo.getCurrentColor()!!)
            setCurrentColorViewDescription(mRecentColorInfo.getCurrentColor()!!, 0)
            mSelectedColorBackground!!.setColor(mRecentColorInfo.getCurrentColor()!!)
            mapColorOnColorWheel(mRecentColorInfo.getCurrentColor()!!)
            updateHexAndRGBValues(mCurrentColorBackground!!.color!!.defaultColor)
        } else if (recentColorValues != 0) {
            mCurrentColorBackground!!.setColor(mRecentColorValues[0]!!)
            setCurrentColorViewDescription(mRecentColorValues[0]!!, 0)
            mSelectedColorBackground!!.setColor(mRecentColorValues[0]!!)
            mapColorOnColorWheel(mRecentColorValues[0]!!)
            updateHexAndRGBValues(mCurrentColorBackground!!.color!!.defaultColor)
        }
        if (mRecentColorInfo.getNewColor() != null) {
            mSelectedColorBackground!!.setColor(mRecentColorInfo.getNewColor()!!)
            mapColorOnColorWheel(mRecentColorInfo.getNewColor()!!)
            updateHexAndRGBValues(mSelectedColorBackground!!.color!!.defaultColor)
        }
    }


    private fun setImageColor(view: View, num: Int?) {
        val gradientDrawable =
            ContextCompat.getDrawable(context, R.drawable.material_color_picker_used_color_item_slot) as GradientDrawable?
        if (num != null) {
            gradientDrawable!!.setColor(num)
        }
        view.background = RippleDrawable(
            ColorStateList(
                arrayOf<IntArray?>(IntArray(0)),
                intArrayOf(Color.argb(61, 0, 0, 0))
            ), gradientDrawable, null
        )
        view.setOnClickListener(mImageButtonClickListener)
    }

    private fun mapColorOnColorWheel(i: Int) {
        mPickedColor?.color = i

        mColorSwatchView?.updateCursorPosition(i)

        mColorSpectrumView?.setColor(i)

        mGradientColorSeekBar?.restoreColor(i)

        mOpacitySeekBar?.restoreColor(i)

        if (mSelectedColorBackground != null) {
            mSelectedColorBackground!!.setColor(i)
            setCurrentColorViewDescription(i, 1)
        }

        if (mColorSpectrumView != null) {
            val v: Float = mPickedColor.v
            val alpha: Int = mPickedColor.alpha
            mPickedColor.v = 1.0f
            mPickedColor.alpha = 255
            mColorSpectrumView.updateCursorColor(mPickedColor.color!!)
            mPickedColor.v = v
            mPickedColor.alpha = alpha
        }

        if (mOpacitySeekBar != null) {
            val ceil = ceil((((mOpacitySeekBar.progress * 100).toFloat()) / 255.0f).toDouble()).toInt()
            mColorPickerOpacityEditText.setText("" + String.format(Locale.getDefault(), "%d", ceil)
            )
            mColorPickerOpacityEditText.setSelection(ceil.toString().length)
        }
    }

    private fun setCurrentColorViewDescription(i: Int, i2: Int) {
        val sb = StringBuilder()
        val colorSwatchDescriptionAt: StringBuilder? =
            mColorSwatchView.getColorSwatchDescriptionAt(i)
        if (colorSwatchDescriptionAt != null) {
            sb.append(", ").append(colorSwatchDescriptionAt as CharSequence)
        }
        if (i2 == 0) {
            sb.insert(0, resources.getString(R.string.material_color_picker_current))
        } else if (i2 == 1) {
            sb.insert(0, resources.getString(R.string.material_color_picker_new))
        }
    }

    fun saveSelectedColor() {
        if (mPickedColor?.color != null) {
            mRecentColorInfo.saveSelectedColor(mPickedColor.color!!)
        }
    }

    fun getRecentColorInfo(): RecentColorInfo {
        return mRecentColorInfo
    }

    fun isUserInputValid(): Boolean {
        return mIsInputFromUser
    }

    fun setOpacityBarEnabled(z: Boolean) {
        mIsOpacityBarEnabled = z
        if (z) {
            mOpacitySeekBar.visibility = VISIBLE
            mOpacitySeekBarContainer.visibility = VISIBLE
        }
    }

    private fun updateHexAndRGBValues(i: Int) {
        if (i != 0) {
            val format = String.format("%08x", i)
            val substring = format.substring(2, format.length)
            mColorPickerHexEditText.setText("" + substring.uppercase(Locale.getDefault()))
            mColorPickerHexEditText.setSelection(mColorPickerHexEditText.getText().length)

            val color = "#$substring".toColorInt()
            mColorPickerRedEditText.setText("" + Color.red(color))
            mColorPickerBlueEditText.setText("" + Color.blue(color))
            mColorPickerGreenEditText.setText("" + Color.green(color))
        }
    }

    inner class RecentColorInfo {
        private var mSelectedColor: Int? = null
        private var mCurrentColor: Int? = null
        private var mNewColor: Int? = null
        private val mRecentColorInfo = java.util.ArrayList<Int?>()

        fun getRecentColorInfo(): java.util.ArrayList<Int?> {
            return mRecentColorInfo
        }

        fun getCurrentColor(): Int? {
            return mCurrentColor
        }

        fun getNewColor(): Int? {
            return mNewColor
        }

        fun getSelectedColor(): Int? {
            return mSelectedColor
        }

        fun setCurrentColor(num: Int?) {
            mCurrentColor = num
        }

        fun setNewColor(num: Int?) {
            mNewColor = num
        }

        fun saveSelectedColor(i: Int) {
            mSelectedColor = i
        }

        fun initRecentColorInfo(iArr: IntArray?) {
            if (iArr != null) {
                var i = 0
                if (iArr.size <= RECENT_COLOR_SLOT_COUNT) {
                    val length = iArr.size
                    while (i < length) {
                        this.mRecentColorInfo.add(iArr[i])
                        i++
                    }
                    return
                }
                while (i < RECENT_COLOR_SLOT_COUNT) {
                    mRecentColorInfo.add(iArr[i])
                    i++
                }
            }
        }
    }

    class PickedColor {
        private var mColor: Int? = null
        private var mAlpha = 255
        private val mHsv = FloatArray(3)

        fun setColorWithAlpha(i: Int, i2: Int) {
            mColor = i
            mAlpha = ceil((((i2 * 100).toFloat()) / 255.0f).toDouble()).toInt()
            Color.colorToHSV(mColor!!, mHsv)
        }

        var color: Int?
            get() = mColor
            set(i) {
                mColor = i
                mAlpha = Color.alpha(i!!)
                Color.colorToHSV(mColor!!, mHsv)
            }

        fun setHS(f: Float, f2: Float, i: Int) {
            val fArr = mHsv
            fArr[0] = f
            fArr[1] = f2
            fArr[2] = 1.0f
            mColor = Color.HSVToColor(mAlpha, fArr)
            mAlpha = ceil((((i * 100).toFloat()) / 255.0f).toDouble()).toInt()
        }

        var v: Float
            get() = mHsv[2]
            set(f) {
                val fArr = mHsv
                fArr[2] = f
                mColor = Color.HSVToColor(mAlpha, fArr)
            }

        var alpha: Int
            get() = mAlpha
            set(i) {
                mAlpha = i
                mColor = Color.HSVToColor(i, mHsv)
            }
    }

    fun interface OnColorChangedListener {
        fun onColorChanged(newColor: Int)
    }

}