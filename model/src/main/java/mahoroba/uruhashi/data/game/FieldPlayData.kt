package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.FieldPlayFactor

@Entity(
    tableName = "field_play",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber", "seqNumber"]
)
class FieldPlayData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var seqNumber: Int = -1

    var factor: FieldPlayFactor? = null

    fun isChildOf(playData: PlayData) =
        this.gameId == playData.gameId
                && this.inningSeqNumber == playData.inningSeqNumber
                && this.plateAppearanceSeqNumber == playData.plateAppearanceSeqNumber
                && this.periodSeqNumber == playData.periodSeqNumber
}