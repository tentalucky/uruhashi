package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.FieldPosition

class FieldPositionConverter {
    @TypeConverter
    fun fromInt(value: Int?): FieldPosition? {
        return when (value) {
            1 -> FieldPosition.PITCHER
            2 -> FieldPosition.CATCHER
            3 -> FieldPosition.FIRST_BASEMAN
            4 -> FieldPosition.SECOND_BASEMAN
            5 -> FieldPosition.THIRD_BASEMAN
            6 -> FieldPosition.SHORT_STOP
            7 -> FieldPosition.LEFT_FIELDER
            8 -> FieldPosition.CENTER_FIELDER
            9 -> FieldPosition.RIGHT_FIELDER
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to FieldPosition.")
        }
    }

    @TypeConverter
    fun toInt(value: FieldPosition?): Int? {
        return when (value) {
            FieldPosition.PITCHER -> 1
            FieldPosition.CATCHER -> 2
            FieldPosition.FIRST_BASEMAN -> 3
            FieldPosition.SECOND_BASEMAN -> 4
            FieldPosition.THIRD_BASEMAN -> 5
            FieldPosition.SHORT_STOP -> 6
            FieldPosition.LEFT_FIELDER -> 7
            FieldPosition.CENTER_FIELDER -> 8
            FieldPosition.RIGHT_FIELDER -> 9
            null -> null
        }
    }
}