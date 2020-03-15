package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.BattedBallType
import mahoroba.uruhashi.presentation.customView.BattedBallTypeSelectingView

object BattedBallTypeSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: BattedBallTypeSelectingView, value: BattedBallType?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: BattedBallTypeSelectingView): BattedBallType? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: BattedBallTypeSelectingView, attrChange: InverseBindingListener) {
        view.addOnSelectedValueChangedListener { attrChange.onChange() }
    }
}