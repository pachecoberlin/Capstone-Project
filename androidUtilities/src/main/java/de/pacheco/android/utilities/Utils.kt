package de.pacheco.android.utilities

import android.content.Context

fun calculateNoOfColumns(context: Context?): Int {
    if (context == null) {
        return 2
    }
    val displayMetrics = context.resources.displayMetrics
    val dpWidth = displayMetrics.widthPixels / displayMetrics.density
    val scalingFactor = 200
    val noOfColumns = (dpWidth / scalingFactor).toInt()
    return if (noOfColumns < 2) 2 else noOfColumns
}