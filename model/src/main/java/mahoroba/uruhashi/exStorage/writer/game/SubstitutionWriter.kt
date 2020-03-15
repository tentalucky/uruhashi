package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Substitution
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class SubstitutionWriter(writer: Writer, private val substitution: Substitution) :
    PeriodWriter(writer, substitution) {
    override fun getPeriodTypeName() = "Substitution"

    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("teamClass", substitution.teamClass)
        jsonWriter.writeItem("cancelDH", substitution.cancelDH)
        jsonWriter.writeItem("positionChangingList") {
            writer.write("[")
            substitution.positionChangingList.forEachIndexed { idx, changing ->
                if (idx > 0) writer.write(", ")
                PositionChangingWriter(writer, changing).write()
            }
            writer.write("]")
        }
        jsonWriter.writeItem("playerChangingList") {
            writer.write("[")
            substitution.playerChangingList.forEachIndexed { idx, changing ->
                if (idx > 0) writer.write(", ")
                PlayerChangingWriter(writer, changing).write()
            }
            writer.write("]")
        }
    }
}