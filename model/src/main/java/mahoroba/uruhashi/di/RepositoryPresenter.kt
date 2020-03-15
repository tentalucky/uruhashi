package mahoroba.uruhashi.di

import android.app.Application
import mahoroba.uruhashi.data.GameRepository
import mahoroba.uruhashi.data.PlayerRepository
import mahoroba.uruhashi.data.StadiumRepository
import mahoroba.uruhashi.data.TeamRepository
import mahoroba.uruhashi.domain.IPlayerRepository
import mahoroba.uruhashi.domain.IStadiumRepository
import mahoroba.uruhashi.domain.ITeamRepository
import mahoroba.uruhashi.domain.game.IGameRepository

object RepositoryPresenter {
    fun getStadiumRepository(application: Application) : IStadiumRepository
        = StadiumRepository(application)

    fun getTeamRepository(application: Application) : ITeamRepository
        = TeamRepository(application)

    fun getPlayerRepository(application: Application) : IPlayerRepository
        = PlayerRepository(application)

    fun getGameRepository(application: Application) : IGameRepository
        = GameRepository(application)
}