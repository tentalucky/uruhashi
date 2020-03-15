package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.StadiumInformation
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class StadiumInformationWriter(
    private val writer: Writer,
    private val stadiumInfo: StadiumInformation
) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("stadiumId", stadiumInfo.stadiumId)
        jsonWriter.writeItem("stadiumName", stadiumInfo.stadiumName)
        jsonWriter.writeItem("abbreviatedName", stadiumInfo.abbreviatedName)

        writer.write("}")
    }
}