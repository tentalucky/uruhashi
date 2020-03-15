package mahoroba.uruhashi.domain.game.secondary

import mahoroba.uruhashi.domain.game.*

class Lineup {
    var historyNo: Int = 0
        private set

    var hasDH: Boolean = false
        private set
    // If the lineup is using DH, a pitcher is placed in the 10th of the order.
    private val battingOrders: ArrayList<BattingOrder> = ArrayList()

    constructor(startingLineup: StartingLineup) {
        this.historyNo = 0
        this.hasDH = startingLineup.hasDH
        for (i in 0..8) {
            battingOrders.add(BattingOrder(startingLineup.positions[i], startingLineup.players[i]))
        }

        if (hasDH) {
            battingOrders.add(BattingOrder(Position.PITCHER, startingLineup.playerOutOfOrder))
        }
    }

    private constructor(historyNo: Int, hasDH: Boolean, battingOrders: List<BattingOrder>) {
        this.historyNo = historyNo
        this.hasDH = hasDH
        this.battingOrders.addAll(battingOrders)
    }

    fun after(substitution: Substitution): Lineup {
        val newPositions = Array(if (hasDH) 10 else 9) { battingOrders[it].position }
        val newPlayers = Array(if (hasDH) 10 else 9) { battingOrders[it].player }

        substitution.positionChangingList.forEach { newPositions[it.battingOrder] = it.newPosition }
        substitution.playerChangingList.forEach { newPlayers[it.battingOrder] = it.newPlayer }

        val newHasDH = hasDH && !substitution.cancelDH
        val newBattingOrders = ArrayList<BattingOrder>().apply {
            for (i in 0..if (newHasDH) 9 else 8) {
                this.add(BattingOrder(newPositions[i], newPlayers[i]))
            }
        }

        return Lineup(historyNo + 1, newHasDH, newBattingOrders)
    }

    fun getPlayer(order: Int): Player {
        return battingOrders[order].player
    }

    fun getPlayer(position: Position): Player? {
        return battingOrders.firstOrNull { it.position == position }?.player
    }

    fun getPosition(order: Int): Position {
        return battingOrders[order].position
    }
}