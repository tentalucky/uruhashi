package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.Balk
import java.io.Writer

class BalkWriter(writer: Writer, private val balk: Balk): PlayWriter(writer, balk) {
    override fun getPeriodTypeName() = "Balk"
}