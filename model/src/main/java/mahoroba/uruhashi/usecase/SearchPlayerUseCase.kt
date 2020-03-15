package mahoroba.uruhashi.usecase

import mahoroba.uruhashi.data.TeamRepository
import mahoroba.uruhashi.domain.*
import mahoroba.uruhashi.usecase.query.IPlayerQueryService

class SearchPlayerUseCase(
    private val playerQueryService: IPlayerQueryService,
    private val teamRepository: ITeamRepository) {

    fun findPlayersBelongingIn(teamId: ID): List<PlayerSummary> {
        val list = ArrayList<PlayerSummary>()
        playerQueryService.getPlayersBelongingIn(teamId).forEach {
            list.add(
                PlayerSummary(
                    it.playerId,
                    PersonName(it.familyName, it.firstName, it.nameType),
                    it.batHand,
                    it.throwHand,
                    it.uniformNumber))
        }
        return list
    }

    fun findPlayersBelongingInNothing(): List<PlayerSummary> {
        val list = ArrayList<PlayerSummary>()
        playerQueryService.getPlayersBelongingInNothing().forEach {
            list.add(
                PlayerSummary(
                    it.playerId,
                    PersonName(it.familyName, it.firstName, it.nameType),
                    it.batHand,
                    it.throwHand,
                    it.uniformNumber))
        }
        return list
    }

    data class PlayerSummary(
        val playerId: ID,
        val name: PersonName,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?)

    fun findAllTeams() : List<TeamSummary> {
        val teams = ArrayList<TeamSummary>()
        teamRepository.findAll().forEach {
            teams.add(TeamSummary(it.id, it.name))
        }
        return teams
    }

    data class TeamSummary(
        val teamId: ID,
        val teamName: String?
    )
}