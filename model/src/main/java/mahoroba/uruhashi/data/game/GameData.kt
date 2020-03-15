package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.domain.game.GameStatus
import java.util.*

@Entity(tableName = "game")
class GameData() {
    @PrimaryKey var id: String = ""
    var gameName: String = ""
    var date: Date? = null
    var homeTeamId: String? = null
    var homeTeamName: String? = null
    var homeTeamAbbreviatedName: String? = null
    var visitorTeamId: String? = null
    var visitorTeamName: String? = null
    var visitorTeamAbbreviatedName: String? = null
    var stadiumId: String? = null
    var stadiumName: String? = null
    var stadiumAbbreviatedName: String? = null
    var gameStatus: GameStatus? = null

    constructor(game: Game) : this() {
        id = game.id.value
        gameName = game.gameInfo.gameName
        date = game.gameInfo.date
        homeTeamId = game.gameInfo.homeTeam.teamId?.value
        homeTeamName = game.gameInfo.homeTeam.teamName
        homeTeamAbbreviatedName = game.gameInfo.homeTeam.abbreviatedName
        visitorTeamId = game.gameInfo.visitorTeam.teamId?.value
        visitorTeamName = game.gameInfo.visitorTeam.teamName
        visitorTeamAbbreviatedName = game.gameInfo.visitorTeam.abbreviatedName
        stadiumId = game.gameInfo.stadiumInfo.stadiumId?.value
        stadiumName = game.gameInfo.stadiumInfo.stadiumName
        stadiumAbbreviatedName = game.gameInfo.stadiumInfo.abbreviatedName
        gameStatus = game.boxScore.gameStatus
    }

    fun toGame() : Game {
        val game = Game(ID(id))

        game.gameInfo.let {
            it.gameName = gameName
            it.date = date
            it.homeTeam.teamId = if (homeTeamId != null) ID(homeTeamId!!) else null
            it.homeTeam.teamName = homeTeamName
            it.homeTeam.abbreviatedName = homeTeamAbbreviatedName
            it.visitorTeam.teamId = if (visitorTeamId != null) ID(visitorTeamId!!) else null
            it.visitorTeam.teamName = visitorTeamName
            it.visitorTeam.abbreviatedName = visitorTeamAbbreviatedName
            it.stadiumInfo.stadiumId = if (stadiumId != null) ID(stadiumId!!) else null
            it.stadiumInfo.stadiumName = stadiumName
            it.stadiumInfo.abbreviatedName = stadiumAbbreviatedName
        }

        return game
    }
}