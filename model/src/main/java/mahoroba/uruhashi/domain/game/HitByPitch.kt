package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class HitByPitch(
    pitchType: PitchType,
    pitchSpeed: Int?,
    pitchLocation: PitchLocation?,
    battingOption: BattingOption,
    withRunnerStarting: Boolean,
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation,
    fieldPlays: FieldActiveDuration
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
    BattingResult.HIT_BY_PITCH
) {
}