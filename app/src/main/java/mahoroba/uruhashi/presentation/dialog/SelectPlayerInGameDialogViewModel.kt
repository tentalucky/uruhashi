package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.*
import android.support.v4.app.DialogFragment
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.SearchPlayerUseCase
import kotlin.concurrent.thread

class SelectPlayerInGameDialogViewModel(
    application: Application, val teamId: ID?, val tag: String, val listener: Listener?
) : BaseViewModel(application) {

    // region inner classes
    class Factory(
        private val application: Application,
        private val teamId: ID?,
        private val tag: String,
        private val listener: Listener?
    ) : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SelectPlayerInGameDialogViewModel(application, teamId, tag, listener) as T
        }
    }

    interface Listener {
        fun onSelected(arg: ResultArg)
    }

    data class ResultArg(
        val playerId: ID,
        val playerName: PersonName,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?,
        val tag: String
    )
    // endregion

    private val useCase = SearchPlayerUseCase(
        QueryServicePresenter.getPlayerQueryService(application),
        RepositoryPresenter.getTeamRepository(application)
    )

    val searchUniformNumber = MutableLiveData<String>()

    private val mPlayerList = MediatorLiveData<List<SearchPlayerUseCase.PlayerSummary>>()
    val filteredPlayerList = MediatorLiveData<List<SearchPlayerUseCase.PlayerSummary>>()

    private val mTeamList = MutableLiveData<List<SearchPlayerUseCase.TeamSummary>>()
    val teamList = Transformations.map(mTeamList) { source ->
        ArrayList<String?>().apply {
            this.add("")
            source.forEach { this.add(it.teamName) }
        }
    }!!

    val selectedTeamPosition = MutableLiveData<Int>()
    private var selectedTeamId: ID?
        get() {
            if (selectedTeamPosition.value == null) return null
            if (selectedTeamPosition.value == 0) return null
            return mTeamList.value?.get(selectedTeamPosition.value!! - 1)?.teamId
        }
        set(value) {
            if (mTeamList.value == null) return
            for (i in 0 until mTeamList.value!!.count()) {
                if (mTeamList.value!![i].teamId == value) {
                    selectedTeamPosition.value = i + 1
                    return
                }
            }
            selectedTeamPosition.value = 0
        }

    val dialogMethods = LiveEvent<(DialogFragment) -> Unit>()

    init {
        dialogMethods.call { mTeamList.observe(it, Observer { selectedTeamId = teamId }) }

        mPlayerList.addSource(mTeamList) { updatePlayerList(selectedTeamId) }
        mPlayerList.addSource(selectedTeamPosition) { updatePlayerList(selectedTeamId) }

        filteredPlayerList.addSource(mPlayerList) { filterPlayerList() }
        filteredPlayerList.addSource(searchUniformNumber) { filterPlayerList() }

        thread {
            mTeamList.postValue(useCase.findAllTeams())
        }
    }

    private fun updatePlayerList(teamId: ID?) {
        thread {
            if (teamId == null) {
                mPlayerList.postValue(useCase.findPlayersBelongingInNothing())
            } else {
                mPlayerList.postValue(useCase.findPlayersBelongingIn(teamId))
            }
        }
    }

    private fun filterPlayerList() {
        filteredPlayerList.value = mPlayerList.value?.let {
            (if (searchUniformNumber.value ?: "" == "") it else it.filter { it.uniformNumber == searchUniformNumber.value })
                .sortedBy { it.uniformNumber }
        }
    }

    fun onSelect(player: SearchPlayerUseCase.PlayerSummary) {
        listener?.onSelected(
            ResultArg(
                player.playerId,
                player.name,
                player.bats,
                player.throws,
                player.uniformNumber,
                tag
            )
        )
        dialogMethods.call { it.dismiss() }
    }
}