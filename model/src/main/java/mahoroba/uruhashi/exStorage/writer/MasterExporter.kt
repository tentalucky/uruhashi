package mahoroba.uruhashi.exStorage.writer

import android.app.Application
import android.os.Environment
import android.os.Handler
import android.widget.Toast
import mahoroba.uruhashi.domain.IMasterExporter
import mahoroba.uruhashi.domain.PlayerProfile
import mahoroba.uruhashi.domain.Stadium
import mahoroba.uruhashi.domain.TeamProfile
import mahoroba.uruhashi.exStorage.writer.master.PlayerProfileWriter
import mahoroba.uruhashi.exStorage.writer.master.StadiumWriter
import mahoroba.uruhashi.exStorage.writer.master.TeamProfileWriter
import mahoroba.uruhashi.exStorage.writer.utility.JsonItemWriteHelper
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.util.*

class MasterExporter(val application: Application) : IMasterExporter {
    private companion object {
        const val VERSION = "1.0"
    }

    override fun export(
        stadiums: List<Stadium>,
        teams: List<TeamProfile>,
        players: List<PlayerProfile>
    ) {
        if (!isExternalStorageWritable()) return

        val path = application.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val fileName =
            SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date()) +
                    "_master.data"
        val file = File(path, fileName)

        val handler = Handler()
        FileOutputStream(file).use { stream ->
            OutputStreamWriter(stream, StandardCharsets.UTF_8).use { osw ->
                BufferedWriter(osw).use { bw -> writeMasterData(bw, stadiums, teams, players) }
                val message = "File: ${file.absolutePath} is exported."
                handler.post { Toast.makeText(application, message, Toast.LENGTH_SHORT).show() }
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun writeMasterData(
        writer: BufferedWriter,
        stadiums: List<Stadium>,
        teams: List<TeamProfile>,
        players: List<PlayerProfile>
    ) {
        val jsonWriter = JsonItemWriteHelper(writer)

        writer.write("{")

        jsonWriter.writeItem("version", VERSION)

        jsonWriter.writeItem("stadiums") {
            writer.write("[")
            stadiums.forEachIndexed { idx, stadium ->
                if (idx > 0) writer.write(", ")
                StadiumWriter(writer, stadium).write()
            }
            writer.write("]")
        }

        jsonWriter.writeItem("teams") {
            writer.write("[")
            teams.forEachIndexed { idx, team ->
                if (idx > 0) writer.write(", ")
                TeamProfileWriter(writer, team).write()
            }
            writer.write("]")
        }

        jsonWriter.writeItem("players") {
            writer.write("[")
            players.forEachIndexed { idx, player ->
                if (idx > 0) writer.write(", ")
                PlayerProfileWriter(writer, player).write()
            }
            writer.write("]")
        }

        writer.write("}")
        writer.flush()
    }
}