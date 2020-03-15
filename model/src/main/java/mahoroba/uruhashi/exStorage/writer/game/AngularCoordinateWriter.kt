package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.AngularCoordinate
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class AngularCoordinateWriter(private val writer: Writer, private val angularCoordinate: AngularCoordinate) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("angle", angularCoordinate.angle)
        jsonWriter.writeItem("distance", angularCoordinate.distance)

        writer.write("}")
    }
}