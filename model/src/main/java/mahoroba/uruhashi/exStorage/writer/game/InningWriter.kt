package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Inning
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class InningWriter(private val writer: Writer, private val inning: Inning) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("seqNumber", inning.seqNumber)
        jsonWriter.writeItem("plateAppearances") {
            writer.write("[")
            inning.plateAppearances.forEachIndexed { idx, pa ->
                if (idx > 0) writer.write(", ")
                PlateAppearanceWriter(writer, pa).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}