package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.TeamClass

class TeamClassConverter {
    @TypeConverter
    fun fromInt(value: Int?): TeamClass? {
        return when (value) {
            1 -> TeamClass.HOME
            2 -> TeamClass.VISITOR
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to TeamClass.")
        }
    }

    @TypeConverter
    fun toInt(value: TeamClass?): Int? {
        return when (value) {
            TeamClass.HOME -> 1
            TeamClass.VISITOR -> 2
            null -> null
        }
    }
}