package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

abstract class Period(
    val previousSituation: Situation,
    var homeLineup: Lineup,
    var visitorLineup: Lineup
) {
}