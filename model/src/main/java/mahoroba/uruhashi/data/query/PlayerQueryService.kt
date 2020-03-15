package mahoroba.uruhashi.data.query

import android.app.Application
import mahoroba.uruhashi.data.AppDatabase
import mahoroba.uruhashi.data.AppDatabaseProvider
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PlayerProfile
import mahoroba.uruhashi.usecase.query.IPlayerQueryService
import mahoroba.uruhashi.usecase.query.PlayerBelongingInfoDto
import mahoroba.uruhashi.usecase.query.PlayerInGameDto
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

class PlayerQueryService(application: Application) : IPlayerQueryService {
    private val db: AppDatabase = AppDatabaseProvider.getDatabase(application)
    private val dao: PlayerQueryServiceDao

    init {
        dao = db.playerQueryDao
    }

    override fun getPlayerBelongingInfo(playerProfile: PlayerProfile): List<PlayerBelongingInfoDto> {
        return dao.getPlayerBelongingInfo(playerProfile.id.value)
    }

    override fun getPlayersBelongingIn(teamId: ID): List<PlayerInGameDto> {
        return dao.getPlayersBelongingIn(teamId.value)
    }

    override fun getPlayersBelongingInNothing(): List<PlayerInGameDto> {
        return dao.getPlayersBelongingInNothing()
    }
}