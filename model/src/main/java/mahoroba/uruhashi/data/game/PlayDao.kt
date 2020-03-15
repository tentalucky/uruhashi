package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface PlayDao {
    @Insert
    fun insert(playData: PlayData)

    @Query("DELETE FROM play WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
        SELECT *
        FROM play
        WHERE gameId = :gameId
        ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber
    """
    )
    fun findByGameId(gameId: ID): List<PlayData>

    @Query("DELETE FROM play")
    fun deleteAll()
}