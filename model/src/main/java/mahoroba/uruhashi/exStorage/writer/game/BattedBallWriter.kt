package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.BattedBall
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class BattedBallWriter(private val writer: Writer, private val battedBall: BattedBall) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("direction") {
            BattedBallDirectionWriter(writer, battedBall.direction).write()
        }
        jsonWriter.writeItem("type", battedBall.type)
        jsonWriter.writeItem("strength", battedBall.strength)
        jsonWriter.writeItem("distance", battedBall.distance)
        jsonWriter.writeItem("bunt", battedBall.bunt)

        writer.write("}")
    }
}