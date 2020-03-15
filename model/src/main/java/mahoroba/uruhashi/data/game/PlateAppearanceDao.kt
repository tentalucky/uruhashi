package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface PlateAppearanceDao {
    @Insert
    fun insert(plateAppearance: PlateAppearanceData)

    @Query("DELETE FROM plate_appearance WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query(
        """
            SELECT *
            FROM plate_appearance
            WHERE gameId = :gameId
            ORDER BY inningSeqNumber, seqNumber
        """
    )
    fun findByGameId(gameId: ID): List<PlateAppearanceData>

    @Query("DELETE FROM plate_appearance")
    fun deleteAll()
}