package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class PitchInputViewModel(
    application: Application,
    private val playInputSuite: PlayInputSuite,
    defaultPlay: ScoreKeepingUseCase.PlayDto?
) :
    BaseViewModel(application) {

    enum class UIPitchResult {
        STRIKE_SWUNG,
        STRIKE_CALLED,
        BALL,
        FOUL,
        HIT_BY_PITCH,
        BATTED,
        NO_PITCH_INTENTIONAL_WALK,
        BALK,
        OTHER
    }

    // region Properties to show

    val situation = Transformations.map(playInputSuite.gameState) { it.situation }!!

    val inningText: LiveData<String> = Transformations.map(playInputSuite.gameState) {
        ((it.situation.inningNumber + 1) / 2).toString() + "回" + if (it.situation.inningNumber % 2 == 1) "表" else "裏"
    }
    val playerNameOfHome: LiveData<String?> = Transformations.map(playInputSuite.gameState) {
        when (it.situation.topOrBottom) {
            TOP -> it.currentPitcherName?.fullName
            BOTTOM -> it.currentBatterName?.fullName
        }
    }
    val playerNameOfVisitor: LiveData<String?> = Transformations.map(playInputSuite.gameState) {
        when (it.situation.topOrBottom) {
            TOP -> it.currentBatterName?.fullName
            BOTTOM -> it.currentPitcherName?.fullName
        }
    }
    val playerInfoOfHome: LiveData<String?> = Transformations.map(playInputSuite.gameState) {
        when (it.situation.topOrBottom) {
            TOP -> it.currentPitchersPitchCount.toString() + "球"
            BOTTOM -> null
        }
    }
    val playerInfoOfVisitor: LiveData<String?> = Transformations.map(playInputSuite.gameState) {
        when (it.situation.topOrBottom) {
            TOP -> null
            BOTTOM -> it.currentPitchersPitchCount.toString() + "球"
        }
    }

    val balls: LiveData<String> =
        Transformations.map(playInputSuite.gameState) { it.balls.toString() }
    val strikes: LiveData<String> =
        Transformations.map(playInputSuite.gameState) { it.strikes.toString() }
    val outs: LiveData<String> =
        Transformations.map(playInputSuite.gameState) { it.outs.toString() }

    val homeTeamAbbreviatedName: LiveData<String> =
        Transformations.map(playInputSuite.gameBaseInfo) { it.homeTeamAbbreviatedName }
    val visitorTeamAbbreviatedName: LiveData<String> =
        Transformations.map(playInputSuite.gameBaseInfo) { it.visitorTeamAbbreviatedName }
    val runsOfHome: LiveData<String> =
        Transformations.map(playInputSuite.gameState) { it.situation.runsOfHome.toString() }
    val runsOfVisitor: LiveData<String> =
        Transformations.map(playInputSuite.gameState) { it.situation.runsOfVisitor.toString() }
    val isLHD: LiveData<Boolean> =
        Transformations.map(playInputSuite.gameState) {
            it.currentPitchersThrowHandType == HandType.LEFT
        }

    val pitchList = playInputSuite.pitchList

    // endregion Properties to show

    val pitchLocationX = MutableLiveData<Float?>()
    val pitchLocationY = MutableLiveData<Float?>()
    val pitchSpeed = MutableLiveData<Int?>()
    val selectedPitchType = MutableLiveData<PitchType?>()
    val selectedPitchResult = MutableLiveData<UIPitchResult?>()
    val selectedBattingOption = MutableLiveData<BattingOption>()
    val withCheckSwing = MutableLiveData<Boolean>()
    val withHitAndRun = MutableLiveData<Boolean>()
    val areRunnersMakingAction = MutableLiveData<Boolean>()
    val isFinalizing = MutableLiveData<Boolean>()

    val enabledRunnerMakingAction: LiveData<Boolean> =
        Transformations.map(playInputSuite.gameState) {
            it.situation.let { n -> n.orderOf1R != null || n.orderOf2R != null || n.orderOf3R != null }
        }

    val enabledIsFinalizing = Transformations.map(selectedPitchResult) {
        (it == UIPitchResult.STRIKE_SWUNG || it == UIPitchResult.STRIKE_CALLED || it == UIPitchResult.BALL)
    }!!

    val onPitchResultSelectedValueChanged: (UIPitchResult?) -> Unit = {
        when (it) {
            UIPitchResult.STRIKE_SWUNG, UIPitchResult.STRIKE_CALLED ->
                isFinalizing.value =
                    (playInputSuite.gameState.value?.nextStrikeWillFinalize == true)
            UIPitchResult.BALL ->
                isFinalizing.value = (playInputSuite.gameState.value?.nextBallWillFinalize == true)
            UIPitchResult.FOUL, UIPitchResult.BALK, UIPitchResult.OTHER ->
                isFinalizing.value = false
            UIPitchResult.HIT_BY_PITCH, UIPitchResult.BATTED, UIPitchResult.NO_PITCH_INTENTIONAL_WALK ->
                isFinalizing.value = true
        }
    }

    val onOptionMenuOpen = LiveEvent<Unit>()

    init {
        defaultPlay?.let { reproduceInput(it) }
    }

    fun optionCommand(v: View) {
        onOptionMenuOpen.call(Unit)
    }

    fun onMenuSelected(selectedItemId: Int) {
        when (selectedItemId) {
            R.id.offenceTeamSubstitution -> playInputSuite.openOffenseSubstitution()
            R.id.defenceTeamSubstitution -> playInputSuite.openDefenceSubstitution()
            R.id.easyReference -> playInputSuite.openEasyReferenceView()
            R.id.inputGameInfo -> playInputSuite.openGameInfoInputView()
            R.id.saveGame -> playInputSuite.saveGame()
        }
    }

    fun resetLocationCommand(v: View) {
        pitchLocationX.value = null
        pitchLocationY.value = null
    }

    fun commitCommand(v: View) {
        playInputSuite.settlePitchInput()
    }

    fun cancelCommand(v: View) {
        playInputSuite.back()
    }

    private fun reproduceInput(period: ScoreKeepingUseCase.PeriodDto?) {
        if (period is ScoreKeepingUseCase.PitchDto) {
            pitchLocationX.value = period.pitchLocationX
            pitchLocationY.value = period.pitchLocationY
            pitchSpeed.value = period.pitchSpeed
            selectedPitchType.value = period.pitchType
            selectedBattingOption.value = period.battingOption
        }

        when (period) {
            is ScoreKeepingUseCase.BallDto -> {
                selectedPitchResult.value = UIPitchResult.BALL
                isFinalizing.value = period.settled
                areRunnersMakingAction.value = period.fieldPlays.isNotEmpty()
            }
            is ScoreKeepingUseCase.StrikeDto -> {
                selectedPitchResult.value = when (period.strikeType) {
                    Strike.StrikeType.LOOKING -> UIPitchResult.STRIKE_CALLED
                    Strike.StrikeType.SWINGING -> UIPitchResult.STRIKE_SWUNG
                    Strike.StrikeType.THIRD_BUNT_MISS -> UIPitchResult.FOUL
                }
                isFinalizing.value = period.settled
                areRunnersMakingAction.value = period.fieldPlays.isNotEmpty()
            }
            is ScoreKeepingUseCase.FoulDto -> {
                selectedPitchResult.value = UIPitchResult.FOUL
                isFinalizing.value = false
                areRunnersMakingAction.value = false
            }
            is ScoreKeepingUseCase.HitByPitchDto -> {
                selectedPitchResult.value = UIPitchResult.HIT_BY_PITCH
                isFinalizing.value = true
                areRunnersMakingAction.value = false
            }
            is ScoreKeepingUseCase.BattingDto -> {
                selectedPitchResult.value = UIPitchResult.BATTED
                isFinalizing.value = true
                areRunnersMakingAction.value = false
            }
            is ScoreKeepingUseCase.NoPitchIntentionalWalkDto -> {
                selectedPitchResult.value = UIPitchResult.NO_PITCH_INTENTIONAL_WALK
                isFinalizing.value = true
                areRunnersMakingAction.value = false
            }
            is ScoreKeepingUseCase.BalkDto -> {
                selectedPitchResult.value = UIPitchResult.BALK
                isFinalizing.value = false
                areRunnersMakingAction.value = false
            }
            is ScoreKeepingUseCase.PlayWithoutPitchDto -> {
                selectedPitchResult.value = UIPitchResult.OTHER
                isFinalizing.value = false
                areRunnersMakingAction.value = true
            }
        }
    }
}