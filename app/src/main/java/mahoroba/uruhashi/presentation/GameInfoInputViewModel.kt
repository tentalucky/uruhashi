package mahoroba.uruhashi.presentation

import android.app.AlertDialog
import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.arch.lifecycle.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.support.v7.widget.AppCompatEditText
import android.view.View
import mahoroba.uruhashi.common.*
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.domain.game.TeamClass
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.*
import mahoroba.uruhashi.presentation.enumStringConverter.PositionConverter
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class GameInfoInputViewModel(
    application: Application,
    private val parentViewModel: ScoreKeepingViewModel,
    private val useCase: ScoreKeepingUseCase
) :
    BaseViewModel(application)
    , SelectStadiumDialogViewModel.Listener
    , SelectTeamDialogViewModel.Listener
    , SelectPositionDialogViewModel.Listener
    , SelectPlayerInGameDialogViewModel.Listener {

    // region * Child classes *
    class LineupEditing(parent: GameInfoInputViewModel, val teamClass: TeamClass) {
        private val converter = PositionConverter(parent.myApplication)

        val teamId = when (teamClass) {
            TeamClass.HOME -> parent.homeTeamId
            TeamClass.VISITOR -> parent.visitorTeamId
        }
        val teamName = when (teamClass) {
            TeamClass.HOME -> parent.homeTeamName
            TeamClass.VISITOR -> parent.visitorTeamName
        }
        val hasDH: LiveData<Boolean> = when (teamClass) {
            TeamClass.HOME -> parent.homeTeamHasDH
            TeamClass.VISITOR -> parent.visitorTeamHasDH
        }
        private val startingPositions = when (teamClass) {
            TeamClass.HOME -> parent.homeTeamStartingPositions
            TeamClass.VISITOR -> parent.visitorTeamStartingPositions
        }
        val position1 = Transformations.map(startingPositions[0]) { converter.toString(it) }!!
        val position2 = Transformations.map(startingPositions[1]) { converter.toString(it) }!!
        val position3 = Transformations.map(startingPositions[2]) { converter.toString(it) }!!
        val position4 = Transformations.map(startingPositions[3]) { converter.toString(it) }!!
        val position5 = Transformations.map(startingPositions[4]) { converter.toString(it) }!!
        val position6 = Transformations.map(startingPositions[5]) { converter.toString(it) }!!
        val position7 = Transformations.map(startingPositions[6]) { converter.toString(it) }!!
        val position8 = Transformations.map(startingPositions[7]) { converter.toString(it) }!!
        val position9 = Transformations.map(startingPositions[8]) { converter.toString(it) }!!

        private val startingMembers = when (teamClass) {
            TeamClass.HOME -> parent.homeTeamStartingMembers
            TeamClass.VISITOR -> parent.visitorTeamStartingMembers
        }
        val player1Name: LiveData<String?> =
            Transformations.map(startingMembers[0]) { it.name.fullName }
        val player2Name: LiveData<String?> =
            Transformations.map(startingMembers[1]) { it.name.fullName }
        val player3Name: LiveData<String?> =
            Transformations.map(startingMembers[2]) { it.name.fullName }
        val player4Name: LiveData<String?> =
            Transformations.map(startingMembers[3]) { it.name.fullName }
        val player5Name: LiveData<String?> =
            Transformations.map(startingMembers[4]) { it.name.fullName }
        val player6Name: LiveData<String?> =
            Transformations.map(startingMembers[5]) { it.name.fullName }
        val player7Name: LiveData<String?> =
            Transformations.map(startingMembers[6]) { it.name.fullName }
        val player8Name: LiveData<String?> =
            Transformations.map(startingMembers[7]) { it.name.fullName }
        val player9Name: LiveData<String?> =
            Transformations.map(startingMembers[8]) { it.name.fullName }
        val playerPName: LiveData<String?> =
            Transformations.map(startingMembers[9]) { it.name.fullName }
    }

    data class PlayerProfileSet(
        val id: ID?,
        val name: PersonName,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?
    )
    // endregion

    private val myApplication = application

    // region * properties *
    val isLoaded = parentViewModel.isLoaded

    val gameName = MutableLiveData<String?>()
    val gameDate = MutableLiveData<Date?>()
    val gameDateText: LiveData<String?> =
        Transformations.map(gameDate) {
            if (it == null) "" else SimpleDateFormat("yyyy/MM/dd").format(
                it
            )
        }
    val playBallTimeText: LiveData<String?> =
        Transformations.map(gameDate) { if (it == null) "" else SimpleDateFormat("HH:mm").format(it) }
    val homeTeamId = MutableLiveData<ID?>()
    val homeTeamName = MutableLiveData<String?>()
    val homeTeamAbbreviatedName = MutableLiveData<String?>()
    val visitorTeamId = MutableLiveData<ID?>()
    val visitorTeamName = MutableLiveData<String?>()
    val visitorTeamAbbreviatedName = MutableLiveData<String?>()
    val stadiumId = MutableLiveData<ID?>()
    val stadiumName = MutableLiveData<String?>()
    val stadiumAbbreviatedName = MutableLiveData<String?>()

    val homeTeamStartingPositions = Array(9) {
        MutableLiveData<Position>()
    }
    val visitorTeamStartingPositions = Array(9) {
        MutableLiveData<Position>()
    }

    val homeTeamStartingMembers = Array(10) {
        MutableLiveData<PlayerProfileSet>()
    }
    val visitorTeamStartingMembers = Array(10) {
        MutableLiveData<PlayerProfileSet>()
    }

    private val mHomeTeamHasDH = MutableLiveData<Boolean>()
    val homeTeamHasDH: LiveData<Boolean> = mHomeTeamHasDH
    private val mVisitorTeamHasDH = MutableLiveData<Boolean>()
    val visitorTeamHasDH: LiveData<Boolean> = mVisitorTeamHasDH

    val fragmentMethod = LiveEvent<(Fragment) -> Unit>()
    val fragmentTransit = LiveEvent<(Fragment, Int) -> Unit>()

    var lineupEditing: LineupEditing? = null
        private set

    // endregion

    // region initialize
    init {
        // TODO: Implement
        setupInitialData()
    }

    private fun setupInitialData() {
        useCase.getGameBaseInfo()?.let {
            gameName.value = it.gameName
            gameDate.value = it.gameDate
            homeTeamId.value = it.homeTeamId
            homeTeamName.value = it.homeTeamName
            homeTeamAbbreviatedName.value = it.homeTeamAbbreviatedName
            visitorTeamId.value = it.visitorTeamId
            visitorTeamName.value = it.visitorTeamName
            visitorTeamAbbreviatedName.value = it.visitorTeamAbbreviatedName
            stadiumId.value = it.stadiumId
            stadiumName.value = it.stadiumName
            stadiumAbbreviatedName.value = it.stadiumAbbreviatedName
        }

        mHomeTeamHasDH.value = useCase.hasDHAtStarting(TeamClass.HOME)
        useCase.getStartingPositions(TeamClass.HOME).let {
            for (i in 0..8) {
                homeTeamStartingPositions[i].value = it[i]
            }
        }
        useCase.getStartingPlayersInformation(TeamClass.HOME).let {
            for (i in 0..8) {
                homeTeamStartingMembers[i].value = PlayerProfileSet(
                    it[i].playerID,
                    it[i].playerName ?: PersonName.getEmpty(),
                    it[i].bats,
                    it[i].throws,
                    it[i].uniformNumber
                )
            }
            if (mHomeTeamHasDH.value == true) {
                homeTeamStartingMembers[9].value = PlayerProfileSet(
                    it[9].playerID,
                    it[9].playerName ?: PersonName.getEmpty(),
                    it[9].bats,
                    it[9].throws,
                    it[9].uniformNumber
                )
            } else {
                homeTeamStartingMembers[9].value = PlayerProfileSet(
                    null,
                    PersonName.getEmpty(),
                    null,
                    null,
                    null
                )
            }
        }

        mVisitorTeamHasDH.value = useCase.hasDHAtStarting(TeamClass.VISITOR)
        useCase.getStartingPositions(TeamClass.VISITOR).let {
            for (i in 0..8) {
                visitorTeamStartingPositions[i].value = it[i]
            }
        }
        useCase.getStartingPlayersInformation(TeamClass.VISITOR).let {
            for (i in 0..8) {
                visitorTeamStartingMembers[i].value = PlayerProfileSet(
                    it[i].playerID,
                    it[i].playerName ?: PersonName.getEmpty(),
                    it[i].bats,
                    it[i].throws,
                    it[i].uniformNumber
                )
            }
            if (mVisitorTeamHasDH.value == true) {
                visitorTeamStartingMembers[9].value = PlayerProfileSet(
                    it[9].playerID,
                    it[9].playerName ?: PersonName.getEmpty(),
                    it[9].bats,
                    it[9].throws,
                    it[9].uniformNumber
                )
            } else {
                visitorTeamStartingMembers[9].value = PlayerProfileSet(
                    null,
                    PersonName.getEmpty(),
                    null,
                    null,
                    null
                )
            }
        }
    }
    // endregion

    // region game name
    fun onGameNameClicked(v: View?) {
        fragmentMethod.call(::openGameNameInputBox)
    }

    private fun openGameNameInputBox(fragment: Fragment) {
        val editText = AppCompatEditText(fragment.context)
        AlertDialog.Builder(fragment.context)
            .setView(editText)
            .setPositiveButton("OK") { _, _ -> gameName.value = editText.text.toString() }
            .show()
    }
    // endregion

    // region game date
    fun onGameDateClicked(v: View?) {
        fragmentMethod.call(::openGameDateInputBox)
    }

    private fun openGameDateInputBox(fragment: Fragment) {
        val calendar = Calendar.getInstance().apply {
            if (gameDate.value != null) {
                this.time = gameDate.value
            } else {
                this.time = Date()
            }
        }

        DatePickerDialog(
            fragment.context,
            { _, y, m, d ->
                gameDate.value = Calendar.getInstance().apply {
                    set(y, m, d, calendar.hourOfDay, calendar.minute)
                }.time
            },
            calendar.year,
            calendar.month,
            calendar.dayOfMonth
        ).show()
    }
    // endregion

    // region play ball
    fun onPlayBallTimeClicked(v: View?) {
        fragmentMethod.call(::openPlayBallTimeInputBox)
    }

    private fun openPlayBallTimeInputBox(fragment: Fragment) {
        val calendar = Calendar.getInstance().apply {
            if (gameDate.value != null) {
                this.time = gameDate.value
            } else {
                this.time = Date()
            }
        }

        TimePickerDialog(
            fragment.context,
            { _, h, m ->
                gameDate.value = Calendar.getInstance().apply {
                    set(calendar.year, calendar.month, calendar.dayOfMonth, h, m)
                }.time
            },
            calendar.hourOfDay,
            calendar.minute,
            true
        ).show()
    }
    // endregion

    // region * stadium *
    fun onSelectStadiumClicked(v: View?) {
        fragmentMethod.call {
            SelectStadiumDialogFragment.newInstance(this::class)
                .show(it.childFragmentManager, "")
        }
    }

    override fun onSelected(arg: SelectStadiumDialogViewModel.ResultArg) {
        thread {
            useCase.getStadiumInfo(arg.stadiumId).let {
                stadiumId.postValue(it.id)
                stadiumName.postValue(it.name)
                stadiumAbbreviatedName.postValue(it.abbreviatedName)
            }
        }
    }
    // endregion

    // region teams
    fun onSelectHomeTeamClicked(v: View?) {
        fragmentMethod.call {
            SelectTeamDialogFragment.newInstance(this::class)
                .show(it.childFragmentManager, "home")
        }
    }

    fun onSelectVisitorTeamClicked(v: View?) {
        fragmentMethod.call {
            SelectTeamDialogFragment.newInstance(this::class)
                .show(it.childFragmentManager, "visitor")
        }
    }

    override fun onSelected(arg: SelectTeamDialogViewModel.ResultArg) {
        thread {
            useCase.getTeamInfo(arg.teamId).let {
                when (arg.tag) {
                    "home" -> {
                        homeTeamId.postValue(it.id)
                        homeTeamName.postValue(it.name)
                        homeTeamAbbreviatedName.postValue(it.abbreviatedName)
                    }
                    "visitor" -> {
                        visitorTeamId.postValue(it.id)
                        visitorTeamName.postValue(it.name)
                        visitorTeamAbbreviatedName.postValue(it.abbreviatedName)
                    }
                }
            }
        }
    }
    // endregion

    // region starting lineups
    fun onSetHomeTeamStartingLineupClicked(v: View?) {
        lineupEditing = LineupEditing(this, TeamClass.HOME)
        fragmentTransit.call { frg, id -> openStartingLineupFragment(frg, id) }
    }

    fun onSetVisitorTeamStartingLineupClicked(v: View?) {
        lineupEditing = LineupEditing(this, TeamClass.VISITOR)
        fragmentTransit.call { frg, id -> openStartingLineupFragment(frg, id) }
    }

    private fun openStartingLineupFragment(frg: Fragment, id: Int) {
        frg.childFragmentManager.beginTransaction().apply {
            this.addToBackStack("")
            this.replace(id, StartingLineupEditFragment.newInstance())
            this.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
            this.commit()
        }
    }
    // endregion

    // region select positions
    fun onSetPosition1Clicked(v: View?) = openSelectPositionDialogFragment(0.toString())

    fun onSetPosition2Clicked(v: View?) = openSelectPositionDialogFragment(1.toString())
    fun onSetPosition3Clicked(v: View?) = openSelectPositionDialogFragment(2.toString())
    fun onSetPosition4Clicked(v: View?) = openSelectPositionDialogFragment(3.toString())
    fun onSetPosition5Clicked(v: View?) = openSelectPositionDialogFragment(4.toString())
    fun onSetPosition6Clicked(v: View?) = openSelectPositionDialogFragment(5.toString())
    fun onSetPosition7Clicked(v: View?) = openSelectPositionDialogFragment(6.toString())
    fun onSetPosition8Clicked(v: View?) = openSelectPositionDialogFragment(7.toString())
    fun onSetPosition9Clicked(v: View?) = openSelectPositionDialogFragment(8.toString())

    private fun openSelectPositionDialogFragment(tag: String) {
        fragmentMethod.call {
            SelectPositionDialogFragment
                .newInstance(this::class, lineupEditing!!.hasDH.value ?: false)
                .show(it.childFragmentManager, tag)
        }
    }

    override fun onSelected(arg: SelectPositionDialogViewModel.ResultArg) {
        when (lineupEditing!!.teamClass) {
            TeamClass.HOME -> homeTeamStartingPositions
            TeamClass.VISITOR -> visitorTeamStartingPositions
        }[arg.tag.toInt()].value = arg.position
    }
    // endregion

    // region select players
    fun onSetPlayer1Clicked(v: View?) = openSelectPlayerDialogFragment(0.toString())

    fun onSetPlayer2Clicked(v: View?) = openSelectPlayerDialogFragment(1.toString())
    fun onSetPlayer3Clicked(v: View?) = openSelectPlayerDialogFragment(2.toString())
    fun onSetPlayer4Clicked(v: View?) = openSelectPlayerDialogFragment(3.toString())
    fun onSetPlayer5Clicked(v: View?) = openSelectPlayerDialogFragment(4.toString())
    fun onSetPlayer6Clicked(v: View?) = openSelectPlayerDialogFragment(5.toString())
    fun onSetPlayer7Clicked(v: View?) = openSelectPlayerDialogFragment(6.toString())
    fun onSetPlayer8Clicked(v: View?) = openSelectPlayerDialogFragment(7.toString())
    fun onSetPlayer9Clicked(v: View?) = openSelectPlayerDialogFragment(8.toString())
    fun onSetPlayerPClicked(v: View?) = openSelectPlayerDialogFragment(9.toString())

    private fun openSelectPlayerDialogFragment(tag: String) {
        fragmentMethod.call {
            SelectPlayerInGameDialogFragment
                .newInstance(this::class, lineupEditing!!.teamId.value)
                .show(it.childFragmentManager, tag)
        }
    }

    override fun onSelected(arg: SelectPlayerInGameDialogViewModel.ResultArg) {
        when (lineupEditing!!.teamClass) {
            TeamClass.HOME -> homeTeamStartingMembers
            TeamClass.VISITOR -> visitorTeamStartingMembers
        }.get(arg.tag.toInt()).value =
            PlayerProfileSet(arg.playerId, arg.playerName, arg.bats, arg.throws, arg.uniformNumber)
    }
    // endregion

    // region switch DH
    fun onHasDHCheckedChanged(v: View?, isChecked: Boolean) {
        when (lineupEditing!!.teamClass) {
            TeamClass.HOME -> mHomeTeamHasDH
            TeamClass.VISITOR -> mVisitorTeamHasDH
        }.value = isChecked

        when (lineupEditing!!.teamClass) {
            TeamClass.HOME -> homeTeamStartingPositions
            TeamClass.VISITOR -> visitorTeamStartingPositions
        }.let {
            if (isChecked) {
                it.forEach { if (it.value == Position.PITCHER) it.value = Position.NO_ENTRY }
            } else {
                it.forEach {
                    if (it.value == Position.DESIGNATED_HITTER) it.value = Position.NO_ENTRY
                }
            }
        }

        when (lineupEditing!!.teamClass) {
            TeamClass.HOME -> homeTeamStartingMembers
            TeamClass.VISITOR -> visitorTeamStartingMembers
        }.let {
            if (!isChecked) {
                it.get(9).value = PlayerProfileSet(
                    null,
                    PersonName.getEmpty(),
                    null,
                    null,
                    null
                )
            }
        }
    }
    // endregion

    // region commit
    fun commitCommand(v: View?) = parentViewModel.commitGameInfo(v)
    // endregion

    fun goToBoxScoreInputCommand(v: View?) = parentViewModel.completeInputGameInfo(v)

    private fun commit() {
        useCase.setGameInformation(
            ScoreKeepingUseCase.GameInfoDto(
                gameName.value,
                gameDate.value,
                homeTeamId.value,
                homeTeamName.value,
                homeTeamAbbreviatedName.value,
                visitorTeamId.value,
                visitorTeamName.value,
                visitorTeamAbbreviatedName.value,
                stadiumId.value,
                stadiumName.value,
                stadiumAbbreviatedName.value
            )
        )

        val homeStartingPositions = ArrayList<Position>().apply {
            homeTeamStartingPositions.forEach { this.add(it.value ?: Position.NO_ENTRY) }
        }
        useCase.setStartingPosition(
            TeamClass.HOME, homeStartingPositions, homeTeamHasDH.value ?: false
        )

        val visitorStartingPositions = ArrayList<Position>().apply {
            visitorTeamStartingPositions.forEach { this.add(it.value ?: Position.NO_ENTRY) }
        }
        useCase.setStartingPosition(
            TeamClass.VISITOR, visitorStartingPositions, visitorTeamHasDH.value ?: false
        )

        for (i in 0..9) {
            if (homeTeamHasDH.value != true && i == 9) continue

            useCase.setStartingPlayersInformation(
                TeamClass.HOME,
                i,
                homeTeamStartingMembers[i].value?.id,
                homeTeamStartingMembers[i].value?.name,
                homeTeamStartingMembers[i].value?.bats,
                homeTeamStartingMembers[i].value?.throws,
                homeTeamStartingMembers[i].value?.uniformNumber
            )
        }

        for (i in 0..9) {
            if (visitorTeamHasDH.value != true && i == 9) continue

            useCase.setStartingPlayersInformation(
                TeamClass.VISITOR,
                i,
                visitorTeamStartingMembers[i].value?.id,
                visitorTeamStartingMembers[i].value?.name,
                visitorTeamStartingMembers[i].value?.bats,
                visitorTeamStartingMembers[i].value?.throws,
                visitorTeamStartingMembers[i].value?.uniformNumber
            )
        }
    }
}