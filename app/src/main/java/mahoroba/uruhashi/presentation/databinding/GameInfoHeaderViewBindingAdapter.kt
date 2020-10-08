package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import mahoroba.uruhashi.domain.game.secondary.Situation
import mahoroba.uruhashi.presentation.customView.GameInfoHeaderView
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

object GameInfoHeaderViewBindingAdapter {

    @BindingAdapter("app:gameState")
    @JvmStatic
    fun setGameState(view: GameInfoHeaderView, gameState: ScoreKeepingUseCase.GameStateDto) {
        view.gameState = gameState
    }

    @BindingAdapter("app:gameBaseInfo")
    @JvmStatic
    fun setGameBaseInfo(view: GameInfoHeaderView, gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo) {
        view.gameBaseInfo = gameBaseInfo
    }
}