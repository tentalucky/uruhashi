package mahoroba.uruhashi.domain.game

import mahoroba.uruhashi.domain.game.TeamClass.*
import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation

class BoxScore {
    val homeStartingLineup = StartingLineup(this::onStartingPositionChanged)
    val visitorStartingLineup = StartingLineup(this::onStartingPositionChanged)

    var gameStatus: GameStatus
        private set

    private val mInnings = ArrayList<Inning>()
    val innings: List<Inning>
        get() = mInnings

    init {
        gameStatus = GameStatus.PLAYING
        mInnings.add(Inning(1, -1, this::onInningCompleted, this::onSubstitutionInsertedIntoInning))
    }

    private fun onInningCompleted() {
        // TODO: implement
        newInning()
    }

    private fun onSubstitutionInsertedIntoInning(inning: Inning) {
        if (inning != innings.last()) {
            var lastHomeLineup: Lineup
            var lastVisitorLineup: Lineup

            inning.plateAppearances.last().periods.last().let {
                lastHomeLineup =
                    if (it is Substitution && it.teamClass == HOME) it.homeLineup.after(it)
                    else it.homeLineup
                lastVisitorLineup =
                    if (it is Substitution && it.teamClass == VISITOR) it.visitorLineup.after(it)
                    else it.visitorLineup
            }

            innings.subList(innings.indexOf(inning) + 1, innings.size).forEach { inn ->
                inn.replaceFirstPeriodLineup(lastHomeLineup, lastVisitorLineup)

                inn.plateAppearances.lastOrNull()?.periods?.lastOrNull()?.let {
                    lastHomeLineup =
                        if (it is Substitution && it.teamClass == HOME) it.homeLineup.after(it)
                        else it.homeLineup
                    lastVisitorLineup =
                        if (it is Substitution && it.teamClass == VISITOR) it.visitorLineup.after(it)
                        else it.visitorLineup
                }
            }
        }
    }

    private fun newInning() {
        val previousBattingOrder =
            if (innings.size < 2) -1 else innings[innings.size - 2].lastCompleteBattingOrder
        mInnings.add(
            Inning(
                innings.last().seqNumber + 1,
                previousBattingOrder,
                this::onInningCompleted,
                this::onSubstitutionInsertedIntoInning
            )
        )
    }

    private fun onStartingPositionChanged() {
        var lastHomeLineup = Lineup(homeStartingLineup)
        var lastVisitorLineup = Lineup(visitorStartingLineup)

        innings.forEach { inn ->
            inn.replaceFirstPeriodLineup(lastHomeLineup, lastVisitorLineup)

            inn.plateAppearances.lastOrNull()?.periods?.lastOrNull()?.let {
                lastHomeLineup =
                    if (it is Substitution && it.teamClass == HOME) it.homeLineup.after(it)
                    else it.homeLineup
                lastVisitorLineup =
                    if (it is Substitution && it.teamClass == VISITOR) it.visitorLineup.after(it)
                    else it.visitorLineup
            }
        }
    }

    fun undo(): Period? {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        var period: Period?
        do {
            period = mInnings.last().popLastPeriod()
            if (period != null) break
            if (mInnings.size <= 1) break
            mInnings.remove(mInnings.last())
        } while (true)

        return period
    }

    fun getSituation(period: Period?): Situation {
        var runsOfHome = 0
        var runsOfVisitor = 0
        var situation: Situation

        innings.forEach { inn ->
            if (!inn.contains(period) && !(period == null && inn == innings.last())) {
                when (inn.topOrBottom) {
                    TopOrBottom.TOP -> runsOfVisitor += inn.getRunsUntil(null)
                    TopOrBottom.BOTTOM -> runsOfHome += inn.getRunsUntil(null)
                }
            }

            if (inn.contains(period) || period == null && inn == innings.last()) {
                situation = Situation(
                    inn.seqNumber, runsOfHome, runsOfVisitor,
                    0, 0, 0,
                    inn.plateAppearances.first().battingOrder,
                    null, null, null
                )

                inn.plateAppearances.forEach { pa ->
                    pa.periods.forEach { p ->
                        if (p == period) return situation
                        if (p is Play) {
                            val newBattingOrder =
                                when {
                                    pa.periods.last() != p -> pa
                                    inn.plateAppearances.last() == pa -> pa
                                    else -> inn.plateAppearances.let { it[it.indexOf(pa) + 1] }
                                }.battingOrder
                            situation = situation.after(newBattingOrder, p)
                        }
                    }
                }

                return situation
            }
        }

        throw RuntimeException("Cannot find the specified period object in the history.")
    }

    fun getLineup(teamClass: TeamClass, period: Period?): Lineup {
        if (period != null) {
            return when (teamClass) {
                HOME -> period.homeLineup
                VISITOR -> period.visitorLineup
            }
        }

        var lastPeriod: Period? = null
        innings.forEach { inn ->
            inn.plateAppearances.forEach { pa ->
                pa.periods.forEach {
                    lastPeriod = it
                }
            }
        }

        if (lastPeriod == null) {
            return when (teamClass) {
                HOME -> Lineup(homeStartingLineup)
                VISITOR -> Lineup(visitorStartingLineup)
            }
        }

        if (lastPeriod is Substitution && (lastPeriod as Substitution).teamClass == teamClass) {
            val lastSubstitution = lastPeriod as Substitution
            if (lastSubstitution.teamClass == teamClass) {
                return when (teamClass) {
                    HOME -> lastSubstitution.homeLineup.after(lastSubstitution)
                    VISITOR -> lastSubstitution.visitorLineup.after(lastSubstitution)
                }
            }
        }

        return when (teamClass) {
            HOME -> lastPeriod!!.homeLineup
            VISITOR -> lastPeriod!!.visitorLineup
        }
    }

