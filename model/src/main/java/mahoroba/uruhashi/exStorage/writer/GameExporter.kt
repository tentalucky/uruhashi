package mahoroba.uruhashi.exStorage.writer

import android.app.Application
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Toast
import mahoroba.uruhashi.domain.game.Game
import mahoroba.uruhashi.domain.game.IGameExporter
import mahoroba.uruhashi.exStorage.writer.game.GameWriter
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class GameExporter(val application: Application) : IGameExporter {
    private companion object {
        const val VERSION = "1.0"
    }

    override fun export(game: Game) {
        if (!isExternalStorageWritable()) return

        val path = application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val fileName =
            SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date()) +
                    "_" + game.id.value + ".data"
        val file = File(path, fileName)

        val handler = Handler()
        FileOutputStream(file).use { stream ->
            OutputStreamWriter(stream, StandardCharsets.UTF_8).use { osw ->
                BufferedWriter(osw).use { bw -> write(bw, game) }
                val message = "File: ${file.absolutePath} is exported."
                handler.post { Toast.makeText(application, message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun write(writer: BufferedWriter, game: Game) {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("version", VERSION)
        jsonWriter.writeItem("game") { GameWriter(writer, game).write() }

        writer.write("}")

        writer.flush()
    }
}