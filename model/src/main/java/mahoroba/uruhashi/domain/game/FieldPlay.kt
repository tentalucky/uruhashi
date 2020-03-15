package mahoroba.uruhashi.domain.game

class FieldPlay(
    val factor: FieldPlayFactor,
    fieldersActionList: List<FieldersAction>,
    runnersActionList: List<RunnersAction>
) {
    private val mFieldersActionList = ArrayList<FieldersAction>()
    val fieldersActionList: List<FieldersAction>
        get() = mFieldersActionList

    private val mRunnersActionList = ArrayList<RunnersAction>()
    val batterRunnersAction
        get() = mRunnersActionList.find { n -> n.type == RunnerType.BATTER_RUNNER }
    val firstRunnersAction
        get() = mRunnersActionList.find { n -> n.type == RunnerType.FIRST_RUNNER }
    val secondRunnersAction
        get() = mRunnersActionList.find { n -> n.type == RunnerType.SECOND_RUNNER }
    val thirdRunnersAction
        get() = mRunnersActionList.find { n -> n.type == RunnerType.THIRD_RUNNER }

    val madeOut get() = mRunnersActionList.any { it.isOut }
    val runs get() = mRunnersActionList.count { it.state == RunnerState.HOME_IN }

    init {
        if (runnersActionList.count { it.isOut } > 1)
            throw RuntimeException("Only one runner can be put out in a play.")
        if (fieldersActionList.count { it.record != null } > 1)
            throw RuntimeException("Only one player can make record in a play.")
        if (runnersActionList.count { it.isOut } > 0 && fieldersActionList.count { it.record?.preventMakingOut == true } > 0)
            throw RuntimeException("When a player makes record, runners cannot got out.")
        if (runnersActionList.count { it.isOut } > 0 && fieldersActionList.count() == 0)
            throw RuntimeException("It needs one or more fielders play when a runner be putout.")

        mFieldersActionList.addAll(fieldersActionList)
        mRunnersActionList.addAll(runnersActionList)
    }
}