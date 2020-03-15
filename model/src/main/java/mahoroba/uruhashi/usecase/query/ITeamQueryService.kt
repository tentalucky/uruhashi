package mahoroba.uruhashi.usecase.query

import mahoroba.uruhashi.domain.TeamProfile

interface ITeamQueryService {
    fun getTeamMembersInfo(teamProfile: TeamProfile): List<TeamMemberInfoDto>
}