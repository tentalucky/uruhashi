package mahoroba.uruhashi.presentation

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PlayerProfile
import mahoroba.uruhashi.presentation.utility.ActivityNavigator
import mahoroba.uruhashi.usecase.PlayerManagementUseCase
import mahoroba.uruhashi.usecase.query.PlayerBelongingInfoDto
import kotlin.concurrent.thread

class PlayerListViewModel(application: Application) : AndroidViewModel(application) {
    private val useCase = PlayerManagementUseCase(
        RepositoryPresenter.getPlayerRepository(application),
        QueryServicePresenter.getPlayerQueryService(application),
        RepositoryPresenter.getTeamRepository(application)
    )


    private val playerSummaryList: LiveData<List<PlayerManagementUseCase.PlayerSummary>>
        get() = useCase.playerSummaryList

    val searchWord = MutableLiveData<String>()
    val filteredPlayerList = MediatorLiveData<List<PlayerManagementUseCase.PlayerSummary>>()

    private val mSelectedPlayer = MutableLiveData<PlayerProfile>()
    val isSelected: LiveData<Boolean> = Transformations.map(mSelectedPlayer) { it != null }
    val selectedPlayerId: LiveData<String> = Transformations.map(mSelectedPlayer) { it?.id?.value }
    val selectedPlayerFullName: LiveData<String> =
        Transformations.map(mSelectedPlayer) { it?.name?.fullName }
    val selectedPlayerFamilyName: LiveData<String> =
        Transformations.map(mSelectedPlayer) { it?.name?.familyName }
    val selectedPlayerFirstName: LiveData<String> =
        Transformations.map(mSelectedPlayer) { it?.name?.firstName }
    val selectedPlayerBats: LiveData<String> =
        Transformations.map(mSelectedPlayer) { it?.bats.toString() }
    val selectedPlayerThrows: LiveData<String> =
        Transformations.map(mSelectedPlayer) { it?.throws.toString() }

    private val mSelectedPlayerBelonging = MutableLiveData<List<PlayerBelongingInfoDto>>()
    val selectedPlayerBelonging: LiveData<List<PlayerBelongingInfoDto>>
        get() = mSelectedPlayerBelonging

    val onSelected = LiveEvent<Unit>()
    val navigating = LiveEvent<ActivityNavigator>()
    val activityMethod = LiveEvent<(Activity) -> Unit>()

    init {
        filteredPlayerList.addSource(playerSummaryList) { updatePlayerList() }
        filteredPlayerList.addSource(searchWord) { updatePlayerList() }
    }

    private fun updatePlayerList() {
        thread {
            filteredPlayerList.postValue(
                if (searchWord.value.isNullOrEmpty()) {
                    playerSummaryList.value
                } else {
                    playerSummaryList.value?.filter {
                        it.fullName?.contains(searchWord.value!!, true) ?: true
                    } ?: emptyList()
                }
            )
        }
    }

    fun selectCommand(playerId: ID) {
        thread {
            useCase.findPlayer(playerId).let {
                mSelectedPlayer.postValue(it)
                mSelectedPlayerBelonging.postValue(useCase.findPlayerBelongings(it))
            }
            onSelected.postCall(Unit)
        }
    }

    fun createCommand(v: View?) {
        navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.PLAYER_EDIT))
    }

    fun editCommand(v: View?) {
        if (mSelectedPlayer.value == null) return

        Bundle().let {
            it.putSerializable("id", mSelectedPlayer.value?.id)
            navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.PLAYER_EDIT, it))
        }
    }

    fun deleteCommand(v: View?) {
        if (mSelectedPlayer.value == null) return

        activityMethod.call(::showConfirmDeletingDialog)
    }

    private fun deleteCommandBody() {
        thread {
            useCase.deletePlayer(mSelectedPlayer.value!!.id)
            mSelectedPlayer.postValue(null)
            mSelectedPlayerBelonging.postValue(null)
        }
    }


    fun navigateToGameList() =
        navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.GAME_LIST))

    fun navigateToTeamList() =
        navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.TEAM_LIST))

    fun navigateToPlayerList() =
        navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.PLAYER_LIST))

    fun navigateToStadiumList() =
        navigating.call(ActivityNavigator(ActivityNavigator.ActivityType.STADIUM_LIST))

    private fun showConfirmDeletingDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("TITLE")
            .setMessage("MESSAGE")
            .setPositiveButton("No") { _, _ -> Unit }
            .setNegativeButton("OK") { _, _ -> deleteCommandBody() }
            .show()
    }
}