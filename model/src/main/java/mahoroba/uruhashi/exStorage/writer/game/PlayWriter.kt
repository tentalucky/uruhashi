package mahoroba.uruhashi.exStorage.writer.game

import android.support.annotation.CallSuper
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

abstract class PlayWriter(writer: Writer, private val play: Play): PeriodWriter(writer, play) {

    @CallSuper
    override fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
        super.writeOwnProperties(jsonWriter)

        jsonWriter.writeItem("fieldActiveDuration") {
            FieldActiveDurationWriter(writer, play.fieldActiveDuration).write()
        }
        jsonWriter.writeItem("battingResult", play.battingResult)

    }
}