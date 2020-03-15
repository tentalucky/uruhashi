package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.TeamClass.*
import mahoroba.uruhashi.domain.game.secondary.Lineup

class PlateAppearance(
    val battingOrder: Int,
    private val onNewPlayAddedListener: () -> Unit,
    private val onSubstitutionModifiedListener: (PlateAppearance) -> Unit,
    private val onPlayModifiedListener: (PlateAppearance) -> Unit
) {
    private val mPeriods = ArrayList<Period>()
    val periods: List<Period> = mPeriods

    val strikes: Int
        get() {
            var s = 0
            mPeriods.forEach { if (it is Strike || it is Foul && s < 2) s++ }
            return s
        }

    val balls: Int
        get() = mPeriods.count { p -> p is Ball }

    val outsInThis: Int
        get() {
            var o = 0
            mPeriods.filterIsInstance<Play>().forEach { o += (it as Play).fieldActiveDuration.outs }
            return o
        }

    val isCompleted: Boolean
        get() = mPeriods.count { it is Pitch && it.settled || it is NoPitchIntentionalWalk } > 0

    fun addPeriod(period: Period) {
        if (isCompleted) throw RuntimeException("Trying to add period to completed plate appearance.")

        mPeriods.add(period)
        if (period is Play) onNewPlayAddedListener.invoke()
    }

    fun insertSubstitution(target: Substitution, anchor: Period) {
        mPeriods.add(mPeriods.indexOf(anchor), target)

        val latestHomeLineup: Lineup = when (target.teamClass) {
            HOME -> target.homeLineup.after(target)
            VISITOR -> target.homeLineup
        }
        val latestVisitorLineup: Lineup = when (target.teamClass) {
            HOME -> target.visitorLineup
            VISITOR -> target.visitorLineup.after(target)
        }

        replaceLineups(latestHomeLineup, latestVisitorLineup, anchor)

        onSubstitutionModifiedListener.invoke(this)
    }

    fun replaceSubstitution(newSubstitution: Substitution, oldSubstitution: Substitution) {
        mPeriods[mPeriods.indexOf(oldSubstitution)] = newSubstitution

        if (mPeriods.last() != newSubstitution) {
            val latestHomeLineup: Lineup = when (newSubstitution.teamClass) {
                HOME -> newSubstitution.homeLineup.after(newSubstitution)
                VISITOR -> newSubstitution.homeLineup
            }
            val latestVisitorLineup: Lineup = when (newSubstitution.teamClass) {
                HOME -> newSubstitution.visitorLineup
                VISITOR -> newSubstitution.visitorLineup.after(newSubstitution)
            }

            replaceLineups(
                latestHomeLineup, latestVisitorLineup,
                mPeriods[mPeriods.indexOf(newSubstitution) + 1]
            )
        }

        onSubstitutionModifiedListener.invoke(this)
    }

    fun deleteSubstitution(substitution: Substitution) {
        if (mPeriods.last() != substitution) {
            replaceLineups(
                substitution.homeLineup, substitution.visitorLineup,
                mPeriods[mPeriods.indexOf(substitution) + 1]
            )
        }

        mPeriods.remove(substitution)

        onSubstitutionModifiedListener.invoke(this)
    }

    fun replaceFirstPeriodLineup(homeLineup: Lineup, visitorLineup: Lineup) {
        if (mPeriods.isEmpty()) return

        replaceLineups(homeLineup, visitorLineup, mPeriods.first())
    }

    private fun replaceLineups(homeLineup: Lineup, visitorLineup: Lineup, startPeriod: Period) {
        var latestHomeLineup = homeLineup
        var latestVisitorLineup = visitorLineup

        mPeriods.subList(mPeriods.indexOf(startPeriod), mPeriods.size).forEach {
            it.homeLineup = latestHomeLineup
            it.visitorLineup = latestVisitorLineup

            if (it is Substitution) {
                when (it.teamClass) {
                    HOME -> latestHomeLineup = it.homeLineup.after(it)
                    VISITOR -> latestVisitorLineup = it.visitorLineup.after(it)
                }
            }
        }
    }

    fun replacePlay(newPlay: Play, oldPlay: Play) {
        mPeriods[mPeriods.indexOf(oldPlay)] = newPlay

        var nextSituation = newPlay.previousSituation.after(battingOrder, newPlay)

        mPeriods.subList(mPeriods.indexOf(newPlay) + 1, mPeriods.size).forEach {
            it.previousSituation.modify(
                nextSituation.balls, nextSituation.strikes,
                nextSituation.orderOf1R, nextSituation.orderOf2R, nextSituation.orderOf3R
            )

            if (it is Play) nextSituation = it.previousSituation.after(battingOrder, it)
        }

        onPlayModifiedListener.invoke(this)
    }

    fun popLastPeriod(): Period? {
        if (periods.isEmpty()) return null

        val period = mPeriods.last()
        mPeriods.remove(period)

        return period
    }
}