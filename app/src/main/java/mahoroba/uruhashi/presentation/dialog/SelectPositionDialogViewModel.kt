package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.DialogFragment
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.presentation.base.BaseViewModel

class SelectPositionDialogViewModel(
    application: Application,
    val enableDH: Boolean,
    val tag: String,
    val listener: Listener?)
    : BaseViewModel(application) {

    class Factory(
        private val application: Application,
        private val enableDH: Boolean,
        private val tag: String,
        private val listener: Listener?)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SelectPositionDialogViewModel(application, enableDH, tag, listener) as T
        }
    }

    interface Listener {
        fun onSelected(arg: ResultArg)
    }

    data class ResultArg(val position: Position, val tag: String)

    val dialogMethods = LiveEvent<(DialogFragment) -> Unit>()

    fun onPitcherSelected(v: View?) = onSelected(Position.PITCHER)
    fun onCatcherSelected(v: View?) = onSelected(Position.CATCHER)
    fun onFirstBasemanSelected(v: View?) = onSelected(Position.FIRST_BASEMAN)
    fun onSecondBasemanSelected(v: View?) = onSelected(Position.SECOND_BASEMAN)
    fun onThirdBasemanSelected(v: View?) = onSelected(Position.THIRD_BASEMAN)
    fun onShortstopSelected(v: View?) = onSelected(Position.SHORT_STOP)
    fun onLeftFielderSelected(v: View?) = onSelected(Position.LEFT_FIELDER)
    fun onCenterFielderSelected(v: View?) = onSelected(Position.CENTER_FIELDER)
    fun onRightFielderSelected(v: View?) = onSelected(Position.RIGHT_FIELDER)
    fun onDesignatedHitterSelected(v: View?) = onSelected(Position.DESIGNATED_HITTER)

    private fun onSelected(position: Position) {
        listener?.onSelected(
            ResultArg(
                position,
                tag
            )
        )
        dialogMethods.call { it.dismiss() }
    }
}