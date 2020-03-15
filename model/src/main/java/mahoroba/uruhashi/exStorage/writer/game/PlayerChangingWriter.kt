package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.PlayerChanging
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PlayerChangingWriter(private val writer: Writer, private val playerChanging: PlayerChanging) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("battingOrder", playerChanging.battingOrder)
        jsonWriter.writeItem("newPlayer") {
            PlayerWriter(writer, playerChanging.newPlayer).write()
        }

        writer.write("}")
    }
}