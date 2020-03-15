package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Period
import mahoroba.uruhashi.domain.game.Play
import mahoroba.uruhashi.domain.game.Substitution

@Entity(
    tableName = "period",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "seqNumber"]
)
class PeriodData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var seqNumber: Int = -1
    var periodType: Int = -1

    fun isChildOf(pa: PlateAppearanceData) =
        this.gameId == pa.gameId
                && this.inningSeqNumber == pa.inningSeqNumber
                && this.plateAppearanceSeqNumber == pa.seqNumber

    companion object {
        enum class PeriodDataType(val value: Int) {
            SUBSTITUTION(1),
            PLAY(2)
        }

        fun toPeriodType(period: Period): Int = when (period) {
            is Substitution -> PeriodDataType.SUBSTITUTION.value
            is Play -> PeriodDataType.PLAY.value
            else -> throw TypeCastException("Must be instance of Substitution or Play class.")
        }

        fun getPeriodType(data: PeriodData) =
            PeriodDataType.values().find { n -> n.value == data.periodType }
                ?: throw RuntimeException("Undefined period type value.")
    }
}