package mahoroba.uruhashi.presentation

import android.app.Activity
import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.usecase.TeamManagementUseCase
import kotlin.concurrent.thread

class TeamEditViewModel(application: Application, private val teamId: ID?) : AndroidViewModel(application) {

    class Factory(private val application: Application, private val bundle: Bundle?)
        : ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TeamEditViewModel(application, bundle?.getSerializable("id") as ID?) as T
        }
    }

    private val useCase = TeamManagementUseCase(
        RepositoryPresenter.getTeamRepository(application),
        QueryServicePresenter.getTeamQueryService(application),
        RepositoryPresenter.getPlayerRepository(application))

    private val mIsLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean>
        get() = mIsLoaded

    private val mTeamId = MutableLiveData<String>()
    val id: LiveData<String>
        get() = mTeamId

    val name = MutableLiveData<String>()
    val abbreviatedName = MutableLiveData<String>()
    val priority = MutableLiveData<Float>()
    val members = MutableLiveData<List<TeamMemberInfo>>()

    val activityMethod = LiveEvent<(Activity) -> Unit>()

    init {
        mIsLoaded.value = false
        thread {
            if (teamId?.isJustGenerated == false) {
                useCase.findTeam(teamId).let { team ->
                    mTeamId.postValue(team.id.value)
                    name.postValue(team.name)
                    abbreviatedName.postValue(team.abbreviatedName)
                    priority.postValue(team.priority.toFloat())
                    members.postValue(ArrayList<TeamMemberInfo>().apply {
                        useCase.findTeamMembers(team).forEach { m ->
                            this.add(TeamMemberInfo(m.playerId, m.fullName, m.uniformNumber))
                        }
                    })
                }
            } else {
                mTeamId.postValue("")
                name.postValue("")
                abbreviatedName.postValue("")
                priority.postValue(0f)
                members.postValue(emptyList())
            }

            mIsLoaded.postValue(true)
        }
    }

    fun registerCommand(v: View) {
        thread {
            if (teamId == null) {
                useCase.registerNewTeam(
                    name.value,
                    abbreviatedName.value,
                    priority.value!!.toInt()
                )
            } else {
                useCase.modifyTeam(
                    teamId,
                    name.value,
                    abbreviatedName.value,
                    priority.value!!.toInt()
                )
            }
        }

        activityMethod.call(::closeActivity)
    }

    private fun closeActivity(activity: Activity) = activity.finish()

    class TeamMemberInfo(val playerId: ID, val playerName: String?, var uniformNumber: String?)
}