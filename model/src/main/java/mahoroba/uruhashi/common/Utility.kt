package mahoroba.uruhashi.common

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

fun sin_deg(degrees: Float): Float = sin(degrees * PI / 180.0).toFloat()
fun cos_deg(degrees: Float): Float = cos(degrees * PI / 180.0).toFloat()