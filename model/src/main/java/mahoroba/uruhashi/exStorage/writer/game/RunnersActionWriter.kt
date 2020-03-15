package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.RunnersAction
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class RunnersActionWriter(private val writer: Writer, private val runnersAction: RunnersAction) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("type", runnersAction.type)
        jsonWriter.writeItem("state", runnersAction.state)

        writer.write("}")
    }
}