package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.BattingResult

class BattingResultConverter {
    @TypeConverter
    fun fromInt(value: Int?): BattingResult? {
        return when (value) {
            0 -> BattingResult.NO_ENTRY
            1 -> BattingResult.SINGLE
            2 -> BattingResult.DOUBLE
            3 -> BattingResult.TRIPLE
            4 -> BattingResult.HOME_RUN
            5 -> BattingResult.GROUND_OUT
            6 -> BattingResult.LINE_OUT
            7 -> BattingResult.FLY_OUT
            8 -> BattingResult.STRIKEOUT
            9 -> BattingResult.SACRIFICE_FLY
            10 -> BattingResult.SACRIFICE_HIT
            11 -> BattingResult.WALK
            12 -> BattingResult.INTENTIONAL_WALK
            13 -> BattingResult.HIT_BY_PITCH
            14 -> BattingResult.INTERFERE
            15 -> BattingResult.OBSTRUCTION
            16 -> BattingResult.INTERRUPT
            17 -> BattingResult.FOUL_LINE_OUT
            18 -> BattingResult.FOUL_FLY_OUT
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to BattingResult.")
        }
    }

    @TypeConverter
    fun toInt(battingResult: BattingResult?): Int? {
        return when(battingResult) {
            BattingResult.NO_ENTRY -> 0
            BattingResult.SINGLE -> 1
            BattingResult.DOUBLE -> 2
            BattingResult.TRIPLE -> 3
            BattingResult.HOME_RUN -> 4
            BattingResult.GROUND_OUT -> 5
            BattingResult.LINE_OUT -> 6
            BattingResult.FLY_OUT -> 7
            BattingResult.STRIKEOUT -> 8
            BattingResult.SACRIFICE_FLY -> 9
            BattingResult.SACRIFICE_HIT -> 10
            BattingResult.WALK -> 11
            BattingResult.INTENTIONAL_WALK -> 12
            BattingResult.HIT_BY_PITCH -> 13
            BattingResult.INTERFERE -> 14
            BattingResult.OBSTRUCTION -> 15
            BattingResult.INTERRUPT -> 16
            BattingResult.FOUL_LINE_OUT -> 17
            BattingResult.FOUL_FLY_OUT -> 18
            null -> null
        }
    }
}