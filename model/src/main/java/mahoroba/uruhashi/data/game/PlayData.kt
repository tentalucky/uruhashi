package mahoroba.uruhashi.data.game

import android.arch.persistence.room.Entity
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.game.*

@Entity(
    tableName = "play",
    primaryKeys = ["gameId", "inningSeqNumber", "plateAppearanceSeqNumber", "periodSeqNumber"]
)
class PlayData {
    lateinit var gameId: ID
    var inningSeqNumber: Int = -1
    var plateAppearanceSeqNumber: Int = -1
    var periodSeqNumber: Int = -1
    var playType: Int = -1

    var battingResult: BattingResult? = null
    var pitchType: PitchType? = null
    var pitchSpeed: Int? = null
    var pitchLocationX: Float? = null
    var pitchLocationY: Float? = null
    var battingOption: BattingOption? = null
    var settled: Boolean? = null
    var strikeType: Strike.StrikeType? = null
    var foulBallDirection: FoulBallDirection? = null
    var foulBallIsAtLine: Boolean? = null
    var battedBallType: BattedBallType? = null
    var battedBallStrength: BattedBallStrength? = null
    var battedBallDistance: Int? = null
    var withRunnerStarting: Boolean? = null

    fun isDescriptionOf(periodData: PeriodData) =
        this.gameId == periodData.gameId
                && this.inningSeqNumber == periodData.inningSeqNumber
                && this.plateAppearanceSeqNumber == periodData.plateAppearanceSeqNumber
                && this.periodSeqNumber == periodData.seqNumber

    companion object {
        enum class PlayDataType(val value: Int) {
            BALL(1),
            STRIKE(2),
            FOUL(3),
            BATTING(4),
            HIT_BY_PITCH(5),
            PLAY_IN_INTERVAL(6),
            NO_PITCH_INTENTIONAL_WALK(7),
            BALK(8)
        }

        fun getPlayTypeValueOf(play: Play) = when (play) {
            is Ball -> PlayDataType.BALL
            is Strike -> PlayDataType.STRIKE
            is Foul -> PlayDataType.FOUL
            is Batting -> PlayDataType.BATTING
            is HitByPitch -> PlayDataType.HIT_BY_PITCH
            is PlayInInterval -> PlayDataType.PLAY_IN_INTERVAL
            is NoPitchIntentionalWalk -> PlayDataType.NO_PITCH_INTENTIONAL_WALK
            is Balk -> PlayDataType.BALK
            else -> throw TypeCastException("${play::class} is not acceptable.")
        }.value

        fun getPlayType(data: PlayData) =
            PlayDataType.values().find { n -> n.value == data.playType }
                ?: throw RuntimeException("Undefined play type value.")
    }
}