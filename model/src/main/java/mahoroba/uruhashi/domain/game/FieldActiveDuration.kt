package mahoroba.uruhashi.domain.game

class FieldActiveDuration(playList: List<FieldPlay>) {
    private val mFieldPlayList = ArrayList<FieldPlay>()
    val fieldPlayList: List<FieldPlay>
        get() = mFieldPlayList

    val outs: Int
        get() {
            var o = 0
            mFieldPlayList.forEach { o += if (it.madeOut) 1 else 0 }
            return o
        }

    val runs: Int
        get() {
            var r = 0
            mFieldPlayList.forEach { fp ->
                fp.batterRunnersAction?.let { if (it.state == RunnerState.HOME_IN) r++ }
                fp.firstRunnersAction?.let { if (it.state == RunnerState.HOME_IN) r++ }
                fp.secondRunnersAction?.let { if (it.state == RunnerState.HOME_IN) r++ }
                fp.thirdRunnersAction?.let { if (it.state == RunnerState.HOME_IN) r++ }
            }
            return r
        }

    init {
        val lastStates = Array<RunnerState?>(4) { null }

        playList.forEach { pl ->
            if (lastStates[0]?.hasCompleted == true && pl.batterRunnersAction != null ||
                lastStates[1]?.hasCompleted == true && pl.firstRunnersAction != null ||
                lastStates[2]?.hasCompleted == true && pl.secondRunnersAction != null ||
                lastStates[3]?.hasCompleted == true && pl.thirdRunnersAction != null
            ) throw RuntimeException("Runners cannot make action after put out or home-in.")

            pl.batterRunnersAction?.let { lastStates[0] = it.state }
            pl.firstRunnersAction?.let { lastStates[1] = it.state }
            pl.secondRunnersAction?.let { lastStates[2] = it.state }
            pl.thirdRunnersAction?.let { lastStates[3] = it.state }
        }

        if (lastStates.count { it == RunnerState.FLOATING } > 0)
            throw RuntimeException("Runner's state are not determined.")
        if (lastStates.filter { it == RunnerState.ON_FIRST_BASE }.count() > 1)
            throw RuntimeException("Two or more runners on the first base.")
        if (lastStates.filter { it == RunnerState.ON_SECOND_BASE }.count() > 1)
            throw RuntimeException("Two or more runners on the second base.")
        if (lastStates.filter { it == RunnerState.ON_THIRD_BASE }.count() > 1)
            throw RuntimeException("Two or more runners on the third base.")

        mFieldPlayList.addAll(playList)
    }
}