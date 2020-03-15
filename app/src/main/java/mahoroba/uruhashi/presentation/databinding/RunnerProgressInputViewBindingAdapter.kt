package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import android.databinding.InverseBindingAdapter
import android.databinding.InverseBindingListener
import mahoroba.uruhashi.presentation.FieldPlayInputViewModel
import mahoroba.uruhashi.presentation.customView.RunnerProgressInputView

object RunnerProgressInputViewBindingAdapter {

    @BindingAdapter("android:enabled")
    @JvmStatic
    fun setEnabled(view: RunnerProgressInputView, value: Boolean) {
        view.isEnabled = value
    }

    @BindingAdapter("app:movement")
    @JvmStatic
    fun setMovement(view: RunnerProgressInputView, value: FieldPlayInputViewModel.RunnerMovement?) {
        view.runnersMovement = value
    }

    @InverseBindingAdapter(attribute = "app:movement")
    @JvmStatic
    fun getMovement(view: RunnerProgressInputView): FieldPlayInputViewModel.RunnerMovement? {
        return view.runnersMovement
    }

    @BindingAdapter("app:movementAttrChanged")
    @JvmStatic
    fun setMovementChangedListener(view: RunnerProgressInputView, attrChange: InverseBindingListener) {
        view.addOnMovementChangedListener { attrChange.onChange() }
    }

    @BindingAdapter("onMovementChanged")
    @JvmStatic
    fun setMovementChangedListener(
        view: RunnerProgressInputView, listener: (FieldPlayInputViewModel.RunnerMovement?) -> Unit
    ) {
        view.addOnMovementChangedListener(listener)
    }

    @BindingAdapter("app:latest_status")
    @JvmStatic
    fun setLatestStatus(view: RunnerProgressInputView, value: FieldPlayInputViewModel.RunnerMovement?) {
        view.latestStatus = value
    }
}