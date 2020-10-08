package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import mahoroba.uruhashi.domain.game.TopOrBottom.*
import mahoroba.uruhashi.domain.game.secondary.Situation
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import kotlin.math.min

class GameInfoHeaderView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private companion object {
        val backgroundColor = Color.argb(255, 0, 87, 75)
        val whitishColor = Color.argb(255, 236, 236, 236)
        val darkColor = Color.argb(255, 0, 43, 37)
    }

    private val backgroundPaint = Paint()
    private val workPaint = Paint()
    private val basePath = Path()
    private val ballCountCaptionPath = Path()
    private val ballCountValuePath = Path()
    private val teamNamesPath = Path()
    private val runsPath = Path()

    var gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo? = null
    var gameState: ScoreKeepingUseCase.GameStateDto? = null
    private val situation: Situation?
        get() = gameState?.situation

    private val widthOfColumns = arrayOf(0f, 0f, 0f, 0f)
    private val widthOfIntervalsInDp = arrayOf(4f, 4f, 8f, 4f, 4f)

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = backgroundColor

        workPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val widthOfIntervals = context.dpToPx(widthOfIntervalsInDp.sum())

        if (context.dpToPx(56f + 48f + 104f) >= w * 0.6f) {
            widthOfColumns[0] = (w - widthOfIntervals) * 0.16f
            widthOfColumns[1] = (w - widthOfIntervals) * 0.14f
            widthOfColumns[2] = (w - widthOfIntervals) * 0.24f
            widthOfColumns[3] = w - widthOfIntervals - widthOfColumns.slice(0..2).sum()
        } else {
            widthOfColumns[0] = context.dpToPx(56f)
            widthOfColumns[1] = context.dpToPx(48f)
            widthOfColumns[2] = context.dpToPx(104f)
            widthOfColumns[3] = w - widthOfIntervals - widthOfColumns.slice(0..2).sum()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width * 1f, height * 1f, backgroundPaint)

        var l = context.dpToPx(widthOfIntervalsInDp[0])
        var r = l + widthOfColumns[0]
        drawInning(canvas, l, height * 0.05f, r, height * 0.35f)
        drawRunnersState(canvas, l, height * 0.4f, r, height * 0.95f)

        l = r + context.dpToPx(widthOfIntervalsInDp[1])
        r = l + widthOfColumns[1]
        drawBallCount(canvas, l, height * 0.05f, r, height * 0.95f)

        l = r + context.dpToPx(widthOfIntervalsInDp[2])
        r = l + widthOfColumns[2]
        drawTeamsAndRuns(canvas, l, height * 0.05f, r, height * 0.95f)

        l = r + context.dpToPx(widthOfIntervalsInDp[3])
        r = l + widthOfColumns[3]
        drawPlayers(canvas, l, height * 0.05f, r, height * 0.95f)
    }

    private fun drawInning(canvas: Canvas, l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(l, t, r, b, w * 0.1f, w * 0.1f, workPaint)

        workPaint.color = Color.BLACK
        workPaint.textSize = h * 0.9f
        val inningText = situation?.let {
            ((it.inningNumber + 1) / 2).toString() + "回" + if (it.inningNumber % 2 == 1) "表" else "裏"
        }
        canvas.drawTextWithinArea(
            inningText, l, r, (t + b) / 2, w * 0.1f, workPaint
        )
    }

    private fun drawRunnersState(canvas: Canvas, l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(l, t, r, b, w * 0.1f, w * 0.1f, workPaint)

        // Figure of bases
        basePath.rewind()
        basePath.moveTo(-w * 0.22f, 0f)
        basePath.lineTo(0f, -h * 0.3f)
        basePath.lineTo(w * 0.22f, 0f)
        basePath.lineTo(0f, h * 0.3f)
        basePath.close()

        // Draw the first base
        workPaint.color = if (situation?.orderOf1R != null) Color.RED else Color.BLACK
        workPaint.style = Paint.Style.FILL
        canvas.save()
        canvas.translate(l + w * 3 / 4f, t + h * 2 / 3f)
        canvas.drawPath(basePath, workPaint)
        canvas.restore()

        // Draw the second base
        workPaint.color = if (situation?.orderOf2R != null) Color.RED else Color.BLACK
        workPaint.style = Paint.Style.FILL
        canvas.save()
        canvas.translate(l + w * 2 / 4f, t + h / 3f)
        canvas.drawPath(basePath, workPaint)
        canvas.restore()

        // Draw the third base
        workPaint.color = if (situation?.orderOf3R != null) Color.RED else Color.BLACK
        workPaint.style = Paint.Style.FILL
        canvas.save()
        canvas.translate(l + w / 4f, t + h * 2 / 3f)
        canvas.drawPath(basePath, workPaint)
        canvas.restore()
    }

    private fun drawBallCount(canvas: Canvas, l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t
        val rad = context.dpToPx(4f)

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        ballCountCaptionPath.let {
            it.rewind()
            it.moveTo((l + r) / 2, t)
            it.lineTo((l + r) / 2, b)
            it.lineTo(l + rad, b)
            it.arcTo(
                l, b - rad * 2, l + rad * 2, b,
                90f, 90f, false
            )
            it.lineTo(l, t + rad)
            it.arcTo(
                l, t, l + rad * 2, t + rad * 2,
                180f, 90f, false
            )
            it.close()
            canvas.drawPath(it, workPaint)
        }

        workPaint.color = darkColor
        workPaint.style = Paint.Style.FILL
        ballCountValuePath.let {
            it.rewind()
            it.moveTo((l + r) / 2, t)
            it.lineTo((l + r) / 2, b)
            it.lineTo(r - rad, b)
            it.arcTo(
                r - rad * 2, b - rad * 2, r, b,
                90f, -90f, false
            )
            it.lineTo(r, t + rad)
            it.arcTo(
                r - rad * 2, t, r, t + rad * 2,
                0f, -90f, false
            )
            it.close()
            canvas.drawPath(it, workPaint)
        }

        workPaint.color = backgroundColor
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.3f
        canvas.drawTextWithinArea("B", l, (l + r) / 2f, t + h / 6f, w * 0.1f, workPaint)
        canvas.drawTextWithinArea("S", l, (l + r) / 2f, t + h * 3 / 6f, w * 0.1f, workPaint)
        canvas.drawTextWithinArea("O", l, (l + r) / 2f, t + h * 5 / 6f, w * 0.1f, workPaint)

        workPaint.color = Color.WHITE
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.3f
        canvas.drawTextWithinArea(
            situation?.balls?.toString(), (l + r) / 2f, r, t + h / 6f, w * 0.1f, workPaint
        )
        canvas.drawTextWithinArea(
            situation?.strikes?.toString(), (l + r) / 2f, r, t + h * 3 / 6f, w * 0.1f, workPaint
        )
        canvas.drawTextWithinArea(
            situation?.outs?.toString(), (l + r) / 2f, r, t + h * 5 / 6f, w * 0.1f, workPaint
        )

        workPaint.color = backgroundColor
        workPaint.style = Paint.Style.STROKE
        workPaint.strokeWidth = 1f
        canvas.drawLine(l, t + h / 3f, r, t + h / 3f, workPaint)
        canvas.drawLine(l, t + h * 2 / 3f, r, t + h * 2 / 3f, workPaint)
    }

    private fun drawTeamsAndRuns(canvas: Canvas, l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t
        val rad = context.dpToPx(4f)

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        teamNamesPath.let {
            it.rewind()
            it.moveTo(l + w * 0.7f, t)
            it.lineTo(l + w * 0.7f, b)
            it.lineTo(l + rad, b)
            it.arcTo(
                l, b - rad * 2, l + rad * 2, b,
                90f, 90f, false
            )
            it.lineTo(l, t + rad)
            it.arcTo(
                l, t, l + rad * 2, t + rad * 2,
                180f, 90f, false
            )
            it.close()
            canvas.drawPath(it, workPaint)
        }

        workPaint.color = darkColor
        workPaint.style = Paint.Style.FILL
        runsPath.let {
            it.rewind()
            it.moveTo(l + w * 0.7f, t)
            it.lineTo(l + w * 0.7f, b)
            it.lineTo(r - rad, b)
            it.arcTo(
                r - rad * 2, b - rad * 2, r, b,
                90f, -90f, false
            )
            it.lineTo(r, t + rad)
            it.arcTo(
                r - rad * 2, t, r, t + rad * 2,
                0f, -90f, false
            )
            it.close()
            canvas.drawPath(it, workPaint)
        }

        workPaint.color = backgroundColor
        workPaint.style = Paint.Style.STROKE
        canvas.drawLine(l, (t + b) / 2, r, (t + b) / 2, workPaint)

        workPaint.color = Color.BLACK
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.35f
        canvas.drawTextWithinArea(
            gameBaseInfo?.visitorTeamAbbreviatedName,
            l, l + w * 0.7f, t + 0.25f * h, w * 0.05f, workPaint
        )
        canvas.drawTextWithinArea(
            gameBaseInfo?.homeTeamAbbreviatedName,
            l, l + w * 0.7f, t + 0.75f * h, w * 0.05f, workPaint
        )

        workPaint.color = Color.WHITE
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.45f
        canvas.drawTextWithinArea(
            situation?.runsOfVisitor.toString(),
            l + w * 0.7f, r, t + 0.25f * h, w * 0.05f, workPaint
        )
        canvas.drawTextWithinArea(
            situation?.runsOfHome.toString(),
            l + w * 0.7f, r, t + 0.75f * h, w * 0.05f, workPaint
        )
    }

    private fun drawPlayers(canvas: Canvas, l: Float, t: Float, r: Float, b: Float) {
        val w = r - l
        val h = b - t
        val rad = context.dpToPx(4f)
        val widthOfPitchCount = min(context.dpToPx(64f), w * 0.3f)

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(l, t, r, b, rad, rad, workPaint)

        workPaint.color = backgroundColor
        workPaint.style = Paint.Style.STROKE
        canvas.drawLine(l, (t + b) / 2, r, (t + b) / 2, workPaint)

        workPaint.color = backgroundColor
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.35f
        canvas.drawTextWithinArea(
            "P", l, l + context.dpToPx(24f),
            if (situation?.topOrBottom == TOP) t + h * 0.75f else t + h * 0.25f,
            context.dpToPx(4f), workPaint
        )
        canvas.drawTextWithinArea(
            "B", l, l + context.dpToPx(24f),
            if (situation?.topOrBottom == TOP) t + h * 0.25f else t + h * 0.75f,
            context.dpToPx(4f), workPaint
        )

        // Draw player names.
        workPaint.color = Color.BLACK
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = h * 0.3f
        canvas.drawTextWithinArea(
            when (situation?.topOrBottom) {
                TOP -> gameState?.currentBatterName?.fullName
                BOTTOM -> gameState?.currentPitcherName?.fullName
                else -> null
            },
            l + context.dpToPx(16f), r - context.dpToPx(4f) - widthOfPitchCount,
            t + h * 0.25f, context.dpToPx(8f), workPaint, Paint.Align.LEFT
        )
        canvas.drawTextWithinArea(
            when (situation?.topOrBottom) {
                TOP -> gameState?.currentPitcherName?.fullName
                BOTTOM -> gameState?.currentBatterName?.fullName
                else -> null
            },
            l + context.dpToPx(16f), r - context.dpToPx(4f) - widthOfPitchCount,
            t + h * 0.75f, context.dpToPx(8f), workPaint, Paint.Align.LEFT
        )

        // Draw pitch count.
        situation?.topOrBottom?.let {
            workPaint.color = darkColor
            workPaint.style = Paint.Style.FILL
            canvas.drawRoundRect(
                r - context.dpToPx(4f) - widthOfPitchCount,
                if (it == TOP) t + h * 0.55f else t + h * 0.05f,
                r - context.dpToPx(4f),
                if (it == TOP) t + h * 0.95f else t + h * 0.45f,
                rad, rad, workPaint
            )

            workPaint.color = Color.WHITE
            workPaint.style = Paint.Style.FILL
            workPaint.textSize = h * 0.35f
            canvas.drawTextWithinArea(
                gameState?.currentPitchersPitchCount?.toString() + "球",
                r - context.dpToPx(4f) - widthOfPitchCount,
                r - context.dpToPx(4f),
                if (it == TOP) t + h * 0.75f else t + h * 0.25f,
                context.dpToPx(4f), workPaint
            )
        }
    }
}