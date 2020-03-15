package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Position

@Entity(
    tableName = "position_changing",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber", "battingOrder"]
)
class PositionChangingData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var battingOrder: Int = -1
    lateinit var position: Position

    fun isChildOf(sub: SubstitutionData) =
        this.gameId == sub.gameId
                && this.inningSeqNumber == sub.inningSeqNumber
                && this.plateAppearanceSeqNumber == sub.plateAppearanceSeqNumber
                && this.periodSeqNumber == sub.periodSeqNumber
}