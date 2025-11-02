package com.hooshkar.materialcolorpicker

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.view.View
import androidx.annotation.ColorInt
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.hooshkar.materialcolorpicker.views.MaterialColorPickerView

class MaterialColorPickerDialogBuilder {

    constructor(context: Context) : this(context, R.style.MaterialColoPickerAlertDialog)

    constructor(context: Context, overrideThemeResId: Int) {
        materialAlertDialogBuilder = MaterialAlertDialogBuilder(context, overrideThemeResId)
        colorPickerView = MaterialColorPickerView(context)

        colorPickerView.onColorChangedListener = MaterialColorPickerView.OnColorChangedListener { color ->
            onColorChangedListener?.onColorChanged(color)
            lastSelectedColor = color
        }

        materialAlertDialogBuilder.setView(colorPickerView)
    }

    private val materialAlertDialogBuilder: MaterialAlertDialogBuilder
    private val colorPickerView: MaterialColorPickerView
    private var onColorChangedListener: MaterialColorPickerView.OnColorChangedListener? = null
    private var lastSelectedColor: Int = 0

    val context: Context get() = materialAlertDialogBuilder.context
    val background: Drawable? get() = materialAlertDialogBuilder.background

    fun setBackground(background: Drawable?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setBackground(background)
        return this
    }

    fun setBackgroundInsetStart(backgroundInsetStart: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setBackgroundInsetStart(backgroundInsetStart)
        return this
    }

    fun setBackgroundInsetTop(backgroundInsetTop: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setBackgroundInsetTop(backgroundInsetTop)
        return this
    }

    fun setBackgroundInsetEnd(backgroundInsetEnd: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setBackgroundInsetEnd(backgroundInsetEnd)
        return this
    }

    fun setBackgroundInsetBottom(backgroundInsetBottom: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setBackgroundInsetBottom(backgroundInsetBottom)
        return this
    }

    fun setTitle(titleId: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setTitle(titleId)
        return this
    }

    fun setTitle(title: CharSequence?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setTitle(title)
        return this
    }

    fun setCustomTitle(customTitleView: View?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setCustomTitle(customTitleView)
        return this
    }

    fun setMessage(messageId: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setMessage(messageId)
        return this
    }

    fun setMessage(message: CharSequence?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setMessage(message)
        return this
    }

    fun setIcon(iconId: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setIcon(iconId)
        return this
    }

    fun setIcon(icon: Drawable?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setIcon(icon)
        return this
    }

    fun setIconAttribute(attrId: Int): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setIconAttribute(attrId)
        return this
    }

    fun setPositiveButton(
        textId: Int,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setPositiveButton(textId, wrapListener)
        return this
    }

    fun setPositiveButton(
        text: CharSequence?,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setPositiveButton(text, wrapListener)
        return this
    }

    fun setPositiveButtonIcon(icon: Drawable?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setPositiveButtonIcon(icon)
        return this
    }

    fun setNegativeButton(
        textId: Int,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setNegativeButton(textId, wrapListener)
        return this
    }

    fun setNegativeButton(
        text: CharSequence?,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setNegativeButton(text, wrapListener)
        return this
    }

    fun setNegativeButtonIcon(icon: Drawable?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setNegativeButtonIcon(icon)
        return this
    }

    fun setNeutralButton(
        textId: Int,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setNeutralButton(textId, wrapListener)
        return this
    }

    fun setNeutralButton(
        text: CharSequence?,
        listener: OnClickListener?
    ): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnClickListener { dialog, which ->
                it.onClick(dialog, which, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setNeutralButton(text, wrapListener)
        return this
    }

    fun setNeutralButtonIcon(icon: Drawable?): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setNeutralButtonIcon(icon)
        return this
    }

    fun setCancelable(cancelable: Boolean): MaterialColorPickerDialogBuilder {
        this.materialAlertDialogBuilder.setCancelable(cancelable)
        return this
    }

    fun setOnCancelListener(listener: OnCancelListener?): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnCancelListener { dialog ->
                it.onCancel(dialog, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setOnCancelListener(wrapListener)
        return this
    }

    fun setOnDismissListener(listener: OnDismissListener?): MaterialColorPickerDialogBuilder {
        val wrapListener = listener?.let {
            DialogInterface.OnDismissListener { dialog ->
                it.onDismiss(dialog, lastSelectedColor)
            }
        }
        this.materialAlertDialogBuilder.setOnDismissListener(wrapListener)
        return this
    }

    fun setOpacityBarEnabled(enabled: Boolean): MaterialColorPickerDialogBuilder {
        colorPickerView.setOpacityBarEnabled(enabled)
        return this
    }

    fun setRecentColor(color: Int): MaterialColorPickerDialogBuilder {
        colorPickerView.setRecentColor(color)
        return this
    }

    fun setNewColor(color: Int): MaterialColorPickerDialogBuilder {
        colorPickerView.setNewColor(color)
        lastSelectedColor = color
        return this
    }

    fun setOnlySpectrumMode(): MaterialColorPickerDialogBuilder {
        colorPickerView.setOnlySpectrumMode()
        return this
    }

    fun setOnColorChangeListener(listener: MaterialColorPickerView.OnColorChangedListener?): MaterialColorPickerDialogBuilder {
        this.onColorChangedListener = listener
        return this
    }

    fun create(): AlertDialog {
        return materialAlertDialogBuilder.create()
    }

    fun show(): AlertDialog {
        return materialAlertDialogBuilder.show()
    }


    fun interface OnClickListener {
        /**
         * This method will be invoked when a button in the dialog is clicked.
         *
         * @param dialog the dialog that received the click
         * @param which the button that was clicked (ex.
         * [DialogInterface.BUTTON_POSITIVE]) or the position
         * of the item clicked
         * @param color the color that was picked
         */
        fun onClick(dialog: DialogInterface, which: Int, @ColorInt color: Int)
    }

    fun interface OnCancelListener {
        /**
         * This method will be invoked when the dialog is canceled.
         *
         * @param dialog the dialog that was canceled will be passed into the
         * @param color the color that was picked
         * method
         */
        fun onCancel(dialog: DialogInterface?, @ColorInt color: Int)
    }

    fun interface OnDismissListener {
        /**
         * This method will be invoked when the dialog is dismissed.
         *
         * @param dialog the dialog that was dismissed will be passed into the
         * @param color the color that was picked
         * method
         */
        fun onDismiss(dialog: DialogInterface?, @ColorInt color: Int)
    }
}