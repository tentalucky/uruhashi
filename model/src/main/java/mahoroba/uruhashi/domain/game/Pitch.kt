package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

abstract class Pitch(
    val pitchType: PitchType,
    val pitchSpeed: Int?,
    val pitchLocation: PitchLocation?,
    val battingOption: BattingOption,
    val withRunnerStarting: Boolean,
    val settled: Boolean,
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation,
    fieldPlays: FieldActiveDuration,
    battingResult: BattingResult?
) : Play(previousSituation, fieldPlays, battingResult, homeLineup, visitorLineup) {

    init {
        if (settled && battingResult == null)
            throw IllegalArgumentException("When the pitch is settled, batting result must not be null.")
        if (!settled && battingResult != null)
            throw IllegalArgumentException("When the pitch is not settled, batting result must be null.")
    }
}