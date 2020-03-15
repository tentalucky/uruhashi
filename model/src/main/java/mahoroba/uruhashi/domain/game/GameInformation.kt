package mahoroba.uruhashi.domain.game

import java.util.*

class GameInformation {
    val homeTeam = Team()
    val visitorTeam = Team()
    val stadiumInfo = StadiumInformation()

    var gameName: String = ""
    var date: Date? = null

}