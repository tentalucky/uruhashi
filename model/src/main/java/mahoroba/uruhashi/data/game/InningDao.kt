package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface InningDao {
    @Insert
    fun insert(inning: InningData)

    @Query("DELETE FROM inning WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
        SELECT *
        FROM inning
        WHERE gameId = :gameId
        ORDER BY seqNumber"""
    )
    fun findByGameId(gameId: ID): List<InningData>

    @Query("DELETE FROM inning")
    fun deleteAll()
}