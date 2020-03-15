package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.FieldersAction
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class FieldersActionWriter(private val writer: Writer, private val fieldersAction: FieldersAction) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("position", fieldersAction.position)
        jsonWriter.writeItem("record", fieldersAction.record)

        writer.write("}")
    }
}