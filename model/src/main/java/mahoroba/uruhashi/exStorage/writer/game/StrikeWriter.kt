package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Strike
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class StrikeWriter(writer: Writer, private val strike: Strike): PitchWriter(writer, strike) {
    override fun getPeriodTypeName() = "Strike"

    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("strikeType", strike.strikeType)
    }
}