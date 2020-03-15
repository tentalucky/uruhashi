package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import java.util.*

class DateConverter {
    @TypeConverter
    fun fromLong(value: Long?): Date? {
        return if (value == null) null else Date(value)
    }

    @TypeConverter
    fun toLong(value: Date?): Long? {
        return if (value == null) null else value.time
    }
}