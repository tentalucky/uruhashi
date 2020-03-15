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
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.Stadium
import mahoroba.uruhashi.usecase.StadiumManagementUseCase

class StadiumListViewModel(application: Application) : AndroidViewModel(application) {
    private val useCase = StadiumManagementUseCase(RepositoryPresenter.getStadiumRepository(application))

    val allStadiums : LiveData<List<Stadium>>
        get() = useCase.allStadiums

    private val mSelectedStadium = MutableLiveData<Stadium>()
    val isSelected: LiveData<Boolean> = Transformations.map(mSelectedStadium) { it != null }
    val selectedStadiumId: LiveData<String> = Transformations.map(mSelectedStadium) { it?.id?.value }
    val selectedStadiumName: LiveData<String> = Transformations.map(mSelectedStadium) { it?.name }
    val selectedStadiumAbbreviatedName: LiveData<String> = Transformations.map(mSelectedStadium) { it?.abbreviatedName }
    val selectedStadiumPriority: LiveData<Float> = Transformations.map(mSelectedStadium) {
        if (it != null) it.priority.toFloat() else 0f
    }

    val onSelected = LiveEvent<Unit>()
    val navigate = LiveEvent<ActivityNavigator>()
    val activityMethod = LiveEvent<(Activity) -> Unit>()

    fun select(position: Int) {
        mSelectedStadium.value = allStadiums.value!![position]
        onSelected.call(Unit)
    }

    fun createCommand(v: View?) {
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.STADIUM_EDIT))
    }

    fun editCommand(v: View?) {
        if (mSelectedStadium.value == null) return

        Bundle().let {
            it.putSerializable("id", mSelectedStadium.value?.id!!)
            navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.STADIUM_EDIT, it))
        }
    }

    fun deleteCommand(v: View?) {
        if (mSelectedStadium.value == null) return

        activityMethod.call(::showConfirmDeletingDialog)
    }

    private fun deleteCommandBody() {
        useCase.deleteStadium(mSelectedStadium.value?.id!!)
        mSelectedStadium.postValue(null)
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