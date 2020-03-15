package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface PositionChangingDao {
    @Insert
    fun insert(positionChanging: PositionChangingData)

    @Query("DELETE FROM position_changing WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
        SELECT *
        FROM position_changing
        WHERE gameId = :gameId
        ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, battingOrder
    """
    )
    fun findByGameId(gameId: ID): List<PositionChangingData>

    @Query("DELETE FROM position_changing")
    fun deleteAll()
}