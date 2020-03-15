package mahoroba.uruhashi.data.query

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.usecase.query.PlayerBelongingInfoDto
import mahoroba.uruhashi.usecase.query.PlayerInGameDto
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

@Dao
interface PlayerQueryServiceDao {

    @Query("""
        SELECT
          teamProfile.id AS teamId,
          teamProfile.name AS teamName,
          register.uniformNumber AS uniformNumber
        FROM playerProfile
         INNER JOIN register ON playerProfile.id = register.playerId
         INNER JOIN teamProfile ON register.teamId = teamProfile.id
        WHERE playerProfile.id = :playerId
    """)
    fun getPlayerBelongingInfo(playerId: String) : List<PlayerBelongingInfoDto>

    @Query("""
        SELECT
            playerProfile.id AS playerId,
            playerProfile.firstName AS firstName,
            playerProfile.familyName AS familyName,
            playerProfile.nameType AS nameType,
            playerProfile.batHand AS batHand,
            playerProfile.throwHand As throwHand,
            register.uniformNumber As uniformNumber
        FROM teamProfile
         INNER JOIN register ON teamProfile.id = register.teamId
         INNER JOIN playerProfile ON register.playerId = playerProfile.id
        WHERE teamProfile.id = :teamId
    """)
    fun getPlayersBelongingIn(teamId: String) : List<PlayerInGameDto>

    @Query("""
        SELECT
            playerProfile.id AS playerId,
            playerProfile.firstName AS firstName,
            playerProfile.familyName AS familyName,
            playerProfile.nameType AS nameType,
            playerProfile.batHand AS batHand,
            playerProfile.throwHand As throwHand,
            null AS uniformNumber
        FROM playerProfile
         LEFT JOIN register ON playerProfile.id = register.playerId
        WHERE register.playerId
    """)
    fun getPlayersBelongingInNothing() : List<PlayerInGameDto>
}