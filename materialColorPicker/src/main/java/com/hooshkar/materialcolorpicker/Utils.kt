package com.hooshkar.materialcolorpicker

import android.content.Context
import android.content.res.Resources

internal fun Resources.dpToPx(dp: Float): Int {
    return (dp * displayMetrics.density).toInt()
}

internal fun Context.isTablet(): Boolean {
    return (resources.configuration.screenLayout and 15) >= 3
}
