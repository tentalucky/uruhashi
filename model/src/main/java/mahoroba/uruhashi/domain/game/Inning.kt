package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.TeamClass.*
import mahoroba.uruhashi.domain.game.TopOrBottom.*
import mahoroba.uruhashi.domain.game.secondary.Lineup

class Inning(
    val seqNumber: Int,
    previousBattingOrder: Int,
    private val onInningCompletedListener: () -> Unit,
    private val onSubstitutionInsertedListener: (Inning) -> Unit
) {

    val topOrBottom: TopOrBottom
        get() = if (seqNumber % 2 == 1) TOP else BOTTOM

    private val mPlateAppearances = ArrayList<PlateAppearance>()
    val plateAppearances: List<PlateAppearance>
        get() = mPlateAppearances

    val lastCompleteBattingOrder: Int
        get() = plateAppearances.last { p -> p.isCompleted }.battingOrder

    val outs: Int
        get() {
            var outs = 0
            plateAppearances.forEach { outs += it.outsInThis }
            return outs
        }

    val leftOnBases: Int
        get() {
            if (outs < 3) return 0

            val lastPlay = plateAppearances.last().periods.filterIsInstance<Play>().last()
            val lastSituation = lastPlay.previousSituation.after(
                (lastCompleteBattingOrder + 1) % 9, lastPlay
            )

            var leftOnBases = 0
            leftOnBases += if (lastSituation.orderOf1R != null) 1 else 0
            leftOnBases += if (lastSituation.orderOf2R != null) 1 else 0
            leftOnBases += if (lastSituation.orderOf3R != null) 1 else 0

            return leftOnBases
        }

    init {
        mPlateAppearances.add(
            PlateAppearance(
                (previousBattingOrder + 1) % 9,
                this::onNewPlayAdded,
                this::onSubstitutionInsertedIntoPlateAppearance,
                this::onPlayModified
            )
        )
    }

    fun contains(period: Period?): Boolean {
        plateAppearances.forEach { pa -> if (pa.periods.contains(period)) return true }
        return false
    }

    fun popLastPeriod(): Period? {
        var period: Period?
        do {
            period = mPlateAppearances.last().popLastPeriod()
            if (period != null) break
            if (mPlateAppearances.size <= 1) break
            mPlateAppearances.remove(mPlateAppearances.last())
        } while (true)

        return period
    }

    fun getRunsUntil(period: Period?): Int {
        var r = 0
        plateAppearances.forEach { pa ->
            pa.periods.forEach {
                if (it == period) return r
                if (it is Play) r += it.fieldActiveDuration.runs
            }
        }
        return r
    }

    fun replaceFirstPeriodLineup(homeLineup: Lineup, visitorLineup: Lineup) {
        adjustLineupsOfPlateAppearances(homeLineup, visitorLineup, 0)
    }

    private fun newPlateAppearance() {
        mPlateAppearances.add(
            PlateAppearance(
                (mPlateAppearances.last().battingOrder + 1) % 9,
                this::onNewPlayAdded,
                this::onSubstitutionInsertedIntoPlateAppearance,
                this::onPlayModified
            )
        )
    }

    private fun onNewPlayAdded() {
        // TODO: Implement
        if (outs >= 3) {
            onInningCompletedListener.invoke()
            return
        }

        if (plateAppearances.last().isCompleted)
            newPlateAppearance()
    }

    private fun onSubstitutionInsertedIntoPlateAppearance(pa: PlateAppearance) {
        if (pa != plateAppearances.last()) {
            val p = pa.periods.last()
            val homeLineup =
                if (p is Substitution && p.teamClass == HOME) p.homeLineup.after(p)
                else p.homeLineup
            val visitorLineup =
                if (p is Substitution && p.teamClass == VISITOR) p.visitorLineup.after(p)
                else p.visitorLineup

            adjustLineupsOfPlateAppearances(
                homeLineup, visitorLineup, plateAppearances.indexOf(pa) + 1
            )
        }
        onSubstitutionInsertedListener.invoke(this)
    }

    private fun adjustLineupsOfPlateAppearances(
        homeLineup: Lineup, visitorLineup: Lineup, startIndex: Int
    ) {
        var lastHomeLineup = homeLineup
        var lastVisitorLineup = visitorLineup

        plateAppearances.subList(startIndex, plateAppearances.size).forEach {
            it.replaceFirstPeriodLineup(lastHomeLineup, lastVisitorLineup)

            it.periods.lastOrNull()?.let {
                lastHomeLineup =
                    if (it is Substitution && it.teamClass == HOME) it.homeLineup.after(it)
                    else it.homeLineup
                lastVisitorLineup =
                    if (it is Substitution && it.teamClass == VISITOR) it.visitorLineup.after(it)
                    else it.visitorLineup
            }
        }
    }

    private fun onPlayModified(pa: PlateAppearance) {
        // TODO: Implement
    }
}