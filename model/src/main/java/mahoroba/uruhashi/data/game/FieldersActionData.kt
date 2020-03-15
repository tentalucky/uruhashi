package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.FieldPosition
import mahoroba.uruhashi.domain.game.FieldingRecord

@Entity(
    tableName = "fielders_action",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber", "fieldPlaySeqNumber", "seqNumber"]
)
class FieldersActionData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var fieldPlaySeqNumber: Int = -1
    var seqNumber: Int = -1

    lateinit var position: FieldPosition
    var record: FieldingRecord? = null

    fun isChildOf(fieldPlayData: FieldPlayData) =
        this.gameId == fieldPlayData.gameId
                && this.inningSeqNumber == fieldPlayData.inningSeqNumber
                && this.plateAppearanceSeqNumber == fieldPlayData.plateAppearanceSeqNumber
                && this.periodSeqNumber == fieldPlayData.periodSeqNumber
                && this.fieldPlaySeqNumber == fieldPlayData.seqNumber
}