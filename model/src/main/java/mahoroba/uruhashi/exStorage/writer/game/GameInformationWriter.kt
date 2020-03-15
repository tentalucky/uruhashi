package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.GameInformation
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class GameInformationWriter(
    private val writer: Writer,
    private val gameInformation: GameInformation
) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("gameName", gameInformation.gameName)
        jsonWriter.writeItem("date", gameInformation.date)
        jsonWriter.writeItem("homeTeam") {
            TeamWriter(writer, gameInformation.homeTeam).write()
        }
        jsonWriter.writeItem("visitorTeam") {
            TeamWriter(writer, gameInformation.visitorTeam).write()
        }
        jsonWriter.writeItem("stadiumInfo") {
            StadiumInformationWriter(writer, gameInformation.stadiumInfo).write()
        }

        writer.write("}")
    }
}