package mahoroba.uruhashi.usecase.query

import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType

data class PlayerInGameDto(
    val playerId: ID,
    val familyName: String,
    val firstName: String,
    val nameType: NameType,
    val batHand: HandType?,
    val throwHand: HandType?,
    val uniformNumber: String?
)
