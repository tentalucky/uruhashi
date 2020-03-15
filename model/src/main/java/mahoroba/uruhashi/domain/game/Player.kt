package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.HandType
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.PersonName

class Player(
    var playerId: ID?,
    var playerName: PersonName?,
    var uniformNumber: String?,
    var bats: HandType?,
    var throws: HandType?
)