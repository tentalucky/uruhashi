package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.Inning

@Entity(tableName = "inning", primaryKeys = ["gameId", "seqNumber"])
class InningData {
    lateinit var gameId: ID
    var seqNumber: Int = -1

    companion object {
        fun toInningDataList(gameId: ID, innings: List<Inning>): List<InningData> {
            return ArrayList<InningData>().apply {
                innings.forEach { inn ->
                    this.add(InningData().apply {
                        this.gameId = gameId
                        this.seqNumber = inn.seqNumber
                    })
                }
            }
        }
    }
}