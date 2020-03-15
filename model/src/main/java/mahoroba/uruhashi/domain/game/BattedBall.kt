package mahoroba.uruhashi.domain.game

data class BattedBall(
    val direction: BattedBallDirection?,
    val type: BattedBallType,
    val strength: BattedBallStrength,
    val distance: Int?,
    val bunt: Boolean
)