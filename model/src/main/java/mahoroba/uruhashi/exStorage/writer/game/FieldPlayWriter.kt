package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.FieldPlay
import mahoroba.uruhashi.domain.game.RunnersAction
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class FieldPlayWriter(private val writer: Writer, private val fieldPlay: FieldPlay) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("factor", fieldPlay.factor)
        jsonWriter.writeItem("fieldersActionList") {
            writer.write("[")
            fieldPlay.fieldersActionList.forEachIndexed { idx, action ->
                if (idx > 0) writer.write(", ")
                FieldersActionWriter(writer, action).write()
            }
            writer.write("]")
        }

        val runnersActionList = ArrayList<RunnersAction>().apply {
            fieldPlay.batterRunnersAction?.let { this.add(it) }
            fieldPlay.firstRunnersAction?.let { this.add(it)}
            fieldPlay.secondRunnersAction?.let { this.add(it)}
            fieldPlay.thirdRunnersAction?.let { this.add(it) }
        }
        jsonWriter.writeItem("runnersActionList") {
            writer.write("[")
            runnersActionList.forEachIndexed { idx, action ->
                if (idx > 0) writer.write(", ")
                RunnersActionWriter(writer, action).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}