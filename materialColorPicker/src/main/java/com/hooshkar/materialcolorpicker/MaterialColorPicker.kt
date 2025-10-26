package com.hooshkar.materialcolorpicker

import android.content.Context
import android.content.DialogInterface
import androidx.annotation.StyleRes
import androidx.appcompat.app.AlertDialog

class MaterialColorPicker : AlertDialog {

    constructor(context: Context) : super(context)
    constructor(context: Context, @StyleRes themeResId: Int) : super(context, themeResId)
    constructor(
        context: Context,
        cancelable: Boolean,
        cancelListener: DialogInterface.OnCancelListener?
    ) : super(context, cancelable, cancelListener)




}