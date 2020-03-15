package mahoroba.uruhashi.presentation

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.View
import mahoroba.uruhashi.presentation.utility.ActivityNavigator
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.TeamProfile
import mahoroba.uruhashi.usecase.TeamManagementUseCase
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto
import kotlin.concurrent.thread

class TeamListViewModel(application: Application) : AndroidViewModel(application) {
    private val useCase = TeamManagementUseCase(
        RepositoryPresenter.getTeamRepository(application),
        QueryServicePresenter.getTeamQueryService(application),
        RepositoryPresenter.getPlayerRepository(application)
        )

    val teamSummaryList : LiveData<List<TeamManagementUseCase.TeamSummary>>
        get() = useCase.teamSummaryList

    private val mSelectedTeam = MutableLiveData<TeamProfile>()
    val isSelected: LiveData<Boolean> = Transformations.map(mSelectedTeam){ it != null }
    val selectedTeamId: LiveData<String> = Transformations.map(mSelectedTeam) { it?.id?.value }
    val selectedTeamName: LiveData<String> = Transformations.map(mSelectedTeam) { it?.name }
    val selectedTeamAbbreviatedName: LiveData<String> = Transformations.map(mSelectedTeam) { it?.abbreviatedName }
    val selectedTeamPriority: LiveData<Float> = Transformations.map(mSelectedTeam) {
        if (it != null) it.priority.toFloat() else 0f
    }

    private val mSelectedTeamMembers = MutableLiveData<List<TeamMemberInfoDto>>()
    val selectedTeamMembers: LiveData<List<TeamMemberInfoDto>>
        get() = mSelectedTeamMembers

    val onSelected = LiveEvent<Unit>()
    val navigate = LiveEvent<ActivityNavigator>()
    val activityMethod = LiveEvent<(Activity) -> Unit>()

    fun selectCommand(teamId: ID) {
        thread {
            useCase.findTeam(teamId).let {
                mSelectedTeam.postValue(it)
                mSelectedTeamMembers.postValue(useCase.findTeamMembers(it))
            }
            onSelected.postCall(Unit)
        }
    }

    fun createCommand(v: View?) {
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.TEAM_EDIT))
    }

    fun editCommand(v: View?) {
        if (mSelectedTeam.value == null) return

        Bundle().let {
            it.putSerializable("id", mSelectedTeam.value?.id)
            navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.TEAM_EDIT, it))
        }
    }

    fun editMemberCommand(v: View?) {
        if (mSelectedTeam.value == null) return

        Bundle().let {
            it.putSerializable("id", mSelectedTeam.value?.id)
            navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.TEAM_MEMBER_EDIT, it))
        }
    }

    fun deleteCommand(v: View?) {
        if (mSelectedTeam.value == null) return

        activityMethod.call(::showConfirmDeletingDialog)
    }

    private fun deleteCommandBody() {
        thread {
            useCase.deleteTeam(mSelectedTeam.value!!.id)
            mSelectedTeam.postValue(null)
            mSelectedTeamMembers.postValue(null)
        }
    }

    fun navigateToGameList() =
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.GAME_LIST))

    fun navigateToTeamList() =
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.TEAM_LIST))

    fun navigateToPlayerList() =
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.PLAYER_LIST))

    fun navigateToStadiumList() =
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.STADIUM_LIST))

    private fun showConfirmDeletingDialog(activity: Activity) {
        AlertDialog.Builder(activity)
            .setTitle("TITLE")
            .setMessage("MESSAGE")
            .setPositiveButton("No") { _, _ -> Unit }
            .setNegativeButton("OK") { _, _ -> deleteCommandBody()}
            .show()
    }
}