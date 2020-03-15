package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.Strike

class StrikeTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): Strike.StrikeType? {
        return when (value) {
            1 -> Strike.StrikeType.LOOKING
            2 -> Strike.StrikeType.SWINGING
            3 -> Strike.StrikeType.THIRD_BUNT_MISS
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to StrikeType.")
        }
    }

    @TypeConverter
    fun toInt(strikeType: Strike.StrikeType?): Int? {
        return when (strikeType) {
            Strike.StrikeType.LOOKING -> 1
            Strike.StrikeType.SWINGING -> 2
            Strike.StrikeType.THIRD_BUNT_MISS -> 3
            null -> null
        }
    }
}