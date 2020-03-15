package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PlateAppearanceWriter(
    private val writer: Writer,
    private val plateAppearance: PlateAppearance
) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("battingOrder", plateAppearance.battingOrder)
        jsonWriter.writeItem("periods") {
            writer.write("[")
            plateAppearance.periods.forEachIndexed { idx, p ->
                if (idx > 0) writer.write(", ")
                when (p) {
                    is Balk -> BalkWriter(writer, p)
                    is Ball -> BallWriter(writer, p)
                    is Batting -> BattingWriter(writer, p)
                    is Foul -> FoulWriter(writer, p)
                    is HitByPitch -> HitByPitchWriter(writer, p)
                    is NoPitchIntentionalWalk -> NoPitchIntentionalWalkWriter(writer, p)
                    is PlayInInterval -> PlayInIntervalWriter(writer, p)
                    is Strike -> StrikeWriter(writer, p)
                    is Substitution -> SubstitutionWriter(writer, p)
                    else -> throw RuntimeException("This class inherits Period is unidentified.")
                }.write()
            }
            writer.write("]")
        }

        writer.write("}")
    }
}