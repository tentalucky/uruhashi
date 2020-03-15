package mahoroba.uruhashi.exStorage.writer.game

import android.support.annotation.CallSuper
import mahoroba.uruhashi.domain.game.Period
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

abstract class PeriodWriter(protected val writer: Writer, protected val period: Period) {
    protected abstract fun getPeriodTypeName(): String

    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("periodType", getPeriodTypeName())
        writeOwnProperties(jsonWriter)

        writer.write("}")
    }

    @CallSuper
    protected open fun writeOwnProperties(jsonWriter: JsonItemWriteHelper) {
    }
}