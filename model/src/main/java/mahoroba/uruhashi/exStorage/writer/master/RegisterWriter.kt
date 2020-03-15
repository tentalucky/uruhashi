package mahoroba.uruhashi.exStorage.writer.master

import mahoroba.uruhashi.domain.Register
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class RegisterWriter(private val writer: Writer, private val register: Register) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("teamId", register.teamId)
        jsonWriter.writeItem("playerId", register.playerId)
        jsonWriter.writeItem("uniformNumber", register.uniformNumber)

        writer.write("}")
    }
}