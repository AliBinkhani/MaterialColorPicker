package com.hooshkar.materialcolorpicker.views

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.RippleDrawable
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import com.hooshkar.materialcolorpicker.R
import com.hooshkar.materialcolorpicker.isTablet
import java.util.Locale
import kotlin.math.ceil
import androidx.core.graphics.toColorInt
import com.google.android.material.button.MaterialButtonToggleGroup

@SuppressLint("SetTextI18n")
class MaterialColorPickerView: LinearLayout {
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

    private var recentColorSlotCount: Int = 6
    private var beforeValue: String? = null

    private val colorPickerRedEditText: EditText
    private var colorPickerGreenEditText: EditText //TODO: VAR???
    private val colorPickerBlueEditText: EditText
    private val colorPickerHexEditText: EditText
    private val colorPickerOpacityEditText: EditText
    private val colorPickerSaturationEditText: EditText
    private val colorPickerTabSpectrumText: Button
    private val colorPickerTabSwatchesText: Button
    private val colorSpectrumView: ColorSpectrumView
    private val colorSwatchView: ColorSwatchView
    private val currentColorView: View
    private val pickedColorView: View
    private val gradientColorSeekBar: MaterialGradientColorSeekBar
    private val gradientSeekBarContainer: LinearLayout
    private val opacityLayout: LinearLayout
    private val opacitySeekBar: MaterialOpacitySeekBar
    private val opacitySeekBarContainer: FrameLayout
    private val recentColorListLayout: LinearLayout
    private val spectrumViewContainer: FrameLayout
    private val swatchViewContainer: FrameLayout
    private val tabLayoutContainer: MaterialButtonToggleGroup
    private val currentColorBackground: GradientDrawable
    private var flagVar = false
    private val pickedColor: PickedColor = PickedColor()
    private val recentColorInfo: RecentColorInfo = RecentColorInfo()
    private val recentColorValues: ArrayList<Int> = recentColorInfo.recentColorInfo
    private val selectedColorBackground: GradientDrawable
    private val smallestWidthDp = intArrayOf(320, 360, 411)
    private var isInputFromUser = false
    private var isOpacityBarEnabled = false
    private var isSpectrumSelected: Boolean = false
    private val editTexts: List<EditText>
    private var colorDescription: Array<String?>? = null
    private var fromEditText = false
    private var fromSaturationSeekbar = false
    private var fromSpectrumTouch = false
    private var fromRGB = false
    private var textFromRGB = false

    var onColorChangedListener: OnColorChangedListener? = null

    private val imageButtonClickListener: OnClickListener = OnClickListener { v ->
        var i = 0
        while (i < recentColorValues.size && i < recentColorSlotCount) {
            if (recentColorListLayout.getChildAt(i) == v) {
                isInputFromUser = true

                val intValue: Int = recentColorValues[i]
                //pickedColor.color = intValue
                mapColorOnColorWheel(intValue)
                updateHexAndRGBValues(intValue)

                val progress = gradientColorSeekBar.progress
                colorPickerSaturationEditText.setText(
                    "" + String.format(Locale.getDefault(), "%d", progress)
                )
                colorPickerSaturationEditText.setSelection(progress.toString().length)

                onColorChangedListener?.onColorChanged(intValue)
            }
            i++
        }
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.material_color_picker_oneui_3_layout, this)

