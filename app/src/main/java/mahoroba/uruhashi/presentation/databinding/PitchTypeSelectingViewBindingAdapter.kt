package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.PitchType
import mahoroba.uruhashi.presentation.customView.PitchTypeSelectingView

object PitchTypeSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: PitchTypeSelectingView, value: PitchType?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: PitchTypeSelectingView): PitchType? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: PitchTypeSelectingView, attrChange: InverseBindingListener) {
        view.onSelectedListener = fun() { attrChange.onChange() }
    }
}