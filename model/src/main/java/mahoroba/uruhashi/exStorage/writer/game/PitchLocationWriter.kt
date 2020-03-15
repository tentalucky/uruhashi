package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.PitchLocation
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PitchLocationWriter(private val writer: Writer, private val pitchLocation: PitchLocation?) {
    fun write() {
        if (pitchLocation == null) {
            writer.write("null")
            return
        }

        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("x", pitchLocation.x)
        jsonWriter.writeItem("y", pitchLocation.y)

        writer.write("}")
    }
}