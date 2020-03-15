package mahoroba.uruhashi.domain

import java.io.Serializable
import java.util.*

class ID : Serializable {
    val value: String
    val isJustGenerated: Boolean

    constructor() {
        value = UUID.randomUUID().toString()
        isJustGenerated = true
    }

    constructor(value: String) {
        this.value = value
        isJustGenerated = false
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ID
        return value == other.value && isJustGenerated == other.isJustGenerated
    }

    override fun toString() = value
}