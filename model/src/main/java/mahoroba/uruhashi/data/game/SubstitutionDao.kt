package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface SubstitutionDao {
    @Insert
    fun insert(substitution: SubstitutionData)

    @Query("DELETE FROM substitution WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
            SELECT *
            FROM substitution
            WHERE gameId = :gameId
            ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber
        """
    )
    fun findByGameId(gameId: ID): List<SubstitutionData>

    @Query("DELETE FROM substitution")
    fun deleteAll()
}