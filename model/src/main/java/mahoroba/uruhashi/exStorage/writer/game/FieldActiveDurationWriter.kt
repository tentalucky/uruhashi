package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.FieldActiveDuration
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class FieldActiveDurationWriter(
    private val writer: Writer,
    private val fieldActiveDuration: FieldActiveDuration
) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("fieldPlayList") {
            writer.write("[")
            fieldActiveDuration.fieldPlayList.forEachIndexed { idx, fp ->
                if (idx > 0) writer.write(", ")
                FieldPlayWriter(writer, fp).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}