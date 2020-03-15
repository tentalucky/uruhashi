package mahoroba.uruhashi.exStorage.writer.game

import mahoroba.uruhashi.domain.game.PlayInInterval
import java.io.Writer

class PlayInIntervalWriter(writer: Writer, private val playInInterval: PlayInInterval) :
    PlayWriter(writer, playInInterval) {
    override fun getPeriodTypeName() = "PlayInInterval"
}