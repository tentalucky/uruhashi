package mahoroba.uruhashi.data

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface RegisterDao {

    @Insert
    fun insertRegisters(registers: List<RegisterData>)

    @Query("DELETE FROM register WHERE teamId = :teamId")
    fun deleteRegisterWithTeamId(teamId: String)

    @Query("DELETE FROM register WHERE playerId = :playerId")
    fun deleteRegisterWithPlayerId(playerId: String)

    @Query("DELETE FROM register")
    fun deleteAll()

    @Query("SELECT * FROM register WHERE teamId = :teamId")
    fun findRegisterWithTeamId(teamId: String): List<RegisterData>

    @Query("SELECT * FROM register WHERE playerId = :playerId")
    fun findRegisterWithPlayerId(playerId: String): List<RegisterData>

}