package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import mahoroba.uruhashi.domain.game.BattingResult.*
import mahoroba.uruhashi.domain.game.FieldPosition.*
import mahoroba.uruhashi.domain.game.TeamClass
import mahoroba.uruhashi.domain.game.TopOrBottom
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea
import mahoroba.uruhashi.usecase.scoreKeeping.GamePlayerBattingStatsDto
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase

class ScoreBoardView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private companion object {
        val backgroundColor = Color.argb(255, 0, 87, 75)
        val whitishColor = Color.argb(255, 236, 236, 236)
        val darkColor = Color.argb(255, 0, 43, 37)
    }

    private val backgroundPaint = Paint()
    private val workPaint = Paint()

    private val battingOrderBgPath = Path()
    private val summaryCaptionPath = Path()
    private val summaryValuePath = Path()

    var gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo? = null
    var gameState: ScoreKeepingUseCase.GameStateDto? = null

    var boxScore: ScoreKeepingUseCase.BoxScoreDto? = null
        set(value) {
            field = value

            runsVisitor.clear()
            runsHome.clear()
            hitsVisitor.clear()
            hitsHome.clear()
            walksVisitor = 0
            walksHome = 0
            hitsByPitchVisitor = 0
            hitsByPitchHome = 0
            strikeoutsVisitor = 0
            strikeoutsHome = 0
            errorVisitor = 0
            errorHome = 0
            leftOnBasesVisitor = 0
            leftOnBasesHome = 0

            value?.innings?.let { innings ->
                innings.forEachIndexed { index, inningDto ->
                    (if (index % 2 == 0) runsVisitor else runsHome).add(inningDto.runs)
                    (if (index % 2 == 0) hitsVisitor else hitsHome).add(inningDto.hits)
                    if (index % 2 == 0) {
                        walksVisitor += inningDto.walks
                        hitsByPitchVisitor += inningDto.hitsByPitch
                        strikeoutsVisitor += inningDto.strikeouts
                        leftOnBasesVisitor += inningDto.leftOnBases
                        errorHome += inningDto.errors
                    } else {
                        walksHome += inningDto.walks
                        hitsByPitchHome += inningDto.hitsByPitch
                        strikeoutsHome += inningDto.strikeouts
                        leftOnBasesHome += inningDto.leftOnBases
                        errorVisitor += inningDto.errors
                    }
                }
            }

            invalidate()
        }

    var battingStats: GamePlayerBattingStatsDto? = null

    private val runsVisitor = ArrayList<Int>()
    private val runsHome = ArrayList<Int>()
    private val hitsVisitor = ArrayList<Int>()
    private val hitsHome = ArrayList<Int>()

    private var walksVisitor: Int = 0
    private var walksHome: Int = 0
    private var hitsByPitchVisitor: Int = 0
    private var hitsByPitchHome: Int = 0
    private var strikeoutsVisitor: Int = 0
    private var strikeoutsHome: Int = 0
    private var errorVisitor: Int = 0
    private var errorHome: Int = 0
    private var leftOnBasesVisitor: Int = 0
    private var leftOnBasesHome: Int = 0

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = backgroundColor

        workPaint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width * 1f, height * 1f, backgroundPaint)

        workPaint.style = Paint.Style.STROKE
        workPaint.color = Color.WHITE

        val drawBox =
            fun(
                left: Float, top: Float, right: Float, bottom: Float,
                rx: Float, ry: Float, padding: Float,
                backgroundColor: Int, foregroundColor: Int, text: String?
            ) {
                workPaint.style = Paint.Style.FILL
                workPaint.color = backgroundColor
                canvas.drawRoundRect(left, top, right, bottom, rx, ry, workPaint)

                workPaint.style = Paint.Style.FILL
                workPaint.color = foregroundColor
                canvas.drawTextWithinArea(text, left, right, (top + bottom) / 2, padding, workPaint)
            }

        // region ### Draw running table ###

        workPaint.textSize = height * 0.0300f
        drawBox(
            width * 0.005f, height * 0.010f, width * 0.2f, height * 0.0550f,
            width * 0.03f, width * 0.03f, height * 0.005f,
            whitishColor, Color.BLACK, gameBaseInfo?.visitorTeamAbbreviatedName
        )
        drawBox(
            width * 0.005f, height * 0.070f, width * 0.2f, height * 0.1150f,
            width * 0.03f, width * 0.03f, height * 0.005f,
            whitishColor, Color.BLACK, gameBaseInfo?.homeTeamAbbreviatedName
        )

        // Draw background of inning caption
        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            width * 0.2f, height * 0.0550f, width * 0.995f, height * 0.070f,
            width * 0.05f, width * 0.05f, workPaint
        )

        // Draw background of runs and hits
        workPaint.color = darkColor
        canvas.drawRoundRect(
            width * 0.2f, height * 0.005f, width * 0.995f, height * 0.020f,
            width * 0.03f, width * 0.03f, workPaint
        )
        canvas.drawRoundRect(
            width * 0.2f, height * 0.020f, width * 0.995f, height * 0.055f,
            width * 0.03f, width * 0.03f, workPaint
        )
        canvas.drawRoundRect(
            width * 0.2f, height * 0.070f, width * 0.995f, height * 0.105f,
            width * 0.03f, width * 0.03f, workPaint
        )
        canvas.drawRoundRect(
            width * 0.2f, height * 0.105f, width * 0.995f, height * 0.120f,
            width * 0.03f, width * 0.03f, workPaint
        )

        workPaint.style = Paint.Style.STROKE
        workPaint.color = Color.WHITE

        val innWidth = width * 0.8f / 14f
        for (i in 0..11) {
            val left = width * 0.2f + innWidth * i

            workPaint.textSize = height * 0.0125f
            drawBox(
                left, height * 0.0050f, left + innWidth, height * 0.0200f,
                width * 0.01f, height * 0.01f, height * 0.001f,
                Color.TRANSPARENT, Color.WHITE,
                if (i < hitsVisitor.size) hitsVisitor[i].toString() else ""
            )

            workPaint.textSize = height * 0.0225f
            drawBox(
                left, height * 0.0200f, left + innWidth, height * 0.0550f,
                width * 0.01f, height * 0.01f, height * 0.001f,
                Color.TRANSPARENT, Color.WHITE,
                if (i < runsVisitor.size) runsVisitor[i].toString() else ""
            )

            workPaint.textSize = height * 0.0125f
            drawBox(
                left, height * 0.0550f, left + innWidth, height * 0.0700f,
                width * 0.01f, height * 0.01f, height * 0.001f,
                Color.TRANSPARENT, Color.BLACK, (i + 1).toString()
            )

            workPaint.textSize = height * 0.0225f
            drawBox(
                left, height * 0.0700f, left + innWidth, height * 0.1050f,
                width * 0.01f, height * 0.01f, height * 0.001f,
                Color.TRANSPARENT, Color.WHITE,
                if (i < runsHome.size) runsHome[i].toString() else ""
            )

            workPaint.textSize = height * 0.0125f
            drawBox(
                left, height * 0.1050f, left + innWidth, height * 0.1200f,
                width * 0.01f, height * 0.01f, height * 0.001f,
                Color.TRANSPARENT, Color.WHITE,
                if (i < hitsHome.size) hitsHome[i].toString() else ""
            )
        }

        workPaint.textSize = height * 0.0125f
        drawBox(
            width * 0.995f - (innWidth * 2), height * 0.005f, width * 0.995f, height * 0.020f,
            0f, 0f, 0f,
            Color.TRANSPARENT, Color.WHITE,
            hitsVisitor.sum().toString()
        )
        workPaint.textSize = height * 0.0225f
        drawBox(
            width * 0.995f - (innWidth * 2), height * 0.020f, width * 0.995f, height * 0.055f,
            0f, 0f, 0f,
            Color.TRANSPARENT, Color.WHITE,
            runsVisitor.sum().toString()
        )
        workPaint.textSize = height * 0.0225f
        drawBox(
            width * 0.995f - (innWidth * 2), height * 0.070f, width * 0.995f, height * 0.105f,
            0f, 0f, 0f,
            Color.TRANSPARENT, Color.WHITE,
            runsHome.sum().toString()
        )
        workPaint.textSize = height * 0.0125f
        drawBox(
            width * 0.995f - (innWidth * 2), height * 0.105f, width * 0.995f, height * 0.120f,
            0f, 0f, 0f,
            Color.TRANSPARENT, Color.WHITE,
            hitsHome.sum().toString()
        )

        // endregion ### Draw running table ###

        // region ### Draw batting order ###

        workPaint.textSize = height * 0.025f
        drawBox(
            width * 0.005f, height * 0.125f, width * 0.475f, height * 0.165f,
            width * 0.01f, height * 0.01f, 4f,
            Color.WHITE, Color.BLACK, gameBaseInfo?.visitorTeamName
        )
        drawBox(
            width * 0.525f, height * 0.125f, width * 0.995f, height * 0.165f,
            width * 0.01f, height * 0.01f, 4f,
            Color.WHITE, Color.BLACK, gameBaseInfo?.homeTeamName
        )

        for (i in 0..9) {
            val top = height * 0.165f + height * 0.07f * i

            battingOrderBgPath.let {
                it.rewind()
                it.moveTo(width * 0.005f, top + height * 0.005f)
                it.arcTo(
                    width * 0.470f, top + height * 0.005f,
                    width * 0.475f, top + height * 0.010f,
                    270f, 90f, false
                )
                it.arcTo(
                    width * 0.475f, top + height * 0.005f,
                    width * 0.480f, top + height * 0.010f,
                    180f, -90f, false
                )
                it.arcTo(
                    width * 0.520f, top + height * 0.005f,
                    width * 0.525f, top + height * 0.010f,
                    90f, -90f, false
                )
                it.arcTo(
                    width * 0.525f, top + height * 0.005f,
                    width * 0.530f, top + height * 0.010f,
                    180f, 90f, false
                )
                it.arcTo(
                    width * 0.985f, top + height * 0.005f,
                    width * 0.995f, top + height * 0.015f,
                    270f, 90f, false
                )
                it.arcTo(
                    width * 0.985f, top + height * 0.030f,
                    width * 0.995f, top + height * 0.040f,
                    0f, 90f, false
                )
                if (i < 9) {
                    it.arcTo(
                        width * 0.525f, top + height * 0.040f,
                        width * 0.535f, top + height * 0.050f,
                        270f, -90f, false
                    )
                    it.arcTo(
                        width * 0.515f, top + height * 0.060f,
                        width * 0.525f, top + height * 0.070f,
                        0f, 90f, false
                    )
                    it.arcTo(
                        width * 0.475f, top + height * 0.060f,
                        width * 0.485f, top + height * 0.070f,
                        90f, 90f, false
                    )
                    it.arcTo(
                        width * 0.465f, top + height * 0.040f,
                        width * 0.475f, top + height * 0.050f,
                        0f, -90f, false
                    )
                }
                it.arcTo(
                    width * 0.005f, top + height * 0.030f,
                    width * 0.015f, top + height * 0.040f,
                    90f, 90f, false
                )
                it.arcTo(
                    width * 0.005f, top + height * 0.005f,
                    width * 0.015f, top + height * 0.015f,
                    180f, 90f, false
                )
                it.close()
            }

            workPaint.color = whitishColor
            workPaint.style = Paint.Style.FILL
            canvas.drawPath(battingOrderBgPath, workPaint)

            workPaint.textSize = height * 0.028f
            drawBox(
                width * 0.475f,
                top + height * 0.01f,
                width * 0.525f,
                top + height * if (i < 9) 0.07f else 0.04f,
                10f, 10f, 2f,
                Color.TRANSPARENT, backgroundColor,
                if (i < 9) (i + 1).toString() else "P"
            )

            if (i < 9 || gameState?.visitorTeamLineup?.hasDH == true) {
                workPaint.textSize = height * 0.020f
                drawBox(
                    width * 0.01f,
                    top + height * 0.010f,
                    width * 0.01f + height * 0.03f,
                    top + height * 0.035f,
                    2f, 2f, 2f,
                    Color.argb(255, 0, 64, 16),
                    Color.WHITE,
                    gameState?.visitorTeamLineup?.getPosition(i)?.abbreviated
                )
                workPaint.textSize = height * 0.025f
                drawBox(
                    width * 0.100f,
                    top + height * 0.005f,
                    width * 0.425f,
                    top + height * 0.04f,
                    4f, 4f, 4f,
                    Color.TRANSPARENT, Color.BLACK,
                    gameState?.visitorTeamLineup?.getPlayer(i)?.playerName?.fullName
                )
            }

            if (i < 9 || gameState?.homeTeamLineup?.hasDH == true) {
                workPaint.textSize = height * 0.020f
                drawBox(
                    width * 0.99f - height * 0.03f,
                    top + height * 0.010f,
                    width * 0.99f,
                    top + height * 0.035f,
                    2f, 2f, 2f,
                    Color.argb(255, 0, 64, 16), Color.WHITE,
                    gameState?.homeTeamLineup?.getPosition(i)?.abbreviated
                )
                workPaint.textSize = height * 0.025f
                drawBox(
                    width * 0.575f,
                    top + height * 0.005f,
                    width * 0.900f,
                    top + height * 0.04f,
                    4f,
                    4f,
                    4f,
                    Color.TRANSPARENT, Color.BLACK,
                    gameState?.homeTeamLineup?.getPlayer(i)?.playerName?.fullName
                )
            }

            val writeState = fun(text: String, bgColor: Int) {
                workPaint.textSize = height * 0.02f
                drawBox(
                    if (gameState?.situation?.topOrBottom == TopOrBottom.TOP) width * 0.425f else width * 0.525f,
                    top + height * 0.010f,
                    if (gameState?.situation?.topOrBottom == TopOrBottom.TOP) width * 0.475f else width * 0.575f,
                    top + height * 0.035f,
                    width * 0.1f,
                    width * 0.1f,
                    4f,
                    bgColor, Color.WHITE,
                    text
                )
            }
            if (i == gameState?.situation?.orderOfBatter) {
                writeState("B", Color.RED)
            }
            if (i == gameState?.situation?.orderOf1R) {
                writeState("1", Color.argb(255, 255, 128, 0))
            }
            if (i == gameState?.situation?.orderOf2R) {
                writeState("2", Color.argb(255, 255, 128, 0))
            }
            if (i == gameState?.situation?.orderOf3R) {
                writeState("3", Color.argb(255, 255, 128, 0))
            }

            val resultWidth = width * 0.470f / 6
            if (i < 9) {
                workPaint.color = darkColor
                workPaint.style = Paint.Style.FILL
                canvas.drawRoundRect(
                    width * 0.005f,
                    top + height * 0.045f,
                    width * 0.475f,
                    top + height * 0.07f,
                    10f,
                    10f,
                    workPaint
                )
                canvas.drawRoundRect(
                    width * 0.525f,
                    top + height * 0.045f,
                    width * 0.995f,
                    top + height * 0.07f,
                    10f,
                    10f,
                    workPaint
                )

                workPaint.textSize = height * 0.015f
                workPaint.color = Color.WHITE
                gameState?.visitorTeamLineup?.getPlayer(i)?.let { player ->
                    battingStats?.getBattingResultsOf(player)?.forEachIndexed { idx, stat ->
                        canvas.drawTextWithinArea(
                            getBattingResultString(stat),
                            width * 0.005f + resultWidth * idx,
                            width * 0.005f + resultWidth * (idx + 1),
                            top + height * 0.0575f,
                            1f,
                            workPaint
                        )
                    }
                }
                gameState?.homeTeamLineup?.getPlayer(i)?.let { player ->
                    battingStats?.getBattingResultsOf(player)?.forEachIndexed { idx, stat ->
                        canvas.drawTextWithinArea(
                            getBattingResultString(stat),
                            width * 0.525f + resultWidth * idx,
                            width * 0.525f + resultWidth * (idx + 1),
                            top + height * 0.0575f,
                            1f,
                            workPaint
                        )
                    }
                }
            }
        }

        // endregion ### Draw batting order ###

        // region ### Draw summary ###

        val drawSummary = fun(teamClass: TeamClass) {
            canvas.save()
            if (teamClass == TeamClass.HOME) canvas.translate(width * 0.520f, 0f)

            workPaint.color = whitishColor
            workPaint.style = Paint.Style.FILL
            summaryCaptionPath.let {
                it.rewind()
                it.moveTo(width * 0.025f, height * 0.845f)
                it.arcTo(
                    width * 0.455f, height * 0.845f, width * 0.475f, height * 0.865f,
                    270f, 90f, false
                )
                it.lineTo(width * 0.475f, height * 0.875f)
                it.lineTo(width * 0.005f, height * 0.875f)
                it.arcTo(
                    width * 0.005f, height * 0.845f, width * 0.025f, height * 0.865f,
                    180f, 90f, false
                )
                it.close()
            }
            canvas.drawPath(summaryCaptionPath, workPaint)

            workPaint.color = darkColor
            workPaint.style = Paint.Style.FILL
            summaryValuePath.let {
                it.rewind()
                it.moveTo(width * 0.005f, height * 0.865f)
                it.lineTo(width * 0.475f, height * 0.865f)
                it.arcTo(
                    width * 0.455f, height * 0.895f, width * 0.475f, height * 0.905f,
                    0f, 90f, false
                )
                it.arcTo(
                    width * 0.005f, height * 0.895f, width * 0.025f, height * 0.905f,
                    90f, 90f, false
                )
                it.lineTo(width * 0.005f, height * 0.865f)
                it.close()
            }
            canvas.drawPath(summaryValuePath, workPaint)

            workPaint.color = backgroundColor
            workPaint.style = Paint.Style.STROKE
            workPaint.strokeWidth = width * 0.002f
            canvas.drawLine(
                width * 0.1225f, height * 0.845f, width * 0.1225f, height * 0.905f, workPaint
            )
            canvas.drawLine(
                width * 0.240f, height * 0.845f, width * 0.240f, height * 0.905f, workPaint
            )
            canvas.drawLine(
                width * 0.3575f, height * 0.845f, width * 0.3575f, height * 0.905f, workPaint
            )

            workPaint.textSize = height * 0.015f
            drawBox(
                width * 0.005f, height * 0.845f, width * 0.1225f, height * 0.865f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, backgroundColor,
                "四死球"
            )
            drawBox(
                width * 0.1225f, height * 0.845f, width * 0.240f, height * 0.865f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, backgroundColor,
                "三振"
            )
            drawBox(
                width * 0.240f, height * 0.845f, width * 0.3575f, height * 0.865f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, backgroundColor,
                "失策"
            )
            drawBox(
                width * 0.3575f, height * 0.845f, width * 0.475f, height * 0.865f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, backgroundColor,
                "残塁"
            )

            workPaint.textSize = height * 0.020f
            drawBox(
                width * 0.005f, height * 0.865f, width * 0.1225f, height * 0.905f,
                0f, 0f, width * 0.01f,
               Color.TRANSPARENT, Color.WHITE,
                when (teamClass) {
                    TeamClass.HOME -> (walksHome + hitsByPitchHome).toString()
                    TeamClass.VISITOR -> (walksVisitor + hitsByPitchVisitor).toString()
                }
            )
            drawBox(
                width * 0.1225f, height * 0.865f, width * 0.240f, height * 0.905f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, Color.WHITE,
                when (teamClass) {
                    TeamClass.HOME -> strikeoutsHome.toString()
                    TeamClass.VISITOR -> strikeoutsVisitor.toString()
                }
            )
            drawBox(
                width * 0.240f, height * 0.865f, width * 0.3575f, height * 0.905f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, Color.WHITE,
                when (teamClass) {
                    TeamClass.HOME -> errorHome.toString()
                    TeamClass.VISITOR -> errorVisitor.toString()
                }
            )
            drawBox(
                width * 0.3575f, height * 0.865f, width * 0.475f, height * 0.905f,
                0f, 0f, width * 0.01f,
                Color.TRANSPARENT, Color.WHITE,
                when (teamClass) {
                    TeamClass.HOME -> leftOnBasesHome.toString()
                    TeamClass.VISITOR -> leftOnBasesVisitor.toString()
                }
            )

            canvas.restore()
        }

        drawSummary(TeamClass.VISITOR)
        drawSummary(TeamClass.HOME)

        // endregion ### Draw summary ###
    }

    private fun getBattingResultString(stat: GamePlayerBattingStatsDto.BattingStat): String {
        // TODO: make resource
        val pos = when (stat.fieldersPosition) {
            PITCHER -> "投"
            CATCHER -> "捕"
            FIRST_BASEMAN -> "一"
            SECOND_BASEMAN -> "二"
            THIRD_BASEMAN -> "三"
            SHORT_STOP -> "遊"
            LEFT_FIELDER -> "左"
            CENTER_FIELDER -> "中"
            RIGHT_FIELDER -> "右"
            null -> ""
        }

        return when (stat.result) {
            SINGLE -> "安打"
            DOUBLE -> "二塁打"
            TRIPLE -> "三塁打"
            HOME_RUN -> "本塁打"
            GROUND_OUT -> pos + "ゴ"
            LINE_OUT -> pos + "直"
            FLY_OUT -> pos + "飛"
            FOUL_LINE_OUT -> pos + "邪直"
            FOUL_FLY_OUT -> pos + "邪飛"
            STRIKEOUT -> "三振"
            SACRIFICE_FLY -> pos + "犠飛"
            SACRIFICE_HIT -> pos + "犠"
            WALK -> "四球"
            INTENTIONAL_WALK -> "敬遠"
            HIT_BY_PITCH -> "死球"
            INTERFERE -> "守備妨害"
            OBSTRUCTION -> "走塁妨害"
            INTERRUPT -> "打撃妨害"
            NO_ENTRY -> ""
        }
    }
}