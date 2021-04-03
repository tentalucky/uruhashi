package mahoroba.uruhashi.presentation

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.presentation.PlayInputViewModel.*
import mahoroba.uruhashi.presentation.PlayInputViewModel.ActiveFragmentType.*
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*
import java.util.*
import kotlin.collections.ArrayList

class PlayInputSuite(
    private val parentViewModel: PlayInputViewModel,
    private val useCase: ScoreKeepingUseCase,
    val mode: Mode,
    val loadedPlay: PlayDto?,
    onCompleteListener: () -> Unit,
    onCancelListener: () -> Unit,
    activeFragmentSetter: (ActiveFragmentType, ScreenSuite) -> Unit
) : ScreenSuite(onCompleteListener, onCancelListener, activeFragmentSetter) {

    enum class Mode {
        NEW,
        MODIFY
    }

    // region * View models *

    private var mPitchInputViewModel: PitchInputViewModel? = null
    val pitchInputViewModel: PitchInputViewModel
        get() {
            if (mPitchInputViewModel == null) mPitchInputViewModel =
                PitchInputViewModel(parentViewModel.getApplication(), this, loadedPlay)
            return mPitchInputViewModel!!
        }

    private var mFoulBallInputViewModel: FoulBallInputViewModel? = null
    val foulBallInputViewModel: FoulBallInputViewModel
        get() {
            if (mFoulBallInputViewModel == null) mFoulBallInputViewModel =
                FoulBallInputViewModel(
                    parentViewModel.getApplication(), this, loadedPlay as? FoulDto
                )
            return mFoulBallInputViewModel!!
        }

    private var mBattingInputViewModel: BattingInputViewModel? = null
    val battingInputViewModel: BattingInputViewModel
        get() {
            if (mBattingInputViewModel == null) mBattingInputViewModel =
                BattingInputViewModel(
                    parentViewModel.getApplication(), this, loadedPlay as? BattingDto
                )
            return mBattingInputViewModel!!
        }

    private var mFieldPlayInputViewModel: FieldPlayInputViewModel? = null
    val fieldPlayInputViewModel: FieldPlayInputViewModel
        get() {
            if (mFieldPlayInputViewModel == null) mFieldPlayInputViewModel =
                FieldPlayInputViewModel(
                    parentViewModel.getApplication(), this, loadedPlay
                )
            return mFieldPlayInputViewModel!!
        }

    // endregion

    private val transitionStack = Stack<ActiveFragmentType>()

    private var mActiveFragmentType: ActiveFragmentType
    override val activeFragmentType: ActiveFragmentType
        get() = mActiveFragmentType

    val gameBaseInfo = useCase.gameBaseInfo
    val gameState = useCase.latestGameState
    val pitchList: LiveData<List<PitchInfoDto>> =
        Transformations.map(useCase.pitchListOfLastPlateAppearance) {
            when (mode) {
                Mode.NEW -> it
                Mode.MODIFY -> emptyList()
            }
        }

    val isBatterRunnerActive: Boolean
        get() {
            return when (pitchInputViewModel.selectedPitchResult.value) {
                PitchInputViewModel.UIPitchResult.BATTED, PitchInputViewModel.UIPitchResult.HIT_BY_PITCH ->
                    true
                PitchInputViewModel.UIPitchResult.FOUL ->
                    false
                PitchInputViewModel.UIPitchResult.BALL,
                PitchInputViewModel.UIPitchResult.STRIKE_CALLED,
                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG ->
                    (pitchInputViewModel.isFinalizing.value) == true
                else ->
                    false
            }
        }

    val pitchResult: PitchInputViewModel.UIPitchResult?
        get() = pitchInputViewModel.selectedPitchResult.value
    val isFinalizing: Boolean
        get() = pitchInputViewModel.isFinalizing.value ?: false

    fun saveGame() {
        parentViewModel.saveGame()
    }

    fun openOffenseSubstitution() {
        parentViewModel.openOffenseSubstitution()
    }

    fun openDefenceSubstitution() {
        parentViewModel.openDefenceSubstitution()
    }

    fun openEasyReferenceView() {
        parentViewModel.openEasyReferenceView()
    }

    fun openGameInfoInputView() {
        parentViewModel.openGameInfoInput()
    }

    fun finishGameAsComplete() {
        parentViewModel.finishGameAsCompleted()
    }

    fun settlePitchInput() {
        pitchInputViewModel.let {
            when (it.selectedPitchResult.value) {
                PitchInputViewModel.UIPitchResult.BALL -> {
                    if (it.areRunnersMakingAction.value == true || it.isFinalizing.value == true) {
                        transitionProgressTo(FIELD_PLAY_INPUT)
                    } else {
                        if (registerBall(false)) complete()
                    }
                }

                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG, PitchInputViewModel.UIPitchResult.STRIKE_CALLED -> {
                    if (it.areRunnersMakingAction.value == true || it.isFinalizing.value == true) {
                        transitionProgressTo(FIELD_PLAY_INPUT)
                    } else {
                        if (registerStrike(false)) complete()
                    }
                }

                PitchInputViewModel.UIPitchResult.FOUL -> transitionProgressTo(FOUL_BALL_INPUT)

                PitchInputViewModel.UIPitchResult.BATTED -> transitionProgressTo(BATTED_BALL_INPUT)

                PitchInputViewModel.UIPitchResult.HIT_BY_PITCH -> {
                    if (registerHitByPitch()) complete()
                }

                PitchInputViewModel.UIPitchResult.NO_PITCH_INTENTIONAL_WALK -> {
                    if (registerNoPitchIntentionalWalk()) complete()
                }

                PitchInputViewModel.UIPitchResult.BALK -> {
                    if (registerBalk()) complete()
                }

                PitchInputViewModel.UIPitchResult.OTHER -> transitionProgressTo(FIELD_PLAY_INPUT)
            }
        }
    }

    fun settleFoulBallInput() {
        if (registerFoul()) complete()
    }

    fun settleBattedBallInput() {
        transitionProgressTo(FIELD_PLAY_INPUT)
    }

    fun settleFieldPlayInput() {
        if (when (pitchInputViewModel.selectedPitchResult.value) {
                PitchInputViewModel.UIPitchResult.STRIKE_CALLED,
                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG -> registerStrike(true)
                PitchInputViewModel.UIPitchResult.BALL -> registerBall(true)
                PitchInputViewModel.UIPitchResult.BATTED -> registerBatting()
                PitchInputViewModel.UIPitchResult.OTHER -> registerPlayWithoutPitch()
                else -> false
            }
        ) complete()
    }

    fun back() {
        if (transitionStack.isNotEmpty()) {
            transitionStack.pop().let {
                mActiveFragmentType = it
                activeFragmentSetter(it, this)
            }
        } else {
            cancel()
        }
    }

    private fun transitionProgressTo(fragmentType: ActiveFragmentType) {
        transitionStack.push(mActiveFragmentType)
        mActiveFragmentType = fragmentType
        activeFragmentSetter(fragmentType, this)
    }

    private fun registerBall(fieldPlayOccurred: Boolean): Boolean {
        return when (mode) {
            Mode.NEW -> {
                pitchInputViewModel.let {
                    useCase.addBall(
                        it.selectedPitchType.value,
                        it.pitchSpeed.value,
                        it.pitchLocationX.value,
                        it.pitchLocationY.value,
                        it.selectedBattingOption.value,
                        it.withHitAndRun.value ?: false,
                        it.isFinalizing.value!!,
                        if (fieldPlayOccurred) mFieldPlayInputViewModel!!.getFieldPlayDtoList() else ArrayList()
                    )
                    true
                }
            }
            Mode.MODIFY -> {
                pitchInputViewModel.let {
                    useCase.replacePlayWithBall(
                        loadedPlay!!,
                        it.selectedPitchType.value,
                        it.pitchSpeed.value,
                        it.pitchLocationX.value,
                        it.pitchLocationY.value,
                        it.selectedBattingOption.value,
                        it.withHitAndRun.value ?: false,
                        it.isFinalizing.value!!,
                        if (fieldPlayOccurred) mFieldPlayInputViewModel!!.getFieldPlayDtoList() else ArrayList()
                    )
                }
            }
        }
    }

    private fun registerStrike(fieldPlayOccurred: Boolean): Boolean {
        return when (mode) {
            Mode.NEW -> {
                pitchInputViewModel.let {
                    useCase.addStrike(
                        it.selectedPitchType.value,
                        it.pitchSpeed.value,
                        it.pitchLocationX.value,
                        it.pitchLocationY.value,
                        it.selectedBattingOption.value,
                        it.withHitAndRun.value ?: false,
                        it.isFinalizing.value!!,
                        when (it.selectedPitchResult.value) {
                            PitchInputViewModel.UIPitchResult.STRIKE_SWUNG -> Strike.StrikeType.SWINGING
                            PitchInputViewModel.UIPitchResult.STRIKE_CALLED -> Strike.StrikeType.LOOKING
                            else -> throw RuntimeException("Pitch result must be Strike.")
                        },
                        if (fieldPlayOccurred) mFieldPlayInputViewModel!!.getFieldPlayDtoList() else ArrayList()
                    )
                }
                true
            }
            Mode.MODIFY -> {
                pitchInputViewModel.let {
                    useCase.replacePlayWithStrike(
                        loadedPlay!!,
                        it.selectedPitchType.value,
                        it.pitchSpeed.value,
                        it.pitchLocationX.value,
                        it.pitchLocationY.value,
                        it.selectedBattingOption.value,
                        it.withHitAndRun.value ?: false,
                        it.isFinalizing.value!!,
                        when (it.selectedPitchResult.value) {
                            PitchInputViewModel.UIPitchResult.STRIKE_SWUNG -> Strike.StrikeType.SWINGING
                            PitchInputViewModel.UIPitchResult.STRIKE_CALLED -> Strike.StrikeType.LOOKING
                            else -> throw RuntimeException("Pitch result must be Strike.")
                        },
                        if (fieldPlayOccurred) mFieldPlayInputViewModel!!.getFieldPlayDtoList() else ArrayList()
                    )
                }
            }
        }
    }

    private fun registerFoul(): Boolean {
        return when (mode) {
            Mode.NEW -> {
                useCase.addFoul(
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false,
                    foulBallInputViewModel.direction.value,
                    foulBallInputViewModel.isAtLine.value,
                    foulBallInputViewModel.battedBallType.value,
                    foulBallInputViewModel.strength.value,
                    foulBallInputViewModel.positionMakesError.value
                )
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithFoul(
                    loadedPlay!!,
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false,
                    foulBallInputViewModel.direction.value,
                    foulBallInputViewModel.isAtLine.value,
                    foulBallInputViewModel.battedBallType.value,
                    foulBallInputViewModel.strength.value,
                    foulBallInputViewModel.positionMakesError.value
                )
            }
        }
    }

    private fun registerHitByPitch(): Boolean {
        return when (mode) {
            Mode.NEW -> {
                useCase.addHitByPitch(
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false
                )
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithHitByPitch(
                    loadedPlay!!,
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false
                )
            }
        }
    }

    private fun registerBatting(): Boolean {
        val battedBall = BattedBall(
            battingInputViewModel.direction.value,
            battingInputViewModel.battedBallType.value ?: BattedBallType.NO_ENTRY,
            battingInputViewModel.strength.value ?: BattedBallStrength.NO_ENTRY,
            battingInputViewModel.distance.value,
            pitchInputViewModel.selectedBattingOption.value == BattingOption.BUNT
        )

        return when (mode) {
            Mode.NEW -> {
                useCase.addBatting(
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false,
                    battedBall,
                    fieldPlayInputViewModel.getFieldPlayDtoList(),
                    battingInputViewModel.result.value!!
                )
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithBatting(
                    loadedPlay!!,
                    pitchInputViewModel.selectedPitchType.value,
                    pitchInputViewModel.pitchSpeed.value,
                    pitchInputViewModel.pitchLocationX.value,
                    pitchInputViewModel.pitchLocationY.value,
                    pitchInputViewModel.selectedBattingOption.value,
                    pitchInputViewModel.withHitAndRun.value ?: false,
                    battedBall,
                    fieldPlayInputViewModel.getFieldPlayDtoList(),
                    battingInputViewModel.result.value!!
                )
            }
        }
    }

    private fun registerNoPitchIntentionalWalk(): Boolean {
        return when (mode) {
            Mode.NEW -> {
                useCase.addNoPitchIntentionalWalk()
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithNoPitchIntentionalWalk(loadedPlay!!)
            }
        }
    }

    private fun registerBalk(): Boolean {
        return when (mode) {
            Mode.NEW -> {
                useCase.addBalk()
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithBalk(loadedPlay!!)
            }
        }
    }

    private fun registerPlayWithoutPitch(): Boolean {
        return when (mode) {
            Mode.NEW -> {
                useCase.addPlayWithoutPitch(
                    mFieldPlayInputViewModel!!.getFieldPlayDtoList()
                )
                true
            }
            Mode.MODIFY -> {
                useCase.replacePlayWithPlayInInterval(
                    loadedPlay!!,
                    mFieldPlayInputViewModel!!.getFieldPlayDtoList()
                )
            }
        }
    }

    init {
        mActiveFragmentType = PITCH_INPUT

        if (mode == Mode.NEW && loadedPlay != null) {
            if (loadedPlay is FoulDto) {
                transitionProgressTo(FOUL_BALL_INPUT)
            }
            if (loadedPlay is BattingDto) {
                transitionProgressTo(BATTED_BALL_INPUT)
            }
            if (loadedPlay is BallDto && loadedPlay.fieldPlays.isNotEmpty() ||
                loadedPlay is StrikeDto && loadedPlay.fieldPlays.isNotEmpty() ||
                loadedPlay is BattingDto ||
                loadedPlay is PlayWithoutPitchDto
            ) {
                transitionProgressTo(FIELD_PLAY_INPUT)
            }
        }
    }
}