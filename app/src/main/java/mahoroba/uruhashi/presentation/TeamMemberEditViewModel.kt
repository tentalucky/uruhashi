package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.*
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Toast
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.TeamProfile
import mahoroba.uruhashi.R
import mahoroba.uruhashi.presentation.base.BaseActivity
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.dialog.SelectPlayerDialogFragment
import mahoroba.uruhashi.usecase.TeamManagementUseCase
import kotlin.concurrent.thread

class TeamMemberEditViewModel(application: Application, private val teamId: ID)
    : BaseViewModel(application), SelectPlayerDialogFragment.OnSelectListener {

    class Factory(private val application: Application, private val bundle: Bundle?)
        :ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return TeamMemberEditViewModel(application, bundle?.getSerializable("id") as ID) as T
        }
    }

    private val useCase = TeamManagementUseCase(
        RepositoryPresenter.getTeamRepository(application),
        QueryServicePresenter.getTeamQueryService(application),
        RepositoryPresenter.getPlayerRepository(application))

    private val mIsLoaded = MutableLiveData<Boolean>()
    val isLoaded: LiveData<Boolean>
        get() = mIsLoaded

    private val mTeam = MutableLiveData<TeamProfile>()
    val teamName = Transformations.map(mTeam) { it?.name }

    private val mMembers = MutableLiveData<ArrayList<TeamMemberInfo>>()
    val members = Transformations.map(mMembers) { it as List<TeamMemberInfo> }!!

    val activityMethod = LiveEvent<(BaseActivity) -> Unit>()

    init {
        mIsLoaded.value = false
        thread {
            val team = useCase.findTeam(teamId)
            mTeam.postValue(team)
            mMembers.postValue(ArrayList<TeamMemberInfo>().apply {
                useCase.findTeamMembers(team).forEach { m ->
                    this.add(TeamMemberInfo(application, m.playerId, m.fullName, m.uniformNumber, false, false))
                }
            })

            mIsLoaded.postValue(true)
        }
    }

    fun addCommand(v: View?) {
        activityMethod.call{
            SelectPlayerDialogFragment().let { dlg ->
                dlg.onSelect = { s ->
                    mMembers.value?.add(TeamMemberInfo(getApplication(), ID(), s, null, true, false))
                    mMembers.value = mMembers.value
                }
                dlg.show(it.supportFragmentManager, "")
            }
        }
    }

    fun deletePlayer(playerId: ID) {
        mMembers.value?.firstOrNull{ it.playerId == playerId}?.let {
            if (it.adding) mMembers.value?.remove(it) else it.removing = true
            mMembers.value = mMembers.value
        }
    }

    fun cancelToDeletePlayer(playerId: ID) {
        mMembers.value?.firstOrNull{ it.playerId == playerId }?.removing = false
        mMembers.value = mMembers.value
    }

    fun changeUniformNumber(playerId: ID, uniformNumber: String) {
        mMembers.value?.firstOrNull{ it.playerId == playerId }?.uniformNumber = uniformNumber
        mMembers.value = mMembers.value
    }

    fun registerCommand(v: View?) {
        thread {
            useCase.updateTeamMembers(
                teamId,
                ArrayList<TeamManagementUseCase.TeamMember>().apply {
                    members.value?.forEach {
                        if (!it.removing) this.add(TeamManagementUseCase.TeamMember(it.playerId, it.uniformNumber))
                    }
                })
        }
        activityMethod.call(::closeActivity)
    }

    private fun closeActivity(activity: BaseActivity) = activity.finish()

    override fun onSelectPlayer(playerId: ID) {
        val handler = Handler()
        thread {
            val player = useCase.findPlayer(playerId)
            mMembers.value?.firstOrNull{ it.playerId == player.id }?.let {
                val message = "${player.name.fullName} has already been added."
                handler.post { Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show() }
                return@thread
            }

            mMembers.value?.add(
                TeamMemberInfo(getApplication(), player.id, player.name.fullName, null, true, false)
            )
            mMembers.postValue(mMembers.value)
        }
    }

    class TeamMemberInfo(
        private val application: Application,
        val playerId: ID,
        val playerName: String?,
        var uniformNumber: String?,
        val adding: Boolean,
        var removing: Boolean) {
        val status: String
            get() {
                if (adding) return application.getString(R.string.edit_member_status_added)
                if (removing) return application.getString(R.string.edit_member_status_removing)
                return application.getString(R.string.edit_member_status_default)
            }
    }
}