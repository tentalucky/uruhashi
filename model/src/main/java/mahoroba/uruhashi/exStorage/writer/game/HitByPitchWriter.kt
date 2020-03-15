package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.HitByPitch
import java.io.Writer

class HitByPitchWriter(writer: Writer, private val hitByPitch: HitByPitch) :
    PitchWriter(writer, hitByPitch) {
    override fun getPeriodTypeName() = "HitByPitch"
}