package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import mahoroba.uruhashi.domain.ID

@Dao
interface BattedBallDirectionDao {
    @Insert
    fun insert(battedBall: BattedBallDirectionData)

    @Query("DELETE FROM batted_ball_direction WHERE gameId = :gameId")
    fun deleteByGameId(gameId: ID)

    @Query("""
        SELECT *
        FROM batted_ball_direction
        WHERE gameId = :gameId
        ORDER BY inningSeqNumber, plateAppearanceSeqNumber, periodSeqNumber, seqNumber
    """)
    fun findByGameId(gameId: ID): List<BattedBallDirectionData>

    @Query("DELETE FROM batted_ball_direction")
    fun deleteAll()
}