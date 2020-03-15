package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class NoPitchIntentionalWalk(
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation,
    fieldPlays: FieldActiveDuration
) : Play(previousSituation, fieldPlays, BattingResult.INTENTIONAL_WALK, homeLineup, visitorLineup) {
}