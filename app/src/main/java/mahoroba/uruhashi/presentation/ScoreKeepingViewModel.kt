package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.coroutines.*
import mahoroba.uruhashi.di.RepositoryPresenter
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.GameStatus
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.domain.game.TeamClass
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class ScoreKeepingViewModel(application: Application, gameId: ID?) :
    BaseViewModel(application) {

    class Factory(private val application: Application, private val bundle: Bundle?) :
        ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return ScoreKeepingViewModel(application, bundle?.getSerializable("id") as ID?) as T
        }
    }

    private val useCase =
        ScoreKeepingUseCase(
            RepositoryPresenter.getGameRepository(application),
            RepositoryPresenter.getStadiumRepository(application),
            RepositoryPresenter.getTeamRepository(application),
            gameId
        )

    private val myApplication = application

    enum class ActiveFragmentType {
        GAME_INFO_INPUT,
        BOX_SCORE_INPUT,
        GAME_ENDING
    }

    private val mActiveFragmentType = MutableLiveData<ActiveFragmentType>()
    val activeFragmentType: LiveData<ActiveFragmentType> = mActiveFragmentType

    private var mGameInfoInputViewModel: GameInfoInputViewModel? = null
    val gameInfoInputViewModel: GameInfoInputViewModel
        get() {
            if (mGameInfoInputViewModel == null) mGameInfoInputViewModel =
                GameInfoInputViewModel(myApplication, this, useCase)
            return mGameInfoInputViewModel!!
        }

    private var mPlayKeepingViewModel: PlayInputViewModel? = null
    val playInputViewModel: PlayInputViewModel
        get() {
            if (mPlayKeepingViewModel == null) {
                mPlayKeepingViewModel = PlayInputViewModel(myApplication, this, useCase)
            }
            return mPlayKeepingViewModel!!
        }

    private var mGameEndingViewModel: GameEndingViewModel? = null
    val gameEndingViewModel: GameEndingViewModel
        get() {
            if (mGameEndingViewModel == null) {
                mGameEndingViewModel = GameEndingViewModel(myApplication, this, useCase)
            }
            return mGameEndingViewModel!!
        }

    private var mScoreBoardViewModel: ScoreBoardViewModel? = null
    val scoreBoardViewModel: ScoreBoardViewModel
        get() {
            if (mScoreBoardViewModel == null) {
                mScoreBoardViewModel = ScoreBoardViewModel(
                    myApplication,
                    useCase.gameBaseInfo,
                    useCase.latestGameState,
                    useCase.boxScore,
                    useCase.playersBattingStats
                )
            }
            return mScoreBoardViewModel!!
        }

    private var mPeriodHistoryListViewModel: PeriodHistoryListViewModel? = null
    val periodHistoryListViewModel: PeriodHistoryListViewModel
        get() {
            if (mPeriodHistoryListViewModel == null) {
                mPeriodHistoryListViewModel = PeriodHistoryListViewModel(
                    myApplication, useCase.gameBaseInfo, useCase.boxScore, this
                )
            }
            return mPeriodHistoryListViewModel!!
        }

    // region * Properties *

    val gameLoadedEvent = useCase.gameLoadedEvent
    val isLoaded = useCase.isLoaded

    // endregion * properties *

    fun onGameLoaded() {
        mActiveFragmentType.value = ActiveFragmentType.GAME_INFO_INPUT
    }

    override fun onCleared() {
        GlobalScope.launch(Dispatchers.Main) {
            useCase.persistGame()
            Toast.makeText(myApplication, "Saved.", Toast.LENGTH_SHORT).show()
        }
        super.onCleared()
    }

    fun saveGame() {
        GlobalScope.launch(Dispatchers.Main) {
            try {
                useCase.persistGame()
                Toast.makeText(myApplication, "Saved.", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(myApplication, "Failed to save.\n" + e.message, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }

    fun switchToGameInfoInputFragment() {
        mActiveFragmentType.value = ActiveFragmentType.GAME_INFO_INPUT
    }

    fun finishGameAsCompleted() {
        useCase.finishGameAsCompleted()
        switchToGameEndingFragment()
    }

    fun cancelGameFinishing() {
        useCase.cancelGameFinishing()
        switchToBoxScoreInputFragment()
    }

    fun onModifyingPlayRequired(playDto: ScoreKeepingUseCase.PlayDto) {
        if (activeFragmentType.value == ActiveFragmentType.BOX_SCORE_INPUT) {
            mPlayKeepingViewModel?.openModifyPlay(playDto)
        }
    }

    fun onInsertingOffenceSubstitutionRequired(periodDto: ScoreKeepingUseCase.PeriodDto) {
        if (activeFragmentType.value == ActiveFragmentType.BOX_SCORE_INPUT) {
            mPlayKeepingViewModel?.openInsertOffenceSubstitution(periodDto)
        }
    }

    fun onInsertingDefenseSubstitutionRequired(periodDto: ScoreKeepingUseCase.PeriodDto) {
        if (activeFragmentType.value == ActiveFragmentType.BOX_SCORE_INPUT) {
            mPlayKeepingViewModel?.openInsertDefenceSubstitution(periodDto)
        }
    }

    fun onModifyingSubstitutionRequired(substitutionDto: ScoreKeepingUseCase.SubstitutionDto) {
        if (activeFragmentType.value == ActiveFragmentType.BOX_SCORE_INPUT) {
            mPlayKeepingViewModel?.openModifySubstitution(substitutionDto)
        }
    }

    fun onDeletingSubstitutionRequired(substitutionDto: ScoreKeepingUseCase.SubstitutionDto) {
        if (activeFragmentType.value == ActiveFragmentType.BOX_SCORE_INPUT) {
            mPlayKeepingViewModel?.openDeleteSubstitution(substitutionDto)
        }
    }

    private fun switchToBoxScoreInputFragment() {
        mActiveFragmentType.value = ActiveFragmentType.BOX_SCORE_INPUT
    }

    private fun switchToGameEndingFragment() {
        mActiveFragmentType.value = ActiveFragmentType.GAME_ENDING
    }

    fun commitGameInfo(v: View?) {
        setGameInfo()
    }

    fun completeInputGameInfo(v: View?) {
        setGameInfo()
        if (useCase.gameStatus == GameStatus.PLAYING) {
            switchToBoxScoreInputFragment()
        } else {
            switchToGameEndingFragment()
        }
    }

    private fun setGameInfo() {
        gameInfoInputViewModel.let {
            useCase.setGameInformation(
                ScoreKeepingUseCase.GameInfoDto(
                    it.gameName.value,
                    it.gameDate.value,
                    it.homeTeamId.value,
                    it.homeTeamName.value,
                    it.homeTeamAbbreviatedName.value,
                    it.visitorTeamId.value,
                    it.visitorTeamName.value,
                    it.visitorTeamAbbreviatedName.value,
                    it.stadiumId.value,
                    it.stadiumName.value,
                    it.stadiumAbbreviatedName.value
                )
            )

            useCase.setStartingPosition(
                TeamClass.HOME,
                ArrayList<Position>().apply {
                    it.homeTeamStartingPositions.forEach { n ->
                        this.add(n.value ?: Position.NO_ENTRY)
                    }
                },
                it.homeTeamHasDH.value ?: false
            )

            useCase.setStartingPosition(
                TeamClass.VISITOR,
                ArrayList<Position>().apply {
                    it.visitorTeamStartingPositions.forEach { n ->
                        this.add(n.value ?: Position.NO_ENTRY)
                    }
                },
                it.visitorTeamHasDH.value ?: false
            )

            for (i in 0..9) {
                if (it.homeTeamHasDH.value != true && i == 9) continue

                useCase.setStartingPlayersInformation(
                    TeamClass.HOME,
                    i,
                    it.homeTeamStartingMembers[i].value?.id,
                    it.homeTeamStartingMembers[i].value?.name,
                    it.homeTeamStartingMembers[i].value?.bats,
                    it.homeTeamStartingMembers[i].value?.throws,
                    it.homeTeamStartingMembers[i].value?.uniformNumber
                )
            }

            for (i in 0..9) {
                if (it.visitorTeamHasDH.value != true && i == 9) continue

                useCase.setStartingPlayersInformation(
                    TeamClass.VISITOR,
                    i,
                    it.visitorTeamStartingMembers[i].value?.id,
                    it.visitorTeamStartingMembers[i].value?.name,
                    it.visitorTeamStartingMembers[i].value?.bats,
                    it.visitorTeamStartingMembers[i].value?.throws,
                    it.visitorTeamStartingMembers[i].value?.uniformNumber
                )
            }
        }
    }
}