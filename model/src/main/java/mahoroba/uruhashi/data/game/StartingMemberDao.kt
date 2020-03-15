package mahoroba.uruhashi.data.game

import android.arch.persistence.room.*
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.TeamClass

@Dao
interface StartingMemberDao {
    @Insert
    fun insert(startingMember: StartingMemberData)

    @Update
    fun update(startingMember: StartingMemberData)

    @Query("DELETE FROM starting_member WHERE gameId = :gameId")
    fun deleteMembers(gameId: ID)

    @Query("""
        SELECT *
        FROM starting_member
        WHERE gameId = :gameId AND teamClass = :teamClass
        ORDER BY battingOrder""")
    fun findMembers(gameId: ID, teamClass: TeamClass) : List<StartingMemberData>

    @Query("DELETE FROM starting_member")
    fun deleteAll()
}