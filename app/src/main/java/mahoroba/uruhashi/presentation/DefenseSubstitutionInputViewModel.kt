package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MediatorLiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.util.Log
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.domain.game.TeamClass
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPlayerInGameDialogViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPositionDialogViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class DefenseSubstitutionInputViewModel(
    application: Application,
    private val inputSuite: DefenseSubstitutionInputSuite
) : BaseViewModel(application),
    SelectPositionDialogViewModel.Listener,
    SelectPlayerInGameDialogViewModel.Listener {

    // region * Child classes *
    data class PlayerProfileSet(
        val id: ID?,
        val name: PersonName?,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?
    )
    // endregion * Child classes *

    // region * Properties: Present players *
    val presentPosition1: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(0).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(0)?.abbreviated
        }
    val presentPosition2: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(1).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(1)?.abbreviated
        }
    val presentPosition3: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(2).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(2)?.abbreviated
        }
    val presentPosition4: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(3).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(3)?.abbreviated
        }
    val presentPosition5: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(4).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(4)?.abbreviated
        }
    val presentPosition6: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(5).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(5)?.abbreviated
        }
    val presentPosition7: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(6).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(6)?.abbreviated
        }
    val presentPosition8: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(7).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(7)?.abbreviated
        }
    val presentPosition9: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPosition(8).abbreviated
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPosition(8)?.abbreviated
        }

    val presentPlayerName1: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(0).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(0)?.playerName?.fullName
        }
    val presentPlayerName2: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(1).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(1)?.playerName?.fullName
        }
    val presentPlayerName3: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(2).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(2)?.playerName?.fullName
        }
    val presentPlayerName4: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(3).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(3)?.playerName?.fullName
        }
    val presentPlayerName5: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(4).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(4)?.playerName?.fullName
        }
    val presentPlayerName6: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(5).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(5)?.playerName?.fullName
        }
    val presentPlayerName7: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(6).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(6)?.playerName?.fullName
        }
    val presentPlayerName8: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(7).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(7)?.playerName?.fullName
        }
    val presentPlayerName9: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.getPlayer(8).playerName?.fullName
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.getPlayer(8)?.playerName?.fullName
        }
    val presentPlayerNameP: String?
        get() = if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.defenceTeamLineup.let {
                if (it.hasDH) it.getPlayer(9).playerName?.fullName else null
            }
        } else {
            inputSuite.gameState.value?.defenceTeamLineup?.let {
                if (it.hasDH) it.getPlayer(9).playerName?.fullName else null
            }
        }
    // endregion * Properties: Present players *

    val player10Enable: LiveData<Boolean> = Transformations.map(inputSuite.gameState) {
        it.defenceTeamLineup.hasDH
    }

    private val mNewPosition1 = MutableLiveData<Position>()
    val newPosition1 get() = mNewPosition1.value
    val newPosition1Text: LiveData<String?> =
        Transformations.map(mNewPosition1) { it?.abbreviated }

    private val mNewPosition2 = MutableLiveData<Position>()
    val newPosition2 get() = mNewPosition2.value
    val newPosition2Text: LiveData<String?> =
        Transformations.map(mNewPosition2) { it?.abbreviated }

    private val mNewPosition3 = MutableLiveData<Position>()
    val newPosition3 get() = mNewPosition3.value
    val newPosition3Text: LiveData<String?> =
        Transformations.map(mNewPosition3) { it?.abbreviated }

    private val mNewPosition4 = MutableLiveData<Position>()
    val newPosition4 get() = mNewPosition4.value
    val newPosition4Text: LiveData<String?> =
        Transformations.map(mNewPosition4) { it?.abbreviated }

    private val mNewPosition5 = MutableLiveData<Position>()
    val newPosition5 get() = mNewPosition5.value
    val newPosition5Text: LiveData<String?> =
        Transformations.map(mNewPosition5) { it?.abbreviated }

    private val mNewPosition6 = MutableLiveData<Position>()
    val newPosition6 get() = mNewPosition6.value
    val newPosition6Text: LiveData<String?> =
        Transformations.map(mNewPosition6) { it?.abbreviated }

    private val mNewPosition7 = MutableLiveData<Position>()
    val newPosition7 get() = mNewPosition7.value
    val newPosition7Text: LiveData<String?> =
        Transformations.map(mNewPosition7) { it?.abbreviated }

    private val mNewPosition8 = MutableLiveData<Position>()
    val newPosition8 get() = mNewPosition8.value
    val newPosition8Text: LiveData<String?> =
        Transformations.map(mNewPosition8) { it?.abbreviated }

    private val mNewPosition9 = MutableLiveData<Position>()
    val newPosition9 get() = mNewPosition9.value
    val newPosition9Text: LiveData<String?> =
        Transformations.map(mNewPosition9) { it?.abbreviated }

    private val mNewPlayer1 = MutableLiveData<PlayerProfileSet>()
    val newPlayer1 get() = mNewPlayer1.value
    val newPlayer1Name: LiveData<String?> =
        Transformations.map(mNewPlayer1) { it?.name?.fullName }

    private val mNewPlayer2 = MutableLiveData<PlayerProfileSet>()
    val newPlayer2 get() = mNewPlayer2.value
    val newPlayer2Name: LiveData<String?> =
        Transformations.map(mNewPlayer2) { it?.name?.fullName }

    private val mNewPlayer3 = MutableLiveData<PlayerProfileSet>()
    val newPlayer3 get() = mNewPlayer3.value
    val newPlayer3Name: LiveData<String?> =
        Transformations.map(mNewPlayer3) { it?.name?.fullName }

    private val mNewPlayer4 = MutableLiveData<PlayerProfileSet>()
    val newPlayer4 get() = mNewPlayer4.value
    val newPlayer4Name: LiveData<String?> =
        Transformations.map(mNewPlayer4) { it?.name?.fullName }

    private val mNewPlayer5 = MutableLiveData<PlayerProfileSet>()
    val newPlayer5 get() = mNewPlayer5.value
    val newPlayer5Name: LiveData<String?> =
        Transformations.map(mNewPlayer5) { it?.name?.fullName }

    private val mNewPlayer6 = MutableLiveData<PlayerProfileSet>()
    val newPlayer6 get() = mNewPlayer6.value
    val newPlayer6Name: LiveData<String?> =
        Transformations.map(mNewPlayer6) { it?.name?.fullName }

    private val mNewPlayer7 = MutableLiveData<PlayerProfileSet>()
    val newPlayer7 get() = mNewPlayer7.value
    val newPlayer7Name: LiveData<String?> =
        Transformations.map(mNewPlayer7) { it?.name?.fullName }

    private val mNewPlayer8 = MutableLiveData<PlayerProfileSet>()
    val newPlayer8 get() = mNewPlayer8.value
    val newPlayer8Name: LiveData<String?> =
        Transformations.map(mNewPlayer8) { it?.name?.fullName }

    private val mNewPlayer9 = MutableLiveData<PlayerProfileSet>()
    val newPlayer9 get() = mNewPlayer9.value
    val newPlayer9Name: LiveData<String?> =
        Transformations.map(mNewPlayer9) { it?.name?.fullName }

    private val mNewPlayerP = MutableLiveData<PlayerProfileSet>()
    val newPlayerP get() = mNewPlayerP.value
    val newPlayerPName: LiveData<String?> =
        Transformations.map(mNewPlayerP) { it?.name?.fullName }

    val commitEnable = MediatorLiveData<Boolean>()

    val onOpenSelectPositionDialog = LiveEvent<Pair<Boolean, String>>()
    val onOpenSelectPlayerDialog = LiveEvent<Pair<ID?, String>>()

    init {
        val enable = fun() =
            mNewPosition1.value != null ||
                    mNewPosition2.value != null ||
                    mNewPosition3.value != null ||
                    mNewPosition4.value != null ||
                    mNewPosition5.value != null ||
                    mNewPosition6.value != null ||
                    mNewPosition7.value != null ||
                    mNewPosition8.value != null ||
                    mNewPosition9.value != null ||
                    mNewPlayer1.value != null ||
                    mNewPlayer2.value != null ||
                    mNewPlayer3.value != null ||
                    mNewPlayer4.value != null ||
                    mNewPlayer5.value != null ||
                    mNewPlayer6.value != null ||
                    mNewPlayer7.value != null ||
                    mNewPlayer8.value != null ||
                    mNewPlayer9.value != null ||
                    mNewPlayerP.value != null
        commitEnable.addSource(mNewPosition1) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition2) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition3) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition4) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition5) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition6) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition7) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition8) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPosition9) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer1) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer2) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer3) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer4) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer5) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer6) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer7) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer8) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayer9) { commitEnable.value = enable() }
        commitEnable.addSource(mNewPlayerP) { commitEnable.value = enable() }

        if (inputSuite.mode == DefenseSubstitutionInputSuite.Mode.MODIFY) {
            reproduceInput(inputSuite.selectedPeriod as ScoreKeepingUseCase.SubstitutionDto)
        }
    }

    private val teamID: ID?
        get() = when (if (inputSuite.selectedPeriod != null) {
            inputSuite.selectedPeriod.topOrBottom
        } else {
            inputSuite.gameState.value?.situation?.topOrBottom
        }) {
            TOP -> inputSuite.gameBaseInfo.value?.homeTeamId
            BOTTOM -> inputSuite.gameBaseInfo.value?.visitorTeamId
            else -> null
        }

    fun onSelectingPosition1(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "1"))
    fun onSelectingPosition2(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "2"))
    fun onSelectingPosition3(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "3"))
    fun onSelectingPosition4(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "4"))
    fun onSelectingPosition5(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "5"))
    fun onSelectingPosition6(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "6"))
    fun onSelectingPosition7(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "7"))
    fun onSelectingPosition8(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "8"))
    fun onSelectingPosition9(v: View?) = onOpenSelectPositionDialog.call(Pair(false, "9"))

    fun onSelectingPlayer1(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "1"))
    fun onSelectingPlayer2(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "2"))
    fun onSelectingPlayer3(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "3"))
    fun onSelectingPlayer4(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "4"))
    fun onSelectingPlayer5(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "5"))
    fun onSelectingPlayer6(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "6"))
    fun onSelectingPlayer7(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "7"))
    fun onSelectingPlayer8(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "8"))
    fun onSelectingPlayer9(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "9"))
    fun onSelectingPlayerP(v: View?) = onOpenSelectPlayerDialog.call(Pair(teamID, "P"))

    fun onClearingOrder1(v: View?) {
        mNewPosition1.value = null
        mNewPlayer1.value = null
    }

    fun onClearingOrder2(v: View?) {
        mNewPosition2.value = null
        mNewPlayer2.value = null
    }

    fun onClearingOrder3(v: View?) {
        mNewPosition3.value = null
        mNewPlayer3.value = null
    }

    fun onClearingOrder4(v: View?) {
        mNewPosition4.value = null
        mNewPlayer4.value = null
    }

    fun onClearingOrder5(v: View?) {
        mNewPosition5.value = null
        mNewPlayer5.value = null
    }

    fun onClearingOrder6(v: View?) {
        mNewPosition6.value = null
        mNewPlayer6.value = null
    }

    fun onClearingOrder7(v: View?) {
        mNewPosition7.value = null
        mNewPlayer7.value = null
    }

    fun onClearingOrder8(v: View?) {
        mNewPosition8.value = null
        mNewPlayer8.value = null
    }

    fun onClearingOrder9(v: View?) {
        mNewPosition9.value = null
        mNewPlayer9.value = null
    }

    fun onClearingOrderP(v: View?) {
        mNewPlayerP.value = null
    }

    override fun onSelected(arg: SelectPositionDialogViewModel.ResultArg) {
        when (arg.tag) {
            "1" -> mNewPosition1
            "2" -> mNewPosition2
            "3" -> mNewPosition3
            "4" -> mNewPosition4
            "5" -> mNewPosition5
            "6" -> mNewPosition6
            "7" -> mNewPosition7
            "8" -> mNewPosition8
            "9" -> mNewPosition9
            else -> null
        }?.value = arg.position
    }

    override fun onSelected(arg: SelectPlayerInGameDialogViewModel.ResultArg) {
        when (arg.tag) {
            "1" -> mNewPlayer1
            "2" -> mNewPlayer2
            "3" -> mNewPlayer3
            "4" -> mNewPlayer4
            "5" -> mNewPlayer5
            "6" -> mNewPlayer6
            "7" -> mNewPlayer7
            "8" -> mNewPlayer8
            "9" -> mNewPlayer9
            "P" -> mNewPlayerP
            else -> null
        }?.value =
            PlayerProfileSet(arg.playerId, arg.playerName, arg.bats, arg.throws, arg.uniformNumber)
    }

    fun backCommand(v: View?) {
        inputSuite.back()
    }

    fun commitCommand(v: View?) {
        inputSuite.settleDefenceTeamSubstitution()
    }

    fun reproduceInput(dto: ScoreKeepingUseCase.SubstitutionDto) {
        val newPositions = arrayOf(
            mNewPosition1,
            mNewPosition2,
            mNewPosition3,
            mNewPosition4,
            mNewPosition5,
            mNewPosition6,
            mNewPosition7,
            mNewPosition8,
            mNewPosition9
        )
        dto.newPositionList.forEach {
            newPositions[it.first].value = it.second
        }

        val newPlayers = arrayOf(
            mNewPlayer1,
            mNewPlayer2,
            mNewPlayer3,
            mNewPlayer4,
            mNewPlayer5,
            mNewPlayer6,
            mNewPlayer7,
            mNewPlayer8,
            mNewPlayer9,
            mNewPlayerP
        )
        dto.newPlayerList.forEach {
            newPlayers[it.first].value = PlayerProfileSet(
                it.second.playerID,
                it.second.playerName,
                it.second.bats,
                it.second.throws,
                it.second.uniformNumber
            )
        }
    }
}