package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface FieldPlayDao {
    @Insert
    fun insert(fieldPlayData: FieldPlayData)

    @Query("DELETE FROM field_play WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
            SELECT *
            FROM field_play
            WHERE gameId = :gameId
            ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, seqNumber
        """
    )
    fun findByGameId(gameId: ID): List<FieldPlayData>

    @Query("DELETE FROM field_play")
    fun deleteAll()
}