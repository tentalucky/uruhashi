package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.BattingOption
import mahoroba.uruhashi.presentation.customView.BattingOptionSelectingView

object BattingOptionSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: BattingOptionSelectingView, value: BattingOption?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: BattingOptionSelectingView): BattingOption? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: BattingOptionSelectingView, attrChange: InverseBindingListener) {
        view.onSelectedListener = fun() { attrChange.onChange() }
    }
}