package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class Foul(
    pitchType: PitchType,
    pitchSpeed: Int?,
    pitchLocation: PitchLocation?,
    battingOption: BattingOption,
    withRunnerStarting: Boolean,
    val direction: FoulBallDirection,
    val isAtLine: Boolean?,
    val battedBallType: BattedBallType,
    val battedBallStrength: BattedBallStrength,
    positionMakesError: FieldPosition?,
    isMissedBuntWithTwoStrikes: Boolean,
    homeLineup: Lineup,
    visitorLineup: Lineup,
    previousSituation: Situation
) : Pitch(
    pitchType,
    pitchSpeed,
    pitchLocation,
    battingOption,
    withRunnerStarting,
    isMissedBuntWithTwoStrikes,
    homeLineup,
    visitorLineup,
    previousSituation,
    makeFieldPlays(positionMakesError, isMissedBuntWithTwoStrikes),
    if (isMissedBuntWithTwoStrikes) BattingResult.STRIKEOUT else null
) {

    companion object {
        private fun makeFieldPlays(
            positionMakesError: FieldPosition?,
            isMissedBuntWithTwoStrikes: Boolean
        ): FieldActiveDuration {

            val fieldersActionList = ArrayList<FieldersAction>().apply {
                if (positionMakesError != null) {
                    this.add(FieldersAction(positionMakesError, FieldingRecord.ERROR_CATCHING))
                }
                if (isMissedBuntWithTwoStrikes) {
                    this.add(FieldersAction(FieldPosition.CATCHER, null))
                }
            }

            val runnersActionList = ArrayList<RunnersAction>().apply {
                if (isMissedBuntWithTwoStrikes) {
                    this.add(
                        RunnersAction(
                            RunnerType.BATTER_RUNNER,
                            RunnerState.OUT_IN_PROGRESSING
                        )
                    )
                }
            }

            if (fieldersActionList.isNotEmpty() || runnersActionList.isNotEmpty()) {
                return FieldActiveDuration(
                    listOf(
                        FieldPlay(
                            FieldPlayFactor.FOUL,
                            fieldersActionList,
                            runnersActionList
                        )
                    )
                )
            } else {
                return FieldActiveDuration(emptyList())
            }
        }
    }
}