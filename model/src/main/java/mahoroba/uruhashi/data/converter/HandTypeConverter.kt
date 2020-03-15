package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.HandType

class HandTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): HandType? {
        return when (value) {
            1 -> HandType.RIGHT
            2 -> HandType.LEFT
            3 -> HandType.BOTH
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to HandType.")
        }
    }

    @TypeConverter
    fun toInt(handType: HandType?): Int? {
        return when (handType) {
            HandType.RIGHT -> 1
            HandType.LEFT -> 2
            HandType.BOTH -> 3
            null -> null
        }
    }
}