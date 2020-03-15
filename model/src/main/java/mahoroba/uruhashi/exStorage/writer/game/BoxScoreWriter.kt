package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.BoxScore
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class BoxScoreWriter(private val writer: Writer, private val boxScore: BoxScore) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("homeStartingLineup") {
            StartingLineupWriter(writer, boxScore.homeStartingLineup).write()
        }
        jsonWriter.writeItem("visitorStartingLineup") {
            StartingLineupWriter(writer, boxScore.visitorStartingLineup).write()
        }
        jsonWriter.writeItem("gameStatus", boxScore.gameStatus)
        jsonWriter.writeItem("innings") {
            writer.write("[")
            boxScore.innings.forEachIndexed { idx, inning ->
                if (idx > 0) writer.write(", ")
                InningWriter(writer, inning).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}