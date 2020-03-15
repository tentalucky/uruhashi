package mahoroba.uruhashi.presentation

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
    onCompleteListener: () -> Unit,
    onCancelListener: () -> Unit,
    activeFragmentSetter: (ActiveFragmentType, ScreenSuite) -> Unit
) : ScreenSuite(onCompleteListener, onCancelListener, activeFragmentSetter) {
    // region * View models *

    private var mPitchInputViewModel: PitchInputViewModel? = null
    val pitchInputViewModel: PitchInputViewModel
        get() {
            if (mPitchInputViewModel == null) mPitchInputViewModel =
                parentViewModel.let { PitchInputViewModel(it.getApplication(), this) }
            return mPitchInputViewModel!!
        }

    private var mFoulBallInputViewModel: FoulBallInputViewModel? = null
    val foulBallInputViewModel: FoulBallInputViewModel
        get() {
            if (mFoulBallInputViewModel == null) mFoulBallInputViewModel =
                parentViewModel.let { FoulBallInputViewModel(it.getApplication(), this) }
            return mFoulBallInputViewModel!!
        }

    private var mBattingInputViewModel: BattingInputViewModel? = null
    val battingInputViewModel: BattingInputViewModel
        get() {
            if (mBattingInputViewModel == null) mBattingInputViewModel =
                parentViewModel.let { BattingInputViewModel(it.getApplication(), this) }
            return mBattingInputViewModel!!
        }

    private var mFieldPlayInputViewModel: FieldPlayInputViewModel? = null
    val fieldPlayInputViewModel: FieldPlayInputViewModel
        get() {
            if (mFieldPlayInputViewModel == null) mFieldPlayInputViewModel =
                parentViewModel.let { FieldPlayInputViewModel(it.getApplication(), this) }
            return mFieldPlayInputViewModel!!
        }

    // endregion

    private val transitionStack = Stack<ActiveFragmentType>()

    private var mActiveFragmentType: ActiveFragmentType
    override val activeFragmentType: ActiveFragmentType
        get() = mActiveFragmentType

    val gameBaseInfo = useCase.gameBaseInfo
    val gameState = useCase.latestGameState
    val pitchList = useCase.pitchListOfLastPlateAppearance

    val isBatterRunnerActive: Boolean
        get() {
            return when (mPitchInputViewModel?.selectedPitchResult?.value) {
                PitchInputViewModel.UIPitchResult.BATTED, PitchInputViewModel.UIPitchResult.HIT_BY_PITCH ->
                    true
                PitchInputViewModel.UIPitchResult.FOUL ->
                    false
                PitchInputViewModel.UIPitchResult.BALL,
                PitchInputViewModel.UIPitchResult.STRIKE_CALLED,
                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG ->
                    (mPitchInputViewModel?.isFinalizing?.value) == true
                else ->
                    false
            }
        }

    val pitchResult: PitchInputViewModel.UIPitchResult?
        get() = mPitchInputViewModel?.selectedPitchResult?.value
    val isFinalizing: Boolean
        get() = mPitchInputViewModel?.isFinalizing?.value ?: false

    fun openOffenseSubstitution() {
        parentViewModel.openOffenseSubstitution()
    }

    fun openDefenceSubstitution() {
        parentViewModel.openDefenceSubstitution()
    }

    fun openEasyReferenceView() {
        parentViewModel.openEasyReferenceView()
    }

    fun settlePitchInput() {
        pitchInputViewModel.let {
            when (it.selectedPitchResult.value) {
                PitchInputViewModel.UIPitchResult.BALL -> {
                    if (it.areRunnersMakingAction.value == true || it.isFinalizing.value == true) {
                        transitionProgressTo(FIELD_PLAY_INPUT)
                    } else {
                        registerBall(false)
                        complete()
                    }
                }

                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG, PitchInputViewModel.UIPitchResult.STRIKE_CALLED -> {
                    if (it.areRunnersMakingAction.value == true || it.isFinalizing.value == true) {
                        transitionProgressTo(FIELD_PLAY_INPUT)
                    } else {
                        registerStrike(false)
                        complete()
                    }
                }

                PitchInputViewModel.UIPitchResult.FOUL -> transitionProgressTo(FOUL_BALL_INPUT)

                PitchInputViewModel.UIPitchResult.BATTED -> transitionProgressTo(BATTED_BALL_INPUT)

                PitchInputViewModel.UIPitchResult.HIT_BY_PITCH -> {
                    registerHitByPitch()
                    complete()
                }

                PitchInputViewModel.UIPitchResult.NO_PITCH_INTENTIONAL_WALK -> {
                    registerNoPitchIntentionalWalk()
                    complete()
                }

                PitchInputViewModel.UIPitchResult.BALK -> {
                    registerBalk()
                    complete()
                }

                PitchInputViewModel.UIPitchResult.OTHER -> transitionProgressTo(FIELD_PLAY_INPUT)
            }
        }
    }

    fun settleFoulBallInput() {
        registerFoul()
        complete()
    }

    fun settleBattedBallInput() {
        transitionProgressTo(FIELD_PLAY_INPUT)
    }

    fun settleFieldPlayInput() {
        when (mPitchInputViewModel?.selectedPitchResult?.value) {
            PitchInputViewModel.UIPitchResult.STRIKE_CALLED,
            PitchInputViewModel.UIPitchResult.STRIKE_SWUNG -> registerStrike(true)
            PitchInputViewModel.UIPitchResult.BALL -> registerBall(true)
            PitchInputViewModel.UIPitchResult.BATTED -> registerBatting()
            PitchInputViewModel.UIPitchResult.OTHER -> registerPlayWithoutPitch()
            else -> {
            }
        }
        complete()
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

    fun load(playDto: PlayDto) {
        initialize()

        pitchInputViewModel.reproduceInput(playDto)

        if (playDto is FoulDto) {
            transitionProgressTo(FOUL_BALL_INPUT)
            foulBallInputViewModel.reproduceInput(playDto)
        }

        if (playDto is BattingDto) {
            transitionProgressTo(BATTED_BALL_INPUT)
            battingInputViewModel.reproduceInput(playDto)
        }

        if (playDto is BallDto && playDto.fieldPlays.isNotEmpty() ||
            playDto is StrikeDto && playDto.fieldPlays.isNotEmpty() ||
            playDto is BattingDto ||
            playDto is PlayWithoutPitchDto
        ) {
            transitionProgressTo(FIELD_PLAY_INPUT)
            fieldPlayInputViewModel.reproduceInput(playDto)
        }
    }

    private fun transitionProgressTo(fragmentType: ActiveFragmentType) {
        transitionStack.push(mActiveFragmentType)
        mActiveFragmentType = fragmentType
        activeFragmentSetter(fragmentType, this)
    }

    private fun registerBall(fieldPlayOccurred: Boolean) {
        mPitchInputViewModel!!.let {
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
        }
    }

    private fun registerStrike(fieldPlayOccurred: Boolean) {
        mPitchInputViewModel!!.let {
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
    }

    private fun registerFoul() {
        useCase.addFoul(
            mPitchInputViewModel!!.selectedPitchType.value,
            mPitchInputViewModel!!.pitchSpeed.value,
            mPitchInputViewModel!!.pitchLocationX.value,
            mPitchInputViewModel!!.pitchLocationY.value,
            mPitchInputViewModel!!.selectedBattingOption.value,
            mPitchInputViewModel!!.withHitAndRun.value ?: false,
            mFoulBallInputViewModel!!.direction.value,
            mFoulBallInputViewModel!!.isAtLine.value,
            mFoulBallInputViewModel!!.battedBallType.value,
            mFoulBallInputViewModel!!.strength.value,
            mFoulBallInputViewModel!!.positionMakesError.value
        )
    }

    private fun registerHitByPitch() {
        useCase.addHitByPitch(
            mPitchInputViewModel!!.selectedPitchType.value,
            mPitchInputViewModel!!.pitchSpeed.value,
            mPitchInputViewModel!!.pitchLocationX.value,
            mPitchInputViewModel!!.pitchLocationY.value,
            mPitchInputViewModel!!.selectedBattingOption.value,
            mPitchInputViewModel!!.withHitAndRun.value ?: false
        )
    }

    private fun registerBatting() {
        useCase.addBatting(
            mPitchInputViewModel!!.selectedPitchType.value,
            mPitchInputViewModel!!.pitchSpeed.value,
            mPitchInputViewModel!!.pitchLocationX.value,
            mPitchInputViewModel!!.pitchLocationY.value,
            mPitchInputViewModel!!.selectedBattingOption.value,
            mPitchInputViewModel!!.withHitAndRun.value ?: false,
            BattedBall(
                mBattingInputViewModel!!.direction.value,
                mBattingInputViewModel!!.battedBallType.value ?: BattedBallType.NO_ENTRY,
                mBattingInputViewModel!!.strength.value ?: BattedBallStrength.NO_ENTRY,
                mBattingInputViewModel!!.distance.value,
                mPitchInputViewModel!!.selectedBattingOption.value == BattingOption.BUNT
            ),
            mFieldPlayInputViewModel!!.getFieldPlayDtoList(),
            mBattingInputViewModel!!.result.value!!
        )
    }

    private fun registerNoPitchIntentionalWalk() {
        useCase.addNoPitchIntentionalWalk()
    }

    private fun registerBalk() {
        useCase.addBalk()
    }

    private fun registerPlayWithoutPitch() {
        useCase.addPlayWithoutPitch(
            mFieldPlayInputViewModel!!.getFieldPlayDtoList()
        )
    }

    private fun initialize() {
        mPitchInputViewModel = null
        mFoulBallInputViewModel = null
        mBattingInputViewModel = null
        mFieldPlayInputViewModel = null
        transitionStack.clear()
        mActiveFragmentType = PITCH_INPUT
    }

    init {
        mActiveFragmentType = PITCH_INPUT
    }
}