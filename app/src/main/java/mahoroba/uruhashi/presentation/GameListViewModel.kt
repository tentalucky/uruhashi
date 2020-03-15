package mahoroba.uruhashi.presentation

import android.Manifest
import android.app.Activity
import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.di.ImportExportServicePresenter
import mahoroba.uruhashi.di.QueryServicePresenter
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.presentation.utility.ActivityNavigator
import mahoroba.uruhashi.presentation.utility.PermissionService
import mahoroba.uruhashi.usecase.GameManagementUseCase
import mahoroba.uruhashi.usecase.query.GameSummaryDto
import kotlin.concurrent.thread

class GameListViewModel(application: Application) : BaseViewModel(application) {
    private val useCase = GameManagementUseCase(
        RepositoryPresenter.getGameRepository(application),
        QueryServicePresenter.getGameQueryService(application),
        ImportExportServicePresenter.getGameExporter(application)
    )

    private val PERMISSION_REQUEST_EXPORT_GAME = 1000

    val gameSummaryList: LiveData<List<GameSummaryDto>>
        get() = useCase.gameSummaryList

    private val mSelectedGame = MutableLiveData<Game>()
    val isSelected: LiveData<Boolean> = Transformations.map(mSelectedGame) { it != null }
    val selectedGame: LiveData<Game> = mSelectedGame
    val selectedGameId: LiveData<String> =
        Transformations.map(mSelectedGame) { it?.gameInfo?.homeTeam?.teamId.toString() }
    val selectedGameName: LiveData<String> =
        Transformations.map(mSelectedGame) { it?.gameInfo?.gameName }
    val selectedGameHomeTeam: LiveData<String> =
        Transformations.map(mSelectedGame) { it?.gameInfo?.homeTeam?.teamName }
    val selectedGameVisitorTeam: LiveData<String> =
        Transformations.map(mSelectedGame) { it?.gameInfo?.visitorTeam?.teamName }

    val onSelected = LiveEvent<Unit>()
    val navigate = LiveEvent<ActivityNavigator>()
    val activityMethod = LiveEvent<(Activity) -> Unit>()

    fun selectCommand(gameId: ID) {
        thread {
            useCase.findGame(gameId).let {
                mSelectedGame.postValue(it)
            }
            onSelected.postCall(Unit)
        }
    }

    fun editCommand(v: View?) {
        if (mSelectedGame.value == null) return

        Bundle().let {
            it.putSerializable("id", mSelectedGame.value!!.id)
            navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.SCORE_KEEP, it))
        }
    }

    fun deleteCommand(v: View?) {
        if (mSelectedGame.value == null) return

        activityMethod.call(::showConfirmDeletingDialog)
    }

    private fun deleteCommandBody() {
        thread {
            useCase.deleteGame(mSelectedGame.value!!.id)
            mSelectedGame.postValue(null)
        }
    }

    fun exportCommand(v: View?) {
        if (mSelectedGame.value == null) return

        activityMethod.call(::exportCommandBody)
    }

    private fun exportCommandBody(activity: Activity) {
        if (!PermissionService.requestPermissionIfNotGranted(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                PERMISSION_REQUEST_EXPORT_GAME
            )
        ) {
            thread { useCase.exportGame(mSelectedGame.value!!.id) }
        }
    }

    fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSION_REQUEST_EXPORT_GAME -> {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    thread { useCase.exportGame(mSelectedGame.value!!.id) }
            }
        }
    }

    fun startNewGameCommand(v: View?) =
        navigate.call(ActivityNavigator(ActivityNavigator.ActivityType.SCORE_KEEP))

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
            .setNegativeButton("OK") { _, _ -> deleteCommandBody() }
            .show()
    }

}