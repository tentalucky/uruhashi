package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.NoPitchIntentionalWalk
import java.io.Writer

class NoPitchIntentionalWalkWriter(
    writer: Writer,
    private val noPitchIntentionalWalk: NoPitchIntentionalWalk
) : PlayWriter(writer, noPitchIntentionalWalk) {
    override fun getPeriodTypeName() = "NoPitchIntentionalWalk"
}