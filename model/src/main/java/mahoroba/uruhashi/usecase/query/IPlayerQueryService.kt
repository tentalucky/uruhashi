package mahoroba.uruhashi.usecase.query

import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PlayerProfile

interface IPlayerQueryService {
    fun getPlayerBelongingInfo(playerProfile: PlayerProfile): List<PlayerBelongingInfoDto>

    fun getPlayersBelongingIn(teamId: ID) : List<PlayerInGameDto>
    fun getPlayersBelongingInNothing() : List<PlayerInGameDto>
}