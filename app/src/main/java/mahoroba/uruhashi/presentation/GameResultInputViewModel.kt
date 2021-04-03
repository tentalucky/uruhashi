package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import android.view.View
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class GameResultInputViewModel(
    application: Application,
    private val parentViewModel: ScoreKeepingViewModel,
    private val useCase: ScoreKeepingUseCase
) : BaseViewModel(application) {

    val homeTeamName: LiveData<String?> =
        Transformations.map(useCase.gameBaseInfo) { it.homeTeamName }
    val visitorTeamName: LiveData<String?> =
        Transformations.map(useCase.gameBaseInfo) { it.visitorTeamName }
    val runsOfHome: LiveData<String> =
        Transformations.map(useCase.latestGameState) { it.situation.runsOfHome.toString() }
    val runsOfVisitor: LiveData<String> =
        Transformations.map(useCase.latestGameState) { it.situation.runsOfVisitor.toString() }

    fun cancelFinishingGame(v: View?) {
        parentViewModel.cancelGameFinishing()
    }
}