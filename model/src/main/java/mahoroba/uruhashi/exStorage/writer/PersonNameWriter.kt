package mahoroba.uruhashi.exStorage.writer

import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.Writer

class PersonNameWriter(private val writer: Writer, private val personName: PersonName) {
    fun write() {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("familyName", personName.familyName)
        jsonWriter.writeItem("firstName", personName.firstName)
        jsonWriter.writeItem("nameType", personName.nameType)

        writer.write("}")
    }
}