package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.PositionChanging
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PositionChangingWriter(
    private val writer: Writer,
    private val positionChanging: PositionChanging
) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("battingOrder", positionChanging.battingOrder)
        jsonWriter.writeItem("newPosition", positionChanging.newPosition)

        writer.write("}")
    }
}