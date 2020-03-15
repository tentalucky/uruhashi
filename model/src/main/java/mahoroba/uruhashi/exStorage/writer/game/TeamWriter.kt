package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Team
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class TeamWriter(private val writer: Writer, private val team: Team) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("teamId", team.teamId?.value)
        jsonWriter.writeItem("teamName", team.teamName)
        jsonWriter.writeItem("abbreviatedName", team.abbreviatedName)

        writer.write("}")
    }
}