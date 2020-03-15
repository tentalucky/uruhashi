package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.*
import mahoroba.uruhashi.domain.game.PitchType.*
import mahoroba.uruhashi.presentation.utility.VibrateService
import mahoroba.uruhashi.usecase.scoreKeeping.ScoreKeepingUseCase.*

class PitchLocationView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    // region * Child classes *

    private abstract class PitchMark(val strikeZoneWidth: Int) {
        abstract fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean)
    }

    private class FourSeamFastBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            this.color = Color.argb(160, 255, 0, 0)
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.drawCircle(x, y, strikeZoneWidth * 0.1f, paint)
        }
    }

    private class TwoSeamFastBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            this.color = Color.argb(160, 255, 128, 0)
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            val size = strikeZoneWidth * 0.19f
            canvas.drawRect(x - size / 2, y - size / 2, x + size / 2, y + size / 2, paint)
        }
    }

    private class CurveBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                - size / 2,
                size * 0.288675f,
                size,
                Color.argb(160, 192, 192, 32),
                Color.argb(80, 192, 192, 32),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(0f, -size * 0.711325f)
            this.lineTo(size / 2, size * 0.288675f)
            this.lineTo(- size / 2, size * 0.288675f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SliderMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                - size * 0.711325f,
                0f,
                size,
                Color.argb(160, 0, 255, 0),
                Color.argb(80, 0, 255, 0),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(- size * 0.711325f, 0f)
            this.lineTo(size * 0.288675f, size / 2)
            this.lineTo(size * 0.288675f, - size / 2)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ForkBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                0f,
                size * 0.711325f,
                size,
                Color.argb(160, 255, 112, 112),
                Color.argb(80, 255, 112, 112),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(0f, size * 0.711325f)
            this.lineTo(size / 2, - size * 0.288675f)
            this.lineTo(- size / 2, - size * 0.288675f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ShootBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                size * 0.711325f,
                0f,
                size,
                Color.argb(160, 128, 128, 255),
                Color.argb(80, 128, 128, 255),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(size * 0.711325f, 0f)
            this.lineTo(- size * 0.288675f, size / 2)
            this.lineTo(- size * 0.288675f, - size / 2)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SinkerMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                size / 2,
                size * 0.288675f,
                size,
                Color.argb(160, 0, 192, 192),
                Color.argb(80, 0, 192, 192),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(0f, -size * 0.711325f)
            this.lineTo(size / 2, size * 0.288675f)
            this.lineTo(- size / 2, size * 0.288675f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class ChangeUpMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.19f
            this.shader = RadialGradient(
                0f,
                size * 0.707107f,
                size,
                Color.argb(160, 255, 64, 255),
                Color.argb(80, 255, 64, 255),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.19f
            this.moveTo(0f, - size * 0.707107f)
            this.lineTo(size * 0.707107f, 0f)
            this.lineTo(0f, size * 0.707107f)
            this.lineTo(- size * 0.707107f, 0f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class KnuckleBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.19f
            this.shader = RadialGradient(
                0f,
                0f,
                size,
                Color.argb(160, 128, 128, 192),
                Color.argb(80, 128, 128, 192),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.19f
            this.moveTo(0f, - size * 0.707107f)
            this.lineTo(size * 0.707107f, 0f)
            this.lineTo(0f, size * 0.707107f)
            this.lineTo(- size * 0.707107f, 0f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class SplitterMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                0f,
                size * 0.711325f,
                size,
                Color.argb(160, 255, 128, 192),
                Color.argb(80, 255, 128, 192),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(0f, size * 0.711325f)
            this.lineTo(size / 2, - size * 0.288675f)
            this.lineTo(- size / 2, - size * 0.288675f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class CutterMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.225f
            this.shader = RadialGradient(
                - size / 2,
                size / 2,
                size,
                Color.argb(160, 0, 255, 64),
                Color.argb(80, 0, 255, 64),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.225f
            this.moveTo(- size / 2, size / 2)
            this.lineTo(size / 2, - size / 2)
            this.lineTo(size / 2, size / 2)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class PalmBallMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            val size = strikeZoneWidth * 0.19f
            this.shader = RadialGradient(
                0f,
                size * 0.707107f,
                size,
                Color.argb(160, 160, 96, 255),
                Color.argb(80, 160, 96, 255),
                Shader.TileMode.CLAMP
            )
        }

        private val path = Path().apply {
            val size = strikeZoneWidth * 0.19f
            this.moveTo(0f, - size * 0.707107f)
            this.lineTo(size * 0.707107f, 0f)
            this.lineTo(0f, size * 0.707107f)
            this.lineTo(- size * 0.707107f, 0f)
            this.close()
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.save()
            canvas.translate(x, y)
            if (isLHP) canvas.scale(-1f, 1f)
            canvas.drawPath(path, paint)
            canvas.restore()
        }
    }

    private class PitchTypeNoEntryMark(strikeZoneWidth: Int) : PitchMark(strikeZoneWidth) {
        private val paint = Paint().apply {
            this.color = Color.argb(160, 160, 160, 160)
        }

        override fun paint(canvas: Canvas, x: Float, y: Float, isLHP: Boolean) {
            canvas.drawCircle(x, y, strikeZoneWidth * 0.1f, paint)
        }
    }

    // endregion * Child classes *

    private val effectiveAreaPaint = Paint()
    private val effectiveAreaBorderPaint = Paint()
    private val strikeZoneLinePaint = Paint()
    private val pitchHistoryPaint = Paint()
    private val pitchNumberTextPaint = Paint()
    private val effectiveRect = Rect()
    private val strikeZoneRect = Rect()

    private var receivingPitchLocationX: Boolean = false
    private var lastPitchX: Float? = null
        set(value) {
            field = value
            if (!receivingPitchLocationX) onPitchLocationXChangedListener?.invoke()
        }

    private var receivingPitchLocationY: Boolean = false
    private var lastPitchY: Float? = null
        set(value) {
            field = value
            if (!receivingPitchLocationY) onPitchLocationYChangedListener?.invoke()
        }

    var pitchList: List<PitchInfoDto>? = null

    var pitchLocationX: Float?
        get() {
            return if (lastPitchX == null) {
                null
            } else {
                (lastPitchX!! - strikeZoneRect.left - strikeZoneRect.width() / 2) / (strikeZoneRect.width() / 2)
            }
        }
        set(value) {
            receivingPitchLocationX = true
            lastPitchX = when {
                value == null -> null
                width == 0 -> value
                else -> value * strikeZoneRect.width() / 2 + strikeZoneRect.left + strikeZoneRect.width() / 2
            }
            receivingPitchLocationX = false
            invalidate()
        }

    var pitchLocationY: Float?
        get() {
            return if (lastPitchY == null) {
                null
            } else {
                (lastPitchY!! - strikeZoneRect.top - strikeZoneRect.height() / 2) / (strikeZoneRect.height() / 2)
            }
        }
        set(value) {
            receivingPitchLocationY = true
            lastPitchY = when {
                value == null -> null
                height == 0 -> value
                else -> value * strikeZoneRect.height() / 2 + strikeZoneRect.top + strikeZoneRect.height() / 2
            }
            receivingPitchLocationY = false
            invalidate()
        }

    var isLHP: Boolean? = false
        set(value) {
            field = value
            invalidate()
        }

    var onPitchLocationXChangedListener: (() -> (Unit))? = null
    var onPitchLocationYChangedListener: (() -> (Unit))? = null

    private val pitchMarks = mutableMapOf<PitchType, PitchMark>()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        effectiveAreaPaint.isAntiAlias = true
        effectiveAreaPaint.style = Paint.Style.FILL
        effectiveAreaBorderPaint.isAntiAlias = true
        effectiveAreaBorderPaint.style = Paint.Style.STROKE
        effectiveAreaBorderPaint.strokeWidth = 4.0f
        strikeZoneLinePaint.isAntiAlias = true
        strikeZoneLinePaint.style = Paint.Style.STROKE
        pitchHistoryPaint.isAntiAlias = true
        pitchHistoryPaint.style = Paint.Style.FILL_AND_STROKE
        pitchNumberTextPaint.isAntiAlias = true
        pitchNumberTextPaint.color = Color.argb(255, 96, 96, 96)

        context.obtainStyledAttributes(attrs, R.styleable.PitchLocationView, defStyleAttr, 0)
            .apply {
                effectiveAreaPaint.color =
                    this.getColor(
                        R.styleable.PitchLocationView_effective_area_background,
                        Color.WHITE
                    )
                effectiveAreaBorderPaint.color =
                    this.getColor(
                        R.styleable.PitchLocationView_effective_area_border_color,
                        Color.BLACK
                    )
                strikeZoneLinePaint.color =
                    this.getColor(R.styleable.PitchLocationView_strike_zone_line_color, Color.GRAY)
            }.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (w < h) {
            effectiveRect.left = 0
            effectiveRect.right = w
            effectiveRect.top = (h - w) / 2
            effectiveRect.bottom = effectiveRect.top + w
        } else {
            effectiveRect.top = 0
            effectiveRect.bottom = h
            effectiveRect.left = (w - h) / 2
            effectiveRect.right = effectiveRect.left + h
        }

        strikeZoneRect.left = effectiveRect.left + effectiveRect.width() * 3 / 8
        strikeZoneRect.top = effectiveRect.top + effectiveRect.height() / 3
        strikeZoneRect.right = effectiveRect.left + effectiveRect.width() * 5 / 8
        strikeZoneRect.bottom = effectiveRect.top + effectiveRect.height() * 2 / 3

        if (lastPitchX != null || lastPitchY != null) {
            if (oldw > 0 && oldh > 0) {
                if (lastPitchX != null) lastPitchX = lastPitchX!! * w / oldw
                if (lastPitchY != null) lastPitchY = lastPitchY!! * h / oldh
            } else {
                // When onSizeChanged method is called for first time
                if (lastPitchX != null)
                    lastPitchX =
                        lastPitchX!! * strikeZoneRect.width() / 2 + strikeZoneRect.left + strikeZoneRect.width() / 2
                if (lastPitchY != null)
                    lastPitchY =
                        lastPitchY!! * strikeZoneRect.height() / 2 + strikeZoneRect.top + strikeZoneRect.height() / 2
            }
        }

        pitchMarks[FOUR_SEAM_FASTBALL] = FourSeamFastBallMark(strikeZoneRect.width())
        pitchMarks[TWO_SEAM_FASTBALL] = TwoSeamFastBallMark(strikeZoneRect.width())
        pitchMarks[CURVE_BALL] = CurveBallMark(strikeZoneRect.width())
        pitchMarks[SLIDER] = SliderMark(strikeZoneRect.width())
        pitchMarks[FORK_BALL] = ForkBallMark(strikeZoneRect.width())
        pitchMarks[SHOOT_BALL] = ShootBallMark(strikeZoneRect.width())
        pitchMarks[SINKER] = SinkerMark(strikeZoneRect.width())
        pitchMarks[CHANGE_UP] = ChangeUpMark(strikeZoneRect.width())
        pitchMarks[KNUCKLE_BALL] = KnuckleBallMark(strikeZoneRect.width())
        pitchMarks[SPLITTER] = SplitterMark(strikeZoneRect.width())
        pitchMarks[CUTTER] = CutterMark(strikeZoneRect.width())
        pitchMarks[PALM_BALL] = PalmBallMark(strikeZoneRect.width())
        pitchMarks[NO_ENTRY] = PitchTypeNoEntryMark(strikeZoneRect.width())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(effectiveRect, effectiveAreaPaint)
        canvas.drawRect(
            effectiveRect.left + effectiveAreaBorderPaint.strokeWidth,
            effectiveRect.top + effectiveAreaBorderPaint.strokeWidth,
            effectiveRect.right - effectiveAreaBorderPaint.strokeWidth,
            effectiveRect.bottom - effectiveAreaBorderPaint.strokeWidth,
            effectiveAreaBorderPaint
        )
        canvas.drawRect(strikeZoneRect, strikeZoneLinePaint)

        if (pitchList != null) {
            pitchNumberTextPaint.textSize = effectiveRect.width() * 0.05f
            val metrics = pitchNumberTextPaint.fontMetrics

            var pitchCount = 0
            for (p in pitchList!!) {
                pitchCount++
                val location = p.pitchLocation ?: continue

                val x =
                    (location.x * strikeZoneRect.width() / 2 + strikeZoneRect.left + strikeZoneRect.width() / 2).toFloat()
                val y =
                    (location.y * strikeZoneRect.height() / 2 + strikeZoneRect.top + strikeZoneRect.height() / 2).toFloat()

                pitchMarks[p.pitchType]?.paint(canvas, x, y, isLHP ?: false)

                canvas.drawText(
                    pitchCount.toString(),
                    x - pitchNumberTextPaint.measureText(pitchCount.toString()) / 2,
                    y - (metrics.ascent + metrics.descent) / 2,
                    pitchNumberTextPaint
                )
            }
        }

        if (lastPitchX != null && lastPitchY != null) {
            pitchHistoryPaint.color = Color.argb(255, 255, 0, 0)
            canvas.drawCircle(
                lastPitchX!!,
                lastPitchY!!,
                strikeZoneRect.width() / 8f,
                pitchHistoryPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        event?.let {
            if (it.x >= effectiveRect.left && it.x <= effectiveRect.right && it.y >= effectiveRect.top && it.y <= effectiveRect.bottom) {
                lastPitchX = it.x
                lastPitchY = it.y

                if (it.action == MotionEvent.ACTION_DOWN) VibrateService.makeVibrate(context)
                invalidate()
            }
        }
        return true
    }

    fun clear() {
        lastPitchX = null
        lastPitchY = null
        invalidate()
    }
}