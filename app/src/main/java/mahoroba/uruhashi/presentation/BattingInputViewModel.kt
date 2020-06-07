package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.InputNumberDialogViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class BattingInputViewModel(
    application: Application,
    private val playInputSuite: PlayInputSuite,
    initialValue: ScoreKeepingUseCase.BattingDto?
) :
    BaseViewModel(application), InputNumberDialogViewModel.Listener {

    val result = MutableLiveData<BattingResult>()
    val direction = MutableLiveData<BattedBallDirection>()
    val battedBallType = MutableLiveData<BattedBallType>()
    val strength = MutableLiveData<BattedBallStrength>()
    val distance = MutableLiveData<Int>()
    val battingOption = MutableLiveData<BattingOption>()

    val inputIsValid = Transformations.map(result) {
        it != null && it != BattingResult.NO_ENTRY
    }!!

    data class NumberInputDialogParam(val tag: String, val initialValue: Int?)

    val openNumberInputDialogEvent = LiveEvent<NumberInputDialogParam>()

    init {
        initialValue?.let { reproduceInput(it) }
    }

    fun inputDistanceCommand(v: View?) {
        openNumberInputDialogEvent.call(NumberInputDialogParam("", distance.value))
    }

    fun commitCommand(v: View?) {
        playInputSuite.settleBattedBallInput()
    }

    fun backCommand(v: View?) {
        playInputSuite.back()
    }

    override fun onCommitted(tag: String, value: Int?) {
        distance.value = value
    }

    fun reproduceInput(dto: ScoreKeepingUseCase.BattingDto) {
        result.value = dto.battingResult
        direction.value = dto.battedBall.direction
        battedBallType.value = dto.battedBall.type
        strength.value = dto.battedBall.strength
        distance.value = dto.battedBall.distance
        battingOption.value = dto.battingOption
    }
}