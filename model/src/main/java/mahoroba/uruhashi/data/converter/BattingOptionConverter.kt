package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.BattingOption

class BattingOptionConverter {
    @TypeConverter
    fun fromInt(value: Int?): BattingOption? {
        return when (value) {
            0 -> BattingOption.NONE
            1 -> BattingOption.BUNT
            2 -> BattingOption.SLASH_BUNT
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to HandType.")
        }
    }

    @TypeConverter
    fun toInt(battingOption: BattingOption?): Int? {
        return when (battingOption) {
            BattingOption.NONE -> 0
            BattingOption.BUNT -> 1
            BattingOption.SLASH_BUNT -> 2
            null -> null
        }
    }
}