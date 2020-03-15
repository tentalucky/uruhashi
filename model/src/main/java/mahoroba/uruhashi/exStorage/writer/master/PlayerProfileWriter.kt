package mahoroba.uruhashi.exStorage.writer.master

import mahoroba.uruhashi.domain.PlayerProfile
import mahoroba.uruhashi.exStorage.writer.PersonNameWriter
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PlayerProfileWriter(private val writer: Writer, private val playerProfile: PlayerProfile) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("id", playerProfile.id)
        jsonWriter.writeItem("name") { PersonNameWriter(writer, playerProfile.name).write() }
        jsonWriter.writeItem("bats", playerProfile.bats)
        jsonWriter.writeItem("throws", playerProfile.throws)

        // This does not write belongings data because of TeamProfileWriter writing them.

        writer.write("}")
    }
}