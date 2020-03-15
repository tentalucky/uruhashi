package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID

@Entity(tableName = "plate_appearance", primaryKeys = ["gameId", "inningSeqNumber", "seqNumber"])
class PlateAppearanceData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var seqNumber: Int = -1

    fun isChildOf(inningData: InningData) =
        this.gameId == inningData.gameId && this.inningSeqNumber == inningData.seqNumber
}