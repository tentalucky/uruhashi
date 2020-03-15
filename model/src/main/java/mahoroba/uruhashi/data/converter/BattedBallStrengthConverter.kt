package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.BattedBallStrength

class BattedBallStrengthConverter {
    @TypeConverter
    fun fromInt(value: Int?): BattedBallStrength? {
        return when (value) {
            0 -> BattedBallStrength.NO_ENTRY
            1 -> BattedBallStrength.VERY_HARD
            2 -> BattedBallStrength.HARD
            3 -> BattedBallStrength.MEDIUM
            4 -> BattedBallStrength.WEAK
            5 -> BattedBallStrength.VERY_WEAK
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to BattedBallStrength.")
        }
    }

    @TypeConverter
    fun toInt(battedBallStrength: BattedBallStrength?): Int? {
        return when (battedBallStrength) {
            BattedBallStrength.NO_ENTRY -> 0
            BattedBallStrength.VERY_HARD -> 1
            BattedBallStrength.HARD -> 2
            BattedBallStrength.MEDIUM -> 3
            BattedBallStrength.WEAK -> 4
            BattedBallStrength.VERY_WEAK -> 5
            null -> null
        }
    }
}