package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.NameType

class NameTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): NameType? {
        return when(value) {
            1 -> NameType.FAMILY_NAME_FIRST
            2 -> NameType.FIRST_NAME_FIRST
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to NameType.")
        }
    }

    @TypeConverter
    fun toInt(nameType: NameType?): Int? {
        return when (nameType) {
            NameType.FAMILY_NAME_FIRST -> 1
            NameType.FIRST_NAME_FIRST -> 2
            null -> null
        }
    }
}