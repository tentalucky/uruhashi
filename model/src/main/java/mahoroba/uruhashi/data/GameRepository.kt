package mahoroba.uruhashi.data

import android.app.Application
import android.util.Log
import mahoroba.uruhashi.data.game.*
import mahoroba.uruhashi.domain.ID
import mahoroba.uruhashi.domain.NameType
import mahoroba.uruhashi.domain.PersonName
import mahoroba.uruhashi.domain.game.*

class GameRepository(application: Application) : IGameRepository {
    private val db = AppDatabaseProvider.getDatabase(application)
    private val gameDao: GameDao
    private val startingMemberDao: StartingMemberDao
    private val inningDao: InningDao
    private val plateAppearanceDao: PlateAppearanceDao
    private val periodDao: PeriodDao
    private val substitutionDao: SubstitutionDao
    private val playerChangingDao: PlayerChangingDao
    private val positionChangingDao: PositionChangingDao
    private val playDao: PlayDao
    private val battedBallDirectionDao: BattedBallDirectionDao
    private val fieldPlayDao: FieldPlayDao
    private val fieldersActionDao: FieldersActionDao
    private val runnersActionDao: RunnersActionDao

    init {
        gameDao = db.gameDao
        startingMemberDao = db.startingMemberDao
        inningDao = db.inningDao
        plateAppearanceDao = db.plateAppearanceDao
        periodDao = db.periodDao
        substitutionDao = db.substitutionDao
        playerChangingDao = db.playerChangingDao
        positionChangingDao = db.positionChangingDao
        playDao = db.playDao
        battedBallDirectionDao = db.battedBallDirectionDao
        fieldPlayDao = db.fieldPlayDao
        fieldersActionDao = db.fieldersActionDao
        runnersActionDao = db.runnersActionDao
    }