    fun getPitchCount(pitcher: Player): Int {
        var count = 0
        var homeLineup = Lineup(homeStartingLineup)
        var visitorLineup = Lineup(visitorStartingLineup)

        innings.forEach { inn ->
            inn.plateAppearances.forEach { pa ->
                pa.periods.forEach { p ->
                    if (p is Substitution) {
                        when (p.teamClass) {
                            HOME -> homeLineup = homeLineup.after(p)
                            VISITOR -> visitorLineup = visitorLineup.after(p)
                        }
                    } else if (p is Pitch) {
                        when (inn.topOrBottom) {
                            TopOrBottom.TOP -> if (homeLineup.getPlayer(Position.PITCHER) == pitcher) count++
                            TopOrBottom.BOTTOM -> if (visitorLineup.getPlayer(Position.PITCHER) == pitcher) count++
                        }
                    }
                }
            }
        }

        return count
    }

    fun addNewPeriod(period: Period) {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        innings.last().plateAppearances.last().addPeriod(period)
    }

    fun insertSubstitution(substitution: Substitution, anchorPeriod: Period) {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        var targetPlateAppearance: PlateAppearance? = null
        innings.forEach findTargetPlateAppearance@{ inn ->
            inn.plateAppearances.forEach { pa ->
                if (pa.periods.contains(anchorPeriod)) {
                    targetPlateAppearance = pa
                    return@findTargetPlateAppearance
                }
            }
        }

        targetPlateAppearance?.insertSubstitution(substitution, anchorPeriod)
    }

    fun replaceSubstitution(newSubstitution: Substitution, oldSubstitution: Substitution) {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        var targetPlateAppearance: PlateAppearance? = null
        innings.forEach findTargetPlateAppearance@{ inn ->
            inn.plateAppearances.forEach { pa ->
                if (pa.periods.contains(oldSubstitution)) {
                    targetPlateAppearance = pa
                    return@findTargetPlateAppearance
                }
            }
        }

        targetPlateAppearance?.replaceSubstitution(newSubstitution, oldSubstitution)
    }

    fun deleteSubstitution(substitution: Substitution) {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        var targetPlateAppearance: PlateAppearance? = null
        innings.forEach findTargetPlateAppearance@{ inn ->
            inn.plateAppearances.forEach { pa ->
                if (pa.periods.contains(substitution)) {
                    targetPlateAppearance = pa
                    return@findTargetPlateAppearance
                }
            }
        }

        targetPlateAppearance?.deleteSubstitution(substitution)
    }

    fun canReplacePlay(source: Play, replacing: Play): Boolean {
        val sourcePlayIsSettled =
            source is Pitch && source.settled || source is NoPitchIntentionalWalk
        val replacingPlayIsSettled =
            replacing is Pitch && replacing.settled || replacing is NoPitchIntentionalWalk

        if (sourcePlayIsSettled != replacingPlayIsSettled) return false

        val situationAfterSource = source.previousSituation.let {
            it.after((it.orderOfBatter + if (sourcePlayIsSettled) 1 else 0) % 9, source)
        }
        val situationAfterReplacing = replacing.previousSituation.let {
            it.after((it.orderOfBatter + if (replacingPlayIsSettled) 1 else 0) % 9, replacing)
        }

        if (situationAfterSource.outs != situationAfterReplacing.outs) return false
        if ((situationAfterSource.orderOf1R == null) != (situationAfterReplacing.orderOf1R == null)) return false
        if ((situationAfterSource.orderOf2R == null) != (situationAfterReplacing.orderOf2R == null)) return false
        if ((situationAfterSource.orderOf3R == null) != (situationAfterReplacing.orderOf3R == null)) return false

        return true
    }

    fun replacePlay(newPlay: Play, oldPlay: Play) {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished.")

        if (!canReplacePlay(oldPlay, newPlay))
            throw RuntimeException("This replacing is not allowed. Check it with 'canReplacePlay' method before.")

        var targetPlateAppearance: PlateAppearance? = null
        innings.forEach findTargetPlateAppearance@{ inn ->
            inn.plateAppearances.forEach { pa ->
                if (pa.periods.contains(oldPlay)) {
                    targetPlateAppearance = pa
                    return@findTargetPlateAppearance
                }
            }
        }

        targetPlateAppearance?.replacePlay(newPlay, oldPlay)
    }

    fun finishGameAsCompleted() {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished already.")

        gameStatus = GameStatus.GAME_SET
    }

    fun finishGameAsCalled() {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished already.")

        gameStatus = GameStatus.CALLED
    }

    fun suspendGame() {
        if (gameStatus != GameStatus.PLAYING)
            throw IllegalStateException("This game has finished already.")

        gameStatus = GameStatus.SUSPENDED
    }

    fun cancelGameFinishing() {
        if (gameStatus == GameStatus.PLAYING)
            throw IllegalStateException("This game has not finished.")

        gameStatus = GameStatus.PLAYING
    }
}