package mahoroba.uruhashi.presentation.utility

import android.content.Context

fun Context.dpToPx(dp: Float) : Float {
    return dp * this.resources.displayMetrics.density
}