package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.Position

class PositionConverter {
    @TypeConverter
    fun fromInt(value: Int?): Position? {
        return when (value) {
            0 -> Position.NO_ENTRY
            1 -> Position.PITCHER
            2 -> Position.CATCHER
            3 -> Position.FIRST_BASEMAN
            4 -> Position.SECOND_BASEMAN
            5 -> Position.THIRD_BASEMAN
            6 -> Position.SHORT_STOP
            7 -> Position.LEFT_FIELDER
            8 -> Position.CENTER_FIELDER
            9 -> Position.RIGHT_FIELDER
            10 -> Position.DESIGNATED_HITTER
            11 -> Position.PINCH_HITTER
            12 -> Position.PINCH_RUNNER
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to Position.")
        }
    }

    @TypeConverter
    fun toInt(value: Position?): Int? {
        return when (value) {
            Position.NO_ENTRY -> 0
            Position.PITCHER -> 1
            Position.CATCHER -> 2
            Position.FIRST_BASEMAN -> 3
            Position.SECOND_BASEMAN -> 4
            Position.THIRD_BASEMAN -> 5
            Position.SHORT_STOP -> 6
            Position.LEFT_FIELDER -> 7
            Position.CENTER_FIELDER -> 8
            Position.RIGHT_FIELDER -> 9
            Position.DESIGNATED_HITTER -> 10
            Position.PINCH_HITTER -> 11
            Position.PINCH_RUNNER -> 12
            null -> null
        }
    }
}