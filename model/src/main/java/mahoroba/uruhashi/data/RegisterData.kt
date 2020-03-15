package mahoroba.uruhashi.data

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.Register

@Entity(tableName = "register", primaryKeys = ["teamId", "playerId"])
class RegisterData() {
    var teamId: String = ""
    var playerId: String = ""
    var uniformNumber: String? = null

    constructor(register: Register) : this() {
        teamId = register.teamId.value
        playerId = register.playerId.value
        uniformNumber = register.uniformNumber
    }

    fun toRegister() : Register = Register(ID(teamId), ID(playerId), uniformNumber)
}