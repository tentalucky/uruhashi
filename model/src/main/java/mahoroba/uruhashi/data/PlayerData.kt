package mahoroba.uruhashi.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import mahoroba.uruhashi.domain.*

@Entity(tableName = "playerProfile")
class PlayerData(
) {
    @PrimaryKey var id: String = ""
    var familyName: String? = null
    var firstName: String? = null
    var nameType: NameType = NameType.FAMILY_NAME_FIRST
    var batHand: HandType? = null
    var throwHand: HandType? = null

    constructor(playerProfile: PlayerProfile) : this() {
        id = playerProfile.id.value
        familyName = playerProfile.name.familyName
        firstName = playerProfile.name.firstName
        nameType = playerProfile.name.nameType
        batHand = playerProfile.bats
        throwHand = playerProfile.throws
    }

    fun toPlayer() : PlayerProfile {
        return PlayerProfile(
            ID(id),
            PersonName(familyName, firstName, nameType),
            batHand,
            throwHand
        )
    }
}