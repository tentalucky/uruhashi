package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class Ball(
    pitchType: PitchType,
    pitchSpeed: Int?,
    pitchLocation: PitchLocation?,
    battingOption: BattingOption,
    withRunnerStarting: Boolean,
    settled: Boolean,
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation,
    fieldPlays: FieldActiveDuration,
    battingResult: BattingResult?
) : Pitch(
    pitchType,
    pitchSpeed,
    pitchLocation,
    battingOption,
    withRunnerStarting,
    settled,
    homeLineup,
    visitorLineup,
    previousSituation,
    fieldPlays,
    battingResult
) {
}