package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Batting
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class BattingWriter(writer: Writer, private val batting: Batting): PitchWriter(writer, batting) {
    override fun getPeriodTypeName() = "Batting"

    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("battedBall") {
            BattedBallWriter(writer, batting.battedBall).write()
        }
    }
}