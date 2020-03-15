package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.DialogFragment
import android.util.Log
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.TeamManagementUseCase

class SelectTeamDialogViewModel(application: Application, val tag: String?, val listener: Listener?) :
    BaseViewModel(application) {

    class Factory(private val application: Application, private val tag: String?, private val listener: Listener?) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SelectTeamDialogViewModel(application, tag, listener) as T
        }
    }

    interface Listener {
        fun onSelected(arg: ResultArg)
    }

    data class ResultArg(val teamId: ID, val tag: String?)

    private val useCase = TeamManagementUseCase(
        RepositoryPresenter.getTeamRepository(application),
        QueryServicePresenter.getTeamQueryService(application),
        RepositoryPresenter.getPlayerRepository(application)
    )

    val teams = useCase.teamSummaryList

    val dialogMethods = LiveEvent<(DialogFragment) -> Unit>()

    fun onSelected(teamId: ID) {
        listener?.onSelected(ResultArg(teamId, tag))
        dialogMethods.call { it.dismiss() }
    }
}