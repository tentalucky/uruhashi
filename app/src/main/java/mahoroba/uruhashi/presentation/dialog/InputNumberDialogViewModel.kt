package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.presentation.base.BaseViewModel

class InputNumberDialogViewModel(
    application: Application,
    private val tag: String,
    private val listener: Listener?,
    initialValue: Int?
) :
    BaseViewModel(application) {

    class Factory(
        private val application: Application,
        private val tag: String,
        private val listener: Listener?,
        private val initialValue: Int?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return InputNumberDialogViewModel(application, tag, listener, initialValue) as T
        }
    }

    interface Listener {
        fun onCommitted(tag: String, value: Int?)
    }

    val closeDialogEvent = LiveEvent<Unit>()

    var inputValue = MutableLiveData<String>()

    fun num0InputCommand(v: View?) = onNumberInputted(0)
    fun num1InputCommand(v: View?) = onNumberInputted(1)
    fun num2InputCommand(v: View?) = onNumberInputted(2)
    fun num3InputCommand(v: View?) = onNumberInputted(3)
    fun num4InputCommand(v: View?) = onNumberInputted(4)
    fun num5InputCommand(v: View?) = onNumberInputted(5)
    fun num6InputCommand(v: View?) = onNumberInputted(6)
    fun num7InputCommand(v: View?) = onNumberInputted(7)
    fun num8InputCommand(v: View?) = onNumberInputted(8)
    fun num9InputCommand(v: View?) = onNumberInputted(9)

    private fun onNumberInputted(num: Int) {
        (inputValue.value ?: "").let {
            if (it.length < 4) {
                inputValue.value = (it + num.toString()).toInt().toString()
            }
        }
    }

    fun clearCommand(v: View?) {
        inputValue.value = null
    }

    fun okCommand(v: View?) {
        listener?.onCommitted(tag, inputValue.value?.toInt())
        closeDialogEvent.call(Unit)
    }

    fun cancelCommand(v: View) {
        closeDialogEvent.call(Unit)
    }
}