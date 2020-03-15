package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.presentation.customView.PitchSpeedInputView

object PitchSpeedInputViewBindingAdapter {

    @BindingAdapter("app:pitch_speed")
    @JvmStatic
    fun setPitchSpeed(view: PitchSpeedInputView, value: Int?) {
        view.pitchSpeed = value
    }

    @InverseBindingAdapter(attribute = "app:pitch_speed")
    @JvmStatic
    fun getPitchSpeed(view: PitchSpeedInputView): Int? {
        return view.pitchSpeed
    }

    @BindingAdapter("app:pitch_speedAttrChanged")
    @JvmStatic
    fun setPitchSpeedChangedListener(view: PitchSpeedInputView, attrChange: InverseBindingListener) {
        view.onPitchSpeedChangedListener = fun() { attrChange.onChange() }
    }
}