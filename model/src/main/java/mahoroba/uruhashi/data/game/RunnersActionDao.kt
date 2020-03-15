package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface RunnersActionDao {
    @Insert
    fun insert(runnersActionData: RunnersActionData)

    @Query("DELETE FROM runners_action WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
            SELECT *
            FROM runners_action
            WHERE gameId = :gameId
            ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, fieldPlaySeqNumber, runnerType
        """
    )
    fun findByGameId(gameId: ID): List<RunnersActionData>

    @Query("DELETE FROM runners_action")
    fun deleteAll()
}