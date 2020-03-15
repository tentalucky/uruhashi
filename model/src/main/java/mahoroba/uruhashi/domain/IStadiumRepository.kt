package mahoroba.uruhashi.domain

import android.arch.lifecycle.LiveData

interface IStadiumRepository {
    fun save(stadium: Stadium)
    fun delete(id: ID)
    fun get(id: ID) : Stadium
    fun observeAll() : LiveData<List<Stadium>>
}