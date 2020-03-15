package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.support.v4.app.DialogFragment
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.StadiumManagementUseCase

class SelectStadiumDialogViewModel(application: Application, val listener: Listener?) : BaseViewModel(application) {

    class Factory(private val application: Application, private val caller: Listener?) :
        ViewModelProvider.NewInstanceFactory() {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SelectStadiumDialogViewModel(application, caller) as T
        }
    }

    interface Listener {
        fun onSelected(arg: ResultArg)
    }

    data class ResultArg(val stadiumId: ID)

    private val useCase = StadiumManagementUseCase(
        RepositoryPresenter.getStadiumRepository(application)
    )

    val stadiums = useCase.allStadiums

    val dialogMethods = LiveEvent<(DialogFragment) -> Unit>()

    fun onSelected(stadiumId: ID) {
        listener?.onSelected(ResultArg(stadiumId))
        dialogMethods.call { it.dismiss() }
    }

}