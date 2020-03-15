package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Ball
import java.io.Writer

class BallWriter(writer: Writer, private val ball: Ball): PitchWriter(writer, ball) {
    override fun getPeriodTypeName(): String = "Ball"
}