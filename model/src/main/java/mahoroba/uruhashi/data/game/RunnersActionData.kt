package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.RunnerState
import mahoroba.uruhashi.domain.game.RunnerType

@Entity(
    tableName = "runners_action",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber", "fieldPlaySeqNumber", "runnerType"]
)
class RunnersActionData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var fieldPlaySeqNumber: Int = -1
    lateinit var runnerType: RunnerType
    lateinit var state: RunnerState

    fun isChildOf(fieldPlayData: FieldPlayData) =
        this.gameId == fieldPlayData.gameId
                && this.inningSeqNumber == fieldPlayData.inningSeqNumber
                && this.plateAppearanceSeqNumber == fieldPlayData.plateAppearanceSeqNumber
                && this.periodSeqNumber == fieldPlayData.periodSeqNumber
                && this.fieldPlaySeqNumber == fieldPlayData.seqNumber
}