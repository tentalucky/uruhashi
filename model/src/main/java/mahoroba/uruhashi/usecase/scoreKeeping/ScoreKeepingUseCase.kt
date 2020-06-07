package mahoroba.uruhashi.usecase.scoreKeeping

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import mahoroba.uruhashi.common.LiveEvent
import mahoroba.uruhashi.domain.*
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.RunnerState.*
import mahoroba.uruhashi.domain.game.RunnerType.*
import mahoroba.uruhashi.domain.game.Strike.StrikeType.LOOKING
import mahoroba.uruhashi.domain.game.Strike.StrikeType.SWINGING
import mahoroba.uruhashi.domain.game.TeamClass.HOME
import mahoroba.uruhashi.domain.game.TeamClass.VISITOR
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.domain.game.secondary.Lineup
import mahoroba.uruhashi.domain.game.secondary.Situation
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.PitchResult.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ScoreKeepingUseCase(
    private val gameRepository: IGameRepository,
    private val stadiumRepository: IStadiumRepository,
    private val teamRepository: ITeamRepository,
    private val gameId: ID?
) {
    private val game = MutableLiveData<Game>()

    // region * Game basic info *

    data class GameBaseInfo(
        val gameName: String?,
        val gameDate: Date?,
        val homeTeamId: ID?,
        val homeTeamName: String?,
        val homeTeamAbbreviatedName: String?,
        val visitorTeamId: ID?,
        val visitorTeamName: String?,
        val visitorTeamAbbreviatedName: String?,
        val stadiumId: ID?,
        val stadiumName: String?,
        val stadiumAbbreviatedName: String?
    )

    fun getGameBaseInfo(): GameBaseInfo? {
        return game.value?.let {
            GameBaseInfo(
                it.gameInfo.gameName,
                it.gameInfo.date,
                it.gameInfo.homeTeam.teamId,
                it.gameInfo.homeTeam.teamName,
                it.gameInfo.homeTeam.abbreviatedName,
                it.gameInfo.visitorTeam.teamId,
                it.gameInfo.visitorTeam.teamName,
                it.gameInfo.visitorTeam.abbreviatedName,
                it.gameInfo.stadiumInfo.stadiumId,
                it.gameInfo.stadiumInfo.stadiumName,
                it.gameInfo.stadiumInfo.abbreviatedName
            )
        }
    }

    val gameBaseInfo: LiveData<GameBaseInfo> =
        Transformations.map(game) {
            GameBaseInfo(
                it?.gameInfo?.gameName,
                it?.gameInfo?.date,
                it?.gameInfo?.homeTeam?.teamId,
                it?.gameInfo?.homeTeam?.teamName,
                it?.gameInfo?.homeTeam?.abbreviatedName,
                it?.gameInfo?.visitorTeam?.teamId,
                it?.gameInfo?.visitorTeam?.teamName,
                it?.gameInfo?.visitorTeam?.abbreviatedName,
                it?.gameInfo?.stadiumInfo?.stadiumId,
                it?.gameInfo?.stadiumInfo?.stadiumName,
                it?.gameInfo?.stadiumInfo?.abbreviatedName
            )
        }

    // endregion Game basic info

    // region * Properties *

    val gameLoadedEvent = LiveEvent<Unit>()
    val isLoaded: LiveData<Boolean> = Transformations.map(game) { it != null }

    val boxScore: LiveData<BoxScoreDto> =
        Transformations.map(game) {
            BoxScoreDto(
                it.boxScore
            )
        }

    val latestGameState: LiveData<GameStateDto> =
        Transformations.map(game) { createGameStateDto(it) }

    val playersBattingStats: LiveData<GamePlayerBattingStatsDto> =
        Transformations.map(game) {
            GamePlayerBattingStatsDto(it.boxScore)
        }

    val pitchListOfLastPlateAppearance: LiveData<List<PitchInfoDto>> =
        Transformations.map(game) {
            ArrayList<PitchInfoDto>().apply {
                it?.boxScore?.innings?.last()?.plateAppearances?.last()?.periods?.forEach {
                    if (it is Pitch) this.add(createPitchInfoDto(it))
                }
            }
        }

    // endregion

    init {
        if (gameId == null) {
            game.value = Game(ID())
            gameLoadedEvent.call(Unit)
        } else {
            thread {
                val g = gameRepository.get(gameId)
                game.postValue(g)
                gameLoadedEvent.postCall(Unit)
            }
        }
    }

    suspend fun persistGame() {
        withContext(Dispatchers.IO) {
            gameRepository.save(game.value!!)
        }
    }

    fun persistGameSubThread() {
        thread {
            gameRepository.save(game.value!!)
        }
    }

    data class GameInfoDto(
        val gameName: String?,
        val gameDate: Date?,
        val homeTeamId: ID?,
        val homeTeamName: String?,
        val homeAbbreviatedName: String?,
        val visitorTeamId: ID?,
        val visitorTeamName: String?,
        val visitorAbbreviatedName: String?,
        val stadiumId: ID?,
        val stadiumName: String?,
        val stadiumAbbreviatedName: String?
    )

    fun setGameInformation(dto: GameInfoDto) {
        game.value?.gameInfo?.let {
            it.gameName = dto.gameName ?: ""
            it.date = dto.gameDate
            it.homeTeam.teamId = dto.homeTeamId
            it.homeTeam.teamName = dto.homeTeamName
            it.homeTeam.abbreviatedName = dto.homeAbbreviatedName
            it.visitorTeam.teamId = dto.visitorTeamId
            it.visitorTeam.teamName = dto.visitorTeamName
            it.visitorTeam.abbreviatedName = dto.visitorAbbreviatedName
            it.stadiumInfo.stadiumId = dto.stadiumId
            it.stadiumInfo.stadiumName = dto.stadiumName
            it.stadiumInfo.abbreviatedName = dto.stadiumAbbreviatedName
        }
    }

    fun setStartingPosition(teamClass: TeamClass, positions: List<Position>, hasDH: Boolean) {
        when (teamClass) {
            HOME -> game.value?.boxScore?.homeStartingLineup
            VISITOR -> game.value?.boxScore?.visitorStartingLineup
        }?.setPositions(positions, hasDH)
    }

    fun getStartingPositions(teamClass: TeamClass): List<Position> {
        return when (teamClass) {
            HOME -> game.value?.boxScore?.homeStartingLineup
            VISITOR -> game.value?.boxScore?.visitorStartingLineup
        }?.positions!!
    }

    fun hasDHAtStarting(teamClass: TeamClass): Boolean {
        return when (teamClass) {
            HOME -> game.value?.boxScore?.homeStartingLineup
            VISITOR -> game.value?.boxScore?.visitorStartingLineup
        }?.hasDH ?: false
    }

    fun setStartingPlayersInformation(
        teamClass: TeamClass,
        order: Int,
        playerId: ID?,
        playerName: PersonName?,
        bats: HandType?,
        throws: HandType?,
        uniformNumber: String?
    ) {
        when (teamClass) {
            HOME -> game.value?.boxScore?.homeStartingLineup
            VISITOR -> game.value?.boxScore?.visitorStartingLineup
        }?.setPlayersProfile(order, playerId, playerName, bats, throws, uniformNumber)
    }

    fun getStartingPlayersInformation(teamClass: TeamClass): List<PlayerInfoDto> {
        val returnList = ArrayList<PlayerInfoDto>()

        when (teamClass) {
            HOME -> game.value?.boxScore?.homeStartingLineup!!
            VISITOR -> game.value?.boxScore?.visitorStartingLineup!!
        }.let {
            it.players.forEach {
                returnList.add(
                    PlayerInfoDto(
                        it.playerId,
                        it.playerName,
                        it.bats,
                        it.throws,
                        it.uniformNumber
                    )
                )
            }
            if (it.hasDH) {
                it.playerOutOfOrder.let { p ->
                    returnList.add(
                        PlayerInfoDto(
                            p.playerId,
                            p.playerName,
                            p.bats,
                            p.throws,
                            p.uniformNumber
                        )
                    )
                }
            }
        }

        return returnList
    }

    fun getStadiumInfo(stadiumId: ID): Stadium {
        return stadiumRepository.get(stadiumId)
    }

    fun getTeamInfo(teamId: ID): TeamProfile {
        return teamRepository.get(teamId)
    }

    fun addStrike(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        strikeType: Strike.StrikeType,
        fieldPlays: List<FieldPlayDto>
    ) {
        val strike = createStrike(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, settled, strikeType, fieldPlays, null
        )

        game.value!!.boxScore.addNewPeriod(strike)
        game.value = game.value
    }

    fun replacePlayWithStrike(
        target: PlayDto,
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        strikeType: Strike.StrikeType,
        fieldPlays: List<FieldPlayDto>
    ): Boolean {
        val strike = createStrike(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, settled, strikeType, fieldPlays, target
        )

        if (!game.value!!.boxScore.canReplacePlay(target.play, strike)) return false

        game.value!!.boxScore.replacePlay(strike, target.play)
        game.value = game.value

        return true
    }

    private fun createStrike(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        strikeType: Strike.StrikeType,
        fieldPlays: List<FieldPlayDto>,
        templatePlay: PlayDto?
    ): Strike {
        val p = fieldPlayDtoToFieldPlayList(fieldPlays)

        val pitchLocation =
            if (pitchLocationX == null || pitchLocationY == null) null
            else PitchLocation(pitchLocationX, pitchLocationY)

        return Strike(
            pitchType ?: PitchType.NO_ENTRY,
            pitchSpeed,
            pitchLocation,
            battingOption ?: BattingOption.NONE,
            withRunnerStarting,
            settled,
            strikeType,
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(p),
            if (settled) BattingResult.STRIKEOUT else null
        )
    }

    fun addBall(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        fieldPlays: List<FieldPlayDto>
    ) {
        val ball = createBall(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, settled, fieldPlays, null
        )

        game.value!!.boxScore.addNewPeriod(ball)
        game.value = game.value
    }

    fun replacePlayWithBall(
        target: PlayDto,
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        fieldPlays: List<FieldPlayDto>
    ): Boolean {
        val ball = createBall(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, settled, fieldPlays, target
        )

        if (!game.value!!.boxScore.canReplacePlay(target.play, ball)) return false

        game.value!!.boxScore.replacePlay(ball, target.play)
        game.value = game.value

        return true
    }

    private fun createBall(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        settled: Boolean,
        fieldPlays: List<FieldPlayDto>,
        templatePlay: PlayDto?
    ): Ball {
        val p = fieldPlayDtoToFieldPlayList(fieldPlays)

        val pitchLocation =
            if (pitchLocationX == null || pitchLocationY == null) null
            else PitchLocation(pitchLocationX, pitchLocationY)

        return Ball(
            pitchType ?: PitchType.NO_ENTRY,
            pitchSpeed,
            pitchLocation,
            battingOption ?: BattingOption.NONE,
            withRunnerStarting,
            settled,
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(p),
            if (settled) BattingResult.WALK else null
        )
    }

    fun addFoul(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        direction: FoulBallDirection?,
        isAtLine: Boolean?,
        battedBallType: BattedBallType?,
        battedBallStrength: BattedBallStrength?,
        positionMakesError: FieldPosition?
    ) {
        val foul = createFoul(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, direction, isAtLine, battedBallType, battedBallStrength,
            positionMakesError, null
        )

        game.value!!.boxScore.addNewPeriod(foul)
        game.value = game.value
    }

    fun replacePlayWithFoul(
        target: PlayDto,
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        direction: FoulBallDirection?,
        isAtLine: Boolean?,
        battedBallType: BattedBallType?,
        battedBallStrength: BattedBallStrength?,
        positionMakesError: FieldPosition?
    ): Boolean {
        val foul = createFoul(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, direction, isAtLine, battedBallType, battedBallStrength,
            positionMakesError, target
        )

        if (!game.value!!.boxScore.canReplacePlay(target.play, foul)) return false

        game.value!!.boxScore.replacePlay(foul, target.play)
        game.value = game.value

        return true
    }

    private fun createFoul(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        direction: FoulBallDirection?,
        isAtLine: Boolean?,
        battedBallType: BattedBallType?,
        battedBallStrength: BattedBallStrength?,
        positionMakesError: FieldPosition?,
        templatePlay: PlayDto?
    ): Foul {
        // TODO: Implement three bunt

        val pitchLocation =
            if (pitchLocationX == null || pitchLocationY == null) null
            else PitchLocation(pitchLocationX, pitchLocationY)

        return Foul(
            pitchType ?: PitchType.NO_ENTRY,
            pitchSpeed,
            pitchLocation,
            battingOption ?: BattingOption.NONE,
            withRunnerStarting,
            direction ?: FoulBallDirection.NO_ENTRY,
            isAtLine,
            battedBallType ?: BattedBallType.NO_ENTRY,
            battedBallStrength ?: BattedBallStrength.NO_ENTRY,
            positionMakesError,
            false,
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null)
        )
    }

    fun addHitByPitch(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean
    ) {
        val hitByPitch = createHitByPitch(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, null
        )

        game.value!!.boxScore.addNewPeriod(hitByPitch)
        game.value = game.value
    }

    fun replacePlayWithHitByPitch(
        target: PlayDto,
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean
    ): Boolean {
        val hitByPitch = createHitByPitch(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, target
        )

        if (!game.value!!.boxScore.canReplacePlay(target.play, hitByPitch)) return false

        game.value!!.boxScore.replacePlay(hitByPitch, target.play)
        game.value = game.value

        return true
    }

    private fun createHitByPitch(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        templatePlay: PlayDto?
    ): HitByPitch {
        val pitchLocation =
            if (pitchLocationX == null || pitchLocationY == null) null
            else PitchLocation(pitchLocationX, pitchLocationY)

        val latestSituation = game.value!!.boxScore.getSituation(null)
        val fieldPlayList = getFieldPlayListWithBatterRunnerWalkingToFirst(
            latestSituation,
            FieldPlayFactor.HIT_BY_PITCH
        )

        return HitByPitch(
            pitchType ?: PitchType.NO_ENTRY,
            pitchSpeed,
            pitchLocation,
            battingOption ?: BattingOption.NONE,
            withRunnerStarting,
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(fieldPlayList)
        )
    }

    fun addNoPitchIntentionalWalk() {
        val noPitchIntentionalWalk = createNoPitchIntentionalWalk(null)

        game.value!!.boxScore.addNewPeriod(noPitchIntentionalWalk)
        game.value = game.value
    }

    fun replacePlayWithNoPitchIntentionalWalk(target: PlayDto): Boolean {
        val noPitchIntentionalWalk = createNoPitchIntentionalWalk(target)

        if (!game.value!!.boxScore.canReplacePlay(target.play, noPitchIntentionalWalk)) return false

        game.value!!.boxScore.replacePlay(noPitchIntentionalWalk, target.play)
        game.value = game.value

        return true
    }

    private fun createNoPitchIntentionalWalk(templatePlay: PlayDto?): NoPitchIntentionalWalk {
        val fieldPlayList = getFieldPlayListWithBatterRunnerWalkingToFirst(
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldPlayFactor.WALK
        )

        return NoPitchIntentionalWalk(
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(fieldPlayList)
        )
    }

    fun addBatting(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        battedBall: BattedBall,
        fieldPlays: List<FieldPlayDto>,
        battingResult: BattingResult
    ) {
        val batting = createBatting(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, battedBall, fieldPlays, battingResult, null
        )

        game.value!!.boxScore.addNewPeriod(batting)
        game.value = game.value
    }

    fun replacePlayWithBatting(
        target: PlayDto,
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        battedBall: BattedBall,
        fieldPlays: List<FieldPlayDto>,
        battingResult: BattingResult
    ): Boolean {
        val batting = createBatting(
            pitchType, pitchSpeed, pitchLocationX, pitchLocationY, battingOption,
            withRunnerStarting, battedBall, fieldPlays, battingResult, target
        )

        if (!game.value!!.boxScore.canReplacePlay(target.play, batting)) return false

        game.value!!.boxScore.replacePlay(batting, target.play)
        game.value = game.value

        return true
    }

    private fun createBatting(
        pitchType: PitchType?,
        pitchSpeed: Int?,
        pitchLocationX: Float?,
        pitchLocationY: Float?,
        battingOption: BattingOption?,
        withRunnerStarting: Boolean,
        battedBall: BattedBall,
        fieldPlays: List<FieldPlayDto>,
        battingResult: BattingResult,
        templatePlay: PlayDto?
    ): Batting {
        val p = fieldPlayDtoToFieldPlayList(fieldPlays)

        val pitchLocation =
            if (pitchLocationX == null || pitchLocationY == null) null
            else PitchLocation(pitchLocationX, pitchLocationY)

        return Batting(
            pitchType ?: PitchType.NO_ENTRY,
            pitchSpeed,
            pitchLocation,
            battingOption ?: BattingOption.NONE,
            withRunnerStarting,
            battedBall,
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(p),
            battingResult
        )
    }

    fun addBalk() {
        val balk = createBalk(null)

        game.value!!.boxScore.addNewPeriod(balk)
        game.value = game.value
    }

    fun replacePlayWithBalk(target: PlayDto): Boolean {
        val balk = createBalk(target)

        if (!game.value!!.boxScore.canReplacePlay(target.play, balk)) return false

        game.value!!.boxScore.replacePlay(balk, target.play)
        game.value = game.value

        return true
    }

    private fun createBalk(templatePlay: PlayDto?): Balk {
        val previousSituation =
            templatePlay?.previousSituation ?: game.value!!.boxScore.getSituation(null)
        val fieldPlayList = ArrayList<FieldPlay>().apply {
            this.add(
                FieldPlay(
                    FieldPlayFactor.BALK,
                    ArrayList(),
                    ArrayList<RunnersAction>().apply {
                        if (previousSituation.orderOf1R != null)
                            this.add(RunnersAction(FIRST_RUNNER, ON_SECOND_BASE))
                        if (previousSituation.orderOf2R != null)
                            this.add(RunnersAction(SECOND_RUNNER, ON_THIRD_BASE))
                        if (previousSituation.orderOf3R != null)
                            this.add(RunnersAction(THIRD_RUNNER, HOME_IN))
                    })
            )
        }

        return Balk(
            templatePlay?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            templatePlay?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            previousSituation,
            FieldActiveDuration(fieldPlayList)
        )
    }

    fun addPlayWithoutPitch(fieldPlays: List<FieldPlayDto>) {
        val playInInterval = createPlayInInterval(fieldPlays, null)

        game.value!!.boxScore.addNewPeriod(playInInterval)
        game.value = game.value
    }

    fun replacePlayWithPlayInInterval(target: PlayDto, fieldPlays: List<FieldPlayDto>): Boolean {
        val playInInterval = createPlayInInterval(fieldPlays, target)

        if (!game.value!!.boxScore.canReplacePlay(target.play, playInInterval)) return false

        game.value!!.boxScore.replacePlay(playInInterval, target.play)
        game.value = game.value

        return true
    }

    private fun createPlayInInterval(
        fieldPlays: List<FieldPlayDto>, targetDto: PlayDto?
    ): PlayInInterval {
        val p = fieldPlayDtoToFieldPlayList(fieldPlays)

        return PlayInInterval(
            targetDto?.homeLineup ?: game.value!!.boxScore.getLineup(HOME, null),
            targetDto?.visitorLineup ?: game.value!!.boxScore.getLineup(VISITOR, null),
            targetDto?.previousSituation ?: game.value!!.boxScore.getSituation(null),
            FieldActiveDuration(p)
        )
    }

    fun addOffenceSubstitution(
        pinchHitterInfo: PlayerInfoDto?,
        pinchRunner1Info: PlayerInfoDto?,
        pinchRunner2Info: PlayerInfoDto?,
        pinchRunner3Info: PlayerInfoDto?
    ) {
        val substitution = createOffenceSubstitution(
            pinchHitterInfo,
            pinchRunner1Info,
            pinchRunner2Info,
            pinchRunner3Info,
            game.value!!.boxScore.getSituation(null),
            game.value!!.boxScore.getLineup(HOME, null),
            game.value!!.boxScore.getLineup(VISITOR, null)
        )

        game.value!!.boxScore.addNewPeriod(substitution)
        game.value = game.value
    }

    fun insertOffenceSubstitution(
        anchorPeriod: PeriodDto,
        pinchHitterInfo: PlayerInfoDto?,
        pinchRunner1Info: PlayerInfoDto?,
        pinchRunner2Info: PlayerInfoDto?,
        pinchRunner3Info: PlayerInfoDto?
    ) {
        val substitution = createOffenceSubstitution(
            pinchHitterInfo,
            pinchRunner1Info,
            pinchRunner2Info,
            pinchRunner3Info,
            anchorPeriod.previousSituation,
            anchorPeriod.homeLineup,
            anchorPeriod.visitorLineup
        )

        game.value!!.boxScore.insertSubstitution(substitution, anchorPeriod.period)
        game.value = game.value
    }

    fun modifyOffenceSubstitution(
        targetSubstitution: SubstitutionDto,
        pinchHitterInfo: PlayerInfoDto?,
        pinchRunner1Info: PlayerInfoDto?,
        pinchRunner2Info: PlayerInfoDto?,
        pinchRunner3Info: PlayerInfoDto?
    ) {
        val substitution = createOffenceSubstitution(
            pinchHitterInfo,
            pinchRunner1Info,
            pinchRunner2Info,
            pinchRunner3Info,
            targetSubstitution.previousSituation,
            targetSubstitution.homeLineup,
            targetSubstitution.visitorLineup
        )

        game.value!!.boxScore.replaceSubstitution(
            substitution, targetSubstitution.period as Substitution
        )
        game.value = game.value
    }

    private fun createOffenceSubstitution(
        pinchHitterInfo: PlayerInfoDto?,
        pinchRunner1Info: PlayerInfoDto?,
        pinchRunner2Info: PlayerInfoDto?,
        pinchRunner3Info: PlayerInfoDto?,
        situation: Situation,
        homeTeamLineup: Lineup,
        visitorTeamLineup: Lineup
    ): Substitution {
        val positionChangingList = ArrayList<PositionChanging>()
        val playerChangingList = ArrayList<PlayerChanging>()

        val toPlayer = fun(p: PlayerInfoDto) =
            Player(p.playerID, p.playerName, p.uniformNumber, p.bats, p.throws)

        if (pinchHitterInfo != null) {
            positionChangingList.add(
                PositionChanging(situation.orderOfBatter, Position.PINCH_HITTER)
            )
            playerChangingList.add(
                PlayerChanging(situation.orderOfBatter, toPlayer(pinchHitterInfo))
            )
        }
        if (pinchRunner1Info != null && situation.orderOf1R != null) {
            positionChangingList.add(PositionChanging(situation.orderOf1R!!, Position.PINCH_RUNNER))
            playerChangingList.add(
                PlayerChanging(
                    situation.orderOf1R!!,
                    toPlayer(pinchRunner1Info)
                )
            )
        }
        if (pinchRunner2Info != null && situation.orderOf2R != null) {
            positionChangingList.add(PositionChanging(situation.orderOf2R!!, Position.PINCH_RUNNER))
            playerChangingList.add(
                PlayerChanging(
                    situation.orderOf2R!!,
                    toPlayer(pinchRunner2Info)
                )
            )
        }
        if (pinchRunner3Info != null && situation.orderOf3R != null) {
            positionChangingList.add(PositionChanging(situation.orderOf3R!!, Position.PINCH_RUNNER))
            playerChangingList.add(
                PlayerChanging(
                    situation.orderOf3R!!,
                    toPlayer(pinchRunner3Info)
                )
            )
        }

        return Substitution(
            situation,
            if (situation.topOrBottom == TOP) VISITOR else HOME,
            false,
            positionChangingList,
            playerChangingList,
            homeTeamLineup,
            visitorTeamLineup
        )
    }

    fun addDefenceSubstitution(
        newPositionList: List<Pair<Int, Position>>,
        newPlayerList: List<Pair<Int, PlayerInfoDto>>
    ) {
        val substitution = createDefenceSubstitution(
            newPositionList,
            newPlayerList,
            game.value!!.boxScore.getSituation(null),
            game.value!!.boxScore.getLineup(HOME, null),
            game.value!!.boxScore.getLineup(VISITOR, null)
        )

        game.value!!.boxScore.addNewPeriod(substitution)
        game.value = game.value
    }

    fun insertDefenceSubstitution(
        anchorPeriod: PeriodDto,
        newPositionList: List<Pair<Int, Position>>,
        newPlayerList: List<Pair<Int, PlayerInfoDto>>
    ) {
        val substitution = createDefenceSubstitution(
            newPositionList,
            newPlayerList,
            anchorPeriod.previousSituation,
            anchorPeriod.homeLineup,
            anchorPeriod.visitorLineup
        )

        game.value!!.boxScore.insertSubstitution(substitution, anchorPeriod.period)
        game.value = game.value
    }

    fun modifyDefenceSubstitution(
        targetSubstitution: SubstitutionDto,
        newPositionList: List<Pair<Int, Position>>,
        newPlayerList: List<Pair<Int, PlayerInfoDto>>
    ) {
        val substitution = createDefenceSubstitution(
            newPositionList,
            newPlayerList,
            targetSubstitution.previousSituation,
            targetSubstitution.homeLineup,
            targetSubstitution.visitorLineup
        )

        game.value!!.boxScore.replaceSubstitution(
            substitution, targetSubstitution.period as Substitution
        )
        game.value = game.value
    }

    private fun createDefenceSubstitution(
        newPositionList: List<Pair<Int, Position>>,
        newPlayerList: List<Pair<Int, PlayerInfoDto>>,
        situation: Situation,
        homeTeamLineup: Lineup,
        visitorTeamLineup: Lineup
    ): Substitution {
        val positionChangingList = ArrayList<PositionChanging>()
        val playerChangingList = ArrayList<PlayerChanging>()

        newPositionList.forEach {
            positionChangingList.add(PositionChanging(it.first, it.second))
        }
        newPlayerList.forEach {
            playerChangingList.add(
                PlayerChanging(
                    it.first,
                    Player(
                        it.second.playerID,
                        it.second.playerName,
                        it.second.uniformNumber,
                        it.second.bats,
                        it.second.throws
                    )
                )
            )
        }

        val defenceTeamClass = if (situation.topOrBottom == TOP) HOME else VISITOR
        val lineupBeforeSubstitution = when (defenceTeamClass) {
            HOME -> homeTeamLineup
            VISITOR -> visitorTeamLineup
        }

        val cancelDH = when {
            !lineupBeforeSubstitution.hasDH -> false
            else -> {
                newPositionList.find { p -> p.second == Position.PITCHER }.let {
                    if (it == null) false else it.first < 9
                }
            }
        }

        return Substitution(
            situation,
            defenceTeamClass,
            cancelDH,
            positionChangingList,
            playerChangingList,
            homeTeamLineup,
            visitorTeamLineup
        )
    }

    fun deleteSubstitution(targetSubstitution: SubstitutionDto) {
        game.value!!.boxScore.deleteSubstitution(targetSubstitution.period as Substitution)
        game.value = game.value
    }

    fun popLastPeriod(): PeriodDto? {
        val period = game.value!!.boxScore.undo()
        game.value = game.value

        return when (period) {
            is Strike -> StrikeDto(period, true)
            is Ball -> BallDto(period, true)
            is Foul -> FoulDto(period, true)
            is HitByPitch -> HitByPitchDto(period, true)
            is NoPitchIntentionalWalk -> NoPitchIntentionalWalkDto(period, true)
            is Batting -> BattingDto(period, true)
            is Balk -> BalkDto(period, true)
            is PlayInInterval -> PlayWithoutPitchDto(period, true)
            is Substitution -> SubstitutionDto(period, true)
            else -> null
        }
    }

    private fun getFieldPlayListWithBatterRunnerWalkingToFirst(
        situation: Situation,
        factor: FieldPlayFactor
    ): List<FieldPlay> = ArrayList<FieldPlay>().apply {
        this.add(
            FieldPlay(
                factor,
                ArrayList(),
                ArrayList<RunnersAction>().apply {
                    this.add(RunnersAction(BATTER_RUNNER, ON_FIRST_BASE))
                    if (situation.is1RForcedToProgress)
                        this.add(RunnersAction(FIRST_RUNNER, ON_SECOND_BASE))
                    if (situation.is2RForcedToProgress)
                        this.add(RunnersAction(SECOND_RUNNER, ON_THIRD_BASE))
                    if (situation.is3RForcedToProgress)
                        this.add(RunnersAction(THIRD_RUNNER, HOME_IN))
                })
        )
    }

    private fun fieldPlayDtoToFieldPlayList(fieldPlays: List<FieldPlayDto>): List<FieldPlay> {
        return ArrayList<FieldPlay>().apply {
            fieldPlays.forEach { fp ->
                val runnersActionList = ArrayList<RunnersAction>().apply {
                    fp.batterRunnersState?.let {
                        this.add(RunnersAction(BATTER_RUNNER, it))
                    }
                    fp.firstRunnersState?.let {
                        this.add(RunnersAction(FIRST_RUNNER, it))
                    }
                    fp.secondRunnersState?.let {
                        this.add(RunnersAction(SECOND_RUNNER, it))
                    }
                    fp.thirdRunnersState?.let {
                        this.add(RunnersAction(THIRD_RUNNER, it))
                    }
                }

                this.add(FieldPlay(fp.factor, fp.fieldersActions, runnersActionList))
            }
        }
    }

    private fun createGameStateDto(game: Game): GameStateDto {
        val situation = game.boxScore.getSituation(null)
        val homeLineup = game.boxScore.getLineup(HOME, null)
        val visitorLineup = game.boxScore.getLineup(VISITOR, null)

        val latestBatter = when (situation.topOrBottom) {
            TOP -> visitorLineup
            BOTTOM -> homeLineup
        }.getPlayer(situation.orderOfBatter)

        val latestPitcher = when (situation.topOrBottom) {
            TOP -> homeLineup
            BOTTOM -> visitorLineup
        }.getPlayer(Position.PITCHER)

        val pitchCount = latestPitcher?.let { game.boxScore.getPitchCount(it) } ?: 0
        val balls = situation.balls
        val strikes = situation.strikes
        val outs = situation.outs

        return GameStateDto(
            situation,
            homeLineup,
            visitorLineup,
            latestBatter.playerName,
            latestPitcher?.playerName,
            latestPitcher?.throws,
            pitchCount,
            balls,
            strikes,
            outs
        )
    }

    private fun createPitchInfoDto(pitch: Pitch): PitchInfoDto {
        return PitchInfoDto(
            when {
                pitch is Strike && pitch.strikeType == SWINGING -> STRIKE_SWUNG
                pitch is Strike && pitch.strikeType == LOOKING -> STRIKE_CALLED
                pitch is Ball -> BALL
                pitch is Foul -> FOUL
                pitch is Batting -> BATTED
                pitch is HitByPitch -> HIT_BY_PITCH
                else -> throw RuntimeException("Cannot identify pitch result.")
            },
            pitch.pitchLocation?.x,
            pitch.pitchLocation?.y,
            pitch.pitchSpeed,
            pitch.pitchType,
            pitch.battingOption,
            pitch.settled
        )
    }

    // region * DTO *

    data class PlayerInfoDto(
        val playerID: ID?,
        val playerName: PersonName?,
        val bats: HandType?,
        val throws: HandType?,
        val uniformNumber: String?
    )

    enum class PitchResult {
        BALL,
        STRIKE_SWUNG,
        STRIKE_CALLED,
        FOUL,
        BATTED,
        HIT_BY_PITCH
    }

    data class GameStateDto(
        val situation: Situation,
        val homeTeamLineup: Lineup,
        val visitorTeamLineup: Lineup,
        val currentBatterName: PersonName?,
        val currentPitcherName: PersonName?,
        val currentPitchersThrowHandType: HandType?,
        val currentPitchersPitchCount: Int,
        val balls: Int,
        val strikes: Int,
        val outs: Int
    ) {
        val nextBallWillFinalize: Boolean
            get() = balls >= 3
        val nextStrikeWillFinalize: Boolean
            get() = strikes >= 2
        val offenceTeamLineup: Lineup
            get() = when (situation.topOrBottom) {
                TOP -> visitorTeamLineup
                BOTTOM -> homeTeamLineup
            }
        val defenceTeamLineup: Lineup
            get() = when (situation.topOrBottom) {
                TOP -> homeTeamLineup
                BOTTOM -> visitorTeamLineup
            }
        val firstRunnerName: PersonName?
            get() =
                if (situation.orderOf1R == null) null
                else offenceTeamLineup.getPlayer(situation.orderOf1R!!).playerName
        val secondRunnerName: PersonName?
            get() =
                if (situation.orderOf2R == null) null
                else offenceTeamLineup.getPlayer(situation.orderOf2R!!).playerName
        val thirdRunnerName: PersonName?
            get() =
                if (situation.orderOf3R == null) null
                else offenceTeamLineup.getPlayer(situation.orderOf3R!!).playerName
    }

    data class PitchInfoDto(
        val pitchResult: PitchResult,
        val pitchLocationX: Float?,
        val pitchLocationY: Float?,
        val pitchSpeed: Int?,
        val pitchType: PitchType,
        val battingOption: BattingOption,
        val isFinalizing: Boolean
    ) {
        val pitchLocation: PitchLocation?
            get() =
                if (pitchLocationX == null || pitchLocationY == null) null
                else PitchLocation(pitchLocationX, pitchLocationY)
    }

    data class FieldPlayDto(
        val factor: FieldPlayFactor,
        val fieldersActions: List<FieldersAction>,
        val batterRunnersState: RunnerState?,
        val firstRunnersState: RunnerState?,
        val secondRunnersState: RunnerState?,
        val thirdRunnersState: RunnerState?
    )

    // endregion * DTO *

    // region * BoxScore DTO *

    class BoxScoreDto(private val boxScore: BoxScore) {
        val innings: List<InningDto>
            get() = boxScore.innings.map {
                InningDto(
                    it
                )
            }
    }

    class InningDto(private val inning: Inning) {
        val plateAppearances: List<PlateAppearanceDto>
            get() = inning.plateAppearances.map {
                PlateAppearanceDto(
                    it
                )
            }
        val runs: Int
            get() = inning.getRunsUntil(null)
        val hits: Int
            get() = inning.plateAppearances.count { pa ->
                pa.periods.any { p -> p is Play && p.battingResult?.isBaseHit == true }
            }
        val walks: Int
            get() = inning.plateAppearances.count { pa ->
                pa.periods.any { p -> p is Play && p.battingResult == BattingResult.WALK }
            }
        val hitsByPitch: Int
            get() = inning.plateAppearances.count { pa ->
                pa.periods.any { p -> p is Play && p.battingResult == BattingResult.HIT_BY_PITCH }
            }
        val strikeouts: Int
            get() = inning.plateAppearances.count { pa ->
                pa.periods.any { p -> p is Play && p.battingResult == BattingResult.STRIKEOUT }
            }
        val errors: Int
            get() = inning.plateAppearances.sumBy { pa ->
                pa.periods.filterIsInstance<Play>().sumBy { p ->
                    p.fieldActiveDuration.fieldPlayList.count { fp ->
                        fp.fieldersActionList.any { act -> act.record?.isError == true }
                    }
                }
            }
        val leftOnBases: Int
            get() = inning.leftOnBases
    }

    class PlateAppearanceDto(
        private val plateAppearance: PlateAppearance
    ) {
        val periods: List<PeriodDto>
            get() = plateAppearance.periods.map {
                val isLastOfPlateAppearance = it == plateAppearance.periods.last()
                when (it) {
                    is Strike -> StrikeDto(it, isLastOfPlateAppearance)
                    is Ball -> BallDto(it, isLastOfPlateAppearance)
                    is Foul -> FoulDto(it, isLastOfPlateAppearance)
                    is HitByPitch -> HitByPitchDto(it, isLastOfPlateAppearance)
                    is NoPitchIntentionalWalk -> NoPitchIntentionalWalkDto(
                        it,
                        isLastOfPlateAppearance
                    )
                    is Batting -> BattingDto(it, isLastOfPlateAppearance)
                    is Balk -> BalkDto(it, isLastOfPlateAppearance)
                    is PlayInInterval -> PlayWithoutPitchDto(it, isLastOfPlateAppearance)
                    is Substitution -> SubstitutionDto(it, isLastOfPlateAppearance)
                    else -> throw RuntimeException("Unknown Period expanding class.")
                }
            }
    }

    // endregion * BoxScore DTO *

    // region * Period DTO *

    abstract class PeriodDto(internal val period: Period, val isLastOfPlateAppearance: Boolean) {
        val previousSituation: Situation
            get() = period.previousSituation
        val homeLineup: Lineup
            get() = period.homeLineup
        val visitorLineup: Lineup
            get() = period.visitorLineup
        val offenceTeamLineup: Lineup
            get() = when (period.previousSituation.topOrBottom) {
                TOP -> period.visitorLineup
                BOTTOM -> period.homeLineup
            }
        val defenceTeamLineup: Lineup
            get() = when (period.previousSituation.topOrBottom) {
                TOP -> period.homeLineup
                BOTTOM -> period.visitorLineup
            }
        val inningNumber: Int
            get() = period.previousSituation.inningNumber
        val topOrBottom: TopOrBottom
            get() = period.previousSituation.topOrBottom
        val runsOfHome: Int
            get() = period.previousSituation.runsOfHome
        val runsOfVisitor: Int
            get() = period.previousSituation.runsOfVisitor
        val balls: Int
            get() = period.previousSituation.balls
        val strikes: Int
            get() = period.previousSituation.strikes
        val outs: Int
            get() = period.previousSituation.outs
        val runnerIsOn1B: Boolean
            get() = period.previousSituation.orderOf1R != null
        val runnerIsOn2B: Boolean
            get() = period.previousSituation.orderOf2R != null
        val runnerIsOn3B: Boolean
            get() = period.previousSituation.orderOf3R != null
        val pitchersName: PersonName?
            get() = when (period.previousSituation.topOrBottom) {
                TOP -> period.homeLineup
                BOTTOM -> period.visitorLineup
            }.getPlayer(Position.PITCHER)?.playerName
        val battersName: PersonName?
            get() = when (period.previousSituation.topOrBottom) {
                TOP -> period.visitorLineup
                BOTTOM -> period.homeLineup
            }.getPlayer(period.previousSituation.orderOfBatter).playerName
        val firstRunnerName: PersonName?
            get() =
                if (period.previousSituation.orderOf1R == null) null
                else when (period.previousSituation.topOrBottom) {
                    TOP -> period.visitorLineup
                    BOTTOM -> period.homeLineup
                }.getPlayer(period.previousSituation.orderOf1R!!).playerName
        val secondRunnerName: PersonName?
            get() =
                if (period.previousSituation.orderOf2R == null) null
                else when (period.previousSituation.topOrBottom) {
                    TOP -> period.visitorLineup
                    BOTTOM -> period.homeLineup
                }.getPlayer(period.previousSituation.orderOf2R!!).playerName
        val thirdRunnerName: PersonName?
            get() =
                if (period.previousSituation.orderOf3R == null) null
                else when (period.previousSituation.topOrBottom) {
                    TOP -> period.visitorLineup
                    BOTTOM -> period.homeLineup
                }.getPlayer(period.previousSituation.orderOf3R!!).playerName
        val isLHP: Boolean
            get() = when (period.previousSituation.topOrBottom) {
                TOP -> period.homeLineup
                BOTTOM -> period.visitorLineup
            }.getPlayer(Position.PITCHER)?.throws == HandType.LEFT

        fun getPositionIn(teamClass: TeamClass, order: Int): Position =
            when (teamClass) {
                HOME -> period.homeLineup
                VISITOR -> period.visitorLineup
            }.getPosition(order)

        fun getPlayerIn(teamClass: TeamClass, order: Int): Player =
            when (teamClass) {
                HOME -> period.homeLineup
                VISITOR -> period.visitorLineup
            }.getPlayer(order)
    }

    abstract class PlayDto(internal val play: Play, isLastOfPlateAppearance: Boolean) :
        PeriodDto(play, isLastOfPlateAppearance) {
        val fieldPlayList: List<FieldPlay>
            get() = play.fieldActiveDuration.fieldPlayList
    }

    abstract class PitchDto(private val pitch: Pitch, isLastOfPlateAppearance: Boolean) :
        PlayDto(pitch, isLastOfPlateAppearance) {
        val pitchType: PitchType?
            get() = pitch.pitchType
        val pitchSpeed: Int?
            get() = pitch.pitchSpeed
        val pitchLocationX: Float?
            get() = pitch.pitchLocation?.x
        val pitchLocationY: Float?
            get() = pitch.pitchLocation?.y
        val battingOption: BattingOption?
            get() = pitch.battingOption
        val settled: Boolean
            get() = pitch.settled
        val battingResult: BattingResult?
            get() = pitch.battingResult
    }

    class StrikeDto(private val strike: Strike, isLastOfPlateAppearance: Boolean) :
        PitchDto(strike, isLastOfPlateAppearance) {
        val strikeType: Strike.StrikeType
            get() = strike.strikeType
        val fieldPlays: List<FieldPlayDto>
            get() = strike.fieldActiveDuration.fieldPlayList.map {
                FieldPlayDto(
                    it.factor,
                    it.fieldersActionList,
                    it.batterRunnersAction?.state,
                    it.firstRunnersAction?.state,
                    it.secondRunnersAction?.state,
                    it.thirdRunnersAction?.state
                )
            }
    }

    class BallDto(private val ball: Ball, isLastOfPlateAppearance: Boolean) :
        PitchDto(ball, isLastOfPlateAppearance) {
        val fieldPlays: List<FieldPlayDto>
            get() = ball.fieldActiveDuration.fieldPlayList.map {
                FieldPlayDto(
                    it.factor,
                    it.fieldersActionList,
                    it.batterRunnersAction?.state,
                    it.firstRunnersAction?.state,
                    it.secondRunnersAction?.state,
                    it.thirdRunnersAction?.state
                )
            }
    }

    class FoulDto(private val foul: Foul, isLastOfPlateAppearance: Boolean) :
        PitchDto(foul, isLastOfPlateAppearance) {
        val direction: FoulBallDirection?
            get() = foul.direction
        val battedBallType: BattedBallType?
            get() = foul.battedBallType
        val battedBallStrength: BattedBallStrength?
            get() = foul.battedBallStrength
        val isAtLine: Boolean?
            get() = foul.isAtLine
        val positionMakesError: FieldPosition?
            get() {
                var errorPosition: FieldPosition? = null
                for (fieldPlay in foul.fieldActiveDuration.fieldPlayList) {
                    errorPosition = fieldPlay.fieldersActionList.find { n ->
                        n.record?.isError == true
                    }?.position
                    if (errorPosition != null) break
                }
                return errorPosition
            }
    }

    class HitByPitchDto(private val hitByPitch: HitByPitch, isLastOfPlateAppearance: Boolean) :
        PitchDto(hitByPitch, isLastOfPlateAppearance)

    class NoPitchIntentionalWalkDto(
        private val noPitchIntentionalWalk: NoPitchIntentionalWalk,
        isLastOfPlateAppearance: Boolean
    ) : PlayDto(noPitchIntentionalWalk, isLastOfPlateAppearance)

    class BattingDto(private val batting: Batting, isLastOfPlateAppearance: Boolean) :
        PitchDto(batting, isLastOfPlateAppearance) {
        val battedBall: BattedBall
            get() = batting.battedBall
        val fieldPlays: List<FieldPlayDto>
            get() = batting.fieldActiveDuration.fieldPlayList.map {
                FieldPlayDto(
                    it.factor,
                    it.fieldersActionList,
                    it.batterRunnersAction?.state,
                    it.firstRunnersAction?.state,
                    it.secondRunnersAction?.state,
                    it.thirdRunnersAction?.state
                )
            }
    }

    class BalkDto(
        val balk: Balk, isLastOfPlateAppearance: Boolean
    ) : PlayDto(balk, isLastOfPlateAppearance)

    class PlayWithoutPitchDto(
        private val playInInterval: PlayInInterval,
        isLastOfPlateAppearance: Boolean
    ) :
        PlayDto(playInInterval, isLastOfPlateAppearance) {
        val fieldPlays: List<FieldPlayDto>
            get() = play.fieldActiveDuration.fieldPlayList.map {
                FieldPlayDto(
                    it.factor,
                    it.fieldersActionList,
                    it.batterRunnersAction?.state,
                    it.firstRunnersAction?.state,
                    it.secondRunnersAction?.state,
                    it.thirdRunnersAction?.state
                )
            }
    }

    class SubstitutionDto(
        private val substitution: Substitution,
        isLastOfPlateAppearance: Boolean
    ) : PeriodDto(substitution, isLastOfPlateAppearance) {
        val teamClass: TeamClass
            get() = substitution.teamClass
        val newPositionList: List<Pair<Int, Position>>
            get() = substitution.positionChangingList.map {
                Pair(it.battingOrder, it.newPosition)
            }
        val newPlayerList: List<Pair<Int, PlayerInfoDto>>
            get() = substitution.playerChangingList.map {
                Pair(
                    it.battingOrder, PlayerInfoDto(
                        it.newPlayer.playerId,
                        it.newPlayer.playerName,
                        it.newPlayer.bats,
                        it.newPlayer.throws,
                        it.newPlayer.uniformNumber
                    )
                )
            }
    }

    // endregion * Period DTO *

}