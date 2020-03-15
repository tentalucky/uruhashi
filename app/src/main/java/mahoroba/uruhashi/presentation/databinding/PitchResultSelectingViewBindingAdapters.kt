package mahoroba.uruhashi.presentation.databinding

import android.databinding.*
import mahoroba.uruhashi.presentation.PitchInputViewModel
import mahoroba.uruhashi.presentation.customView.PitchResultSelectingView

object PitchResultSelectingViewBindingAdapters {

    @BindingAdapter("app:selected_value")
    @JvmStatic
    fun setSelectedValue(view: PitchResultSelectingView, value: PitchInputViewModel.UIPitchResult?) {
        view.selectedValue = value
    }

    @InverseBindingAdapter(attribute = "app:selected_value")
    @JvmStatic
    fun getSelectedValue(view: PitchResultSelectingView): PitchInputViewModel.UIPitchResult? {
        return view.selectedValue
    }

    @BindingAdapter("app:selected_valueAttrChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(view: PitchResultSelectingView, attrChange: InverseBindingListener) {
        view.addOnSelectedValueChangedListener { attrChange.onChange() }
    }

    @BindingAdapter("onSelectedValueChanged")
    @JvmStatic
    fun setSelectedValueChangedListener(
        view: PitchResultSelectingView,
        listener: (PitchInputViewModel.UIPitchResult?) -> Unit
    ) {
        view.addOnSelectedValueChangedListener(listener)
    }
}