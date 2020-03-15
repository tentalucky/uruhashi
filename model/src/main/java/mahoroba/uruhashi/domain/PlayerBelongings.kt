package mahoroba.uruhashi.domain

class PlayerBelongings(val playerProfile: PlayerProfile) {

    private val assignedTeamIds = mutableSetOf<String>()
    private val registerList = LinkedHashMap<String, Register>()

    val size: Int
        get() = registerList.size

    fun contains(element: Register): Boolean {
        return registerList.containsValue(element)
    }

    fun addRegister(register: Register) {
        if (register.playerId != playerProfile.id)
                throw Exception("""PlayerProfile id unmatched.
                    | This playerProfile [${playerProfile.id.value}], adding playerProfile [${register.playerId}]""".trimMargin())

        if (assignedTeamIds.contains(register.teamId.value))
            throw Exception("This teamProfile (id:${register.teamId.value}) has been assigned already.")

        assignedTeamIds.add(register.teamId.value)
        registerList[register.teamId.value] = register
    }

    fun getRegister(teamId: ID) : Register {
        registerList[teamId.value]?.let { return it }

        throw Exception("The teamProfile id [${teamId.value}] is not assigned.")
    }

    fun removeRegister(register: Register) {
        registerList.remove(register.teamId.value)
        assignedTeamIds.remove(register.teamId.value)
    }

    fun forEach(action: (Register) -> Unit) {
        registerList.forEach{ action(it.value) }
    }

    inline fun <T> Iterable<T>.forEach(action: (T) -> Unit) {
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