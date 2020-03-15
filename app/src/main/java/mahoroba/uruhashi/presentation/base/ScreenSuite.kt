package mahoroba.uruhashi.presentation.base

import android.arch.lifecycle.LiveData
import mahoroba.uruhashi.presentation.PlayInputViewModel

abstract class ScreenSuite(
    protected val onCompleteListener: () -> Unit,
    protected val onCancelListener: () -> Unit,
    protected val activeFragmentSetter: (PlayInputViewModel.ActiveFragmentType, ScreenSuite) -> Unit
) {
    abstract val activeFragmentType: PlayInputViewModel.ActiveFragmentType

    protected fun complete() {
        onCompleteListener.invoke()
    }

    protected fun cancel() {
        onCancelListener.invoke()
    }
}