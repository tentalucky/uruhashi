package mahoroba.uruhashi.domain

import android.arch.lifecycle.LiveData

interface IPlayerRepository {
    fun save(playerProfile: PlayerProfile)
    fun delete(playerProfile: PlayerProfile)
    fun get(playerId: ID) : PlayerProfile
    fun observeAll() : LiveData<List<PlayerProfile>>
}