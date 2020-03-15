package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.BattingResult
import mahoroba.uruhashi.presentation.customView.BattingResultSelectingView

object BattingResultSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: BattingResultSelectingView, value: BattingResult?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: BattingResultSelectingView): BattingResult? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: BattingResultSelectingView, attrChange: InverseBindingListener) {
        view.addOnSelectedValueChangedListener { attrChange.onChange() }
    }
}