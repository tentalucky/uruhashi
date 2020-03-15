package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.BattedBallStrength.*
import mahoroba.uruhashi.domain.game.BattedBallType.*
import mahoroba.uruhashi.domain.game.BattingResult.*
import mahoroba.uruhashi.domain.game.FieldPosition.*
import mahoroba.uruhashi.domain.game.PitchType.*
import mahoroba.uruhashi.domain.game.TopOrBottom.BOTTOM
import mahoroba.uruhashi.domain.game.TopOrBottom.TOP
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea
import mahoroba.uruhashi.presentation.utility.toPixel
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase
import kotlin.math.PI
import kotlin.math.max

class PeriodHistoryItemView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private companion object {
        val baseColor = Color.argb(255, 0, 87, 75)
        val whitishColor = Color.argb(255, 236, 236, 236)
        val transWhitishColor = Color.argb(128, 236, 236, 236)
        val darkColor = Color.argb(255, 0, 43, 37)
        val dullColor = Color.argb(255, 100, 131, 113)

        val lightAccentColor = Color.argb(255, 255, 192, 0)
    }

    // region * Child classes (Pitch marks) *

    private abstract class PitchMark {
        abstract fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        )
    }

    private class FourSeamFastBallMark : PitchMark() {
        private val paint = Paint().apply {
            this.style = Paint.Style.FILL
            this.color = Color.argb(255, 255, 0, 0)
        }

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            canvas.drawCircle(x, y, strikeZoneWidth * 0.1f, paint)
        }
    }

    private class TwoSeamFastBallMark : PitchMark() {
        private val paint = Paint().apply {
            this.style = Paint.Style.FILL
            this.color = Color.argb(255, 255, 128, 0)
        }

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.19f
            canvas.drawRect(x - size / 2, y - size / 2, x + size / 2, y + size / 2, paint)
        }
    }

    private class CurveBallMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f

            paint.shader = RadialGradient(
                -size / 2,
                size * 0.288675f,
                size,
                Color.argb(255, 160, 160, 0),
                Color.argb(128, 160, 160, 0),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, -size * 0.711325f)
                it.lineTo(size / 2, size * 0.288675f)
                it.lineTo(-size / 2, size * 0.288675f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SliderMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f
            paint.shader = RadialGradient(
                -size * 0.711325f,
                0f,
                size,
                Color.argb(255, 0, 160, 0),
                Color.argb(128, 0, 160, 0),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(-size * 0.711325f, 0f)
                it.lineTo(size * 0.288675f, size / 2)
                it.lineTo(size * 0.288675f, -size / 2)
                it.close()
            }

            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ForkBallMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f

            paint.shader = RadialGradient(
                0f,
                size * 0.711325f,
                size,
                Color.argb(255, 224, 96, 96),
                Color.argb(128, 224, 96, 96),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, size * 0.711325f)
                it.lineTo(size / 2, -size * 0.288675f)
                it.lineTo(-size / 2, -size * 0.288675f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ShootBallMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f
            paint.shader = RadialGradient(
                size * 0.711325f,
                0f,
                size,
                Color.argb(255, 0, 0, 192),
                Color.argb(128, 0, 0, 192),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(size * 0.711325f, 0f)
                it.lineTo(-size * 0.288675f, size / 2)
                it.lineTo(-size * 0.288675f, -size / 2)
                it.close()
            }

            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SinkerMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f

            paint.shader = RadialGradient(
                size / 2,
                size * 0.288675f,
                size,
                Color.argb(255, 0, 128, 128),
                Color.argb(128, 0, 128, 128),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, -size * 0.711325f)
                it.lineTo(size / 2, size * 0.288675f)
                it.lineTo(-size / 2, size * 0.288675f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            canvas.scale(strikeZoneWidth * 0.225f, strikeZoneWidth * 0.225f)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ChangeUpMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.19f

            paint.shader = RadialGradient(
                0f,
                size * 0.707107f,
                size,
                Color.argb(255, 192, 32, 192),
                Color.argb(128, 192, 32, 192),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, -size * 0.707107f)
                it.lineTo(size * 0.707107f, 0f)
                it.lineTo(0f, size * 0.707107f)
                it.lineTo(-size * 0.707107f, 0f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class KnuckleBallMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.19f

            paint.shader = RadialGradient(
                0f,
                0f,
                size,
                Color.argb(255, 128, 128, 192),
                Color.argb(128, 128, 128, 192),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, -size * 0.707107f)
                it.lineTo(size * 0.707107f, 0f)
                it.lineTo(0f, size * 0.707107f)
                it.lineTo(-size * 0.707107f, 0f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SplitterMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f

            paint.shader = RadialGradient(
                0f,
                size * 0.711325f,
                size,
                Color.argb(255, 192, 96, 160),
                Color.argb(128, 192, 96, 160),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, size * 0.711325f)
                it.lineTo(size / 2, -size * 0.288675f)
                it.lineTo(-size / 2, -size * 0.288675f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class CutterMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.225f

            paint.shader = RadialGradient(
                -size / 2,
                size / 2,
                size,
                Color.argb(255, 0, 160, 32),
                Color.argb(128, 0, 160, 32),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(-size / 2, size / 2)
                it.lineTo(size / 2, -size / 2)
                it.lineTo(size / 2, size / 2)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class PalmBallMark : PitchMark() {
        private val paint = Paint()
        private val path = Path()

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            val size = strikeZoneWidth * 0.19f

            paint.shader = RadialGradient(
                0f,
                size * 0.707107f,
                size,
                Color.argb(255, 128, 32, 160),
                Color.argb(128, 128, 32, 160),
                Shader.TileMode.CLAMP
            )
            path.let {
                it.rewind()
                it.moveTo(0f, -size * 0.707107f)
                it.lineTo(size * 0.707107f, 0f)
                it.lineTo(0f, size * 0.707107f)
                it.lineTo(-size * 0.707107f, 0f)
                it.close()
            }
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class PitchTypeNoEntryMark : PitchMark() {
        private val paint = Paint().apply {
            this.color = Color.argb(255, 160, 160, 160)
        }

        override fun paint(
            canvas: Canvas,
            x: Float,
            y: Float,
            isLHP: Boolean,
            strikeZoneWidth: Float
        ) {
            canvas.drawCircle(x, y, strikeZoneWidth * 0.1f, paint)
        }
    }

    // endregion * Child classes (Pitch marks) *

    private val backgroundPaint = Paint()
    private val workPaint = Paint()
    private val basePath = Path()
    private val fieldRect = RectF()

    var gameBaseInfo: ScoreKeepingUseCase.GameBaseInfo? = null
        set(value) {
            field = value
            invalidate()
        }
    var period: ScoreKeepingUseCase.PeriodDto? = null
        set(value) {
            field = value
            layoutParams.height = getNeededHeightForView()
            layoutParams = layoutParams
            invalidate()
        }

    private val pitchMarks = mutableMapOf<PitchType, PitchMark>()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        backgroundPaint.isAntiAlias = true
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.color = baseColor

        workPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        pitchMarks[FOUR_SEAM_FASTBALL] = FourSeamFastBallMark()
        pitchMarks[TWO_SEAM_FASTBALL] = TwoSeamFastBallMark()
        pitchMarks[CURVE_BALL] = CurveBallMark()
        pitchMarks[SLIDER] = SliderMark()
        pitchMarks[FORK_BALL] = ForkBallMark()
        pitchMarks[SHOOT_BALL] = ShootBallMark()
        pitchMarks[SINKER] = SinkerMark()
        pitchMarks[CHANGE_UP] = ChangeUpMark()
        pitchMarks[KNUCKLE_BALL] = KnuckleBallMark()
        pitchMarks[SPLITTER] = SplitterMark()
        pitchMarks[CUTTER] = CutterMark()
        pitchMarks[PALM_BALL] = PalmBallMark()
        pitchMarks[PitchType.NO_ENTRY] = PitchTypeNoEntryMark()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width * 1f, height * 1f, backgroundPaint)

        if (period?.isLastOfPlateAppearance == true) {
            workPaint.style = Paint.Style.FILL
            workPaint.color = Color.argb(255, 0, 255, 255)
            canvas.drawRect(0f, context.dpToPx(1f), width * 1f, context.dpToPx(2f), workPaint)
        }

        if (period is ScoreKeepingUseCase.SubstitutionDto) {
            drawSubstitution(canvas)
        } else if (period != null) {
            drawPlay(canvas)
        }

    }

    private fun drawPlay(canvas: Canvas) {
        if (period is ScoreKeepingUseCase.PitchDto) {
            drawPitchLocation(
                canvas,
                0f,
                bottom * 1f - context.dpToPx(14.4f),
                context.dpToPx(81.6f)
            )
        }

        if (period is ScoreKeepingUseCase.PitchDto && (period as ScoreKeepingUseCase.PitchDto).settled) {
            drawBattingResult(
                canvas,
                context.dpToPx(81.6f),
                bottom * 1f - context.dpToPx(14.4f),
                context.dpToPx(81.6f)
            )
        }

        if (period is ScoreKeepingUseCase.FoulDto) {
            drawFoulBallInfo(
                canvas,
                context.dpToPx(81.6f),
                bottom * 1f - context.dpToPx(14.4f),
                context.dpToPx(81.6f)
            )
        }

        if (period is ScoreKeepingUseCase.PlayDto) {
            drawFieldPlays(
                canvas,
                context.dpToPx(163.2f),
                width * 1f,
                bottom * 1f - context.dpToPx(14.4f)
            )
        }

        workPaint.color = Color.CYAN
        workPaint.style = Paint.Style.STROKE
        workPaint.strokeWidth = 1f
        workPaint.textSize = context.dpToPx(11.5f)

        drawPlayCaption(canvas, bottom * 1f - context.dpToPx(14.4f), bottom * 1f)
    }

    @Suppress("SameParameterValue")
    private fun drawPitchLocation(canvas: Canvas, l: Float, b: Float, size: Float) {
        val t = b - size
        val r = l + size
        val rectMargin = context.dpToPx(3f)

        // Draw background.
        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            l + rectMargin,
            t + rectMargin,
            r - rectMargin,
            b - rectMargin,
            context.dpToPx(2f), context.dpToPx(2f),
            workPaint
        )

        // Draw strike zone.
        workPaint.color = darkColor
        workPaint.strokeWidth = 1f
        workPaint.style = Paint.Style.STROKE
        canvas.drawRect(
            l + size * 3 / 8,
            t + size / 3,
            l + size * 5 / 8,
            t + size * 2 / 3,
            workPaint
        )

        val pitch = period as? ScoreKeepingUseCase.PitchDto
        val pitchX =
            if (pitch?.pitchLocationX != null)
                l + size / 2 + pitch.pitchLocationX!! * size / 8f
            else null
        val pitchY =
            if (pitch?.pitchLocationY != null)
                t + size / 2 + pitch.pitchLocationY!! * size / 6f
            else null

        if (pitchX != null && pitchY != null) {
            pitchMarks[pitch?.pitchType ?: PitchType.NO_ENTRY]?.paint(
                canvas, pitchX, pitchY, pitch?.isLHP == true, size / 3f
            )
        }

        val captionWidth = size * 0.40f
        val captionMargin = size * 0.02f
        val resultTextHeight = context.dpToPx(12f)
        val pitchTypeTextHeight = context.dpToPx(9.6f)
        val pitchSpeedTextHeight = context.dpToPx(9.6f)
        val captionX =
            if ((pitchX ?: 0f) > l + size / 2) l + rectMargin + captionMargin
            else r - rectMargin - captionMargin - captionWidth
        val resultTextY = b - rectMargin - captionMargin - resultTextHeight
        val pitchSpeedTextY = resultTextY - pitchSpeedTextHeight
        val pitchTypeTextY = pitchSpeedTextY - pitchTypeTextHeight

        workPaint.style = Paint.Style.FILL
        workPaint.color = getPitchResultColor()
        canvas.drawRoundRect(
            captionX, resultTextY, captionX + captionWidth, resultTextY + resultTextHeight,
            context.dpToPx(2f), context.dpToPx(2f), workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = resultTextHeight
        canvas.drawTextWithinArea(
            getPitchResultText(),
            captionX,
            captionX + captionWidth,
            resultTextY + resultTextHeight / 2,
            0f, workPaint
        )

        workPaint.color = transWhitishColor
        canvas.drawRect(
            captionX,
            pitchTypeTextY,
            captionX + captionWidth,
            pitchTypeTextY + pitchTypeTextHeight,
            workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = pitchTypeTextHeight
        canvas.drawTextWithinArea(
            getPitchTypeText(),
            captionX,
            captionX + captionWidth,
            pitchTypeTextY + pitchTypeTextHeight / 2,
            0f, workPaint
        )

        workPaint.color = transWhitishColor
        canvas.drawRect(
            captionX,
            pitchSpeedTextY,
            captionX + captionWidth,
            pitchSpeedTextY + pitchSpeedTextHeight,
            workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = pitchSpeedTextHeight
        canvas.drawTextWithinArea(
            "${pitch?.let { it.pitchSpeed?.toString() ?: "---" } ?: "---"}km/h",
            captionX,
            captionX + captionWidth,
            pitchSpeedTextY + pitchSpeedTextHeight / 2,
            0f, workPaint
        )
    }

    private fun getPitchResultColor() = when (period) {
        is ScoreKeepingUseCase.BallDto -> Color.argb(128, 64, 255, 64)
        is ScoreKeepingUseCase.StrikeDto -> when ((period as ScoreKeepingUseCase.StrikeDto).strikeType) {
            Strike.StrikeType.LOOKING -> Color.argb(128, 255, 255, 0)
            Strike.StrikeType.SWINGING -> Color.argb(128, 255, 255, 0)
            Strike.StrikeType.THIRD_BUNT_MISS -> Color.argb(128, 255, 192, 64)
        }
        is ScoreKeepingUseCase.FoulDto -> Color.argb(128, 255, 192, 64)
        is ScoreKeepingUseCase.HitByPitchDto -> Color.argb(128, 128, 128, 255)
        is ScoreKeepingUseCase.BattingDto -> Color.argb(128, 255, 64, 64)
        else -> transWhitishColor
    }

    private fun getPitchResultText() = when (period) {
        is ScoreKeepingUseCase.BallDto -> context.getString(R.string.pitch_result_ball)
        is ScoreKeepingUseCase.StrikeDto -> when ((period as ScoreKeepingUseCase.StrikeDto).strikeType) {
            Strike.StrikeType.LOOKING -> context.getString(R.string.pitch_result_strike_called)
            Strike.StrikeType.SWINGING -> context.getString(R.string.pitch_result_strike_swung)
            Strike.StrikeType.THIRD_BUNT_MISS -> context.getString(R.string.pitch_result_foul)
        }
        is ScoreKeepingUseCase.FoulDto -> context.getString(R.string.pitch_result_foul)
        is ScoreKeepingUseCase.HitByPitchDto -> context.getString(R.string.pitch_result_hit_by_pitch)
        is ScoreKeepingUseCase.BattingDto -> context.getString(R.string.pitch_result_batted)
        else -> period?.let { it::class.simpleName?.replace("Dto", "") } ?: ""
    }

    private fun getPitchTypeText() = when ((period as? ScoreKeepingUseCase.PitchDto)?.pitchType) {
        FOUR_SEAM_FASTBALL -> context.getString(R.string.pitch_type_four_seam_fastball)
        TWO_SEAM_FASTBALL -> context.getString(R.string.pitch_type_two_seam_fastball)
        CURVE_BALL -> context.getString(R.string.pitch_type_curve_ball)
        SLIDER -> context.getString(R.string.pitch_type_slider)
        FORK_BALL -> context.getString(R.string.pitch_type_fork_ball)
        SHOOT_BALL -> context.getString(R.string.pitch_type_shoot_ball)
        SINKER -> context.getString(R.string.pitch_type_sinker)
        CHANGE_UP -> context.getString(R.string.pitch_type_change_up)
        KNUCKLE_BALL -> context.getString(R.string.pitch_type_knuckle)
        SPLITTER -> context.getString(R.string.pitch_type_splitter)
        CUTTER -> context.getString(R.string.pitch_type_cutter)
        PALM_BALL -> context.getString(R.string.pitch_type_palm_ball)
        PitchType.NO_ENTRY -> "---"
        null -> "---"
    }

    @Suppress("UnnecessaryVariable", "SameParameterValue")
    private fun drawBattingResult(canvas: Canvas, l: Float, b: Float, size: Float) {

        val pitch = period as? ScoreKeepingUseCase.PitchDto
        val batted = period as? ScoreKeepingUseCase.BattingDto

        val t = b - size
        val r = l + size
        val rectMargin = height * 0.03f

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            l + rectMargin,
            t + rectMargin,
            r - rectMargin,
            b - rectMargin,
            context.dpToPx(2f),
            context.dpToPx(2f),
            workPaint
        )

        val homeX = l + size / 2
        val homeY = t + size * 13 / 16
        val leftPoleX = l + size / 16
        val leftPoleY = t + size * 6 / 16
        val rightPoleX = l + size - size / 16
        val rightPoleY = t + size * 6 / 16
        val firstX = homeX + (rightPoleX - homeX) * 0.3f
        val firstY = homeY - (rightPoleX - homeX) * 0.3f
        val secondX = homeX
        val secondY = homeY - (homeY - firstY) * 2
        val thirdX = homeX - (rightPoleX - homeX) * 0.3f
        val thirdY = firstY
        fieldRect.let {
            it.top = homeY - (homeX - leftPoleX) * 1.725f
            it.bottom = it.top + (homeX - leftPoleX) * (1.725f - 0.6728448f) * 2
            it.left = homeX - (homeX - leftPoleX) * (1.725f - 0.6728448f)
            it.right = homeX + (homeX - leftPoleX) * (1.725f - 0.6728448f)
        }

        workPaint.style = Paint.Style.STROKE
        workPaint.strokeWidth = 1f
        workPaint.color = darkColor
        canvas.drawLine(homeX, homeY, leftPoleX, leftPoleY, workPaint)
        canvas.drawLine(homeX, homeY, rightPoleX, rightPoleY, workPaint)
        canvas.drawLine(firstX, firstY, secondX, secondY, workPaint)
        canvas.drawLine(secondX, secondY, thirdX, thirdY, workPaint)
        canvas.drawArc(fieldRect, 198.1157777f, 143.768446f, false, workPaint)

        val battedBallIsToLeft =
            batted?.battedBall?.direction?.tracePoints?.firstOrNull()?.angle?.let {
                it > PI / 2 && it < PI * 3 / 2
            } ?: true

        val resultWidth = size * 0.425f
        val resultHeight = (b - rectMargin - homeY) * 0.90f
        val resultMargin = size * 0.02f
        val resultX =
            if (battedBallIsToLeft) homeX + resultMargin else homeX - resultMargin - resultWidth

        workPaint.color = getBattingResultColor(pitch)
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            resultX, homeY, resultX + resultWidth, homeY + resultHeight,
            size * 0.02f, size * 0.02f, workPaint
        )

        workPaint.color = darkColor
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = resultHeight
        canvas.drawTextWithinArea(
            pitch?.let { getBattingResultString(it) } ?: "",
            resultX, resultX + resultWidth, homeY + resultHeight / 2,
            size * 0.05f, workPaint
        )

        val captionWidth = resultWidth
        val captionHeight = size * 0.115f
        val captionX = resultX

        workPaint.style = Paint.Style.FILL
        workPaint.textSize = captionHeight
        workPaint.color = transWhitishColor
        canvas.drawRect(
            captionX,
            homeY - captionHeight * 2,
            captionX + captionWidth,
            homeY,
            workPaint
        )
        workPaint.color = baseColor
        canvas.drawTextWithinArea(
            batted?.let { getBattedBallStrengthString(it.battedBall.strength, it.battedBall.bunt) }
                ?: "",
            captionX,
            captionX + captionWidth,
            homeY - captionHeight * 3 / 2,
            size * 0.05f, workPaint
        )
        canvas.drawTextWithinArea(
            if (batted != null) getBattedBallTypeString(batted.battedBall.type) else "",
            captionX,
            captionX + captionWidth,
            homeY - captionHeight / 2,
            size * 0.05f, workPaint
        )

        batted?.battedBall?.direction?.tracePoints?.let { list ->
            if (list.isNotEmpty()) {
                workPaint.style = Paint.Style.STROKE
                workPaint.strokeWidth = 4f
                workPaint.color = getBattedBallColor(batted)
                var lastX = homeX
                var lastY = homeY
                list.forEach {
                    val p = it.toPixel(homeX, homeY, homeY - secondY)
                    canvas.drawLine(lastX, lastY, p.first, p.second, workPaint)
                    lastX = p.first
                    lastY = p.second
                }
                workPaint.style = Paint.Style.FILL
                canvas.drawCircle(lastX, lastY, size * 0.03f, workPaint)
            }
        }
    }

    private fun getBattingResultColor(pitch: ScoreKeepingUseCase.PitchDto?): Int {
        return when (pitch?.battingResult) {
            SINGLE, DOUBLE, TRIPLE ->
                Color.argb(96, 224, 128, 0)
            HOME_RUN ->
                Color.argb(96, 255, 32, 32)
            GROUND_OUT, LINE_OUT, FLY_OUT, FOUL_LINE_OUT, FOUL_FLY_OUT ->
                Color.argb(96, 128, 96, 192)
            STRIKEOUT ->
                Color.argb(96, 128, 0, 255)
            SACRIFICE_FLY, SACRIFICE_HIT ->
                Color.argb(96, 216, 224, 0)
            WALK, INTENTIONAL_WALK, HIT_BY_PITCH ->
                Color.argb(96, 64, 255, 64)
            INTERFERE, OBSTRUCTION, INTERRUPT ->
                Color.argb(96, 128, 128, 128)
            else ->
                Color.argb(96, 128, 128, 128)
        }
    }

    private fun getBattingResultString(pitch: ScoreKeepingUseCase.PitchDto): String {
        // TODO: make resource
        val pos =
            when ((pitch as? ScoreKeepingUseCase.BattingDto)?.fieldPlays?.firstOrNull()?.fieldersActions?.firstOrNull()?.position) {
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

        return when (pitch.battingResult) {
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
            BattingResult.NO_ENTRY -> ""
            null -> ""
        }
    }

    private fun getBattedBallColor(batting: ScoreKeepingUseCase.BattingDto?): Int {
        return when (batting?.battedBall?.type) {
            GROUND_HIGH_BOUND -> Color.argb(255, 0, 64, 255)
            GROUND -> Color.argb(255, 0, 64, 255)
            LINE_DRIVE -> Color.argb(255, 255, 80, 0)
            FLY_BALL -> Color.argb(255, 255, 0, 96)
            HIGH_FLY_BALL -> Color.argb(255, 255, 0, 96)
            BattedBallType.NO_ENTRY -> Color.DKGRAY
            null -> Color.DKGRAY
        }
    }

    private fun drawFoulBallInfo(canvas: Canvas, l: Float, b: Float, size: Float) {
        val foul = period as ScoreKeepingUseCase.FoulDto

        val t = b - size
        val r = l + size
        val rectMargin = height * 0.03f

        workPaint.color = dullColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(
            l + rectMargin,
            t + rectMargin,
            r - rectMargin,
            b - rectMargin,
            context.dpToPx(2f),
            context.dpToPx(2f),
            workPaint
        )

        val homeX = l + size / 2
        val homeY = t + size * 13 / 16
        val leftPoleX = l + size / 16
        val leftPoleY = t + size * 6 / 16
        val rightPoleX = l + size - size / 16
        val rightPoleY = t + size * 6 / 16
        val firstX = homeX + (rightPoleX - homeX) * 0.3f
        val firstY = homeY - (rightPoleX - homeX) * 0.3f
        val secondX = homeX
        val secondY = homeY - (homeY - firstY) * 2
        val thirdX = homeX - (rightPoleX - homeX) * 0.3f
        val thirdY = firstY
        fieldRect.let {
            it.top = homeY - (homeX - leftPoleX) * 1.725f
            it.bottom = it.top + (homeX - leftPoleX) * (1.725f - 0.6728448f) * 2
            it.left = homeX - (homeX - leftPoleX) * (1.725f - 0.6728448f)
            it.right = homeX + (homeX - leftPoleX) * (1.725f - 0.6728448f)
        }

        workPaint.style = Paint.Style.STROKE
        workPaint.strokeWidth = 1f
        workPaint.color = Color.WHITE
        canvas.drawLine(homeX, homeY, leftPoleX, leftPoleY, workPaint)
        canvas.drawLine(homeX, homeY, rightPoleX, rightPoleY, workPaint)
        canvas.drawLine(firstX, firstY, secondX, secondY, workPaint)
        canvas.drawLine(secondX, secondY, thirdX, thirdY, workPaint)
        canvas.drawArc(fieldRect, 198.1157777f, 143.768446f, false, workPaint)

        val angle = when (foul.direction) {
            FoulBallDirection.TO_BACKSTOP ->
                PI * 1.5
            FoulBallDirection.TO_FIRST_SIDE ->
                if (foul.isAtLine == true) PI * 0.225 else 0.0
            FoulBallDirection.TO_THIRD_SIDE ->
                if (foul.isAtLine == true) PI * 0.775 else PI
            FoulBallDirection.TO_RIGHT_FIELD ->
                if (foul.isAtLine == true) PI * 0.225 else PI * 0.125
            FoulBallDirection.TO_LEFT_FIELD ->
                if (foul.isAtLine == true) PI * 0.775 else PI * 0.875
            else -> null
        }

        val distance = when (foul.direction) {
            FoulBallDirection.TO_BACKSTOP -> if (foul.isAtLine == true) 0.25f else 0.5f
            FoulBallDirection.TO_FIRST_SIDE,
            FoulBallDirection.TO_THIRD_SIDE -> 0.75f
            FoulBallDirection.TO_RIGHT_FIELD,
            FoulBallDirection.TO_LEFT_FIELD -> 1.75f
            else -> null
        }

        val captionWidth = size * 0.425f
        val captionHeight = size * 0.115f
        val captionX = when (foul.direction) {
            FoulBallDirection.TO_THIRD_SIDE,
            FoulBallDirection.TO_LEFT_FIELD -> homeX + size * 0.02f
            else -> homeX - size * 0.02f - captionWidth
        }

        workPaint.style = Paint.Style.FILL
        workPaint.color = Color.WHITE
        workPaint.textSize = captionHeight
        canvas.drawTextWithinArea(
            getBattedBallStrengthString(
                foul.battedBallStrength, foul.battingOption == BattingOption.BUNT
            ),
            captionX,
            captionX + captionWidth,
            homeY - captionHeight * 3 / 2,
            size * 0.05f, workPaint
        )
        canvas.drawTextWithinArea(
            getBattedBallTypeString(foul.battedBallType),
            captionX,
            captionX + captionWidth,
            homeY - captionHeight / 2,
            size * 0.05f, workPaint
        )

        if (angle != null && distance != null) {
            val p =
                AngularCoordinate(angle.toFloat(), distance).toPixel(homeX, homeY, homeY - secondY)
            workPaint.style = Paint.Style.STROKE
            workPaint.strokeWidth = 4f
            workPaint.color = getFoulBallColor(foul)
            canvas.drawLine(homeX, homeY, p.first, p.second, workPaint)
        }
    }

    private fun getBattedBallTypeString(battedBallType: BattedBallType?): String {
        return when (battedBallType) {
            GROUND_HIGH_BOUND -> context.getString(R.string.batted_ball_type_ground_high_bound)
            GROUND -> context.getString(R.string.batted_ball_type_ground)
            LINE_DRIVE -> context.getString(R.string.batted_ball_type_line_drive)
            FLY_BALL -> context.getString(R.string.batted_ball_type_fly_ball)
            HIGH_FLY_BALL -> context.getString(R.string.batted_ball_type_high_fly_ball)
            else -> ""
        }
    }

    private fun getBattedBallStrengthString(
        battedBallStrength: BattedBallStrength?, isBunt: Boolean
    ): String {
        return when (battedBallStrength) {
            VERY_HARD -> context.getString(R.string.batted_ball_strength_very_hard)
            HARD -> context.getString(R.string.batted_ball_strength_hard)
            MEDIUM -> context.getString(R.string.batted_ball_strength_medium)
            WEAK -> context.getString(R.string.batted_ball_strength_weak)
            VERY_WEAK -> context.getString(R.string.batted_ball_strength_very_weak)
            else -> ""
        } + if (isBunt) ":" + context.getString(R.string.batting_option_bunt) else ""
    }

    private fun getFoulBallColor(foul: ScoreKeepingUseCase.FoulDto): Int {
        return when (foul.battedBallType) {
            GROUND_HIGH_BOUND -> Color.argb(255, 0, 64, 255)
            GROUND -> Color.argb(255, 0, 192, 255)
            LINE_DRIVE -> Color.argb(255, 255, 192, 0)
            FLY_BALL -> Color.argb(255, 255, 0, 128)
            HIGH_FLY_BALL -> Color.argb(255, 255, 0, 128)
            BattedBallType.NO_ENTRY -> Color.LTGRAY
            null -> Color.DKGRAY
        }
    }

    private fun drawFieldPlays(canvas: Canvas, l: Float, r: Float, b: Float) {
        val rectMargin = context.dpToPx(2f)

        val rowWidth = r - l - rectMargin * 2
        val rowHeight = context.dpToPx(20.4f)
        val rowContentHeight = rowHeight - rectMargin * 2
        val recordLeft = l + rectMargin
        val recordRight = l + rectMargin + rowWidth * 0.16f
        val cutoffLeft = l + rectMargin + rowWidth * 0.16f
        val cutoffRight = l + rectMargin + rowWidth * 0.52f
        val runnersLeft = l + rectMargin + rowWidth * 0.52f
        val runnersRight = l + rectMargin + rowWidth * 0.68f
        val runsHomeLeft = l + rectMargin + rowWidth * 0.68f
        val runsHomeRight = l + rectMargin + rowWidth * 0.74f
        val runsHyphenLeft = l + rectMargin + rowWidth * 0.74f
        val runsHyphenRight = l + rectMargin + rowWidth * 0.78f
        val runsVisitorLeft = l + rectMargin + rowWidth * 0.78f
        val runsVisitorRight = l + rectMargin + rowWidth * 0.84f
        val outsLeft = l + rectMargin + rowWidth * 0.84f
        val outsRight = l + rectMargin + rowWidth * 1.00f

        val itemPadding = context.dpToPx(2f)

        val play = period as ScoreKeepingUseCase.PlayDto

        val drawBase = fun(
            baseLeft: Float,
            baseTop: Float,
            baseWidth: Float,
            baseHeight: Float,
            isOnBase: Boolean
        ) {
            val margin = rowWidth * 0.01f
            val baseRight = baseLeft + baseWidth
            val baseBottom = baseTop + baseHeight
            basePath.let {
                it.rewind()
                it.moveTo(baseLeft + margin, (baseTop + baseBottom) / 2)
                it.lineTo((baseLeft + baseRight) / 2, baseTop + margin)
                it.lineTo(baseRight - margin, (baseTop + baseBottom) / 2)
                it.lineTo((baseLeft + baseRight) / 2, baseBottom - margin)
                it.close()
                if (isOnBase) {
                    workPaint.style = Paint.Style.FILL_AND_STROKE
                    workPaint.strokeWidth = 2f
                    workPaint.color = Color.argb(255, 255, 128, 0)
                } else {
                    workPaint.style = Paint.Style.STROKE
                    workPaint.strokeWidth = 2f
                    workPaint.color = whitishColor
                }
                canvas.drawPath(it, workPaint)
            }
        }

        val drawSeparator = fun(x: Float, rowTop: Float, rowBottom: Float) {
            workPaint.style = Paint.Style.STROKE
            workPaint.color = baseColor
            workPaint.strokeWidth = 1f
            canvas.drawLine(
                x, rowTop + rectMargin + 2f, x, rowBottom - rectMargin - 2f,
                workPaint
            )
        }


        val getNewBase =
            fun(state: RunnerState?, originalBase: Int?) = when (state) {
                RunnerState.ON_FIRST_BASE -> 1
                RunnerState.ON_SECOND_BASE -> 2
                RunnerState.ON_THIRD_BASE -> 3
                RunnerState.HOME_IN,
                RunnerState.OUT_IN_PROGRESSING,
                RunnerState.OUT_IN_RETURNING -> null
                RunnerState.FLOATING,
                null -> originalBase
            }

        var batterRunnerIsOn: Int? = 0
        var firstRunnerIsOn = if (play.runnerIsOn1B) 1 else null
        var secondRunnerIsOn = if (play.runnerIsOn2B) 2 else null
        var thirdRunnerIsOn = if (play.runnerIsOn3B) 3 else null
        var runsOfHome = play.runsOfHome
        var runsOfVisitor = play.runsOfVisitor
        var outs = play.outs
        play.fieldPlayList.forEachIndexed { index, fieldPlay ->
            val rowTop = b - (index + 1) * rowHeight
            val rowMiddle = b - (index + 0.5f) * rowHeight
            val rowBottom = b - index * rowHeight

            batterRunnerIsOn = getNewBase(fieldPlay.batterRunnersAction?.state, batterRunnerIsOn)
            firstRunnerIsOn = getNewBase(fieldPlay.firstRunnersAction?.state, firstRunnerIsOn)
            secondRunnerIsOn = getNewBase(fieldPlay.secondRunnersAction?.state, secondRunnerIsOn)
            thirdRunnerIsOn = getNewBase(fieldPlay.thirdRunnersAction?.state, thirdRunnerIsOn)

            when (play.topOrBottom) {
                TOP -> runsOfVisitor += fieldPlay.runs
                BOTTOM -> runsOfHome += fieldPlay.runs
            }
            outs += if (fieldPlay.madeOut) 1 else 0

            workPaint.style = Paint.Style.FILL
            workPaint.color = darkColor
            canvas.drawRoundRect(
                l + rectMargin,
                rowTop + rectMargin,
                r - rectMargin,
                rowBottom - rectMargin,
                rectMargin, rectMargin, workPaint
            )

            workPaint.style = Paint.Style.FILL
            workPaint.color = whitishColor
            workPaint.textSize = context.dpToPx(10f)
            canvas.drawTextWithinArea(
                getFieldPlayRecordText(play, fieldPlay),
                recordLeft, recordRight, rowMiddle,
                itemPadding, workPaint
            )

            drawSeparator(recordRight, rowTop, rowBottom)

            workPaint.style = Paint.Style.FILL
            workPaint.color = whitishColor
            workPaint.textSize = context.dpToPx(10f)
            canvas.drawTextWithinArea(
                getCutoffPlayText(fieldPlay),
                cutoffLeft, cutoffRight, rowMiddle,
                itemPadding, workPaint, Paint.Align.LEFT
            )

            drawSeparator(cutoffRight, rowTop, rowBottom)

            val runnersStates =
                listOf(batterRunnerIsOn, firstRunnerIsOn, secondRunnerIsOn, thirdRunnerIsOn)
            drawBase(
                runnersLeft,
                rowTop + rectMargin + rowContentHeight / 3,
                (runnersRight - runnersLeft) / 2,
                rowContentHeight * 2 / 3,
                runnersStates.any { it == 3 }
            )
            drawBase(
                runnersLeft + (runnersRight - runnersLeft) / 4,
                rowTop + rectMargin,
                (runnersRight - runnersLeft) / 2,
                rowContentHeight * 2 / 3,
                runnersStates.any { it == 2 }
            )
            drawBase(
                runnersLeft + (runnersRight - runnersLeft) / 2,
                rowTop + rectMargin + rowContentHeight / 3,
                (runnersRight - runnersLeft) / 2,
                rowContentHeight * 2 / 3,
                runnersStates.any { it == 1 }
            )

            drawSeparator(runnersRight, rowTop, rowBottom)

            workPaint.style = Paint.Style.FILL
            workPaint.textSize = context.dpToPx(10f)
            workPaint.color =
                if (play.topOrBottom == BOTTOM && fieldPlay.runs > 0) lightAccentColor
                else whitishColor
            canvas.drawTextWithinArea(
                runsOfHome.toString(),
                runsHomeLeft, runsHomeRight, rowMiddle,
                itemPadding, workPaint
            )

            workPaint.color = whitishColor
            canvas.drawTextWithinArea(
                "-",
                runsHyphenLeft, runsHyphenRight, rowMiddle,
                0f, workPaint
            )

            workPaint.color =
                if (play.topOrBottom == TOP && fieldPlay.runs > 0) lightAccentColor
                else whitishColor
            canvas.drawTextWithinArea(
                runsOfVisitor.toString(),
                runsVisitorLeft, runsVisitorRight, rowMiddle,
                itemPadding, workPaint
            )

            drawSeparator(runsVisitorRight, rowTop, rowBottom)

            workPaint.style = Paint.Style.FILL
            workPaint.textSize = context.dpToPx(10f)
            workPaint.color = if (fieldPlay.madeOut) lightAccentColor else whitishColor
            canvas.drawTextWithinArea(
                "Out:${outs}",
                outsLeft, outsRight, rowMiddle,
                itemPadding, workPaint
            )
        }
    }

    private fun getFieldPlayRecordText(
        play: ScoreKeepingUseCase.PlayDto,
        fieldPlay: FieldPlay
    ): String {
        // TODO: make resource
        return when (fieldPlay.factor) {
            FieldPlayFactor.STEALING -> {
                if (fieldPlay.madeOut || fieldPlay.fieldersActionList.count { it.record?.isError == true } > 0)
                    "盗塁死"
                else
                    "盗塁"
            }
            FieldPlayFactor.PICK_OFF_PLAY ->
                "牽制"
            FieldPlayFactor.WILD_PITCH ->
                "暴投"
            FieldPlayFactor.PASSED_BALL ->
                "捕逸"
            FieldPlayFactor.INTERFERENCE_BATTING ->
                "打撃妨害"
            FieldPlayFactor.INTERFERENCE_FIELDING ->
                "守備妨害"
            FieldPlayFactor.OBSTRUCTION ->
                "走塁妨害"
            FieldPlayFactor.BATTING ->
                "打撃"
            FieldPlayFactor.WALK ->
                "四球"
            FieldPlayFactor.HIT_BY_PITCH ->
                "死球"
            FieldPlayFactor.BALK ->
                "ボーク"
            else ->
                if (play is ScoreKeepingUseCase.StrikeDto && play.settled)
                    "三振"
                else
                    ""
        }
    }

    private fun getCutoffPlayText(fieldPlay: FieldPlay): String {
        val sb = StringBuilder()

        fieldPlay.fieldersActionList.forEach {
            if (!sb.isEmpty()) sb.append("-")
            if (it.record == FieldingRecord.ERROR_CATCHING) sb.append("E")
            if (it.record == FieldingRecord.FIELDERS_CHOICE) sb.append("Fc")
            sb.append(it.position.singleCharacter)
            if (it.record == FieldingRecord.ERROR_THROWING) sb.append("E")
        }
        return sb.toString()
    }

    private fun drawPlayCaption(canvas: Canvas, t: Float, b: Float) {
        val captionHeight = b - t

        workPaint.color = whitishColor
        workPaint.style = Paint.Style.FILL
        canvas.drawRect(0f, t, width * 1f, b, workPaint)

        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)

        val period = period as ScoreKeepingUseCase.PeriodDto
        val y = (t + b) / 2

        canvas.drawTextWithinArea(
            ((period.inningNumber + 1) / 2).toString() + if (period.topOrBottom == TOP) "表" else "裏",
            0f, width * 0.06f, y, context.dpToPx(4f), workPaint
        )

        workPaint.color = baseColor
        canvas.drawRoundRect(
            width * 0.06f, t + captionHeight * 0.05f,
            width * 0.09f, b - captionHeight * 0.05f,
            width * 0.005f, width * 0.005f, workPaint
        )
        workPaint.color = whitishColor
        workPaint.textSize = context.dpToPx(9.6f)
        canvas.drawTextWithinArea(
            "P",
            width * 0.06f, width * 0.09f, y, context.dpToPx(2f), workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)
        canvas.drawTextWithinArea(
            period.pitchersName?.familyName,
            width * 0.09f, width * 0.25f, y, context.dpToPx(2f), workPaint
        )

        workPaint.color = baseColor
        canvas.drawRoundRect(
            width * 0.25f, t + captionHeight * 0.05f,
            width * 0.28f, b - captionHeight * 0.05f,
            width * 0.005f, width * 0.005f, workPaint
        )
        workPaint.color = whitishColor
        workPaint.textSize = context.dpToPx(9.6f)
        canvas.drawTextWithinArea(
            "B",
            width * 0.25f, width * 0.28f, y, context.dpToPx(2f), workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)
        canvas.drawTextWithinArea(
            period.battersName?.familyName,
            width * 0.28f, width * 0.44f, y, context.dpToPx(4f), workPaint
        )


        workPaint.color = Color.argb(255, 0, 96, 0)
        canvas.drawRoundRect(
            width * 0.44f, t + captionHeight * 0.05f,
            width * 0.465f, b - captionHeight * 0.05f,
            width * 0.005f, width * 0.005f, workPaint
        )
        workPaint.color = whitishColor
        workPaint.textSize = context.dpToPx(9.6f)
        canvas.drawTextWithinArea(
            "B",
            width * 0.44f, width * 0.465f, y, 0f, workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)
        canvas.drawTextWithinArea(
            period.balls.toString(),
            width * 0.465f, width * 0.49f, y, 0f, workPaint
        )

        workPaint.color = Color.argb(255, 96, 108, 0)
        canvas.drawRoundRect(
            width * 0.49f, t + captionHeight * 0.05f,
            width * 0.515f, b - captionHeight * 0.05f,
            width * 0.005f, width * 0.005f, workPaint
        )
        workPaint.color = whitishColor
        workPaint.textSize = context.dpToPx(9.6f)
        canvas.drawTextWithinArea(
            "S",
            width * 0.49f, width * 0.515f, y, 0f, workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)
        canvas.drawTextWithinArea(
            period.strikes.toString(),
            width * 0.515f, width * 0.54f, y, 0f, workPaint
        )

        workPaint.color = Color.argb(255, 128, 0, 0)
        canvas.drawRoundRect(
            width * 0.54f, t + captionHeight * 0.05f,
            width * 0.565f, b - captionHeight * 0.05f,
            width * 0.005f, width * 0.005f, workPaint
        )
        workPaint.color = whitishColor
        workPaint.textSize = context.dpToPx(9.6f)
        canvas.drawTextWithinArea(
            "O",
            width * 0.54f, width * 0.565f, y, 0f, workPaint
        )
        workPaint.color = darkColor
        workPaint.textSize = context.dpToPx(11.5f)
        canvas.drawTextWithinArea(
            period.outs.toString(),
            width * 0.565f, width * 0.59f, y, 0f, workPaint
        )

        val drawBase =
            fun(drawLeft: Float, drawRight: Float, isUpper: Boolean, isOnBase: Boolean) {
                val margin = width * 0.0025f
                val drawTop = if (isUpper) t else t + captionHeight * 1f / 3f
                val drawBottom = if (isUpper) t + captionHeight * 2f / 3f else bottom * 1f
                basePath.let {
                    it.rewind()
                    it.moveTo(drawLeft + margin, (drawTop + drawBottom) / 2)
                    it.lineTo((drawLeft + drawRight) / 2, drawTop + margin)
                    it.lineTo(drawRight - margin, (drawTop + drawBottom) / 2)
                    it.lineTo((drawLeft + drawRight) / 2, drawBottom - margin)
                    it.close()
                    workPaint.style = Paint.Style.FILL
                    workPaint.color = if (isOnBase) Color.argb(255, 255, 128, 0) else darkColor
                    canvas.drawPath(it, workPaint)
                }
            }
        drawBase(width * 0.60f, width * 0.64f, false, period.runnerIsOn3B)
        drawBase(width * 0.62f, width * 0.66f, true, period.runnerIsOn2B)
        drawBase(width * 0.64f, width * 0.68f, false, period.runnerIsOn1B)



        workPaint.color = Color.argb(255, 255, 96, 128)
        workPaint.strokeWidth = 6f
        canvas.drawLine(
            if (period.topOrBottom == TOP) width * 0.90f else width * 0.70f,
            b - workPaint.strokeWidth / 2,
            if (period.topOrBottom == TOP) width * 1.00f else width * 0.80f,
            b - workPaint.strokeWidth / 2,
            workPaint
        )

        workPaint.color = darkColor
        canvas.drawTextWithinArea(
            gameBaseInfo?.homeTeamAbbreviatedName,
            width * 0.70f, width * 0.80f, y, context.dpToPx(4f), workPaint
        )
        canvas.drawTextWithinArea(
            period.runsOfHome.toString(),
            width * 0.80f, width * 0.84f, y, context.dpToPx(4f), workPaint
        )
        canvas.drawTextWithinArea(
            "-",
            width * 0.84f, width * 0.86f, y, 0f, workPaint
        )
        canvas.drawTextWithinArea(
            period.runsOfVisitor.toString(),
            width * 0.86f, width * 0.90f, y, context.dpToPx(4f), workPaint
        )
        canvas.drawTextWithinArea(
            gameBaseInfo?.visitorTeamAbbreviatedName,
            width * 0.90f, width * 1.00f, y, context.dpToPx(4f), workPaint
        )


    }

    private fun drawSubstitution(canvas: Canvas) {
        drawPlayerChangingList(canvas, bottom * 1f - context.dpToPx(14.4f))

        drawPlayCaption(canvas, bottom * 1f - context.dpToPx(14.4f), bottom * 1f)
    }

    private fun drawPlayerChangingList(canvas: Canvas, b: Float) {
        val substitution = period as ScoreKeepingUseCase.SubstitutionDto
        val lineup = substitution.newPlayerList

        val rectMargin = context.dpToPx(2f)
        val rowHeight = context.dpToPx(20.4f)
        val rowLeft = left + rectMargin
        val rowWidth = width - rectMargin * 2

        val orders1 = substitution.newPlayerList.map { it.first }
        val orders2 = substitution.newPositionList.map { it.first }
        val orders = orders1.union(orders2).sortedDescending()

        val drawTextBox =
            fun(
                text: String?,
                l: Float, t: Float, r: Float, b: Float,
                foreColor: Int, backColor: Int
            ) {
                workPaint.style = Paint.Style.FILL
                workPaint.color = backColor
                canvas.drawRoundRect(
                    l, t, r, b, context.dpToPx(2f), context.dpToPx(2f), workPaint
                )

                workPaint.color = foreColor
                workPaint.textSize = context.dpToPx(11.5f)
                canvas.drawTextWithinArea(
                    text, l, r, (t + b) / 2, width * 0.005f, workPaint
                )
            }

        orders.forEachIndexed { index, order ->
            val rowTop = b - (index + 1) * rowHeight + rectMargin
            val rowMiddle = b - (index + 0.5f) * rowHeight
            val rowBottom = b - index * rowHeight - rectMargin

            workPaint.style = Paint.Style.FILL
            workPaint.color = whitishColor
            workPaint.textSize = context.dpToPx(11.5f)
            canvas.drawTextWithinArea(
                "${order + 1}",
                rowLeft, rowLeft + rowWidth * 0.1f,
                rowMiddle,
                rowWidth * 0.005f, workPaint
            )

            drawTextBox(
                substitution.getPositionIn(substitution.teamClass, order).abbreviated,
                rowLeft + rowWidth * 0.1f,
                rowTop,
                rowLeft + rowWidth * 0.19f,
                rowBottom,
                whitishColor, darkColor
            )

            drawTextBox(
                substitution.getPlayerIn(substitution.teamClass, order).playerName?.fullName,
                rowLeft + rowWidth * 0.2f,
                rowTop,
                rowLeft + rowWidth * 0.5f,
                rowBottom,
                whitishColor, darkColor
            )

            workPaint.style = Paint.Style.FILL
            workPaint.color = whitishColor
            workPaint.textSize = context.dpToPx(11.5f)
            canvas.drawTextWithinArea(
                ">",
                rowLeft + rowWidth * 0.5f, rowLeft + rowWidth * 0.6f,
                rowMiddle,
                rowWidth * 0.005f, workPaint
            )

            drawTextBox(
                substitution.newPositionList.firstOrNull { it.first == order }?.second?.abbreviated
                    ?: substitution.getPositionIn(substitution.teamClass, order).abbreviated,
                rowLeft + rowWidth * 0.6f,
                rowTop,
                rowLeft + rowWidth * 0.69f,
                rowBottom,
                lightAccentColor, darkColor
            )

            if (substitution.newPlayerList.any { it.first == order }) {
                drawTextBox(
                    substitution.newPlayerList.firstOrNull { it.first == order }?.second?.playerName?.fullName,
                    rowLeft + rowWidth * 0.7f,
                    rowTop,
                    rowLeft + rowWidth * 1.0f,
                    rowBottom,
                    lightAccentColor,
                    darkColor
                )
            }
        }
    }

    private fun getNeededHeightForView(): Int {
        val dp = when {
            period is ScoreKeepingUseCase.PlayDto ->
                96f + max(
                    (period as ScoreKeepingUseCase.PlayDto).fieldPlayList.count() - 4,
                    0
                ) * 20.4f
            period is ScoreKeepingUseCase.SubstitutionDto ->
                14.4f + 20.4f * (period as ScoreKeepingUseCase.SubstitutionDto).let {
                    val orders1 = it.newPlayerList.map { it.first }
                    val orders2 = it.newPositionList.map { it.first }
                    orders1.union(orders2).size
                }
            else ->
                0f
        } + if (period?.isLastOfPlateAppearance == true) 2f else 0f

        return context.dpToPx(dp).toInt()
    }
}