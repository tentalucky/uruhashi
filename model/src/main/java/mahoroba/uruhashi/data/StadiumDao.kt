package mahoroba.uruhashi.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import mahoroba.uruhashi.domain.Stadium

@Dao
interface StadiumDao {

    @Insert
    fun insert(stadium: StadiumData)

    @Update
    fun update(stadium: StadiumData)

    @Delete
    fun delete(stadium: StadiumData)

    @Query("DELETE FROM stadium")
    fun deleteAll()

    @Query("SELECT * FROM stadium WHERE id = :id")
    fun findById(id: String): StadiumData

    @Query("SELECT * FROM stadium ORDER BY priority DESC, name")
    fun observeAll() : LiveData<List<StadiumData>>
}