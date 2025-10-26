package com.hooshkar.materialcolorpicker

import android.content.res.Resources

internal fun Resources.dpToPx(dp: Float): Int {
    return (dp * displayMetrics.density).toInt()
}