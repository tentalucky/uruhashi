package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType

@Entity(
    tableName = "player_changing",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber", "battingOrder"]
)
class PlayerChangingData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var battingOrder: Int = -1
    var playerId: ID? = null
    var playerFamilyName: String? = null
    var playerFirstName: String? = null
    var playerNameType: NameType? = NameType.FAMILY_NAME_FIRST
    var batHand: HandType? = null
    var throwHand: HandType? = null
    var uniformNumber: String? = null

    fun isChildOf(sub: SubstitutionData) =
        this.gameId == sub.gameId
                && this.inningSeqNumber == sub.inningSeqNumber
                && this.plateAppearanceSeqNumber == sub.plateAppearanceSeqNumber
                && this.periodSeqNumber == sub.periodSeqNumber
}