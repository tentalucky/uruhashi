package mahoroba.uruhashi.presentation.utility

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

object VibrateService {
    fun makeVibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val vibrationEffect = VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE)
            vibrator.vibrate(vibrationEffect)
        } else {
            vibrator.vibrate(longArrayOf(0, 20), -1)
        }
    }
}