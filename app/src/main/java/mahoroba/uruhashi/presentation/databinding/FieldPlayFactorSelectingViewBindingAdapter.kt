package mahoroba.uruhashi.presentation.databinding

import android.databinding.*
import mahoroba.uruhashi.domain.game.FieldPlayFactor
import mahoroba.uruhashi.presentation.customView.FieldPlayFactorSelectingView

object FieldPlayFactorSelectingViewBindingAdapter {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: FieldPlayFactorSelectingView, factor: FieldPlayFactor?) {
        view.selectedValue = factor
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: FieldPlayFactorSelectingView): FieldPlayFactor? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: FieldPlayFactorSelectingView, attrChange: InverseBindingListener) {
        view.onValueSelectedListener = fun() { attrChange.onChange() }
    }

    @BindingAdapter("app:fixedFieldPlayFactor")
    @JvmStatic
    fun setFixedFieldPlayFactor(view: FieldPlayFactorSelectingView, factor: FieldPlayFactor?) {
        view.fixedFieldPlayFactor = factor
    }
}