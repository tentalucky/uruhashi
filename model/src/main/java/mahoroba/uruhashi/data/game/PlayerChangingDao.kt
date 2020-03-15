package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface PlayerChangingDao {
    @Insert
    fun insert(playerChanging: PlayerChangingData)

    @Query("DELETE FROM player_changing WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
        SELECT *
        FROM player_changing
        WHERE gameId = :gameId
        ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, battingOrder
    """
    )
    fun findByGameId(gameId: ID): List<PlayerChangingData>

    @Query("DELETE FROM player_changing")
    fun deleteAll()
}