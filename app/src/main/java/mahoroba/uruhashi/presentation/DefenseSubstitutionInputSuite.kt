package mahoroba.uruhashi.presentation

import android.util.Log
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.presentation.base.ScreenSuite
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*

class DefenseSubstitutionInputSuite(
    private val parentViewModel: PlayInputViewModel,
    private val useCase: ScoreKeepingUseCase,
    val mode: Mode,
    val selectedPeriod: PeriodDto?,
    onCompleteListener: () -> Unit,
    onCancelListener: () -> Unit,
    activeFragmentSetter: (PlayInputViewModel.ActiveFragmentType, ScreenSuite) -> Unit
) : ScreenSuite(onCompleteListener, onCancelListener, activeFragmentSetter) {

    enum class Mode {
        NEW,
        INSERT,
        MODIFY,
        DELETE
    }

    override val activeFragmentType: PlayInputViewModel.ActiveFragmentType
        get() = PlayInputViewModel.ActiveFragmentType.DEFENCE_SUBSTITUTION

    private var mDefenseSubstitutionInputViewModel: DefenseSubstitutionInputViewModel? = null
    val defenseSubstitutionInputViewModel: DefenseSubstitutionInputViewModel
        get() {
            if (mDefenseSubstitutionInputViewModel == null) mDefenseSubstitutionInputViewModel =
                DefenseSubstitutionInputViewModel(parentViewModel.getApplication(), this)
            return mDefenseSubstitutionInputViewModel!!
        }

    val gameState = useCase.latestGameState
    val gameBaseInfo = useCase.gameBaseInfo

    fun settleDefenceTeamSubstitution() {
        val newPositionList = ArrayList<Pair<Int, Position>>()
        val newPlayerList = ArrayList<Pair<Int, PlayerInfoDto>>()

        mDefenseSubstitutionInputViewModel?.newPosition1?.let { newPositionList.add(Pair(0, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition2?.let { newPositionList.add(Pair(1, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition3?.let { newPositionList.add(Pair(2, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition4?.let { newPositionList.add(Pair(3, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition5?.let { newPositionList.add(Pair(4, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition6?.let { newPositionList.add(Pair(5, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition7?.let { newPositionList.add(Pair(6, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition8?.let { newPositionList.add(Pair(7, it)) }
        mDefenseSubstitutionInputViewModel?.newPosition9?.let { newPositionList.add(Pair(8, it)) }

        val f = fun(p: DefenseSubstitutionInputViewModel.PlayerProfileSet?, i: Int) {
            if (p == null) return
            newPlayerList.add(
                Pair(i, PlayerInfoDto(p.id, p.name, p.bats, p.throws, p.uniformNumber))
            )
        }

        f(mDefenseSubstitutionInputViewModel?.newPlayer1, 0)
        f(mDefenseSubstitutionInputViewModel?.newPlayer2, 1)
        f(mDefenseSubstitutionInputViewModel?.newPlayer3, 2)
        f(mDefenseSubstitutionInputViewModel?.newPlayer4, 3)
        f(mDefenseSubstitutionInputViewModel?.newPlayer5, 4)
        f(mDefenseSubstitutionInputViewModel?.newPlayer6, 5)
        f(mDefenseSubstitutionInputViewModel?.newPlayer7, 6)
        f(mDefenseSubstitutionInputViewModel?.newPlayer8, 7)
        f(mDefenseSubstitutionInputViewModel?.newPlayer9, 8)
        f(mDefenseSubstitutionInputViewModel?.newPlayerP, 9)

        when (mode) {
            Mode.NEW ->
                useCase.addDefenceSubstitution(newPositionList, newPlayerList)
            Mode.INSERT ->
                useCase.insertDefenceSubstitution(selectedPeriod!!, newPositionList, newPlayerList)
            Mode.MODIFY ->
                useCase.modifyDefenceSubstitution(
                    selectedPeriod!! as SubstitutionDto, newPositionList, newPlayerList
                )
        }
        complete()
    }

    fun back() {
        cancel()
    }

    fun load(substitutionDto: SubstitutionDto) {
        defenseSubstitutionInputViewModel.reproduceInput(substitutionDto)
    }
}