package mahoroba.uruhashi.domain.game

class BattedBallDirection(tracePoints : List<AngularCoordinate>) {
    private val mTracePoints = ArrayList<AngularCoordinate>()
    val tracePoints : List<AngularCoordinate>
        get() = mTracePoints

    init {
        mTracePoints.addAll(tracePoints)
    }
}