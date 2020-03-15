package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.BattedBallDirection
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class BattedBallDirectionWriter(
    private val writer: Writer,
    private val battedBallDirection: BattedBallDirection?
) {
    fun write() {
        if (battedBallDirection == null) {
            writer.write("null")
            return
        }

        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("tracePoints") {
            writer.write("[")
            battedBallDirection.tracePoints.forEachIndexed { idx, coordinate ->
                if (idx > 0) writer.write(", ")
                AngularCoordinateWriter(writer, coordinate).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}