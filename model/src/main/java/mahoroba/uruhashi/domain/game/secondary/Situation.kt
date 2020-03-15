package mahoroba.uruhashi.domain.game.secondary

import mahoroba.uruhashi.domain.game.*

class Situation(
    val inningNumber: Int,
    val runsOfHome: Int,
    val runsOfVisitor: Int,
    balls: Int,
    strikes: Int,
    val outs: Int,
    val orderOfBatter: Int,
    orderOf1R: Int?,
    orderOf2R: Int?,
    orderOf3R: Int?
) {
    val topOrBottom: TopOrBottom
        get() = if (inningNumber % 2 == 1) TopOrBottom.TOP else TopOrBottom.BOTTOM

    var balls = balls
        private set
    var strikes = strikes
        private set
    var orderOf1R = orderOf1R
        private set
    var orderOf2R = orderOf2R
        private set
    var orderOf3R = orderOf3R
        private set

    val is1RForcedToProgress: Boolean
        get() = orderOf1R != null
    val is2RForcedToProgress: Boolean
        get() = orderOf1R != null && orderOf2R != null
    val is3RForcedToProgress: Boolean
        get() = orderOf1R != null && orderOf2R != null && orderOf3R != null

    fun modify(balls: Int, strikes: Int, orderOf1R: Int?, orderOf2R: Int?, orderOf3R: Int?) {
        if ((this.orderOf1R == null) != (orderOf1R == null) ||
            (this.orderOf2R == null) != (orderOf2R == null) ||
            (this.orderOf3R == null) != (orderOf3R == null)
        ) throw RuntimeException("Changing existing of runner is not allowed.")

        this.balls = balls
        this.strikes = strikes
        this.orderOf1R = orderOf1R
        this.orderOf2R = orderOf2R
        this.orderOf3R = orderOf3R
    }

    fun after(newOrderOfBatter: Int, play: Play): Situation {
        var outInPlay = 0
        var runsInPlay = 0
        var lastStateOfBR = RunnerState.FLOATING
        var lastStateOf1R = if (orderOf1R == null) null else RunnerState.ON_FIRST_BASE
        var lastStateOf2R = if (orderOf2R == null) null else RunnerState.ON_SECOND_BASE
        var lastStateOf3R = if (orderOf3R == null) null else RunnerState.ON_THIRD_BASE

        play.fieldActiveDuration.fieldPlayList.forEach { p ->
            if (p.madeOut) outInPlay++
            runsInPlay += p.runs

            p.batterRunnersAction?.let { lastStateOfBR = it.state }
            p.firstRunnersAction?.let { lastStateOf1R = it.state }
            p.secondRunnersAction?.let { lastStateOf2R = it.state }
            p.thirdRunnersAction?.let { lastStateOf3R = it.state }
        }

        var newOrderOf1R: Int? = null
        var newOrderOf2R: Int? = null
        var newOrderOf3R: Int? = null
        if (lastStateOfBR == RunnerState.ON_FIRST_BASE) newOrderOf1R = orderOfBatter
        if (lastStateOfBR == RunnerState.ON_SECOND_BASE) newOrderOf2R = orderOfBatter
        if (lastStateOfBR == RunnerState.ON_THIRD_BASE) newOrderOf3R = orderOfBatter
        if (lastStateOf1R == RunnerState.ON_FIRST_BASE) newOrderOf1R = orderOf1R
        if (lastStateOf1R == RunnerState.ON_SECOND_BASE) newOrderOf2R = orderOf1R
        if (lastStateOf1R == RunnerState.ON_THIRD_BASE) newOrderOf3R = orderOf1R
        if (lastStateOf2R == RunnerState.ON_SECOND_BASE) newOrderOf2R = orderOf2R
        if (lastStateOf2R == RunnerState.ON_THIRD_BASE) newOrderOf3R = orderOf2R
        if (lastStateOf3R == RunnerState.ON_THIRD_BASE) newOrderOf3R = orderOf3R

        val newBalls = when {
            play is Pitch && play.settled -> 0
            play is Ball -> balls + 1
            else -> balls
        }
        val newStrikes = when {
            play is Pitch && play.settled -> 0
            play is Strike || play is Foul && strikes < 2 -> strikes + 1
            else -> strikes
        }

        return Situation(
            inningNumber,
            if (inningNumber % 2 == 1) runsOfHome else runsOfHome + runsInPlay,
            if (inningNumber % 2 == 1) runsOfVisitor + runsInPlay else runsOfVisitor,
            newBalls,
            newStrikes,
            outs + outInPlay,
            newOrderOfBatter,
            newOrderOf1R,
            newOrderOf2R,
            newOrderOf3R
        )
    }

}