    override fun save(game: Game) {
        db.runInTransaction {
            if (gameDao.isGameIdExists(game.id)) {
                gameDao.updateGame(GameData(game))
            } else {
                gameDao.insertGame(GameData(game))
            }

            startingMemberDao.deleteMembers(game.id)
            StartingMemberData.getList(game.id, TeamClass.HOME, game.boxScore.homeStartingLineup)
                .forEach { startingMemberDao.insert(it) }
            StartingMemberData.getList(
                game.id,
                TeamClass.VISITOR,
                game.boxScore.visitorStartingLineup
            )
                .forEach { startingMemberDao.insert(it) }

            inningDao.deleteByGameId(game.id)
            InningData.toInningDataList(game.id, game.boxScore.innings)
                .forEach { inningDao.insert(it) }

            plateAppearanceDao.deleteByGameId(game.id)
            periodDao.deleteByGameId(game.id)
            substitutionDao.deleteByGameId(game.id)
            playDao.deleteByGameId(game.id)
            fieldPlayDao.deleteByGameId(game.id)
            playerChangingDao.deleteByGameId(game.id)
            positionChangingDao.deleteByGameId(game.id)
            fieldersActionDao.deleteByGameId(game.id)
            runnersActionDao.deleteByGameId(game.id)
            battedBallDirectionDao.deleteByGameId(game.id)
            game.boxScore.innings.forEach { inn ->
                var paSeq = 0
                inn.plateAppearances.forEach { pa ->
                    paSeq++
                    plateAppearanceDao.insert(PlateAppearanceData().apply {
                        this.gameId = game.id
                        this.inningSeqNumber = inn.seqNumber
                        this.seqNumber = paSeq
                    })

                    var periodSeq = 0
                    pa.periods.forEach { period ->
                        periodSeq++
                        periodDao.insert(PeriodData().apply {
                            this.gameId = game.id
                            this.inningSeqNumber = inn.seqNumber
                            this.plateAppearanceSeqNumber = paSeq
                            this.seqNumber = periodSeq
                            this.periodType = PeriodData.toPeriodType(period)
                        })

                        when (period) {
                            is Substitution -> {
                                substitutionDao.insert(SubstitutionData().apply {
                                    this.gameId = game.id
                                    this.inningSeqNumber = inn.seqNumber
                                    this.plateAppearanceSeqNumber = paSeq
                                    this.periodSeqNumber = periodSeq
                                    this.teamClass = period.teamClass
                                    this.cancelDH = period.cancelDH
                                })

                                period.playerChangingList.forEach { change ->
                                    playerChangingDao.insert(PlayerChangingData().apply {
                                        this.gameId = game.id
                                        this.inningSeqNumber = inn.seqNumber
                                        this.plateAppearanceSeqNumber = paSeq
                                        this.periodSeqNumber = periodSeq
                                        this.battingOrder = change.battingOrder
                                        this.playerId = change.newPlayer.playerId
                                        this.playerFamilyName =
                                            change.newPlayer.playerName?.familyName
                                        this.playerFirstName =
                                            change.newPlayer.playerName?.firstName
                                        this.playerNameType = change.newPlayer.playerName?.nameType
                                        this.batHand = change.newPlayer.bats
                                        this.throwHand = change.newPlayer.throws
                                        this.uniformNumber = change.newPlayer.uniformNumber
                                    })
                                }

                                period.positionChangingList.forEach { change ->
                                    positionChangingDao.insert(PositionChangingData().apply {
                                        this.gameId = game.id
                                        this.inningSeqNumber = inn.seqNumber
                                        this.plateAppearanceSeqNumber = paSeq
                                        this.periodSeqNumber = periodSeq
                                        this.battingOrder = change.battingOrder
                                        this.position = change.newPosition
                                    })
                                }
                            }
                            is Play -> {
                                playDao.insert(PlayData().apply {
                                    this.gameId = game.id
                                    this.inningSeqNumber = inn.seqNumber
                                    this.plateAppearanceSeqNumber = paSeq
                                    this.periodSeqNumber = periodSeq
                                    this.playType = PlayData.getPlayTypeValueOf(period)

                                    this.battingResult = period.battingResult
                                    this.pitchType = if (period is Pitch) period.pitchType else null
                                    this.pitchSpeed =
                                        if (period is Pitch) period.pitchSpeed else null
                                    this.pitchLocationX =
                                        if (period is Pitch) period.pitchLocation?.x else null
                                    this.pitchLocationY =
                                        if (period is Pitch) period.pitchLocation?.y else null
                                    this.battingOption =
                                        if (period is Pitch) period.battingOption else null
                                    this.settled =
                                        if (period is Pitch) period.settled else null
                                    this.strikeType =
                                        if (period is Strike) period.strikeType else null
                                    this.foulBallDirection =
                                        if (period is Foul) period.direction else null
                                    this.foulBallIsAtLine =
                                        if (period is Foul) period.isAtLine else null
                                    this.battedBallType = when (period) {
                                        is Batting -> period.battedBall.type
                                        is Foul -> period.battedBallType
                                        else -> null
                                    }
                                    this.battedBallStrength = when (period) {
                                        is Batting -> period.battedBall.strength
                                        is Foul -> period.battedBallStrength
                                        else -> null
                                    }
                                    this.battedBallDistance = when (period) {
                                        is Batting -> period.battedBall.distance
                                        else -> null
                                    }
                                    this.withRunnerStarting =
                                        if (period is Pitch) period.withRunnerStarting else null
                                })

                                if (period is Batting) {
                                    var directionSeq = 0
                                    period.battedBall.direction?.tracePoints?.forEach { coord ->
                                        directionSeq++
                                        battedBallDirectionDao.insert(BattedBallDirectionData().apply {
                                            this.gameId = game.id
                                            this.inningSeqNumber = inn.seqNumber
                                            this.plateAppearanceSeqNumber = paSeq
                                            this.periodSeqNumber = periodSeq
                                            this.seqNumber = directionSeq
                                            this.angle = coord.angle
                                            this.distance = coord.distance
                                        })
                                    }
                                }

                                var fieldPlaySeq = 0
                                period.fieldActiveDuration.fieldPlayList.forEach { fp ->
                                    fieldPlaySeq++
                                    fieldPlayDao.insert(FieldPlayData().apply {
                                        this.gameId = game.id
                                        this.inningSeqNumber = inn.seqNumber
                                        this.plateAppearanceSeqNumber = paSeq
                                        this.periodSeqNumber = periodSeq
                                        this.seqNumber = fieldPlaySeq
                                        this.factor = fp.factor
                                    })

                                    var fieldersActionSeq = 0
                                    fp.fieldersActionList.forEach { act ->
                                        fieldersActionSeq++
                                        fieldersActionDao.insert(FieldersActionData().apply {
                                            this.gameId = game.id
                                            this.inningSeqNumber = inn.seqNumber
                                            this.plateAppearanceSeqNumber = paSeq
                                            this.periodSeqNumber = periodSeq
                                            this.fieldPlaySeqNumber = fieldPlaySeq
                                            this.seqNumber = fieldersActionSeq
                                            this.position = act.position
                                            this.record = act.record
                                        })
                                    }

                                    val insertRunnersActionData = fun(action: RunnersAction) {
                                        runnersActionDao.insert(RunnersActionData().apply {
                                            this.gameId = game.id
                                            this.inningSeqNumber = inn.seqNumber
                                            this.plateAppearanceSeqNumber = paSeq
                                            this.periodSeqNumber = periodSeq
                                            this.fieldPlaySeqNumber = fieldPlaySeq
                                            this.runnerType = action.type
                                            this.state = action.state
                                        })
                                    }
                                    fp.batterRunnersAction?.let { insertRunnersActionData(it) }
                                    fp.firstRunnersAction?.let { insertRunnersActionData(it) }
                                    fp.secondRunnersAction?.let { insertRunnersActionData(it) }
                                    fp.thirdRunnersAction?.let { insertRunnersActionData(it) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun delete(game: Game) {
        db.runInTransaction {
            runnersActionDao.deleteByGameId(game.id)
            fieldersActionDao.deleteByGameId(game.id)
            fieldPlayDao.deleteByGameId(game.id)
            battedBallDirectionDao.deleteByGameId(game.id)
            playDao.deleteByGameId(game.id)
            positionChangingDao.deleteByGameId(game.id)
            playerChangingDao.deleteByGameId(game.id)
            substitutionDao.deleteByGameId(game.id)
            periodDao.deleteByGameId(game.id)
            plateAppearanceDao.deleteByGameId(game.id)
            inningDao.deleteByGameId(game.id)
            startingMemberDao.deleteMembers(game.id)
            gameDao.deleteGame(GameData(game))
        }
    }

    override fun get(gameId: ID): Game {
//        val game = gameDao.findById(gameId.value).toGame()
        val gameData = gameDao.findById(gameId.value)
        val homeMemberDataList = startingMemberDao.findMembers(gameId, TeamClass.HOME)
        val visitorMemberDataList = startingMemberDao.findMembers(gameId, TeamClass.VISITOR)
        val inningDataList = inningDao.findByGameId(gameId)
        val plateAppearanceDataList = plateAppearanceDao.findByGameId(gameId)
        val periodDataList = periodDao.findByGameId(gameId)
        val substitutionDataList = substitutionDao.findByGameId(gameId)
        val playerChangingDataList = playerChangingDao.findByGameId(gameId)
        val positionChangingDataList = positionChangingDao.findByGameId(gameId)
        val playDataList = playDao.findByGameId(gameId)
        val battedBallDirectionDataList = battedBallDirectionDao.findByGameId(gameId)
        val fieldPlayDataList = fieldPlayDao.findByGameId(gameId)
        val fieldersActionDataList = fieldersActionDao.findByGameId(gameId)
        val runnersActionDataList = runnersActionDao.findByGameId(gameId)

        val game = Game(gameId)
        game.gameInfo.let {
            it.gameName = gameData.gameName
            it.date = gameData.date
            it.homeTeam.teamId =
                if (gameData.homeTeamId != null) ID(gameData.homeTeamId!!) else null
            it.homeTeam.teamName = gameData.homeTeamName
            it.homeTeam.abbreviatedName = gameData.homeTeamAbbreviatedName
            it.visitorTeam.teamId =
                if (gameData.visitorTeamId != null) ID(gameData.visitorTeamId!!) else null
            it.visitorTeam.teamName = gameData.visitorTeamName
            it.visitorTeam.abbreviatedName = gameData.visitorTeamAbbreviatedName
            it.stadiumInfo.stadiumId =
                if (gameData.stadiumId != null) ID(gameData.stadiumId!!) else null
            it.stadiumInfo.stadiumName = gameData.stadiumName
            it.stadiumInfo.abbreviatedName = gameData.stadiumAbbreviatedName
        }

        val setStartingLineup =
            fun(lineup: StartingLineup, dataList: List<StartingMemberData>) {
                ArrayList<Position>().let { list ->
                    dataList.sortedBy { n -> n.battingOrder }
                        .forEach { list.add(it.position) }
                    lineup.setPositions(list.subList(0, 9), (list.size > 9))
                }

                dataList.forEach { data ->
                    if (data.battingOrder < 9) {
                        lineup.players[data.battingOrder]
                    } else {
                        lineup.playerOutOfOrder
                    }.let { p ->
                        p.playerId = data.playerId
                        p.playerName =
                            PersonName(
                                data.playerFamilyName,
                                data.playerFirstName,
                                data.playerNameType
                            )
                        p.bats = data.batHand
                        p.throws = data.throwHand
                        p.uniformNumber = data.uniformNumber
                    }
                }
            }

        setStartingLineup(game.boxScore.homeStartingLineup, homeMemberDataList)
        setStartingLineup(game.boxScore.visitorStartingLineup, visitorMemberDataList)

        inningDataList.forEach { inn ->
            plateAppearanceDataList
                .filter { n -> n.isChildOf(inn) }.forEach { pa ->
                    periodDataList
                        .filter { n -> n.isChildOf(pa) }
                        .forEach { prd ->
                            val period: Period = when (PeriodData.getPeriodType(prd)) {

                                PeriodData.Companion.PeriodDataType.SUBSTITUTION -> {
                                    val substitutionData =
                                        substitutionDataList.find { n -> n.isDescriptionOf(prd) }!!

                                    val positionChangingList = ArrayList<PositionChanging>().apply {
                                        positionChangingDataList
                                            .filter { n -> n.isChildOf(substitutionData) }
                                            .forEach { data ->
                                                this.add(
                                                    PositionChanging(
                                                        data.battingOrder,
                                                        data.position
                                                    )
                                                )
                                            }
                                    }

                                    val playerChangingList = ArrayList<PlayerChanging>().apply {
                                        playerChangingDataList
                                            .filter { n -> n.isChildOf(substitutionData) }
                                            .forEach { data ->
                                                val personName = PersonName(
                                                    data.playerFamilyName,
                                                    data.playerFirstName,
                                                    data.playerNameType
                                                        ?: NameType.FAMILY_NAME_FIRST
                                                )

                                                this.add(
                                                    PlayerChanging(
                                                        data.battingOrder,
                                                        Player(
                                                            data.playerId,
                                                            personName,
                                                            data.uniformNumber,
                                                            data.batHand,
                                                            data.throwHand
                                                        )
                                                    )
                                                )
                                            }
                                    }

                                    Substitution(
                                        game.boxScore.getSituation(null),
                                        substitutionData.teamClass,
                                        substitutionData.cancelDH,
                                        positionChangingList,
                                        playerChangingList,
                                        game.boxScore.getLineup(TeamClass.HOME, null),
                                        game.boxScore.getLineup(TeamClass.VISITOR, null)
                                    )
                                }

                                PeriodData.Companion.PeriodDataType.PLAY -> {
                                    val playData =
                                        playDataList.find { n -> n.isDescriptionOf(prd) }!!

                                    val fieldActiveDuration = FieldActiveDuration(
                                        ArrayList<FieldPlay>().apply {
                                            fieldPlayDataList.filter { n -> n.isChildOf(playData) }
                                                .forEach { fld ->
                                                    this.add(
                                                        FieldPlay(
                                                            fld.factor ?: FieldPlayFactor.OTHER,
                                                            ArrayList<FieldersAction>().apply {
                                                                fieldersActionDataList.filter { n ->
                                                                    n.isChildOf(fld)
                                                                }.forEach { act ->
                                                                    this.add(
                                                                        FieldersAction(
                                                                            act.position,
                                                                            act.record
                                                                        )
                                                                    )
                                                                }
                                                            },
                                                            ArrayList<RunnersAction>().apply {
                                                                runnersActionDataList.filter { n ->
                                                                    n.isChildOf(fld)
                                                                }.forEach { act ->
                                                                    this.add(
                                                                        RunnersAction(
                                                                            act.runnerType,
                                                                            act.state
                                                                        )
                                                                    )
                                                                }
                                                            }
                                                        )
                                                    )
                                                }
                                        }
                                    )

                                    val pitchType = playData.pitchType ?: PitchType.NO_ENTRY
                                    val pitchLocation =
                                        if (playData.pitchLocationX == null || playData.pitchLocationY == null) null
                                        else PitchLocation(
                                            playData.pitchLocationX!!, playData.pitchLocationY!!
                                        )
                                    val battingOption = playData.battingOption ?: BattingOption.NONE
                                    val withRunnerStarting = playData.withRunnerStarting ?: false
                                    val settled = playData.settled ?: false
                                    val previousSituation = game.boxScore.getSituation(null)
                                    val previousHomeLineup =
                                        game.boxScore.getLineup(TeamClass.HOME, null)
                                    val previousVisitorLineup =
                                        game.boxScore.getLineup(TeamClass.VISITOR, null)

                                    when (PlayData.getPlayType(playData)) {
                                        PlayData.Companion.PlayDataType.BALL -> {
                                            Ball(
                                                pitchType,
                                                playData.pitchSpeed,
                                                pitchLocation,
                                                battingOption,
                                                withRunnerStarting,
                                                settled,
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration,
                                                playData.battingResult
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.STRIKE -> {
                                            Strike(
                                                pitchType,
                                                playData.pitchSpeed,
                                                pitchLocation,
                                                battingOption,
                                                withRunnerStarting,
                                                settled,
                                                playData.strikeType!!,
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration,
                                                playData.battingResult
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.FOUL -> {
                                            var errorPosition: FieldPosition? = null
                                            for (play in fieldActiveDuration.fieldPlayList) {
                                                for (act in play.fieldersActionList) {
                                                    if (act.record == FieldingRecord.ERROR_CATCHING || act.record == FieldingRecord.ERROR_THROWING) {
                                                        errorPosition = act.position
                                                        break
                                                    }
                                                }
                                                if (errorPosition != null) break
                                            }

                                            Foul(
                                                pitchType,
                                                playData.pitchSpeed,
                                                pitchLocation,
                                                battingOption,
                                                withRunnerStarting,
                                                playData.foulBallDirection
                                                    ?: FoulBallDirection.NO_ENTRY,
                                                playData.foulBallIsAtLine,
                                                playData.battedBallType ?: BattedBallType.NO_ENTRY,
                                                playData.battedBallStrength
                                                    ?: BattedBallStrength.NO_ENTRY,
                                                errorPosition,
                                                playData.battingResult == BattingResult.STRIKEOUT,
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.BATTING -> {
                                            val battedBallDirection = BattedBallDirection(
                                                ArrayList<AngularCoordinate>().apply {
                                                    battedBallDirectionDataList.filter { n ->
                                                        n.isChildOf(playData)
                                                    }.forEach { dir ->
                                                        this.add(
                                                            AngularCoordinate(
                                                                dir.angle,
                                                                dir.distance
                                                            )
                                                        )
                                                    }
                                                }
                                            )

                                            Batting(
                                                pitchType,
                                                playData.pitchSpeed,
                                                pitchLocation,
                                                battingOption,
                                                withRunnerStarting,
                                                BattedBall(
                                                    battedBallDirection,
                                                    playData.battedBallType
                                                        ?: BattedBallType.NO_ENTRY,
                                                    playData.battedBallStrength
                                                        ?: BattedBallStrength.NO_ENTRY,
                                                    playData.battedBallDistance,
                                                    playData.battingOption == BattingOption.BUNT
                                                ),
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration,
                                                playData.battingResult ?: BattingResult.NO_ENTRY
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.HIT_BY_PITCH -> {
                                            HitByPitch(
                                                pitchType,
                                                playData.pitchSpeed,
                                                pitchLocation,
                                                battingOption,
                                                withRunnerStarting,
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.PLAY_IN_INTERVAL -> {
                                            PlayInInterval(
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.NO_PITCH_INTENTIONAL_WALK -> {
                                            NoPitchIntentionalWalk(
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration
                                            )
                                        }

                                        PlayData.Companion.PlayDataType.BALK -> {
                                            Balk(
                                                previousHomeLineup,
                                                previousVisitorLineup,
                                                previousSituation,
                                                fieldActiveDuration
                                            )
                                        }
                                    }
                                }
                            }

                            game.boxScore.addNewPeriod(period)
                        }
                }
        }

        when (gameData.gameStatus) {
            GameStatus.GAME_SET -> game.boxScore.finishGameAsCompleted()
            GameStatus.CALLED -> game.boxScore.finishGameAsCalled()
            GameStatus.SUSPENDED -> game.boxScore.suspendGame()
            else -> {
            }
        }

        Log.d("AAA", "*** INNING ***")
        inningDataList.forEach {
            Log.d("AAA", "GameID: ${it.gameId}, Seq: ${it.seqNumber}")
        }

        Log.d("AAA", "*** PA ***")
        plateAppearanceDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, Seq: ${it.seqNumber}"
            )
        }

        Log.d("AAA", "*** PERIOD ***")
        periodDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "Seq: ${it.seqNumber}, Type: ${it.periodType}"
            )
        }

        Log.d("AAA", "*** SUBS ***")
        substitutionDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}"
            )
        }

        Log.d("AAA", "*** PLAYER CHG ***")
        playerChangingDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, Order: ${it.battingOrder}, Name: ${it.playerFamilyName}"
            )
        }

        Log.d("AAA", "*** POS CHG ***")
        positionChangingDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, Order: ${it.battingOrder}, Pos: ${it.position.singleCharacter}"
            )
        }

        Log.d("AAA", "*** PLAY ***")
        playDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, Type: ${it.playType}"
            )
        }

        Log.d("AAA", "*** BATTED BALL ***")
        battedBallDirectionDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, Seq: ${it.seqNumber}, Angle: ${it.angle}, Distance: ${it.distance}"
            )
        }

        Log.d("AAA", "*** FIELD PLAY ***")
        fieldPlayDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, Seq: ${it.seqNumber}, Factor: ${it.factor}"
            )
        }

        Log.d("AAA", "*** FIELDERS ACT ***")
        fieldersActionDataList.forEach {
            Log.d(
                "AAA",
                "GameID: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, PlaySeq: ${it.fieldPlaySeqNumber}, Seq: ${it.seqNumber}, " +
                        "Pos: ${it.position}, Rec: ${it.record}"
            )
        }

        Log.d("AAA", "*** RUNNERS ACT ***")
        runnersActionDataList.forEach {
            Log.d(
                "AAA",
                "GameId: ${it.gameId}, InnSeq: ${it.inningSeqNumber}, PASeq: ${it.plateAppearanceSeqNumber}, " +
                        "PeriodSeq: ${it.periodSeqNumber}, PlaySeq: ${it.fieldPlaySeqNumber}, Runner: ${it.runnerType}, " +
                        "Result: ${it.state}"
            )
        }

        return game
    }
}