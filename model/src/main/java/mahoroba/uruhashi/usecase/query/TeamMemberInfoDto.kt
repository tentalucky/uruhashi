package mahoroba.uruhashi.usecase.query

import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType
import mahoroba.uruhashi.domain.PersonName

data class TeamMemberInfoDto(
    val playerId: ID,
    val firstName: String?,
    val familyName: String?,
    val nameType: NameType,
    val uniformNumber: String?
) {
    val fullName: String?
        get() {
            return if (nameType == NameType.FAMILY_NAME_FIRST)
                "$familyName $firstName"
            else
                "$firstName $familyName"
        }
}