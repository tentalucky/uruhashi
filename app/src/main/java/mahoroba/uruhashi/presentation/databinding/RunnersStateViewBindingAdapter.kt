package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import mahoroba.uruhashi.domain.game.secondary.Situation
import mahoroba.uruhashi.presentation.customView.RunnersStateView

object RunnersStateViewBindingAdapter {

    @BindingAdapter("app:situation")
    @JvmStatic
    fun setSituation(view: RunnersStateView, situation: Situation) {
        view.situation = situation
    }
}