package mahoroba.uruhashi.presentation.utility

import mahoroba.uruhashi.domain.game.AngularCoordinate
import kotlin.math.cos
import kotlin.math.sin

fun AngularCoordinate.toPixel(ox: Float, oy: Float, scale: Float): Pair<Float, Float> {
    val x = cos(this.angle) * this.distance * scale + ox
    val y = -sin(this.angle) * this.distance * scale + oy
    return Pair(x, y)
}