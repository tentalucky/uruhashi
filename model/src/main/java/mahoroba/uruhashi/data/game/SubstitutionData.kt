package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.TeamClass

@Entity(
    tableName = "substitution",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber"]
)
class SubstitutionData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1

    lateinit var teamClass: TeamClass
    var cancelDH: Boolean = false

    fun isDescriptionOf(periodData: PeriodData) =
        this.gameId == periodData.gameId
                && this.inningSeqNumber == periodData.inningSeqNumber
                && this.plateAppearanceSeqNumber == periodData.plateAppearanceSeqNumber
                && this.periodSeqNumber == periodData.seqNumber
}