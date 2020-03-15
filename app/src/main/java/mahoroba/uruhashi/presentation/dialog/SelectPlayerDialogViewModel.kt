package mahoroba.uruhashi.presentation.dialog

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.usecase.PlayerManagementUseCase

class SelectPlayerDialogViewModel(application: Application) : AndroidViewModel(application) {
    private val useCase = PlayerManagementUseCase(
        RepositoryPresenter.getPlayerRepository(application),
        QueryServicePresenter.getPlayerQueryService(application),
        RepositoryPresenter.getTeamRepository(application)
    )

    val players : LiveData<List<PlayerManagementUseCase.PlayerSummary>>
        get() = useCase.playerSummaryList

    val dialogMethod = LiveEvent<(SelectPlayerDialogFragment) -> Unit>()

    fun onSelect(playerId: ID) {
        dialogMethod.call{
            if (it.caller?.viewModel is SelectPlayerDialogFragment.OnSelectListener) {
                (it.caller?.viewModel as SelectPlayerDialogFragment.OnSelectListener).onSelectPlayer(playerId)
            }
            it.dismiss()
        }
    }
}
