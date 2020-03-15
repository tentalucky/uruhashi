package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.ID

class Game(val id: ID) {
    val gameInfo: GameInformation = GameInformation()
    val boxScore: BoxScore = BoxScore()
}