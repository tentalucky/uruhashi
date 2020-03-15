package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.util.Log
import android.view.View
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.GamePlayerBattingStatsDto
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class ScoreBoardViewModel(
    application: Application,
    val gameBaseInfo: LiveData<ScoreKeepingUseCase.GameBaseInfo>,
    val gameState: LiveData<ScoreKeepingUseCase.GameStateDto>,
    val boxScore: LiveData<ScoreKeepingUseCase.BoxScoreDto>,
    val playersBattingStats: LiveData<GamePlayerBattingStatsDto>
) : BaseViewModel(application) {

    interface OnBackListener {
        fun onBack()
    }

    private val onBackListeners = ArrayList<OnBackListener>()

    fun registerOnBackListener(listener: OnBackListener) {
        onBackListeners.add(listener)
    }

    fun removeOnBackListener(listener: OnBackListener) {
        onBackListeners.remove(listener)
    }

    fun backCommand(v: View?) {
        onBackListeners.forEach {
            it.onBack()
        }
    }
}

