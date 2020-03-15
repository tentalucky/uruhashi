package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface FieldersActionDao {
    @Insert
    fun insert(action: FieldersActionData)

    @Query("DELETE FROM fielders_action WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
            SELECT *
            FROM fielders_action
            WHERE gameId = :gameId
            ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, fieldPlaySeqNumber, seqNumber
        """
    )
    fun findByGameId(gameId: ID): List<FieldersActionData>

    @Query("DELETE FROM fielders_action")
    fun deleteAll()
}