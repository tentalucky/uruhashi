package mahoroba.uruhashi.exStorage.writer.utility

import java.io.Writer

class JsonItemWriteHelper(private val writer: Writer) {
    private var hasWritten = false

    fun writeItem(tag: String, value: Any?) {
        writeTag(tag)
        writeValue(value)
    }

    fun writeItem(tag: String, writeMethod: (Unit) -> Unit) {
        writeTag(tag)
        writeMethod(Unit)
    }

    private fun writeTag(tag: String) {
        if (hasWritten) writer.write(", ")
        writer.write("\"$tag\": ")

        hasWritten = true
    }

    private fun writeValue(value: Any?) {
        when (value) {
            null -> writer.write("null")
            is Int,
            is Short,
            is Long,
            is Float,
            is Double -> writer.write(value.toString())
            is Boolean -> writer.write(if (value) "true" else "false")
            is Collection<*> -> {
                writer.write("[")
                value.forEachIndexed { idx, it ->
                    if (idx > 0) writer.write(", ")
                    writeValue(it)
                }
                writer.write("]")
            }
            else -> writer.write("\"$value\"")
        }
    }
}