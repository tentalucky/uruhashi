package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.RunnerType

class RunnerTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): RunnerType? {
        return when (value) {
            0 -> RunnerType.BATTER_RUNNER
            1 -> RunnerType.FIRST_RUNNER
            2 -> RunnerType.SECOND_RUNNER
            3 -> RunnerType.THIRD_RUNNER
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to RunnerType.")
        }
    }

    @TypeConverter
    fun toInt(runnerType: RunnerType?): Int? {
        return when (runnerType) {
            RunnerType.BATTER_RUNNER -> 0
            RunnerType.FIRST_RUNNER -> 1
            RunnerType.SECOND_RUNNER -> 2
            RunnerType.THIRD_RUNNER -> 3
            null -> null
        }
    }
}