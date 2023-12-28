package com.habitude.habit.ui.util

import android.content.Context

object DpPxUtils {
    fun dpToPX(dp:Float,context:Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }

}