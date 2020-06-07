package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.view.View
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class FoulBallInputViewModel(
    application: Application,
    private val playInputSuite: PlayInputSuite,
    initialValue: ScoreKeepingUseCase.FoulDto?
) :
    BaseViewModel(application) {

    val direction = MutableLiveData<FoulBallDirection?>()
    val isAtLine = MutableLiveData<Boolean>()
    val battingOption = MutableLiveData<BattingOption>()
    val battedBallType = MutableLiveData<BattedBallType?>()
    val strength = MutableLiveData<BattedBallStrength?>()
    val positionMakesError = MutableLiveData<FieldPosition?>()

    init {
        initialValue?.let { reproduceInput(it) }
    }

    fun commitCommand(v: View?) {
        playInputSuite.settleFoulBallInput()
    }

    fun backCommand(v: View?) {
        playInputSuite.back()
    }

    fun reproduceInput(dto: ScoreKeepingUseCase.FoulDto) {
        direction.value = dto.direction
        battingOption.value = dto.battingOption
        battedBallType.value = dto.battedBallType
        strength.value = dto.battedBallStrength
        isAtLine.value = dto.isAtLine
        positionMakesError.value = dto.positionMakesError
    }
}