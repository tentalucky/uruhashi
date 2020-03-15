package mahoroba.uruhashi.presentation.databinding

import android.databinding.BindingAdapter
import mahoroba.uruhashi.presentation.customView.ScoreBoardView
import mahoroba.uruhashi.usecase.scoreKeeping.GamePlayerBattingStatsDto
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

object ScoreBoardViewBindingAdapter {
    @BindingAdapter("app:gameBaseInfo")
    @JvmStatic
    fun setGameBaseInfo(view: ScoreBoardView, gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo) {
        view.gameBaseInfo = gameBaseInfo
    }

    @BindingAdapter("app:gameState")
    @JvmStatic
    fun setGameState(view: ScoreBoardView, gameStateDto: ScoreKeepingUseCase.GameStateDto) {
        view.gameState = gameStateDto
    }

    @BindingAdapter("app:boxScore")
    @JvmStatic
    fun setBoxScore(view: ScoreBoardView, boxScoreDto: ScoreKeepingUseCase.BoxScoreDto) {
        view.boxScore = boxScoreDto
    }

    @BindingAdapter("app:playersBattingStats")
    @JvmStatic
    fun setPlayersBattingStats(view: ScoreBoardView, value: GamePlayerBattingStatsDto) {
        view.battingStats = value
    }
}