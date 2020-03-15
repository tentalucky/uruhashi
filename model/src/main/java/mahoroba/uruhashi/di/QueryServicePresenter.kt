package mahoroba.uruhashi.di

import android.app.Application
import mahoroba.uruhashi.data.query.GameQueryService
import mahoroba.uruhashi.data.query.PlayerQueryService
import mahoroba.uruhashi.data.query.TeamQueryService
import mahoroba.uruhashi.usecase.query.IGameQueryService
import mahoroba.uruhashi.usecase.query.IPlayerQueryService
import mahoroba.uruhashi.usecase.query.ITeamQueryService

object QueryServicePresenter {
    fun getTeamQueryService(application: Application) : ITeamQueryService
        = TeamQueryService(application)

    fun getPlayerQueryService(application: Application) : IPlayerQueryService
        = PlayerQueryService(application)

    fun getGameQueryService(application: Application) : IGameQueryService
        = GameQueryService(application)
}