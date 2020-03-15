package mahoroba.uruhashi.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*

@Dao
interface TeamDao {

    @Insert
    fun insertTeam(teamProfile: TeamProfileData): Long

    @Update
    fun updateTeam(teamProfile: TeamProfileData)

    @Delete
    fun deleteTeam(teamProfile: TeamProfileData)

    @Query("DELETE FROM teamProfile")
    fun deleteAll()

    @Query("SELECT * FROM teamProfile WHERE id = :id")
    fun findById(id: String): TeamProfileData

    @Query("SELECT * FROM teamProfile ORDER BY priority DESC, name")
    fun observeAll() : LiveData<List<TeamProfileData>>

    @Query("SELECT * FROM teamProfile ORDER BY priority DESC, name")
    fun findAll() : List<TeamProfileData>
}