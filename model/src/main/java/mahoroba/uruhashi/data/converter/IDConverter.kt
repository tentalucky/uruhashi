package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.ID

class IDConverter {
    @TypeConverter
    fun fromString(value: String?) : ID? {
        return if (value != null) ID(value) else null
    }

    @TypeConverter
    fun toString(value: ID?) : String? {
        return value?.toString()
    }
}