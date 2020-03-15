package mahoroba.uruhashi.exStorage.writer.master

import mahoroba.uruhashi.domain.TeamProfile
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class TeamProfileWriter(private val writer: Writer, private val teamProfile: TeamProfile) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("id", teamProfile.id)
        jsonWriter.writeItem("name", teamProfile.name)
        jsonWriter.writeItem("abbreviatedName", teamProfile.abbreviatedName)
        jsonWriter.writeItem("priority", teamProfile.priority)

        jsonWriter.writeItem("roster") {
            writer.write("[")
            var idx = 0
            teamProfile.roster.forEach { register ->
                if (idx++ > 0) writer.write(", ")
                RegisterWriter(writer, register).write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}