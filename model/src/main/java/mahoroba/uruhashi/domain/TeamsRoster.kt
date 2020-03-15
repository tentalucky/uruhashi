package mahoroba.uruhashi.domain

import java.util.*

class TeamsRoster(val teamProfile: TeamProfile) {

    private val assignedPlayerIds = mutableSetOf<String>()
    private val registerList = LinkedHashMap<String, Register>()

    val size: Int
        get() = registerList.size

    fun contains(element: Register): Boolean {
        return registerList.containsValue(element)
    }

    fun addRegister(register: Register) {
        if (register.teamId != teamProfile.id)
            throw Exception("""TeamProfile id unmatched.
                | This teamProfile [${teamProfile.id.value}], adding teamProfile [${register.teamId.value}]""".trimMargin())

        if (assignedPlayerIds.contains(register.playerId.value))
            throw Exception("This playerProfile (id:${register.playerId.value}) has been assigned already.")

        assignedPlayerIds.add(register.playerId.value)
        registerList[register.playerId.value] = register
    }

    fun getRegister(playerId: ID) : Register {
        registerList[playerId.value]?.let { return it }

        throw Exception("The playerProfile id [${playerId.value}] is not assigned.")
    }

    fun removeRegister(register: Register) {
        registerList.remove(register.playerId.value)
        assignedPlayerIds.remove(register.playerId.value)
    }

    fun clearRegister() {
        registerList.clear()
        assignedPlayerIds.clear()
    }

    fun forEach(action: (Register) -> Unit) {
        registerList.forEach{ action(it.value) }
    }

    inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {
        for (element in this) action(element)
    }

    fun iterator() = Iterator()

    inner class Iterator {
        val keys = registerList.keys
        var position : Int = 0

        operator fun hasNext() : Boolean = position > registerList.size

        operator fun next() : Register = registerList[keys.elementAt(position++)]!!
    }
}
