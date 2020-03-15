package mahoroba.uruhashi.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.Stadium

@Entity(tableName = "stadium")
class StadiumData(
    @PrimaryKey val id: String,
    var name: String,
    var abbreviatedName: String,
    var priority: Int) {

    constructor(stadium: Stadium) : this(stadium.id.value, stadium.name, stadium.abbreviatedName, stadium.priority)

    fun toStadium() : Stadium {
        return Stadium(ID(id), name, abbreviatedName, priority)
    }
}
