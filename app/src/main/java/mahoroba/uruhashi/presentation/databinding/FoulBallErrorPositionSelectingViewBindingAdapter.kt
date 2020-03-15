package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.domain.game.FieldPosition
import mahoroba.uruhashi.presentation.customView.FoulBallErrorPositionSelectingView

object FoulBallErrorPositionSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: FoulBallErrorPositionSelectingView, value: FieldPosition?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: FoulBallErrorPositionSelectingView): FieldPosition? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: FoulBallErrorPositionSelectingView, attrChange: InverseBindingListener) {
        view.addOnSelectedValueChangedListener { attrChange.onChange() }
    }
}