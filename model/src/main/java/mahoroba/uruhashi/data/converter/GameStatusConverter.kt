package mahoroba.uruhashi.data.converter

import android.arch.persistence.room.TypeConverter
import mahoroba.uruhashi.domain.game.GameStatus

class GameStatusConverter {
    @TypeConverter
    fun fromInt(value: Int?): GameStatus? {
        return when (value) {
            1 -> GameStatus.PLAYING
            2 -> GameStatus.GAME_SET
            3 -> GameStatus.CALLED
            4 -> GameStatus.SUSPENDED
            null -> null
            else -> throw TypeCastException("[$value] cannot convert to GameStatus.")
        }
    }

    @TypeConverter
    fun toInt(gameStatus: GameStatus?): Int? {
        return when (gameStatus) {
            GameStatus.PLAYING -> 1
            GameStatus.GAME_SET -> 2
            GameStatus.SUSPENDED -> 3
            GameStatus.CALLED -> 4
            null -> null
        }
    }
}