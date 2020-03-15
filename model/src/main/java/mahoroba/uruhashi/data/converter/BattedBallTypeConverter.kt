package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.BattedBallType

class BattedBallTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): BattedBallType? {
        return when (value) {
            0 -> BattedBallType.NO_ENTRY
            1 -> BattedBallType.GROUND_HIGH_BOUND
            2 -> BattedBallType.GROUND
            3 -> BattedBallType.LINE_DRIVE
            4 -> BattedBallType.FLY_BALL
            5 -> BattedBallType.HIGH_FLY_BALL
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to BattedBallType.")
        }
    }

    @TypeConverter
    fun toInt(battedBallType: BattedBallType?): Int? {
        return when (battedBallType) {
            BattedBallType.NO_ENTRY -> 0
            BattedBallType.GROUND_HIGH_BOUND -> 1
            BattedBallType.GROUND -> 2
            BattedBallType.LINE_DRIVE -> 3
            BattedBallType.FLY_BALL -> 4
            BattedBallType.HIGH_FLY_BALL -> 5
            null -> null
        }
    }
}