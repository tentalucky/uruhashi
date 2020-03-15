package mahoroba.uruhashi.usecase

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.domain.*
import mahoroba.uruhashi.usecase.query.ITeamQueryService
import mahoroba.uruhashi.usecase.query.TeamMemberInfoDto

class TeamManagementUseCase(
    private val teamRepository: ITeamRepository,
    private val teamQueryService: ITeamQueryService,
    private val playerRepository: IPlayerRepository) {

    val teamSummaryList: LiveData<List<TeamSummary>> = Transformations.map(teamRepository.observeAll()){ source ->
        val list = ArrayList<TeamSummary>()
        source.forEach{ list.add(TeamSummary(it.id, it.name)) }
        list
    }

    fun findTeam(teamId: ID): TeamProfile {
        return teamRepository.get(teamId)
    }

    fun findTeamMembers(teamProfile: TeamProfile): List<TeamMemberInfoDto> {
        return teamQueryService.getTeamMembersInfo(teamProfile)
    }

    fun registerNewTeam(name: String?, abbreviatedName: String?, priority: Int) {
        val team = TeamProfile(ID(), name, abbreviatedName, priority)
        teamRepository.save(team)
    }

    fun modifyTeam(teamId: ID, name: String?, abbreviatedName: String?, priority: Int) {
        val team = teamRepository.get(teamId).apply {
            this.name = name
            this.abbreviatedName = abbreviatedName
            this.priority = priority
        }

        teamRepository.save(team)
    }

    fun updateTeamMembers(teamId: ID, members: List<TeamMember>) {
        val team = teamRepository.get(teamId)
        team.roster.clearRegister()
        members.forEach{ team.roster.addRegister(Register(team.id, it.playerId, it.uniformNumber)) }

        teamRepository.save(team)
    }

    fun deleteTeam(teamId: ID) {
        teamRepository.delete(teamRepository.get(teamId))
    }

    fun findPlayer(playerId: ID) : PlayerProfile {
        return playerRepository.get(playerId)
    }

    data class TeamMember(val playerId: ID, val uniformNumber: String?)

    data class TeamSummary(val id: ID, val name: String?)

}