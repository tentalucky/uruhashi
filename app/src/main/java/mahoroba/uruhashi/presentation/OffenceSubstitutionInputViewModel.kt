package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.domain.game.Substitution
import mahoroba.uruhashi.domain.game.TopOrBottom.*
import mahoroba.uruhashi.domain.game.secondary.Situation
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPlayerInGameDialogViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class OffenceSubstitutionInputViewModel(
    application: Application,
    private val inputSuite: OffenceSubstitutionInputSuite
) : BaseViewModel(application), SelectPlayerInGameDialogViewModel.Listener {

    // region * Child classes *
    data class PlayerProfileSet(
        val id: ID?,
        val name: PersonName?,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?
    )
    // endregion * Child classes *

    val presentBatterName: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.battersName?.fullName
        } else {
            inputSuite.gameState.value?.currentBatterName?.fullName
        }

    val presentFirstRunnerName: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.firstRunnerName?.fullName
        } else {
            inputSuite.gameState.value?.firstRunnerName?.fullName
        }

    val presentSecondRunnerName: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.secondRunnerName?.fullName
        } else {
            inputSuite.gameState.value?.secondRunnerName?.fullName
        }

    val presentThirdRunnerName: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.thirdRunnerName?.fullName
        } else {
            inputSuite.gameState.value?.thirdRunnerName?.fullName
        }

    val pinchRunner1Enable: Boolean
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.runnerIsOn1B
        } else {
            inputSuite.gameState.value?.situation?.orderOf1R != null
        }

    val pinchRunner2Enable: Boolean
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.runnerIsOn2B
        } else {
            inputSuite.gameState.value?.situation?.orderOf2R != null
        }

    val pinchRunner3Enable: Boolean
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.runnerIsOn3B
        } else {
            inputSuite.gameState.value?.situation?.orderOf3R != null
        }

    private val mPinchHitter = MutableLiveData<PlayerProfileSet>()
    val pinchHitter
        get() = mPinchHitter.value
    val pinchHitterName: LiveData<String?> =
        Transformations.map(mPinchHitter) { it?.name?.fullName }

    private val mPinchRunner1 = MutableLiveData<PlayerProfileSet>()
    val pinchRunner1
        get() = mPinchRunner1.value
    val pinchRunner1Name: LiveData<String?> =
        Transformations.map(mPinchRunner1) { it?.name?.fullName }

    private val mPinchRunner2 = MutableLiveData<PlayerProfileSet>()
    val pinchRunner2
        get() = mPinchRunner2.value
    val pinchRunner2Name: LiveData<String?> =
        Transformations.map(mPinchRunner2) { it?.name?.fullName }

    private val mPinchRunner3 = MutableLiveData<PlayerProfileSet>()
    val pinchRunner3
        get() = mPinchRunner3.value
    val pinchRunner3Name: LiveData<String?> =
        Transformations.map(mPinchRunner3) { it?.name?.fullName }

    val commitEnable = MediatorLiveData<Boolean>()

    val onOpenSelectPlayerDialog = LiveEvent<Pair<ID?, String>>()

    init {
        val enable = fun() =
            mPinchHitter.value != null || mPinchRunner1.value != null || mPinchRunner2.value != null || mPinchRunner3.value != null

        commitEnable.addSource(mPinchHitter) { commitEnable.value = enable() }
        commitEnable.addSource(mPinchRunner1) { commitEnable.value = enable() }
        commitEnable.addSource(mPinchRunner2) { commitEnable.value = enable() }
        commitEnable.addSource(mPinchRunner3) { commitEnable.value = enable() }

        if (inputSuite.mode == OffenceSubstitutionInputSuite.Mode.MODIFY) {
            (inputSuite.selectedPeriod as ScoreKeepingUseCase.SubstitutionDto).let {
                reproduceInputBody(it, it.previousSituation)
            }
        }
    }

    private val teamID: ID?
        get() =
            when (if (inputSuite.selectedPeriod != null) {
                inputSuite.selectedPeriod.topOrBottom
            } else {
                inputSuite.gameState.value?.situation?.topOrBottom
            }) {
                TOP -> inputSuite.gameBaseInfo.value?.visitorTeamId
                BOTTOM -> inputSuite.gameBaseInfo.value?.homeTeamId
                else -> null
            }

    fun onSelectingPinchHitterClicked(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "PH"))
    fun onSelectingPinchRunner1Clicked(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "1R"))
    fun onSelectingPinchRunner2Clicked(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "2R"))
    fun onSelectingPinchRunner3Clicked(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "3R"))

    override fun onSelected(arg: SelectPlayerInGameDialogViewModel.ResultArg) {
        when (arg.tag) {
            "PH" -> mPinchHitter
            "1R" -> mPinchRunner1
            "2R" -> mPinchRunner2
            "3R" -> mPinchRunner3
            else -> null
        }?.value =
            PlayerProfileSet(arg.playerId, arg.playerName, arg.bats, arg.throws, arg.uniformNumber)
    }

    fun onClearPinchHitterClicked(v: View?) {
        mPinchHitter.value = null
    }

    fun onClearPinchRunner1Clicked(v: View?) {
        mPinchRunner1.value = null
    }

    fun onClearPinchRunner2Clicked(v: View?) {
        mPinchRunner2.value = null
    }

    fun onClearPinchRunner3Clicked(v: View?) {
        mPinchRunner3.value = null
    }

    fun backCommand(v: View?) {
        inputSuite.back()
    }

    fun commitCommand(v: View?) {
        inputSuite.settleOffenceTeamSubstitution()
    }

    fun reproduceInput(dto: ScoreKeepingUseCase.SubstitutionDto) {
        val situation = inputSuite.gameState.value!!.situation

        reproduceInputBody(dto, situation)
    }

    private fun reproduceInputBody(dto: ScoreKeepingUseCase.SubstitutionDto, situation: Situation) {
        mPinchHitter.value =
            dto.newPlayerList.find { n -> n.first == situation.orderOfBatter }?.second?.let {
                PlayerProfileSet(it.playerID, it.playerName, it.bats, it.throws, it.uniformNumber)
            }
        mPinchRunner1.value =
            dto.newPlayerList.find { n -> n.first == situation.orderOf1R }?.second?.let {
                PlayerProfileSet(it.playerID, it.playerName, it.bats, it.throws, it.uniformNumber)
            }
        mPinchRunner2.value =
            dto.newPlayerList.find { n -> n.first == situation.orderOf2R }?.second?.let {
                PlayerProfileSet(it.playerID, it.playerName, it.bats, it.throws, it.uniformNumber)
            }
        mPinchRunner3.value =
            dto.newPlayerList.find { n -> n.first == situation.orderOf3R }?.second?.let {
                PlayerProfileSet(it.playerID, it.playerName, it.bats, it.throws, it.uniformNumber)
            }
    }
}