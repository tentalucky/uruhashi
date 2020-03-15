package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.FoulBallDirection

class FoulBallDirectionConverter {
    @TypeConverter
    fun fromInt(value: Int?): FoulBallDirection? {
        return when (value) {
            0 -> FoulBallDirection.NO_ENTRY
            1 -> FoulBallDirection.TO_BACKSTOP
            2 -> FoulBallDirection.TO_FIRST_SIDE
            3 -> FoulBallDirection.TO_THIRD_SIDE
            4 -> FoulBallDirection.TO_RIGHT_FIELD
            5 -> FoulBallDirection.TO_LEFT_FIELD
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to FoulBallDirection.")
        }
    }

    @TypeConverter
    fun toInt(direction: FoulBallDirection?): Int? {
        return when (direction) {
            FoulBallDirection.NO_ENTRY -> 0
            FoulBallDirection.TO_BACKSTOP -> 1
            FoulBallDirection.TO_FIRST_SIDE -> 2
            FoulBallDirection.TO_THIRD_SIDE -> 3
            FoulBallDirection.TO_RIGHT_FIELD -> 4
            FoulBallDirection.TO_LEFT_FIELD -> 5
            null -> null
        }
    }
}