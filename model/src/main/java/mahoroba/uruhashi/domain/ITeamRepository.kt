package mahoroba.uruhashi.domain

import android.arch.lifecycle.LiveData

interface ITeamRepository {
    fun save(teamProfile: TeamProfile)
    fun delete(teamProfile: TeamProfile)
    fun get(teamId: ID) : TeamProfile
    fun observeAll() : LiveData<List<TeamProfile>>
    fun findAll() : List<TeamProfile>
}