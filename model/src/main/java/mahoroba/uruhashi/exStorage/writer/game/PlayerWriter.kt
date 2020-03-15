package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Player
import mahoroba.uruhashi.exStorage.writer.PersonNameWriter
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PlayerWriter(private val writer: Writer, private val player: Player) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("playerId", player.playerId?.value)
        jsonWriter.writeItem("playerName") {
            if (player.playerName == null) {
                writer.write("null")
            } else {
                PersonNameWriter(
                    writer,
                    player.playerName!!
                ).write()
            }
        }
        jsonWriter.writeItem("uniformNumber", player.uniformNumber)
        jsonWriter.writeItem("bats", player.bats)
        jsonWriter.writeItem("throws", player.throws)

        writer.write("}")
    }
}