        colorPickerRedEditText = findViewById(R.id.material_color_red_edit_text)
        colorPickerGreenEditText = findViewById(R.id.material_color_green_edit_text)
        colorPickerBlueEditText = findViewById(R.id.material_color_blue_edit_text)
        colorPickerHexEditText = findViewById(R.id.material_color_hex_edit_text)
        colorPickerOpacityEditText = findViewById(R.id.material_color_seek_bar_opacity_value_edit_view)
        colorPickerSaturationEditText = findViewById(R.id.material_color_seek_bar_saturation_value_edit_view)
        colorPickerTabSpectrumText = findViewById(R.id.material_color_picker_spectrum_text_view)
        colorPickerTabSwatchesText = findViewById(R.id.material_color_picker_swatches_text_view)
        colorSpectrumView = findViewById(R.id.material_color_picker_color_spectrum_view)
        colorSwatchView = findViewById(R.id.material_color_picker_color_swatch_view)
        currentColorView = findViewById(R.id.material_color_picker_current_color_view)
        gradientColorSeekBar = findViewById(R.id.material_color_picker_saturation_seekbar)
        gradientSeekBarContainer = findViewById(R.id.material_color_picker_saturation_layout)
        opacityLayout = findViewById(R.id.material_color_picker_opacity_layout)
        opacitySeekBar = findViewById(R.id.material_color_picker_opacity_seekbar)
        opacitySeekBarContainer = findViewById(R.id.material_color_picker_opacity_seekbar_container)
        pickedColorView = findViewById(R.id.material_color_picker_picked_color_view)
        recentColorListLayout = findViewById(R.id.material_color_picker_used_color_item_list_layout)
        spectrumViewContainer = findViewById(R.id.material_color_picker_color_spectrum_view_container)
        swatchViewContainer = findViewById(R.id.material_color_picker_color_swatch_view_container)
        tabLayoutContainer = findViewById(R.id.material_color_picker_tab_layout)

        currentColorBackground = currentColorView.background as GradientDrawable
        selectedColorBackground = this.pickedColorView.background as GradientDrawable

        editTexts = listOf(colorPickerRedEditText, colorPickerGreenEditText, colorPickerBlueEditText)

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
            for (i in smallestWidthDp) {
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
        colorPickerOpacityEditText.setPrivateImeOptions("disableDirectWriting=true;")
        colorPickerSaturationEditText.setPrivateImeOptions("disableDirectWriting=true;")
        colorPickerOpacityEditText.tag = 1
        flagVar = true

        if (pickedColor.color != null) {
            selectedColorBackground.setColor(pickedColor.color!!)
        }

        tabLayoutContainer.check(R.id.material_color_picker_swatches_text_view)
        tabLayoutContainer.addOnButtonCheckedListener { toggleButton, checkedId, isChecked ->
            if (!isChecked) return@addOnButtonCheckedListener

            when (checkedId) {
                R.id.material_color_picker_swatches_text_view -> {
                    swatchViewContainer.visibility = VISIBLE
                    spectrumViewContainer.visibility = GONE

                    if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || context.isTablet()) {
                        gradientSeekBarContainer.visibility = GONE
                    } else {
                        gradientSeekBarContainer.visibility = INVISIBLE
                    }
                }

                R.id.material_color_picker_spectrum_text_view -> {
                    initColorSpectrumView()
                    swatchViewContainer.visibility = GONE
                    spectrumViewContainer.visibility = VISIBLE
                    gradientSeekBarContainer.visibility = VISIBLE
                }
            }
        }

        colorPickerOpacityEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val i = if (s.toString().isEmpty()) 0 else s.toString().toInt()
                if (s.toString().trim { it <= ' ' }.isNotEmpty() && i <= 100
                ) {
                    colorPickerOpacityEditText.tag = 0
                    opacitySeekBar.progress = (i * 255) / 100
                }
            }

            override fun afterTextChanged(s: Editable) {
                try {
                    if (s.toString().toInt() > 100) {
                        colorPickerOpacityEditText.setText(
                            "" + String.format(
                                Locale.getDefault(),
                                "%d",
                                100
                            )
                        )
                    }
                } catch (e: NumberFormatException) {
                    e.printStackTrace()
                    colorPickerOpacityEditText.setText("0")
                }
                colorPickerOpacityEditText.setSelection(colorPickerOpacityEditText.getText().length)
            }
        })
        colorPickerOpacityEditText.onFocusChangeListener = OnFocusChangeListener { v, hasFocus ->
            if (!colorPickerOpacityEditText.hasFocus() && colorPickerOpacityEditText.getText()
                    .toString().isEmpty()
            ) {
                colorPickerOpacityEditText.setText(
                    "" + String.format(
                        Locale.getDefault(),
                        "%d",
                        0
                    )
                )
            }
        }
    }

    private fun initColorSpectrumView() {
        colorPickerSaturationEditText.setText(
            "" + java.lang.String.format(
                Locale.getDefault(),
                "%d",
                gradientColorSeekBar.progress
            )
        )

        colorSpectrumView.colorChangedListener = ColorSpectrumView.Listener { newHue, newSaturation ->
            isInputFromUser = true
            try {
                val imm = context.getSystemService<InputMethodManager>()
                imm?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            pickedColor.setHS(newHue, newSaturation, opacitySeekBar.progress)
            updateCurrentColor()
            updateHexAndRGBValues(pickedColor.color!!)
        }

        colorPickerSaturationEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (!textFromRGB) {
                    try {
                        if (s.toString().trim { it <= ' ' }.isNotEmpty()) {
                            val i = s.toString().toInt()
                            fromEditText = true
                            flagVar = false
                            if (i <= 100) {
                                colorPickerSaturationEditText.tag = 0
                                gradientColorSeekBar.progress = i //TODO: cause listener spam in the end.
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            override fun afterTextChanged(s: Editable) {
                if (!textFromRGB) {
                    try {
                        if (s.toString().toInt() > 100) {
                            colorPickerSaturationEditText.setText(
                                "" + String.format(
                                    Locale.getDefault(),
                                    "%d",
                                    100
                                )
                            )
                        }
                    } catch (e: NumberFormatException) {
                        e.printStackTrace()
                        colorPickerSaturationEditText.setText("0")
                    }
                    colorPickerSaturationEditText.setSelection(colorPickerSaturationEditText.getText().length)
                }
            }
        })

        colorPickerSaturationEditText.setOnFocusChangeListener { v, hasFocus ->
            if (!colorPickerSaturationEditText.hasFocus() && colorPickerSaturationEditText.text.toString().isEmpty()) {
                colorPickerSaturationEditText.setText(String.format(Locale.getDefault(), "%d", 0))
            }
        }
    }

    private fun initColorSwatchView() {
        colorSwatchView.onColorSwatchChangedListener =
            ColorSwatchView.OnColorSwatchChangedListener { newColor ->
                isInputFromUser = true
                try {
                    val imm = context.getSystemService<InputMethodManager>()
                    imm?.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }
                pickedColor.setColorWithAlpha(newColor, opacitySeekBar.progress)
                updateCurrentColor()
                updateHexAndRGBValues(newColor)
            }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initGradientColorSeekBar() {
        gradientColorSeekBar.init(pickedColor.color)
        gradientColorSeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isInputFromUser = true
                    fromSaturationSeekbar = true
                }
                val f = (seekBar.progress.toFloat()) / (seekBar.max.toFloat())
                if (progress >= 0 && flagVar) {
                    colorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            progress
                        )
                    )
                    colorPickerSaturationEditText.setSelection(progress.toString().length)
                }
                if (fromRGB) {
                    textFromRGB = true
                    colorPickerSaturationEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            progress
                        )
                    )
                    colorPickerSaturationEditText.setSelection(progress.toString().length)
                    textFromRGB = false
                }
                pickedColor.v = f
                val i: Int = pickedColor.color!!
                if (fromEditText) {
                    updateHexAndRGBValues(i)
                    fromEditText = false
                }
                selectedColorBackground.setColor(i)
                opacitySeekBar.changeColorBase(i, pickedColor.alpha)
                onColorChangedListener?.onColorChanged(i)
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                fromSaturationSeekbar = false
            }
        })
        gradientColorSeekBar.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent): Boolean {
                flagVar = true

                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        gradientColorSeekBar.isSelected = true
                        return true
                    }

                    MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                        gradientColorSeekBar.isSelected = false
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

    @SuppressLint("ClickableViewAccessibility")
    fun initOpacitySeekBar(enable: Boolean) {
        opacityLayout.visibility = if (enable) VISIBLE else GONE
        if (!isOpacityBarEnabled) {
            opacitySeekBar.visibility = GONE
            opacitySeekBarContainer.visibility = GONE
        }
        opacitySeekBar.init(pickedColor.color)
        opacitySeekBar.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isInputFromUser = true
                }
                pickedColor.alpha = progress
                if (progress >= 0 && colorPickerOpacityEditText.tag.toString().toInt() == 1) {
                    colorPickerOpacityEditText.setText(
                        "" + String.format(
                            Locale.getDefault(),
                            "%d",
                            ceil((((progress * 100).toFloat()) / 255.0f).toDouble()).toInt()
                        )
                    )
                }
                if (pickedColor.color != null) {
                    selectedColorBackground.setColor(pickedColor.color!!)
                    onColorChangedListener?.onColorChanged(pickedColor.color!!)
                }
            }
        })
        opacitySeekBar.setOnTouchListener { v, event ->
            colorPickerOpacityEditText.tag = 1
            event.action == MotionEvent.ACTION_DOWN
        }
        opacitySeekBarContainer.setContentDescription(
            resources.getString(R.string.material_color_picker_opacity) + ", " + resources.getString(R.string.material_color_picker_slider) + ", " + resources.getString(R.string.material_color_picker_double_tap_to_select)
        )
    }

    private fun initRecentColorLayout() {
        colorDescription = arrayOf(
            resources.getString(R.string.material_color_picker_color_one),
            resources.getString(R.string.material_color_picker_color_two),
            resources.getString(R.string.material_color_picker_color_three),
            resources.getString(R.string.material_color_picker_color_four),
            resources.getString(R.string.material_color_picker_color_five),
            resources.getString(R.string.material_color_picker_color_six),
            resources.getString(R.string.material_color_picker_color_seven)
        )
        recentColorSlotCount =
            if (resources.configuration.orientation != Configuration.ORIENTATION_LANDSCAPE || context.isTablet()) 6 else 7


        for (i in 0..<recentColorSlotCount) {
            val child = recentColorListLayout.getChildAt(i)
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
        val color: Int = pickedColor.color ?: return
        opacitySeekBar.changeColorBase(color, pickedColor.alpha)
        colorPickerOpacityEditText.setText(
            "" + String.format(
                Locale.getDefault(),
                "%d",
                Integer.valueOf(opacitySeekBar.progress)
            )
        )
        colorPickerOpacityEditText.setSelection(colorPickerOpacityEditText.getText().length)


        selectedColorBackground.setColor(color)
        setCurrentColorViewDescription(color, 1)


        onColorChangedListener?.onColorChanged(color)


        colorSpectrumView.updateCursorColor(color)
        colorSpectrumView.setColor(color)



        gradientColorSeekBar.changeColorBase(color)
        fromSpectrumTouch = true
        colorPickerSaturationEditText.setText(
            String.format(
                Locale.getDefault(),
                "%d",
                gradientColorSeekBar.progress
            )
        )
        colorPickerSaturationEditText.setSelection(gradientColorSeekBar.progress.toString().length)
        fromSpectrumTouch = false
    }

    private fun setInitialColors() {
        if (pickedColor.color != null) {
            mapColorOnColorWheel(pickedColor.color!!)
        }
    }

    private fun initCurrentColorValuesLayout() {
        colorPickerGreenEditText = findViewById(R.id.material_color_green_edit_text)
        colorPickerRedEditText.setPrivateImeOptions("disableDirectWriting=true;")
        colorPickerBlueEditText.setPrivateImeOptions("disableDirectWriting=true;")
        colorPickerGreenEditText.setPrivateImeOptions("disableDirectWriting=true;")

        setTextWatcher()
        colorPickerBlueEditText.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                colorPickerBlueEditText.clearFocus()
            }
            false
        }
    }

    private fun setTextWatcher() {
        colorPickerHexEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val length = s.toString().trim { it <= ' ' }.length
                if (length == 6) {
                    val color = "#$s".toColorInt()
                    if (!colorPickerRedEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.red(color), ignoreCase = true)) {
                        colorPickerRedEditText.setText("" + Color.red(color))
                    }
                    if (!colorPickerGreenEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.green(color), ignoreCase = true)) {
                        colorPickerGreenEditText.setText("" + Color.green(color))
                    }
                    if (!colorPickerBlueEditText.getText().toString().trim { it <= ' ' }
                            .equals("" + Color.blue(color), ignoreCase = true)) {
                        colorPickerBlueEditText.setText("" + Color.blue(color))
                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                isInputFromUser = true
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
                    if (!s.toString().equals(beforeValue, ignoreCase = true) && s.toString().trim { it <= ' ' }.isNotEmpty()) {
                        //TODO: Spam results.
                        updateHexData()
                    }
                }

                // TODO: WTF!
                override fun afterTextChanged(s: Editable) {
                    try {
                        if (s.toString().toInt() > 255) {
                            if (editText === editTexts[0]) {
                                colorPickerRedEditText.setText("255")
                            }
                            if (editText === editTexts[1]) {
                                colorPickerGreenEditText.setText("255")
                            }
                            if (editText === editTexts[2]) {
                                colorPickerBlueEditText.setText("255")
                            }
                        }
                    } catch (e: java.lang.NumberFormatException) {
                        e.printStackTrace()
                        if (editText === editTexts[0]) {
                            colorPickerRedEditText.setText("0")
                        }
                        if (editText === editTexts[1]) {
                            colorPickerGreenEditText.setText("0")
                        }
                        if (editText === editTexts[2]) {
                            colorPickerBlueEditText.setText("0")
                        }
                    }
                    isInputFromUser = true
                    fromRGB = true
                    colorPickerRedEditText.setSelection(colorPickerRedEditText.getText().length)
                    colorPickerGreenEditText.setSelection(colorPickerGreenEditText.getText().length)
                    colorPickerBlueEditText.setSelection(colorPickerBlueEditText.getText().length)
                }
            })
        }
    }

    private fun updateHexData() {
        val redTxt = colorPickerRedEditText.text.toString().trim { it <= ' ' }
        val red = if (redTxt.isNotEmpty()) redTxt.toInt() else 0

        val greenTxt = colorPickerGreenEditText.text.toString().trim { it <= ' ' }
        val green = if (greenTxt.isNotEmpty()) greenTxt.toInt() else 0

        val blueTxt = colorPickerBlueEditText.text.toString().trim { it <= ' ' }
        val blue = if (blueTxt.isNotEmpty()) blueTxt.toInt() else 0

        val color =
            ((red and 255) shl 16) or ((opacitySeekBar.progress and 255) shl 24) or ((green and 255) shl 8) or (blue and 255)
        val colorStr = String.format("%08x", color)

        colorPickerHexEditText.setText(colorStr.substring(2, colorStr.length).uppercase(Locale.getDefault()))
        colorPickerHexEditText.setSelection(colorPickerHexEditText.getText().length)
        if (!fromSaturationSeekbar && !fromSpectrumTouch) {
            mapColorOnColorWheel(color)
        }

        onColorChangedListener?.onColorChanged(color)
    }

    fun setOnlySpectrumMode() {
        tabLayoutContainer.visibility = GONE

        initColorSpectrumView()
        if (!isSpectrumSelected) {
            isSpectrumSelected = true
        }
        swatchViewContainer.visibility = GONE
        spectrumViewContainer.visibility = VISIBLE
        colorPickerHexEditText.setInputType(InputType.TYPE_NULL)
        colorPickerRedEditText.setInputType(InputType.TYPE_NULL)
        colorPickerBlueEditText.setInputType(InputType.TYPE_NULL)
        colorPickerGreenEditText.setInputType(InputType.TYPE_NULL)
    }

    fun updateRecentColorLayout() {
        val recentColorValuesSize = recentColorValues.size
        val description = ", " + resources.getString(R.string.material_color_picker_option)
        recentColorSlotCount =
            if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) 7 else 6

        for (i in 0..<recentColorSlotCount) {
            val child = recentColorListLayout.getChildAt(i)
            if (i < recentColorValuesSize) {
                setImageColor(child, this.recentColorValues[i])

                val sb = java.lang.StringBuilder()
                sb.append(colorSwatchView.getColorSwatchDescriptionAt(this.recentColorValues[i]) as CharSequence?)
                sb.insert(0, colorDescription!![i] + description + ", ")
                child.setContentDescription(sb)

                child.isFocusable = true
                child.isClickable = true
            }
        }

        val currentColor = recentColorInfo.currentColor
        if (currentColor != null) {
            currentColorBackground.setColor(currentColor)
            setCurrentColorViewDescription(currentColor, 0)
            selectedColorBackground.setColor(currentColor)
            mapColorOnColorWheel(currentColor)
            updateHexAndRGBValues(currentColorBackground.color!!.defaultColor)
        } else if (recentColorValuesSize != 0) {
            val recentColorValue = recentColorValues[0]
            currentColorBackground.setColor(recentColorValue)
            setCurrentColorViewDescription(recentColorValue, 0)
            selectedColorBackground.setColor(recentColorValue)
            mapColorOnColorWheel(recentColorValue)
            updateHexAndRGBValues(currentColorBackground.color!!.defaultColor)
        }

        if (recentColorInfo.newColor != null) {
            selectedColorBackground.setColor(recentColorInfo.newColor!!)
            mapColorOnColorWheel(recentColorInfo.newColor!!)
            updateHexAndRGBValues(selectedColorBackground.color!!.defaultColor)
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
        view.setOnClickListener(imageButtonClickListener)
    }

    private fun mapColorOnColorWheel(i: Int) {
        pickedColor.color = i

        colorSwatchView.updateCursorPosition(i)

        colorSpectrumView.setColor(i)

        gradientColorSeekBar.restoreColor(i)

        opacitySeekBar.restoreColor(i)

        selectedColorBackground.setColor(i)
        setCurrentColorViewDescription(i, 1)

        val v: Float = pickedColor.v
        val alpha: Int = pickedColor.alpha
        pickedColor.v = 1.0f
        pickedColor.alpha = 255
        colorSpectrumView.updateCursorColor(pickedColor.color!!)
        pickedColor.v = v
        pickedColor.alpha = alpha


        val ceil = ceil((((opacitySeekBar.progress * 100).toFloat()) / 255.0f).toDouble()).toInt()
        colorPickerOpacityEditText.setText("" + String.format(Locale.getDefault(), "%d", ceil))
        colorPickerOpacityEditText.setSelection(ceil.toString().length)
    }

    private fun setCurrentColorViewDescription(i: Int, i2: Int) {
        val sb = StringBuilder()
        val colorSwatchDescriptionAt: StringBuilder? =
            colorSwatchView.getColorSwatchDescriptionAt(i)
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
        if (pickedColor.color != null) {
            recentColorInfo.selectedColor = pickedColor.color
        }
    }

    fun getRecentColorInfo(): RecentColorInfo {
        return recentColorInfo
    }

    fun isUserInputValid(): Boolean {
        return isInputFromUser
    }

    fun setOpacityBarEnabled(z: Boolean) {
        isOpacityBarEnabled = z
        if (z) {
            opacitySeekBar.visibility = VISIBLE
            opacitySeekBarContainer.visibility = VISIBLE
        }
    }

    private fun updateHexAndRGBValues(i: Int) {
        if (i != 0) {
            val format = String.format("%08x", i)
            val substring = format.substring(2, format.length)
            colorPickerHexEditText.setText("" + substring.uppercase(Locale.getDefault()))
            colorPickerHexEditText.setSelection(colorPickerHexEditText.getText().length)

            val color = "#$substring".toColorInt()
            colorPickerRedEditText.setText("" + Color.red(color))
            colorPickerBlueEditText.setText("" + Color.blue(color))
            colorPickerGreenEditText.setText("" + Color.green(color))
        }
    }

    inner class RecentColorInfo {
        var selectedColor: Int? = null
        var currentColor: Int? = null
        var newColor: Int? = null
        val recentColorInfo = ArrayList<Int>()

        fun initRecentColorInfo(iArr: IntArray?) {
            if (iArr != null) {
                var i = 0
                if (iArr.size <= recentColorSlotCount) {
                    val length = iArr.size
                    while (i < length) {
                        this.recentColorInfo.add(iArr[i])
                        i++
                    }
                    return
                }
                while (i < recentColorSlotCount) {
                    recentColorInfo.add(iArr[i])
                    i++
                }
            }
        }
    }

    class PickedColor {
        private var mColor: Int? = 0xffffffff.toInt()
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