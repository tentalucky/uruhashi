package mahoroba.uruhashi.exStorage.writer.master

import mahoroba.uruhashi.domain.Stadium
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class StadiumWriter(private val writer: Writer, private val stadium: Stadium) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("id", stadium.id)
        jsonWriter.writeItem("name", stadium.name)
        jsonWriter.writeItem("abbreviatedName", stadium.abbreviatedName)
        jsonWriter.writeItem("priority", stadium.priority)

        writer.write("}")
    }
}