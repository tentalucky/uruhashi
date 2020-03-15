package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.presentation.customView.PitchLocationView
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

object PitchLocationViewBindingAdapters {

    @BindingAdapter("app:pitch_location_x")
    @JvmStatic
    fun setPitchLocationX(view: PitchLocationView, x: Float?) {
        view.pitchLocationX = x
    }

    @InverseBindingAdapter(attribute = "app:pitch_location_x")
    @JvmStatic
    fun getPitchLocationX(view: PitchLocationView): Float? {
        return view.pitchLocationX
    }

    @BindingAdapter("app:pitch_location_xAttrChanged")
    @JvmStatic
    fun setPitchLocationXListener(view: PitchLocationView, attrChange: InverseBindingListener) {
        view.onPitchLocationXChangedListener = fun() { attrChange.onChange() }
    }

    @BindingAdapter("app:pitch_location_y")
    @JvmStatic
    fun setPitchLocationY(view: PitchLocationView, y: Float?) {
        view.pitchLocationY = y
    }

    @InverseBindingAdapter(attribute = "app:pitch_location_y")
    @JvmStatic
    fun getPitchLocationY(view: PitchLocationView): Float? {
        return view.pitchLocationY
    }

    @BindingAdapter("app:pitch_location_yAttrChanged")
    @JvmStatic
    fun setPitchLocationYListener(view: PitchLocationView, attrChange: InverseBindingListener) {
        view.onPitchLocationYChangedListener = fun() { attrChange.onChange() }
    }

    @BindingAdapter("app:pitch_list")
    @JvmStatic
    fun setPitchList(view: PitchLocationView, list: List<ScoreKeepingUseCase.PitchInfoDto>?) {
        view.pitchList = list
    }

    @BindingAdapter("app:is_LHP")
    @JvmStatic
    fun setIsLHD(view: PitchLocationView, isLHP: Boolean?) {
        view.isLHP = isLHP
    }
}