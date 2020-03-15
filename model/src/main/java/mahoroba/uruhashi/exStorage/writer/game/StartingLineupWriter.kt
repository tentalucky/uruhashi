package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.StartingLineup
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class StartingLineupWriter(private val writer: Writer, private val startingLineup: StartingLineup) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("hasDH", startingLineup.hasDH)
        jsonWriter.writeItem("positions", startingLineup.positions)
        jsonWriter.writeItem("players") {
            writer.write("[")
            startingLineup.players.forEachIndexed {idx, p ->
                if (idx > 0) writer.write(", ")
                PlayerWriter(writer, p).write()
            }
            writer.write("]")
        }
        jsonWriter.writeItem("playerOutOfOrder") {
            PlayerWriter(writer, startingLineup.playerOutOfOrder).write()
        }

        writer.write("}")
    }
}