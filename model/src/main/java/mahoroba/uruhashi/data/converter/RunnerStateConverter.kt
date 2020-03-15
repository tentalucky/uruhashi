package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.RunnerState

class RunnerStateConverter {
    @TypeConverter
    fun fromInt(value: Int?): RunnerState? {
        return when (value) {
            1 -> RunnerState.ON_FIRST_BASE
            2 -> RunnerState.ON_SECOND_BASE
            3 -> RunnerState.ON_THIRD_BASE
            4 -> RunnerState.HOME_IN
            5 -> RunnerState.OUT_IN_PROGRESSING
            6 -> RunnerState.OUT_IN_RETURNING
            7 -> RunnerState.FLOATING
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to RunnerState.")
        }
    }

    @TypeConverter
    fun toInt(state: RunnerState?): Int? {
        return when (state) {
            RunnerState.ON_FIRST_BASE -> 1
            RunnerState.ON_SECOND_BASE -> 2
            RunnerState.ON_THIRD_BASE -> 3
            RunnerState.HOME_IN -> 4
            RunnerState.OUT_IN_PROGRESSING -> 5
            RunnerState.OUT_IN_RETURNING -> 6
            RunnerState.FLOATING -> 7
            null -> null
        }
    }
}