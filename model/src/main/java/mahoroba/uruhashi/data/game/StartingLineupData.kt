package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType
import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.domain.game.Position
import mahoroba.uruhashi.domain.game.StartingLineup
import mahoroba.uruhashi.domain.game.TeamClass

@Entity(tableName = "starting_member", primaryKeys = ["gameId", "teamClass", "battingOrder"])
class StartingMemberData() {
    lateinit var gameId: ID
    lateinit var teamClass: TeamClass
    var battingOrder: Int = -1
    var position: Position = Position.NO_ENTRY
    var playerId: ID? = null
    var playerFamilyName: String? = null
    var playerFirstName: String? = null
    var playerNameType: NameType = NameType.FAMILY_NAME_FIRST
    var batHand: HandType? = null
    var throwHand: HandType? = null
    var uniformNumber: String? = null

    companion object {
        fun getList(gameId: ID, teamClass: TeamClass, lineup: StartingLineup) : List<StartingMemberData> {
            return ArrayList<StartingMemberData>().apply {
                for (i in 0..8) {
                    this.add(StartingMemberData().apply {
                        this.gameId = gameId
                        this.teamClass = teamClass
                        this.battingOrder = i
                        this.position = lineup.positions[i]
                        this.playerId = lineup.players[i].playerId
                        this.playerFamilyName = lineup.players[i].playerName?.familyName
                        this.playerFirstName = lineup.players[i].playerName?.firstName
                        this.playerNameType = lineup.players[i].playerName?.nameType ?: NameType.FAMILY_NAME_FIRST
                        this.batHand = lineup.players[i].bats
                        this.throwHand = lineup.players[i].throws
                        this.uniformNumber = lineup.players[i].uniformNumber
                    })
                }

                if (lineup.hasDH) {
                    this.add(StartingMemberData().apply {
                        this.gameId = gameId
                        this.teamClass = teamClass
                        this.battingOrder = 9
                        this.position = Position.PITCHER
                        this.playerId = lineup.playerOutOfOrder.playerId
                        this.playerFamilyName = lineup.playerOutOfOrder.playerName?.familyName
                        this.playerFirstName = lineup.playerOutOfOrder.playerName?.firstName
                        this.playerNameType = lineup.playerOutOfOrder.playerName?.nameType ?: NameType.FAMILY_NAME_FIRST
                        this.batHand = lineup.playerOutOfOrder.bats
                        this.throwHand = lineup.playerOutOfOrder.throws
                        this.uniformNumber = lineup.playerOutOfOrder.uniformNumber
                    })
                }
            }
        }
    }
}