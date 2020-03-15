package mahoroba.uruhashi.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface PlayerDao {

    @Insert
    fun insertPlayer(player: PlayerData): Long

    @Update
    fun updatePlayer(player: PlayerData)

    @Delete
    fun deletePlayer(player: PlayerData)

    @Query("DELETE FROM playerProfile")
    fun deleteAll()

    @Query("SELECT * FROM playerProfile WHERE id = :id")
    fun findById(id: String): PlayerData

    @Query("SELECT * FROM playerProfile ORDER BY familyName, firstName")
    fun observeAll(): LiveData<List<PlayerData>>
}