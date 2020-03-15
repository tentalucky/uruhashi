package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.FieldPlayFactor

class FieldPlayFactorConverter {
    @TypeConverter
    fun fromInt(value: Int?): FieldPlayFactor? {
        return when (value) {
            0 -> FieldPlayFactor.OTHER
            1 -> FieldPlayFactor.STEALING
            2 -> FieldPlayFactor.PICK_OFF_PLAY
            3 -> FieldPlayFactor.WILD_PITCH
            4 -> FieldPlayFactor.PASSED_BALL
            5 -> FieldPlayFactor.INTERFERENCE_BATTING
            6 -> FieldPlayFactor.INTERFERENCE_FIELDING
            7 -> FieldPlayFactor.OBSTRUCTION
            8 -> FieldPlayFactor.BATTING
            9 -> FieldPlayFactor.WALK
            10 -> FieldPlayFactor.HIT_BY_PITCH
            11 -> FieldPlayFactor.BALK
            12 -> FieldPlayFactor.FOUL
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to FieldPlayFactor.")
        }
    }

    @TypeConverter
    fun toInt(factor: FieldPlayFactor?): Int? {
        return when (factor) {
            FieldPlayFactor.OTHER -> 0
            FieldPlayFactor.STEALING -> 1
            FieldPlayFactor.PICK_OFF_PLAY -> 2
            FieldPlayFactor.WILD_PITCH -> 3
            FieldPlayFactor.PASSED_BALL -> 4
            FieldPlayFactor.INTERFERENCE_BATTING -> 5
            FieldPlayFactor.INTERFERENCE_FIELDING -> 6
            FieldPlayFactor.OBSTRUCTION -> 7
            FieldPlayFactor.BATTING -> 8
            FieldPlayFactor.WALK -> 9
            FieldPlayFactor.HIT_BY_PITCH -> 10
            FieldPlayFactor.BALK -> 11
            FieldPlayFactor.FOUL -> 12
            null -> null
        }
    }
}