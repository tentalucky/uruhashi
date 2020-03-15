package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.PitchType

class PitchTypeConverter {
    @TypeConverter
    fun fromInt(value: Int?): PitchType? {
        return when (value) {
            0 -> PitchType.NO_ENTRY
            1 -> PitchType.FOUR_SEAM_FASTBALL
            2 -> PitchType.TWO_SEAM_FASTBALL
            3 -> PitchType.CURVE_BALL
            4 -> PitchType.SLIDER
            5 -> PitchType.FORK_BALL
            6 -> PitchType.SHOOT_BALL
            7 -> PitchType.SINKER
            8 -> PitchType.CHANGE_UP
            9 -> PitchType.KNUCKLE_BALL
            10 -> PitchType.SPLITTER
            11 -> PitchType.CUTTER
            12 -> PitchType.PALM_BALL
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to PitchType.")
        }
    }

    @TypeConverter
    fun toInt(pitchType: PitchType?): Int? {
        return when (pitchType) {
            PitchType.NO_ENTRY -> 0
            PitchType.FOUR_SEAM_FASTBALL -> 1
            PitchType.TWO_SEAM_FASTBALL -> 2
            PitchType.CURVE_BALL -> 3
            PitchType.SLIDER -> 4
            PitchType.FORK_BALL -> 5
            PitchType.SHOOT_BALL -> 6
            PitchType.SINKER -> 7
            PitchType.CHANGE_UP -> 8
            PitchType.KNUCKLE_BALL -> 9
            PitchType.SPLITTER -> 10
            PitchType.CUTTER -> 11
            PitchType.PALM_BALL -> 12
            null -> null
        }
    }
}