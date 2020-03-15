package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.MotionEvent.*
import android.view.View
import mahoroba.uruhashi.domain.game.AngularCoordinate
import mahoroba.uruhashi.domain.game.BattedBallDirection
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.toPixel
import kotlin.math.*

class BattedBallDirectionView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus {
        companion object {
            val backGroundPaint = Paint().apply {
                this.isAntiAlias = true
                this.style = Paint.Style.FILL
                this.color = Color.argb(255, 224, 224, 224)
            }
            val pressedBackGroundPaint = Paint().apply {
                this.isAntiAlias = true
                this.style = Paint.Style.FILL
                this.color = Color.argb(255, 160, 160, 160)
            }
            val captionPaint = Paint().apply {
                this.isAntiAlias = true
                this.color = Color.BLACK
            }
        }

        val rect = RectF()
        var caption: String = ""
        var isPressed: Boolean = false

        fun draw(canvas: Canvas?) {
            val metrics = captionPaint.fontMetrics
            canvas?.drawRect(
                rect.left,
                rect.top,
                rect.right,
                rect.bottom,
                if (isPressed) pressedBackGroundPaint else backGroundPaint
            )
            canvas?.drawText(
                caption,
                rect.left + rect.width() / 2 - captionPaint.measureText(caption) / 2,
                rect.top + rect.height() / 2 - (metrics.ascent + metrics.descent) / 2,
                captionPaint
            )
        }
    }

    private val effectiveAreaPaint = Paint()
    private val controlAreaPaint = Paint()
    private val foulLinePaint = Paint()
    private val locusPaint = Paint()
    private val ballPaint = Paint()

    private val effectiveRect = RectF()
    private val controlRect = RectF()
    private val fieldRect = RectF()

    private val clearButtonStatus = ButtonStatus().apply { this.caption = "Clear" }
    private val undoButtonStatus = ButtonStatus().apply { this.caption = "Undo" }
    private val deflectButtonStatus = ButtonStatus().apply { this.caption = "Deflect" }

    private var homeX: Float = 0f
    private var homeY: Float = 0f
    private var firstX: Float = 0f
    private var firstY: Float = 0f
    private var secondX: Float = 0f
    private var secondY: Float = 0f
    private var thirdX: Float = 0f
    private var thirdY: Float = 0f
    private var leftPoleX: Float = 0f
    private var leftPoleY: Float = 0f
    private var rightPoleX: Float = 0f
    private var rightPoleY: Float = 0f

    private var isInputted: Boolean = false
    private var directionX: Float = -1f
    private var directionY: Float = -1f
    private var points = ArrayList<Pair<Float, Float>>()

    var direction: BattedBallDirection?
        get() {
            val traces = ArrayList<AngularCoordinate>()

            points.forEach {
                val ang = convertCoordinatePixelToAngular(it.first, it.second, homeX, homeY, abs(secondY - homeY))
                traces.add(AngularCoordinate(ang.first, ang.second))
            }

            if (isInputted) {
                val ang = convertCoordinatePixelToAngular(directionX, directionY, homeX, homeY, abs(secondY - homeY))
                traces.add(AngularCoordinate(ang.first, ang.second))
            }

            return BattedBallDirection(traces)
        }
        set(value) {
            points.clear()
            isInputted = false

            if (value == null) {
                invalidate()
                return
            }

            // If view size has not been initialized, assume the distance between home base and second base
            // is 1000px temporarily, and home base is located at (0, 1000).
            val scale = if (width == 0 || height == 0) 1000f else abs(secondY - homeY)
            val offsetX = if (width == 0 || height == 0) 0f else homeX
            val offsetY = if (width == 0 || height == 0) 1000f else homeY

            value.tracePoints.forEach {
                val p = it.toPixel(offsetX, offsetY, scale)
                points.add(p)
            }

            if (points.size > 0) {
                directionX = points.last().first
                directionY = points.last().second
                points.remove(points.last())
                isInputted = true
            }

            invalidate()
        }

    private val directionChangedListener = ArrayList<(() -> Unit)>()
    fun addOnDirectionChangedListener(listener: () -> Unit) {
        directionChangedListener.add(listener)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        effectiveAreaPaint.isAntiAlias = true
        effectiveAreaPaint.style = Paint.Style.FILL
        effectiveAreaPaint.color = Color.WHITE

        controlAreaPaint.isAntiAlias = true
        controlAreaPaint.style = Paint.Style.FILL_AND_STROKE
        controlAreaPaint.color = Color.WHITE

        foulLinePaint.isAntiAlias = true
        foulLinePaint.style = Paint.Style.STROKE
        foulLinePaint.color = Color.BLACK

        locusPaint.isAntiAlias = true
        locusPaint.style = Paint.Style.STROKE
        locusPaint.color = Color.RED
        locusPaint.strokeWidth = 2.0f

        ballPaint.isAntiAlias = true
        ballPaint.style = Paint.Style.FILL
        ballPaint.color = Color.RED
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (h >= w) {
            effectiveRect.left = 0f
            effectiveRect.right = w.toFloat()
            effectiveRect.top = (h - w) / 2f
            effectiveRect.bottom = effectiveRect.top + w
        } else {
            effectiveRect.left = (w - h) / 2f
            effectiveRect.right = effectiveRect.left + h
            effectiveRect.top = 0f
            effectiveRect.bottom = h.toFloat()
        }

        controlRect.left = effectiveRect.left
        controlRect.right = effectiveRect.right
        controlRect.top = effectiveRect.bottom - effectiveRect.height() / 16f
        controlRect.bottom = effectiveRect.bottom

        val buttonMargin = 2f
        val buttonWidth = controlRect.width() / 3f - buttonMargin * 2

        deflectButtonStatus.rect.left = controlRect.left
        deflectButtonStatus.rect.right = deflectButtonStatus.rect.left + buttonWidth
        deflectButtonStatus.rect.top = controlRect.top + buttonMargin
        deflectButtonStatus.rect.bottom = controlRect.bottom - buttonMargin

        undoButtonStatus.rect.left = deflectButtonStatus.rect.right + buttonMargin * 2
        undoButtonStatus.rect.right = undoButtonStatus.rect.left + buttonWidth
        undoButtonStatus.rect.top = controlRect.top + buttonMargin
        undoButtonStatus.rect.bottom = controlRect.bottom - buttonMargin

        clearButtonStatus.rect.left = undoButtonStatus.rect.right + buttonMargin * 2
        clearButtonStatus.rect.right = clearButtonStatus.rect.left + buttonWidth
        clearButtonStatus.rect.top = controlRect.top + buttonMargin
        clearButtonStatus.rect.bottom = controlRect.bottom - buttonMargin

        ButtonStatus.captionPaint.textSize = effectiveRect.width() / 32

        val oldHomeX = if (oldh > 0) homeX else 0f
        val oldHomeY = if (oldh > 0) homeY else 1000f
        val oldSecondY = if (oldh > 0) secondY else 0f

        homeX = effectiveRect.left + effectiveRect.width() / 2
        homeY = effectiveRect.top + effectiveRect.height() * 13 / 16

        leftPoleX = effectiveRect.left + effectiveRect.width() / 16
        leftPoleY = effectiveRect.top + effectiveRect.height() * 6 / 16

        rightPoleX = effectiveRect.right - effectiveRect.width() / 16
        rightPoleY = leftPoleY

        firstX = homeX + (rightPoleX - homeX) * 0.3f
        firstY = homeY - (rightPoleX - homeX) * 0.3f

        secondX = homeX
        secondY = homeY - (homeY - firstY) * 2

        thirdX = homeX - (rightPoleX - homeX) * 0.3f
        thirdY = firstY

        fieldRect.top = homeY - (homeX - leftPoleX) * 1.725f
        fieldRect.bottom = fieldRect.top + (homeX - leftPoleX) * (1.725f - 0.6728448f) * 2
        fieldRect.left = homeX - (homeX - leftPoleX) * (1.725f - 0.6728448f)
        fieldRect.right = homeX + (homeX - leftPoleX) * (1.725f - 0.6728448f)

        val newPoints = ArrayList<Pair<Float, Float>>()
        points.forEach {
            newPoints.add(relocate(it.first, it.second, oldHomeX, oldHomeY, oldSecondY, homeX, homeY, secondY))
        }
        points = newPoints

        val newDirection = relocate(directionX, directionY, oldHomeX, oldHomeY, oldSecondY, homeX, homeY, secondY)
        directionX = newDirection.first
        directionY = newDirection.second
    }

    private fun relocate(
        oldX: Float, oldY: Float, oldHomeX: Float, oldHomeY: Float, oldSecondY: Float,
        newHomeX: Float, newHomeY: Float, newSecondY: Float
    ): Pair<Float, Float> {
        val angCoord = convertCoordinatePixelToAngular(
            oldX, oldY, oldHomeX, oldHomeY, abs(oldHomeY - oldSecondY)
        )
        val newCoord = convertCoordinateAngularToPixel(
            angCoord.first, angCoord.second, newHomeX, newHomeY, abs(newSecondY - newHomeY)
        )
        return newCoord
    }

    private fun convertCoordinatePixelToAngular(
        px: Float,
        py: Float,
        ox: Float,
        oy: Float,
        scale: Float
    ): Pair<Float, Float> {
        val distance = sqrt((px - ox) * (px - ox) + (py - oy) * (py - oy)) / scale
        val angle = atan2(-py - (-oy), px - ox)
        return Pair(angle, distance)
    }

    private fun convertCoordinateAngularToPixel(
        angle: Float,
        distance: Float,
        ox: Float,
        oy: Float,
        scale: Float
    ): Pair<Float, Float> {
        val x = cos(angle) * distance * scale + ox
        val y = -sin(angle) * distance * scale + oy
        return Pair(x, y)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawRect(effectiveRect, effectiveAreaPaint)
        canvas?.drawRect(controlRect, controlAreaPaint)

        deflectButtonStatus.draw(canvas)
        undoButtonStatus.draw(canvas)
        clearButtonStatus.draw(canvas)

        canvas?.drawLine(homeX, homeY, leftPoleX, leftPoleY, foulLinePaint)
        canvas?.drawLine(homeX, homeY, rightPoleX, rightPoleY, foulLinePaint)
        canvas?.drawLine(firstX, firstY, secondX, secondY, foulLinePaint)
        canvas?.drawLine(secondX, secondY, thirdX, thirdY, foulLinePaint)
        canvas?.drawArc(fieldRect, 198.1157777f, 143.768446f, false, foulLinePaint)

        var lastX = homeX
        var lastY = homeY
        points.forEach {
            canvas?.drawLine(lastX, lastY, it.first, it.second, locusPaint)
            canvas?.drawCircle(it.first, it.second, effectiveRect.width() / 64, locusPaint)
            lastX = it.first
            lastY = it.second
        }
        if (isInputted) {
            canvas?.drawLine(lastX, lastY, directionX, directionY, locusPaint)
            canvas?.drawCircle(directionX, directionY, effectiveRect.width() / 64, ballPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            ACTION_DOWN -> {
                if (deflectButtonStatus.rect.contains(event.x, event.y)) {
                    deflectButtonStatus.isPressed = true
                    invalidate()
                } else if (undoButtonStatus.rect.contains(event.x, event.y)) {
                    undoButtonStatus.isPressed = true
                    invalidate()
                } else if (clearButtonStatus.rect.contains(event.x, event.y)) {
                    clearButtonStatus.isPressed = true
                    invalidate()
                } else if (effectiveRect.contains(event.x, event.y) && !controlRect.contains(event.x, event.y)) {
                    directionX = event.x
                    directionY = event.y
                    isInputted = true
                    directionChangedListener.forEach { it.invoke() }
                    invalidate()
                }
                makeVibrate(context)
            }

            ACTION_MOVE -> {
                if (effectiveRect.contains(event.x, event.y) && !controlRect.contains(event.x, event.y)) {
                    directionX = event.x
                    directionY = event.y
                    isInputted = true
                    directionChangedListener.forEach { it.invoke() }
                    invalidate()
                }
            }

            ACTION_UP -> {
                if (deflectButtonStatus.isPressed) {
                    deflectButtonStatus.isPressed = false
                    if (deflectButtonStatus.rect.contains(event.x, event.y) && isInputted) {
                        points.add(Pair(directionX, directionY))
                    }
                    directionChangedListener.forEach { it.invoke() }
                    invalidate()
                }

                if (undoButtonStatus.isPressed) {
                    undoButtonStatus.isPressed = false
                    if (undoButtonStatus.rect.contains(event.x, event.y)) {
                        if (isInputted) {
                            isInputted = false
                        } else if (points.size > 0) {
                            points.remove(points.last())
                        }
                    }
                    directionChangedListener.forEach { it.invoke() }
                    invalidate()
                }

                if (clearButtonStatus.isPressed) {
                    clearButtonStatus.isPressed = false
                    if (clearButtonStatus.rect.contains(event.x, event.y)) {
                        isInputted = false
                        points.clear()
                    }
                    directionChangedListener.forEach { it.invoke() }
                    invalidate()
                }
            }
        }

        return true
    }
}