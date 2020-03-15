package mahoroba.uruhashi.exStorage.writer.game

import android.support.annotation.CallSuper
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

abstract class PitchWriter(writer: Writer, private val pitch: Pitch): PlayWriter(writer, pitch) {

    @CallSuper
    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("pitchType", pitch.pitchType)
        jsonWriter.writeItem("pitchSpeed", pitch.pitchSpeed)
        jsonWriter.writeItem("pitchLocation") {
            PitchLocationWriter(writer, pitch.pitchLocation).write()
        }
        jsonWriter.writeItem("battingOption", pitch.battingOption)
        jsonWriter.writeItem("withRunnerStarting", pitch.withRunnerStarting)
        jsonWriter.writeItem("settled", pitch.settled)
    }

//    fun write() {
//        // TODO: Implement
//        val jsonWriter = JsonItemWriteHelper(writer)
//
//        writer.write("{")
//
//        jsonWriter.writeItem(
//            "type", when (pitch) {
//                is Strike -> "Strike"
//                is Ball -> "Ball"
//                is Foul -> "Foul"
//                is Batting -> "Batting"
//                is HitByPitch -> "HitByPitch"
//                else -> null
//            }
//        )
//
//        jsonWriter.writeItem("pitchType", pitch.pitchType)
//        jsonWriter.writeItem("pitchSpeed", pitch.pitchSpeed)
//        jsonWriter.writeItem("pitchLocation") {
//            PitchLocationWriter(writer, pitch.pitchLocation).write()
//        }
//        jsonWriter.writeItem("battingOption", pitch.battingOption)
//        jsonWriter.writeItem("withRunnerStarting", pitch.withRunnerStarting)
//        jsonWriter.writeItem("settled", pitch.settled)
//
//        when (pitch) {
//            is Strike -> jsonWriter.writeItem("strike") {
//                StrikeWriter(writer, pitch).write()
//            }
//            is Foul -> jsonWriter.writeItem("foul") {
//                FoulWriter(writer, pitch).write()
//            }
//            is Batting -> jsonWriter.writeItem("batting") {
//                BattingWriter(writer, pitch).write()
//            }
//        }
//
//        writer.write("}")
//    }
}