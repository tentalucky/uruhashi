package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class Batting(
    pitchType: PitchType,
    pitchSpeed: Int?,
    pitchLocation: PitchLocation?,
    battingOption: BattingOption,
    withRunnerStarting: Boolean,
    val battedBall: BattedBall,
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation,
    fieldPlays: FieldActiveDuration,
    battingResult: BattingResult
) : Pitch(
    pitchType,
    pitchSpeed,
    pitchLocation,
    battingOption,
    withRunnerStarting,
    true,
    homeLineup,
    visitorLineup,
    previousSituation,
    fieldPlays,
    battingResult
) {
}