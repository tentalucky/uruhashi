package mahoroba.uruhashi.usecase

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.Transformations
import mahoroba.uruhashi.data.TeamRepository
import mahoroba.uruhashi.domain.*
import mahoroba.uruhashi.usecase.query.IPlayerQueryService
import mahoroba.uruhashi.usecase.query.PlayerBelongingInfoDto

class PlayerManagementUseCase(
    private val playerRepository: IPlayerRepository,
    private val playerQueryService: IPlayerQueryService,
    private val teamRepository: ITeamRepository
) {

    val playerSummaryList: LiveData<List<PlayerSummary>> =
        Transformations.map(playerRepository.observeAll()) { source ->
            val list = ArrayList<PlayerSummary>()
            source.forEach { list.add(PlayerSummary(it.id, it.name.fullName)) }
            list
        }

    fun findPlayer(playerId: ID): PlayerProfile {
        return playerRepository.get(playerId)
    }

    fun findPlayerBelongings(playerProfile: PlayerProfile): List<PlayerBelongingInfoDto> {
        return playerQueryService.getPlayerBelongingInfo(playerProfile)
    }

    fun registerNewPlayer(
        familyName: String?,
        firstName: String?,
        nameType: NameType,
        bats: HandType?,
        throws: HandType?,
        belongings: List<PlayerBelonging>
    ) {

        val player = PlayerProfile(
            ID(), PersonName(familyName, firstName, nameType), bats, throws
        )
        belongings.forEach {
            player.belongings.addRegister(
                Register(
                    it.teamId,
                    player.id,
                    it.uniformNumber
                )
            )
        }

        playerRepository.save(player)
    }

    fun modifyPlayer(
        playerId: ID,
        familyName: String?,
        firstName: String?,
        nameType: NameType,
        bats: HandType?,
        throws: HandType?,
        belongings: List<PlayerBelonging>
    ) {

        val player = PlayerProfile(
            playerId, PersonName(familyName, firstName, nameType), bats, throws
        )
        belongings.forEach {
            player.belongings.addRegister(
                Register(
                    it.teamId,
                    player.id,
                    it.uniformNumber
                )
            )
        }

        playerRepository.save(player)
    }

    fun deletePlayer(playerId: ID) {
        playerRepository.delete(playerRepository.get(playerId))
    }

    fun findTeam(teamId: ID): TeamProfile {
        return teamRepository.get(teamId)
    }

    data class PlayerBelonging(val teamId: ID, val uniformNumber: String?)

    data class PlayerSummary(val id: ID, val fullName: String?)
}