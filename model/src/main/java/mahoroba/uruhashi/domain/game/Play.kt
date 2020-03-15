package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

abstract class Play(
    previousSituation: Situation,
    val fieldActiveDuration: FieldActiveDuration,
    val battingResult: BattingResult?,
    homeLineup: Lineup,
    visitorLineup: Lineup
) : Period(previousSituation, homeLineup, visitorLineup) {

    init {
        if (previousSituation.orderOf1R == null && fieldActiveDuration.fieldPlayList.any { fp -> fp.firstRunnersAction != null } ||
            previousSituation.orderOf2R == null && fieldActiveDuration.fieldPlayList.any { fp -> fp.secondRunnersAction != null } ||
            previousSituation.orderOf3R == null && fieldActiveDuration.fieldPlayList.any { fp -> fp.thirdRunnersAction != null })
            throw RuntimeException("The non-existent runner's action is detected.")
    }
}