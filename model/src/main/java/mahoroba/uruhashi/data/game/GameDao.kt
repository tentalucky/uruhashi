package mahoroba.uruhashi.data.game

import android.arch.persistence.room.*

@Dao
interface GameDao {
    @Insert
    fun insertGame(game: GameData)

    @Update
    fun updateGame(game: GameData)

    @Delete
    fun deleteGame(game: GameData)

    @Query("DELETE FROM game")
    fun deleteAll()

    @Query("SELECT * FROM game WHERE id = :id")
    fun findById(id: String) : GameData
}