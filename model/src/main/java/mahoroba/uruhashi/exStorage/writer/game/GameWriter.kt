package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class GameWriter(private val writer: Writer, private val game: Game) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("id", game.id.value)
        jsonWriter.writeItem("gameInfo") { GameInformationWriter(writer, game.gameInfo).write() }
        jsonWriter.writeItem("boxScore") { BoxScoreWriter(writer, game.boxScore).write() }

        writer.write("}")
    }
}