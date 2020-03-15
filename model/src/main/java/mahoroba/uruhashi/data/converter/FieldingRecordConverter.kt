package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.FieldingRecord

class FieldingRecordConverter {
    @TypeConverter
    fun fromInt(value: Int?): FieldingRecord? {
        return when (value) {
            1 -> FieldingRecord.ERROR_CATCHING
            2 -> FieldingRecord.ERROR_THROWING
            3 -> FieldingRecord.FIELDERS_CHOICE
            4 -> FieldingRecord.TAGGED_OUT
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to FieldingRecord.")
        }
    }

    @TypeConverter
    fun toInt(record: FieldingRecord?): Int? {
        return when (record) {
            FieldingRecord.ERROR_CATCHING -> 1
            FieldingRecord.ERROR_THROWING -> 2
            FieldingRecord.FIELDERS_CHOICE -> 3
            FieldingRecord.TAGGED_OUT -> 4
            null -> null
        }
    }
}