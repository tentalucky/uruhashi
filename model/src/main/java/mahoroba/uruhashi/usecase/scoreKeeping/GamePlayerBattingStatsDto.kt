package mahoroba.uruhashi.usecase.scoreKeeping

import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.TeamClass.*
import mahoroba.uruhashi.domain.game.TopOrBottom.*
import mahoroba.uruhashi.domain.game.secondary.Lineup

class GamePlayerBattingStatsDto(boxScore: BoxScore) {
    data class BattingStat(
        val result: BattingResult,
        val fieldersPosition: FieldPosition?,
        val takingErrorPosition: FieldPosition?
    )

    private val battingResults = mutableMapOf<Player, ArrayList<BattingStat>>()

    init {
        var homeLineup = Lineup(boxScore.homeStartingLineup)
        var visitorLineup = Lineup(boxScore.visitorStartingLineup)

        boxScore.innings.forEach { inn ->
            inn.plateAppearances.forEach { pa ->
                pa.periods.forEach { prd ->
                    when (prd) {
                        is Substitution -> {
                            when (prd.teamClass) {
                                HOME -> homeLineup = homeLineup.after(prd)
                                VISITOR -> visitorLineup = visitorLineup.after(prd)
                            }
                        }
                        is Play -> {
                            if (prd.battingResult != null) {
                                val player = when (inn.topOrBottom) {
                                    TOP -> visitorLineup
                                    BOTTOM -> homeLineup
                                }.getPlayer(prd.previousSituation.orderOfBatter)

                                if (battingResults[player] == null)
                                    battingResults[player] = ArrayList()

                                battingResults[player]?.add(
                                    BattingStat(
                                        prd.battingResult,
                                        prd.fieldActiveDuration
                                            .fieldPlayList.firstOrNull()
                                            ?.fieldersActionList?.firstOrNull()
                                            ?.position,
                                        prd.fieldActiveDuration
                                            .fieldPlayList.firstOrNull()
                                            ?.fieldersActionList
                                            ?.firstOrNull { a -> a.record?.isError == true }
                                            ?.position
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun getBattingResultsOf(player: Player): List<BattingStat> {
        return battingResults[player] ?: listOf()
    }

}