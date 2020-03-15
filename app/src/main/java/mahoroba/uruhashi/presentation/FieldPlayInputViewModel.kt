package mahoroba.uruhashi.presentation

import android.app.Application
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Transformations
import android.view.View
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.RunnerState.*
import mahoroba.uruhashi.presentation.base.BaseViewModel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class FieldPlayInputViewModel(
    application: Application,
    private val playInputSuite: PlayInputSuite
) :
    BaseViewModel(application) {

    // region * Child classes *

    data class RunnerMovement(val basePlayed: Int, val putout: Boolean)

    class FieldersPlay(val position: FieldPosition) {
        var fieldingRecord: FieldingRecord? = null

        override fun toString(): String {
            return when (fieldingRecord) {
                FieldingRecord.ERROR_CATCHING -> "E" + position.singleCharacter
                FieldingRecord.ERROR_THROWING -> position.singleCharacter + "E"
                FieldingRecord.FIELDERS_CHOICE -> "Fc" + position.singleCharacter
                FieldingRecord.TAGGED_OUT -> position.singleCharacter + "T"
                null -> position.singleCharacter
            }
        }

        fun toFieldersAction(): FieldersAction = FieldersAction(position, fieldingRecord)
    }

    class FieldPlaySet(
        val fieldPlayFactor: FieldPlayFactor,
        fieldersPlayList: List<FieldersPlay>,
        val batterRunnerMovement: RunnerMovement?,
        val firstRunnerMovement: RunnerMovement?,
        val secondRunnerMovement: RunnerMovement?,
        val thirdRunnerMovement: RunnerMovement?
    ) {
        private val mFieldersPlayList =
            ArrayList<FieldersPlay>().apply { this.addAll(fieldersPlayList) }
        val fieldersPlayList: List<FieldersPlay> = mFieldersPlayList

        val fieldPlayFactorText: String
            get() = fieldPlayFactor.toString()

        val fieldPlayListText: String
            get() = StringBuilder().apply {
                fieldersPlayList.joinTo(this, "-")
            }.toString()

        val batterRunnerStatusText: String
            get() = rmToString(batterRunnerMovement)
        val firstRunnerStatusText: String
            get() = rmToString(firstRunnerMovement)
        val secondRunnerStatusText: String
            get() = rmToString(secondRunnerMovement)
        val thirdRunnerStatusText: String
            get() = rmToString(thirdRunnerMovement)

        private fun rmToString(rm: RunnerMovement?) = when {
            rm?.putout == true -> "x"
            rm != null -> rm.basePlayed.toString()
            else -> "-"
        }
    }

    // endregion

    val fieldPlayFactor = MutableLiveData<FieldPlayFactor>()

    private val mFixedFieldPlayFactor = MutableLiveData<FieldPlayFactor?>()
    val fixedFieldPlayFactor: LiveData<FieldPlayFactor?> = mFixedFieldPlayFactor

    private val ballRelayList = ArrayList<FieldersPlay>()

    private val mBallRelay = MutableLiveData<String>()
    val ballRelay: LiveData<String> = mBallRelay

    private val mFieldPlaySetList = MutableLiveData<ArrayList<FieldPlaySet>>()
    val fieldPlaySetList: LiveData<List<FieldPlaySet>> =
        Transformations.map(mFieldPlaySetList) { it as List<FieldPlaySet> }

    fun getFieldPlayDtoList(): List<ScoreKeepingUseCase.FieldPlayDto> {
        val dtoList = ArrayList<ScoreKeepingUseCase.FieldPlayDto>()

        mFieldPlaySetList.value?.forEach { playSet ->
            val fieldersActions = ArrayList<FieldersAction>().apply {
                playSet.fieldersPlayList.forEach { this.add(it.toFieldersAction()) }
            }

            fun toRunnerState(base: Int, rm: RunnerMovement?): RunnerState? {
                return when {
                    rm == null -> null
                    rm.putout -> if (rm.basePlayed == base) OUT_IN_RETURNING else OUT_IN_PROGRESSING
                    rm.basePlayed == 1 -> ON_FIRST_BASE
                    rm.basePlayed == 2 -> ON_SECOND_BASE
                    rm.basePlayed == 3 -> ON_THIRD_BASE
                    rm.basePlayed == 4 -> HOME_IN
                    else -> null
                }
            }

            dtoList.add(
                ScoreKeepingUseCase.FieldPlayDto(
                    playSet.fieldPlayFactor,
                    fieldersActions,
                    toRunnerState(0, playSet.batterRunnerMovement),
                    toRunnerState(1, playSet.firstRunnerMovement),
                    toRunnerState(2, playSet.secondRunnerMovement),
                    toRunnerState(3, playSet.thirdRunnerMovement)
                )
            )
        }

        return dtoList
    }

    val isBRActive = playInputSuite.isBatterRunnerActive
    val is1ROnBase = Transformations.map(playInputSuite.gameState) {
        it.situation.orderOf1R != null
    }!!
    val is2ROnBase = Transformations.map(playInputSuite.gameState) {
        it.situation.orderOf2R != null
    }!!
    val is3ROnBase = Transformations.map(playInputSuite.gameState) {
        it.situation.orderOf3R != null
    }!!

    val batterRunnerMovement = MutableLiveData<RunnerMovement>()
    val firstRunnerMovement = MutableLiveData<RunnerMovement>()
    val secondRunnerMovement = MutableLiveData<RunnerMovement>()
    val thirdRunnerMovement = MutableLiveData<RunnerMovement>()

    private val mBatterRunnerLatestStatus = MutableLiveData<RunnerMovement>()
    val batterRunnerLatestStatus: LiveData<RunnerMovement> = mBatterRunnerLatestStatus
    private val mFirstRunnerLatestStatus = MutableLiveData<RunnerMovement>()
    val firstRunnerLatestStatus: LiveData<RunnerMovement> = mFirstRunnerLatestStatus
    private val mSecondRunnerLatestStatus = MutableLiveData<RunnerMovement>()
    val secondRunnerLatestStatus: LiveData<RunnerMovement> = mSecondRunnerLatestStatus
    private val mThirdRunnerLatestStatus = MutableLiveData<RunnerMovement>()
    val thirdRunnerLatestStatus: LiveData<RunnerMovement> = mThirdRunnerLatestStatus

    private val mInputIsValid = MutableLiveData<Boolean>()
    val inputIsValid: LiveData<Boolean> = mInputIsValid


    init {
        mFieldPlaySetList.value = ArrayList()
        mInputIsValid.value = false
        setDefaultState()
        updateAll()
    }

    fun onFielderSelected(it: FieldPosition) {
        ballRelayList.add(FieldersPlay(it))
        updateAll()
    }

    fun onErrorThrowingClicked(v: View) {
        setLastPlayFieldingRecord(FieldingRecord.ERROR_THROWING)
        updateAll()
    }

    fun onErrorCatchingClicked(v: View) {
        setLastPlayFieldingRecord(FieldingRecord.ERROR_CATCHING)
        updateAll()
    }

    fun onFieldersChoiceClicked(v: View) {
        setLastPlayFieldingRecord(FieldingRecord.FIELDERS_CHOICE)
        updateAll()
    }

    fun onTaggedOutClicked(v: View) {
        setLastPlayFieldingRecord(FieldingRecord.TAGGED_OUT)
        updateAll()
    }

    private fun setLastPlayFieldingRecord(record: FieldingRecord) {
        val last = ballRelayList.last()
        ballRelayList.forEach {
            it.fieldingRecord = when {
                it != last -> null
                it.fieldingRecord == record -> null
                else -> record
            }
        }
    }

    fun onBackSpaceClicked(v: View) {
        if (ballRelayList.count() > 0) ballRelayList.removeAt(ballRelayList.count() - 1)
        updateAll()
    }

    fun onClearClicked(v: View) {
        ballRelayList.clear()
        updateAll()
    }

    fun onAddPlayClicked(v: View) {
        pushPlay()

        fieldPlayFactor.value = FieldPlayFactor.OTHER
        ballRelayList.clear()

        batterRunnerMovement.value = null
        firstRunnerMovement.value = null
        secondRunnerMovement.value = null
        thirdRunnerMovement.value = null

        updateAll()
    }

    fun onBackOneHistoryClicked(v: View) {
        val list = mFieldPlaySetList.value ?: return

        val lastPlay = if (list.isNotEmpty()) list.last() else return
        list.remove(lastPlay)
        mFieldPlaySetList.value = list

        fieldPlayFactor.value = lastPlay.fieldPlayFactor
        ballRelayList.let {
            it.clear()
            it.addAll(lastPlay.fieldersPlayList)
        }

        batterRunnerMovement.value = lastPlay.batterRunnerMovement
        firstRunnerMovement.value = lastPlay.firstRunnerMovement
        secondRunnerMovement.value = lastPlay.secondRunnerMovement
        thirdRunnerMovement.value = lastPlay.thirdRunnerMovement

        (if (list.isNotEmpty()) list.last() else null).let {
            mBatterRunnerLatestStatus.value = it?.batterRunnerMovement
            mFirstRunnerLatestStatus.value = it?.firstRunnerMovement
            mSecondRunnerLatestStatus.value = it?.secondRunnerMovement
            mThirdRunnerLatestStatus.value = it?.thirdRunnerMovement
        }

        updateAll()
    }

    fun onBackButtonClicked(v: View) {
        playInputSuite.back()
    }

    fun onCommitButtonClicked(v: View) {
        pushPlay()
        playInputSuite.settleFieldPlayInput()
    }

    private fun pushPlay() {
        if (ballRelayList.isEmpty()
            && batterRunnerMovement.value == null
            && firstRunnerMovement.value == null
            && secondRunnerMovement.value == null
            && thirdRunnerMovement.value == null
        ) return

        mFieldPlaySetList.value?.add(
            FieldPlaySet(
                fieldPlayFactor.value ?: FieldPlayFactor.OTHER,
                ballRelayList,
                batterRunnerMovement.value,
                firstRunnerMovement.value,
                secondRunnerMovement.value,
                thirdRunnerMovement.value
            )
        )
        mFieldPlaySetList.value = mFieldPlaySetList.value

        batterRunnerMovement.value?.let { mBatterRunnerLatestStatus.value = it }
        firstRunnerMovement.value?.let { mFirstRunnerLatestStatus.value = it }
        secondRunnerMovement.value?.let { mSecondRunnerLatestStatus.value = it }
        thirdRunnerMovement.value?.let { mThirdRunnerLatestStatus.value = it }
    }

    val onBatterRunnerMovementChanged: (RunnerMovement?) -> Unit = {
        if (it?.putout == true) {
            if (is1ROnBase.value == true) makeRunnerMovementSafe(firstRunnerMovement)
            if (is2ROnBase.value == true) makeRunnerMovementSafe(secondRunnerMovement)
            if (is3ROnBase.value == true) makeRunnerMovementSafe(thirdRunnerMovement)
        }
        updateAll()
    }

    val onFirstRunnerMovementChanged: (RunnerMovement?) -> Unit = {
        if (it?.putout == true) {
            if (isBRActive) makeRunnerMovementSafe(batterRunnerMovement)
            if (is2ROnBase.value == true) makeRunnerMovementSafe(secondRunnerMovement)
            if (is3ROnBase.value == true) makeRunnerMovementSafe(thirdRunnerMovement)
        }
        updateAll()
    }

    val onSecondRunnerMovementChanged: (RunnerMovement?) -> Unit = {
        if (it?.putout == true) {
            if (isBRActive) makeRunnerMovementSafe(batterRunnerMovement)
            if (is1ROnBase.value == true) makeRunnerMovementSafe(firstRunnerMovement)
            if (is3ROnBase.value == true) makeRunnerMovementSafe(thirdRunnerMovement)
        }
        updateAll()
    }

    val onThirdRunnerMovementChanged: (RunnerMovement?) -> Unit = {
        if (it?.putout == true) {
            if (isBRActive) makeRunnerMovementSafe(batterRunnerMovement)
            if (is1ROnBase.value == true) makeRunnerMovementSafe(firstRunnerMovement)
            if (is2ROnBase.value == true) makeRunnerMovementSafe(secondRunnerMovement)
        }
        updateAll()
    }

    private fun makeRunnerMovementSafe(movement: MutableLiveData<RunnerMovement>) {
        if (movement.value?.putout == true) movement.value = null
    }

    private fun setDefaultState() {
        val situation = playInputSuite.gameState.value?.situation

        if (playInputSuite.isFinalizing &&
            listOf(
                PitchInputViewModel.UIPitchResult.STRIKE_SWUNG,
                PitchInputViewModel.UIPitchResult.STRIKE_CALLED
            ).contains(playInputSuite.pitchResult)
        ) {
            batterRunnerMovement.value = RunnerMovement(1, true)
            ballRelayList.add(FieldersPlay(FieldPosition.CATCHER))
        }

        if (playInputSuite.isFinalizing &&
            listOf(
                PitchInputViewModel.UIPitchResult.BALL,
                PitchInputViewModel.UIPitchResult.HIT_BY_PITCH
            ).contains(playInputSuite.pitchResult)
        ) {
            batterRunnerMovement.value = RunnerMovement(1, false)
            if (situation?.is1RForcedToProgress == true)
                firstRunnerMovement.value = RunnerMovement(2, false)
            if (situation?.is2RForcedToProgress == true)
                secondRunnerMovement.value = RunnerMovement(3, false)
            if (situation?.is3RForcedToProgress == true)
                thirdRunnerMovement.value = RunnerMovement(4, false)
        }

        if (playInputSuite.pitchResult == PitchInputViewModel.UIPitchResult.BATTED) {
            when (playInputSuite.battingInputViewModel.result.value) {
                BattingResult.SINGLE -> {
                    batterRunnerMovement.value = RunnerMovement(1, false)
                    if (situation?.orderOf1R != null)
                        firstRunnerMovement.value = RunnerMovement(2, false)
                    if (situation?.orderOf2R != null)
                        secondRunnerMovement.value = RunnerMovement(3, false)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
                BattingResult.DOUBLE -> {
                    batterRunnerMovement.value = RunnerMovement(2, false)
                    if (situation?.orderOf1R != null)
                        firstRunnerMovement.value = RunnerMovement(3, false)
                    if (situation?.orderOf2R != null)
                        secondRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
                BattingResult.TRIPLE -> {
                    batterRunnerMovement.value = RunnerMovement(3, false)
                    if (situation?.orderOf1R != null)
                        firstRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf2R != null)
                        secondRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
                BattingResult.HOME_RUN -> {
                    batterRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf1R != null)
                        firstRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf2R != null)
                        secondRunnerMovement.value = RunnerMovement(4, false)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
                BattingResult.GROUND_OUT,
                BattingResult.LINE_OUT,
                BattingResult.FLY_OUT,
                BattingResult.FOUL_LINE_OUT,
                BattingResult.FOUL_FLY_OUT ->
                    batterRunnerMovement.value = RunnerMovement(1, true)
                BattingResult.SACRIFICE_HIT -> {
                    batterRunnerMovement.value = RunnerMovement(1, true)
                    if (situation?.orderOf1R != null)
                        firstRunnerMovement.value = RunnerMovement(2, false)
                    if (situation?.orderOf2R != null)
                        secondRunnerMovement.value = RunnerMovement(3, false)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
                BattingResult.SACRIFICE_FLY -> {
                    batterRunnerMovement.value = RunnerMovement(1, true)
                    if (situation?.orderOf3R != null)
                        thirdRunnerMovement.value = RunnerMovement(4, false)
                }
            }
        }
    }

    private fun updateAll() {
        mFixedFieldPlayFactor.value = when {
            mFieldPlaySetList.value?.isNotEmpty() == true -> null
            playInputSuite.isFinalizing -> when (playInputSuite.pitchResult) {
                PitchInputViewModel.UIPitchResult.BALL -> FieldPlayFactor.WALK
                PitchInputViewModel.UIPitchResult.HIT_BY_PITCH -> FieldPlayFactor.HIT_BY_PITCH
                PitchInputViewModel.UIPitchResult.BATTED -> FieldPlayFactor.BATTING
                else -> null
            }
            else -> null
        }
        mFixedFieldPlayFactor.value?.let { fieldPlayFactor.value = it }

        mInputIsValid.value =
            batterRunnerMovement.value?.putout != true
                    && firstRunnerMovement.value?.putout != true
                    && secondRunnerMovement.value?.putout != true
                    && thirdRunnerMovement.value?.putout != true
                    ||
                    ballRelayList.isNotEmpty()
                    && !ballRelayList.any { fp -> fp.fieldingRecord?.preventMakingOut == true }

        val builder = StringBuilder()
        ballRelayList.joinTo(builder, "-")
        mBallRelay.value = builder.toString()
    }

    fun reproduceInput(periodDto: ScoreKeepingUseCase.PeriodDto) {

        val fieldPlayList = when (periodDto) {
            is ScoreKeepingUseCase.BallDto -> periodDto.fieldPlays
            is ScoreKeepingUseCase.StrikeDto -> periodDto.fieldPlays
            is ScoreKeepingUseCase.BattingDto -> periodDto.fieldPlays
            is ScoreKeepingUseCase.PlayWithoutPitchDto -> periodDto.fieldPlays
            else -> ArrayList()
        }

        val situation = playInputSuite.gameState.value!!.situation
        var whereBR = if (playInputSuite.isBatterRunnerActive) 0 else null
        var where1R = if (situation.orderOf1R != null) 1 else null
        var where2R = if (situation.orderOf2R != null) 2 else null
        var where3R = if (situation.orderOf3R != null) 3 else null

        val nextBase = fun(previous: Int, state: RunnerState) = when (state) {
            ON_FIRST_BASE -> 1
            ON_SECOND_BASE -> 2
            ON_THIRD_BASE -> 3
            HOME_IN -> 4
            OUT_IN_PROGRESSING -> previous + 1
            OUT_IN_RETURNING -> previous
            else -> previous
        }

        for (i in 0..fieldPlayList.size - 2) {
            val play = fieldPlayList[i]
            val playList = ArrayList<FieldersPlay>().apply {
                play.fieldersActions.forEach {
                    this.add(FieldersPlay(it.position).apply { this.fieldingRecord = it.record })
                }
            }

            if (whereBR != null && play.batterRunnersState != null) {
                whereBR = nextBase(whereBR, play.batterRunnersState!!)
            }
            if (where1R != null && play.firstRunnersState != null) {
                where1R = nextBase(where1R, play.firstRunnersState!!)
            }
            if (where2R != null && play.secondRunnersState != null) {
                where2R = nextBase(where2R, play.secondRunnersState!!)
            }
            if (where3R != null && play.thirdRunnersState != null) {
                where3R = nextBase(where3R, play.thirdRunnersState!!)
            }

            val batterRunnerMovement = if (whereBR != null) RunnerMovement(
                whereBR, play.batterRunnersState?.isOut == true
            ) else null
            val firstRunnerMovement = if (where1R != null) RunnerMovement(
                where1R, play.firstRunnersState?.isOut == true
            ) else null
            val secondRunnerMovement = if (where2R != null) RunnerMovement(
                where2R, play.secondRunnersState?.isOut == true
            ) else null
            val thirdRunnerMovement = if (where3R != null) RunnerMovement(
                where3R, play.thirdRunnersState?.isOut == true
            ) else null

            mFieldPlaySetList.value?.add(
                FieldPlaySet(
                    play.factor, playList,
                    batterRunnerMovement, firstRunnerMovement,
                    secondRunnerMovement, thirdRunnerMovement
                )
            )

            batterRunnerMovement?.let { mBatterRunnerLatestStatus.value = it }
            firstRunnerMovement?.let { mFirstRunnerLatestStatus.value = it }
            secondRunnerMovement?.let { mSecondRunnerLatestStatus.value = it }
            thirdRunnerMovement?.let { mThirdRunnerLatestStatus.value = it }

            play.batterRunnersState?.let { if (it.isOut || it == HOME_IN) whereBR = null }
            play.firstRunnersState?.let { if (it.isOut || it == HOME_IN) where1R = null }
            play.secondRunnersState?.let { if (it.isOut || it == HOME_IN) where2R = null }
            play.thirdRunnersState?.let { if (it.isOut || it == HOME_IN) where3R = null }
        }

        if (fieldPlayList.isNotEmpty()) {
            val play = fieldPlayList.last()

            fieldPlayFactor.value = play.factor

            ballRelayList.clear()
            play.fieldersActions.forEach {
                ballRelayList.add(FieldersPlay(it.position).apply {
                    this.fieldingRecord = it.record
                })
            }

            if (whereBR != null && play.batterRunnersState != null) {
                whereBR = nextBase(whereBR!!, play.batterRunnersState!!)
            }
            if (where1R != null && play.firstRunnersState != null) {
                where1R = nextBase(where1R!!, play.firstRunnersState!!)
            }
            if (where2R != null && play.secondRunnersState != null) {
                where2R = nextBase(where2R!!, play.secondRunnersState!!)
            }
            if (where3R != null && play.thirdRunnersState != null) {
                where3R = nextBase(where3R!!, play.thirdRunnersState!!)
            }

            batterRunnerMovement.value = if (whereBR != null) RunnerMovement(
                whereBR!!, play.batterRunnersState?.isOut == true
            ) else null
            firstRunnerMovement.value = if (where1R != null) RunnerMovement(
                where1R!!, play.firstRunnersState?.isOut == true
            ) else null
            secondRunnerMovement.value = if (where2R != null) RunnerMovement(
                where2R!!, play.secondRunnersState?.isOut == true
            ) else null
            thirdRunnerMovement.value = if (where3R != null) RunnerMovement(
                where3R!!, play.thirdRunnersState?.isOut == true
            ) else null

            updateAll()
        }
    }
}