package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Foul
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class FoulWriter(writer: Writer, private val foul: Foul) : PitchWriter(writer, foul) {
    override fun getPeriodTypeName() = "Foul"

    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("direction", foul.direction)
        jsonWriter.writeItem("isAtLine", foul.isAtLine)
        jsonWriter.writeItem("battedBallType", foul.battedBallType)
        jsonWriter.writeItem("battedBallStrength", foul.battedBallStrength)
    }
}