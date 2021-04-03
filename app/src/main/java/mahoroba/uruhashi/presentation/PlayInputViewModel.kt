package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.TeamClass.HOME
import mahoroba.uruhashi.domain.game.TeamClass.VISITOR
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.presentation.base.BaseFragment
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*
import java.util.*

class PlayInputViewModel(
    application: Application,
    private val parentViewModel: ScoreKeepingViewModel,
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

    // endregion

    val gameBaseInfo = useCase.gameBaseInfo
    val gameState = useCase.latestGameState

    val fragmentMethod = LiveEvent<(BaseFragment) -> Unit>()

    fun saveGame() {
        parentViewModel.saveGame()
    }

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

    fun openGameInfoInput() {
        parentViewModel.switchToGameInfoInputFragment()
    }

    fun finishGameAsCompleted() {
        parentViewModel.finishGameAsCompleted()
    }

    fun openModifyPlay(targetPlay: PlayDto?) {
        screenSuiteStack.push(
            newPlayInputSuite(PlayInputSuite.Mode.MODIFY, targetPlay)
        )
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

    init {
        screenSuiteStack.push(newPlayInputSuite(PlayInputSuite.Mode.NEW, null))
        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    private fun suiteCompleted() {
        screenSuiteStack.pop()

        if (screenSuiteStack.isEmpty()) {
            screenSuiteStack.push(newPlayInputSuite(PlayInputSuite.Mode.NEW, null))
        }

        mActiveFragmentType.value = activeSuite!!.activeFragmentType
    }

    private fun suiteCanceled() {
        screenSuiteStack.pop()

        if (screenSuiteStack.isEmpty()) {
            val period = useCase.popLastPeriod()

            newPlayInputSuite(PlayInputSuite.Mode.NEW, period as? PlayDto).let {
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

    private fun newPlayInputSuite(
        mode: PlayInputSuite.Mode,
        targetPlay: PlayDto?
    ): PlayInputSuite {
        return PlayInputSuite(
            this, useCase, mode, targetPlay,
            this::suiteCompleted, this::suiteCanceled, this::changeFragment
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
            this::suiteCompleted,
            this::suiteCanceled,
            this::changeFragment,
            parentViewModel.scoreBoardViewModel
        )
    }

    private fun changeFragment(type: ActiveFragmentType, suite: ScreenSuite) {
        if (suite == activeSuite) mActiveFragmentType.value = type
    }
}