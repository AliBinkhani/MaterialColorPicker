package com.hooshkar.materialcolorpicker

import android.content.Context
import android.content.DialogInterface
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.AdapterView
import android.widget.ListAdapter
import androidx.appcompat.app.AlertDialog

class MaterialColorPickerDialog {

    class Builder {
        constructor(context: Context)
        constructor(context: Context, themeResId: Int)

        fun getContext(): Context {
            TODO("Not yet implemented")
        }

        fun setTitle(titleId: Int): AlertDialog.Builder? {
            TODO("Not yet implemented")

        }

        fun setTitle(title: CharSequence?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setCustomTitle(customTitleView: View?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setMessage(messageId: Int): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setMessage(message: CharSequence?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setIcon(iconId: Int): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setIcon(icon: Drawable?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setIconAttribute(attrId: Int): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setPositiveButton(
            textId: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setPositiveButton(
            text: CharSequence?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setPositiveButtonIcon(icon: Drawable?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNegativeButton(
            textId: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNegativeButton(
            text: CharSequence?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNegativeButtonIcon(icon: Drawable?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNeutralButton(
            textId: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNeutralButton(
            text: CharSequence?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setNeutralButtonIcon(icon: Drawable?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setCancelable(cancelable: Boolean): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setOnCancelListener(onCancelListener: DialogInterface.OnCancelListener?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setOnDismissListener(onDismissListener: DialogInterface.OnDismissListener?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setOnKeyListener(onKeyListener: DialogInterface.OnKeyListener?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setItems(
            itemsId: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setItems(
            items: Array<out CharSequence?>?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setAdapter(
            adapter: ListAdapter?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setCursor(
            cursor: Cursor?,
            listener: DialogInterface.OnClickListener?,
            labelColumn: String?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setMultiChoiceItems(
            itemsId: Int,
            checkedItems: BooleanArray?,
            listener: DialogInterface.OnMultiChoiceClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setMultiChoiceItems(
            items: Array<out CharSequence?>?,
            checkedItems: BooleanArray?,
            listener: DialogInterface.OnMultiChoiceClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setMultiChoiceItems(
            cursor: Cursor?,
            isCheckedColumn: String?,
            labelColumn: String?,
            listener: DialogInterface.OnMultiChoiceClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setSingleChoiceItems(
            itemsId: Int,
            checkedItem: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setSingleChoiceItems(
            cursor: Cursor?,
            checkedItem: Int,
            labelColumn: String?,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setSingleChoiceItems(
            items: Array<out CharSequence?>?,
            checkedItem: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setSingleChoiceItems(
            adapter: ListAdapter?,
            checkedItem: Int,
            listener: DialogInterface.OnClickListener?
        ): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setOnItemSelectedListener(listener: AdapterView.OnItemSelectedListener?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setView(layoutResId: Int): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun setView(view: View?): AlertDialog.Builder? {
            TODO("Not yet implemented")
        }

        fun create(): AlertDialog {
            TODO("Not yet implemented")
        }

        fun show(): AlertDialog? {
            TODO("Not yet implemented")
        }
    }
}