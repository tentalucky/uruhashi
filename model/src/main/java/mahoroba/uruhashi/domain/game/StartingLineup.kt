package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName

class StartingLineup {
    var hasDH: Boolean = false
        private set

    private val mPositionsInOrder = Array(9) { Position.NO_ENTRY }
    val positions: List<Position>
        get() = mPositionsInOrder.toList()

    private val mPlayersInOrder = Array(9) {
        Player(null, null, null, null, null)
    }
    val players: List<Player>
        get() = mPlayersInOrder.toList()

    val playerOutOfOrder = Player(null, null, null, null, null)

    fun setPositions(positions: List<Position>, hasDH: Boolean) {
        if (positions.count() != 9) throw Exception("Number of position must be 9.")

        positions.forEach {
            if (it != Position.NO_ENTRY && !it.isStaticPosition) {
                throw Exception("Positions of starting lineup must be static position.")
            }
            if (it != Position.NO_ENTRY && positions.count { p -> p == it } > 1) {
                throw Exception("Positions are duplicated.")
            }
            if (hasDH && it == Position.PITCHER) {
                throw Exception("When using DH, a pitcher cannot be in the batting battingOrder.")
            }
            if (!hasDH && it == Position.DESIGNATED_HITTER) {
                throw Exception("When not using DH, DH cannot be in the batting battingOrder.")
            }
        }

        for (i in 0..8) {
            mPositionsInOrder[i] = positions[i]
        }
        this.hasDH = hasDH
    }

    fun setPlayersProfile(
        order: Int, playerID: ID?, playerName: PersonName?, bats: HandType?, throws: HandType?, uniformNumber: String?
    ) {

        if (hasDH && (order < 0 || order > 9)) throw IndexOutOfBoundsException()
        if (!hasDH && (order < 0 || order > 8)) throw IndexOutOfBoundsException()

        (if (order < 9) mPlayersInOrder[order] else playerOutOfOrder).let {
            it.playerId = playerID
            it.playerName = playerName
            it.uniformNumber = uniformNumber
            it.bats = bats
            it.throws = throws
        }
    }
}