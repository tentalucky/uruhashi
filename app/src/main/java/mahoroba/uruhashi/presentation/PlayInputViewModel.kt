package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.TeamClass.HOME
import mahoroba.uruhashi.domain.game.TeamClass.VISITOR
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.presentation.PlayInputViewModel.ActiveFragmentType.*
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*
import java.util.*

class PlayInputViewModel(
    application: Application,
    private val useCase: ScoreKeepingUseCase
) :
    BaseViewModel(application) {

    val myApplication = application

    private val screenSuiteStack = Stack<ScreenSuite>()
    private val activeSuite: ScreenSuite?
        get() = if (screenSuiteStack.isEmpty()) null else screenSuiteStack.peek()

    enum class ActiveFragmentType {
        PITCH_INPUT,
        FOUL_BALL_INPUT,
        BATTED_BALL_INPUT,
        FIELD_PLAY_INPUT,
        OFFENCE_SUBSTITUTION,
        DEFENCE_SUBSTITUTION,
        EASY_REFERENCE
    }

    private val transitionStack = Stack<ActiveFragmentType>()

    private val mActiveFragmentType = MutableLiveData<ActiveFragmentType>()
    val activeFragmentType: LiveData<ActiveFragmentType> = mActiveFragmentType

    // region * Child view models *

    val pitchInputViewModel: PitchInputViewModel
        get() {
            return (activeSuite as? PlayInputSuite)?.pitchInputViewModel
                ?: throw RuntimeException("PitchInput is not available now.")
        }

    val foulBallInputViewModel: FoulBallInputViewModel
        get() {
            return (activeSuite as? PlayInputSuite)?.foulBallInputViewModel
                ?: throw RuntimeException("FoulBallInput is not available now.")
        }

    val battingInputViewModel: BattingInputViewModel
        get() {
            return (activeSuite as? PlayInputSuite)?.battingInputViewModel
                ?: throw RuntimeException("BattingInput is not available now.")
        }

    val fieldPlayInputViewModel: FieldPlayInputViewModel
        get() {
            return (activeSuite as? PlayInputSuite)?.fieldPlayInputViewModel
                ?: throw RuntimeException("FieldPlayInput is not available now.")
        }

    val offenseSubstitutionInputViewModel: OffenceSubstitutionInputViewModel
        get() {
            return (activeSuite as? OffenceSubstitutionInputSuite)?.offenceSubstitutionInputViewModel
                ?: throw RuntimeException("OffenceSubstitutionInput is not available now.")
        }

    val defenseSubstitutionInputViewModel: DefenseSubstitutionInputViewModel
        get() {
            return (activeSuite as? DefenseSubstitutionInputSuite)?.defenseSubstitutionInputViewModel
                ?: throw RuntimeException("DefenseSubstitutionInput is not available now.")
        }

    val scoreBoardViewModel = ScoreBoardViewModel(
        getApplication(),
        useCase.gameBaseInfo,
        useCase.latestGameState,
        useCase.boxScore,
        useCase.playersBattingStats
    )

    val periodHistoryListViewModel = PeriodHistoryListViewModel(
        getApplication(),
        useCase.gameBaseInfo,
        useCase.boxScore,
        this
    )

    // endregion

    val gameBaseInfo = useCase.gameBaseInfo
    val gameState = useCase.latestGameState

    val fragmentMethod = LiveEvent<(BaseFragment) -> Unit>()

    fun openOffenseSubstitution() {
        screenSuiteStack.push(
            newOffenceSubstitutionInputSuite(
                OffenceSubstitutionInputSuite.Mode.NEW, null
            )
        )
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    fun openDefenceSubstitution() {
        screenSuiteStack.push(
            newDefenceSubstitutionInputSuite(
                DefenseSubstitutionInputSuite.Mode.NEW, null
            )
        )
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    fun openEasyReferenceView() {
        screenSuiteStack.push(newEasyReferenceSuite())
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    fun openInsertOffenceSubstitution(targetPeriod: PeriodDto?) {
        screenSuiteStack.push(
            newOffenceSubstitutionInputSuite(
                OffenceSubstitutionInputSuite.Mode.INSERT, targetPeriod
            )
        )
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    fun openInsertDefenceSubstitution(targetPeriod: PeriodDto?) {
        screenSuiteStack.push(
            newDefenceSubstitutionInputSuite(
                DefenseSubstitutionInputSuite.Mode.INSERT, targetPeriod
            )
        )
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    fun openModifySubstitution(targetSubstitution: SubstitutionDto?) {
        targetSubstitution?.let {
            if (it.teamClass == HOME && it.previousSituation.topOrBottom == BOTTOM ||
                    it.teamClass == VISITOR && it.previousSituation.topOrBottom == TOP
            ) {
                screenSuiteStack.push(
                    newOffenceSubstitutionInputSuite(
                        OffenceSubstitutionInputSuite.Mode.MODIFY, targetSubstitution
                    )
                )
            } else {
                screenSuiteStack.push(
                    newDefenceSubstitutionInputSuite(
                        DefenseSubstitutionInputSuite.Mode.MODIFY, targetSubstitution
                    )
                )
            }
            mActiveFragmentType.value = activeSuite!!.activeFragmentType
        }
    }

    fun openDeleteSubstitution(targetSubstitution: SubstitutionDto?) {
        // TODO: Implement
        targetSubstitution?.let {
            useCase.deleteSubstitution(targetSubstitution)
        }
    }


    fun back() {
        if (!transitionStack.isEmpty()) {
            mActiveFragmentType.value = transitionStack.pop()
        } else {
            reproduceInput(useCase.popLastPeriod())
        }
    }

    private fun transitionProgressTo(fragmentType: ActiveFragmentType) {
        transitionStack.push(mActiveFragmentType.value)
        mActiveFragmentType.value = fragmentType
    }

    private fun reproduceInput(periodDto: PeriodDto?) {
        initialize()

        pitchInputViewModel.reproduceInput(periodDto)

        if (periodDto is FoulDto) {
            transitionProgressTo(FOUL_BALL_INPUT)
            foulBallInputViewModel.reproduceInput(periodDto)
        }

        if (periodDto is BattingDto) {
            transitionProgressTo(BATTED_BALL_INPUT)
            battingInputViewModel.reproduceInput(periodDto)
        }

        if (periodDto is BallDto && periodDto.fieldPlays.isNotEmpty() ||
            periodDto is StrikeDto && periodDto.fieldPlays.isNotEmpty() ||
            periodDto is BattingDto ||
            periodDto is PlayWithoutPitchDto
        ) {
            transitionProgressTo(FIELD_PLAY_INPUT)
            fieldPlayInputViewModel.reproduceInput(periodDto)
        }

        if (periodDto is SubstitutionDto) {
            val topOrBottom = gameState.value!!.situation.topOrBottom
            if (periodDto.teamClass == HOME && topOrBottom == TOP
                ||
                periodDto.teamClass == TeamClass.VISITOR && topOrBottom == BOTTOM
            ) {
                transitionProgressTo(DEFENCE_SUBSTITUTION)
                defenseSubstitutionInputViewModel.reproduceInput(periodDto)
            } else {
                transitionProgressTo(OFFENCE_SUBSTITUTION)
                offenseSubstitutionInputViewModel.reproduceInput(periodDto)
            }
        }
    }

    private fun initialize() {
        // TODO: Implement
//        mPitchInputViewModel = null
//        mFoulBallInputViewModel = null
//        mBattingInputViewModel = null
//        mFieldPlayInputViewModel = null
//        mOffenseSubstitutionInputViewModel = null
//        mDefenseSubstitutionInputViewModel = null
        transitionStack.clear()
        mActiveFragmentType.value = PITCH_INPUT
    }

    init {
//        mActiveFragmentType.value = PITCH_INPUT
        screenSuiteStack.push(newPlayInputSuite())
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    private fun suiteCompleted() {
        screenSuiteStack.pop()

        if (screenSuiteStack.isEmpty()) {
            screenSuiteStack.push(newPlayInputSuite())
        }

        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    private fun suiteCanceled() {
        screenSuiteStack.pop()

        if (screenSuiteStack.isEmpty()) {
            val period = useCase.popLastPeriod()

            newPlayInputSuite().let {
                if (period is PlayDto) it.load(period)
                screenSuiteStack.push(it)
            }

            if (period is SubstitutionDto) {
                val topOrBottom = gameState.value!!.situation.topOrBottom
                if (period.teamClass == HOME && topOrBottom == TOP
                    ||
                    period.teamClass == TeamClass.VISITOR && topOrBottom == BOTTOM
                ) {
                    newDefenceSubstitutionInputSuite(
                        DefenseSubstitutionInputSuite.Mode.NEW, null
                    ).let {
                        it.load(period)
                        screenSuiteStack.push(it)
                    }
                } else {
                    newOffenceSubstitutionInputSuite(
                        OffenceSubstitutionInputSuite.Mode.NEW, null
                    ).let {
                        it.load(period)
                        screenSuiteStack.push(it)
                    }
                }
            }
        }

        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    private fun newPlayInputSuite(): PlayInputSuite {
        return PlayInputSuite(
            this, useCase, this::suiteCompleted, this::suiteCanceled, this::changeFragment
        )
    }

    private fun newOffenceSubstitutionInputSuite(
        mode: OffenceSubstitutionInputSuite.Mode,
        targetPeriod: PeriodDto?
    ): OffenceSubstitutionInputSuite {
        return OffenceSubstitutionInputSuite(
            this,
            useCase,
            mode,
            targetPeriod,
            this::suiteCompleted,
            this::suiteCanceled,
            this::changeFragment
        )
    }

    private fun newDefenceSubstitutionInputSuite(
        mode: DefenseSubstitutionInputSuite.Mode,
        targetPeriod: PeriodDto?
    ): DefenseSubstitutionInputSuite {
        return DefenseSubstitutionInputSuite(
            this,
            useCase,
            mode,
            targetPeriod,
            this::suiteCompleted,
            this::suiteCanceled,
            this::changeFragment
        )
    }

    private fun newEasyReferenceSuite(): EasyReferenceSuite {
        return EasyReferenceSuite(
            this,
            useCase,
            this::suiteCompleted,
            this::suiteCanceled,
            this::changeFragment,
            scoreBoardViewModel
        )
    }

    private fun changeFragment(type: ActiveFragmentType, suite: ScreenSuite) {
        if (suite == activeSuite) mActiveFragmentType.value = type
    }
}