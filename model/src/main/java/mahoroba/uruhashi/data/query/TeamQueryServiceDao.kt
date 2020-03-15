package mahoroba.uruhashi.data.query

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Query
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

@Dao
interface TeamQueryServiceDao {

    @Query("""
        SELECT
          playerProfile.id AS playerId,
          playerProfile.familyName AS familyName,
          playerProfile.firstName AS firstName,
          playerProfile.nameType AS nameType,
          register.uniformNumber AS uniformNumber
        FROM teamProfile
         INNER JOIN register ON teamProfile.id = register.teamId
         INNER JOIN playerProfile ON register.playerId = playerProfile.id
        WHERE teamProfile.id = :teamId""")
    fun getTeamMemberInfo(teamId: String) : List<TeamMemberInfoDto>

}