package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.presentation.FieldPlayInputViewModel
import mahoroba.uruhashi.presentation.exClasses.PathEx
import mahoroba.uruhashi.presentation.utility.VibrateService

class RunnerProgressInputView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val backgroundPaint = Paint()
    private val baseBorderPaint = Paint()
    private val passedBasePaint = Paint()
    private val unreachedBasePaint = Paint()
    private val disableBasePaint = Paint()
    private val startBasePaint = Paint()
    private val outSignPaint = Paint()
    private val baseRunTracePaint = Paint()
    private val baseRunArrowPaint = Paint()
    private val outTracePaint = Paint()
    private val outArrowPaint = Paint()
    private val outButtonTurnOnPaint = Paint()
    private val outButtonTurnOffPaint = Paint()
    private val outButtonTextPaint = Paint()
    private val clearButtonTextPaint = Paint()

    private val backgroundRect = RectF()
    private val firstBasePath = PathEx()
    private val secondBasePath = PathEx()
    private val thirdBasePath = PathEx()
    private val homeBasePath = PathEx()
    private val baseRunArrowPath = Path()
    private val outButtonPath = PathEx()
    private val clearButtonPath = PathEx()

    private var firstX: Float = 0f
    private var firstY: Float = 0f
    private var secondX: Float = 0f
    private var secondY: Float = 0f
    private var thirdX: Float = 0f
    private var thirdY: Float = 0f
    private var homeX: Float = 0f
    private var homeY: Float = 0f

    private var outButtonTextX: Float = 0f
    private var outButtonTextY: Float = 0f

    private var defaultBase: Int = 0

    var latestStatus: FieldPlayInputViewModel.RunnerMovement? = null

    private val baseLastPlayedAt: Int
        get() = latestStatus?.basePlayed ?: defaultBase
    private val hasBeenPutOut: Boolean
        get() = latestStatus?.putout ?: false

    private val statusChangingListener = ArrayList<(FieldPlayInputViewModel.RunnerMovement?) -> Unit>()
    fun addOnStatusChangingListener(listener: (FieldPlayInputViewModel.RunnerMovement?) -> Unit) {
        statusChangingListener.add(listener)
    }

    var runnersMovement: FieldPlayInputViewModel.RunnerMovement? = null
        set(value) {
            field = value
            movementChangedListener.forEach { it.invoke(field) }
            invalidate()
        }

    private val movementChangedListener = ArrayList<((FieldPlayInputViewModel.RunnerMovement?) -> Unit)>()
    fun addOnMovementChangedListener(listener: (FieldPlayInputViewModel.RunnerMovement?) -> Unit) {
        movementChangedListener.add(listener)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        backgroundPaint.color = Color.TRANSPARENT
        backgroundPaint.style = Paint.Style.FILL
        backgroundPaint.isAntiAlias = true

        baseBorderPaint.color = Color.BLACK
        baseBorderPaint.style = Paint.Style.STROKE
        baseBorderPaint.strokeWidth = 2.0f
        baseBorderPaint.isAntiAlias = true

        passedBasePaint.color = Color.argb(255, 220, 220, 220)
        passedBasePaint.style = Paint.Style.FILL
        passedBasePaint.isAntiAlias = true

        disableBasePaint.color = Color.argb(255, 192, 192, 192)
        disableBasePaint.style = Paint.Style.FILL_AND_STROKE
        disableBasePaint.isAntiAlias = true

        unreachedBasePaint.color = Color.WHITE
        unreachedBasePaint.style = Paint.Style.FILL
        unreachedBasePaint.isAntiAlias = true

        startBasePaint.color = Color.argb(255, 128, 128, 255)
        startBasePaint.style = Paint.Style.FILL_AND_STROKE
        startBasePaint.strokeWidth = 5.0f
        startBasePaint.isAntiAlias = true

        outSignPaint.color = Color.argb(255, 255, 128, 128)
        outSignPaint.style = Paint.Style.STROKE
        outSignPaint.strokeWidth = 5.0f
        outSignPaint.isAntiAlias = true

        baseRunTracePaint.color = Color.argb(255, 0, 255, 0)
        baseRunTracePaint.style = Paint.Style.STROKE
        baseRunTracePaint.strokeWidth = 5.0f
        baseRunTracePaint.isAntiAlias = true

        baseRunArrowPaint.color = Color.argb(255, 0, 255, 0)
        baseRunArrowPaint.style = Paint.Style.FILL
        baseRunArrowPaint.strokeWidth = 5.0f
        baseRunArrowPaint.isAntiAlias = true

        outTracePaint.color = Color.argb(255, 255, 0, 0)
        outTracePaint.style = Paint.Style.STROKE
        outTracePaint.strokeWidth = 5.0f
        outTracePaint.isAntiAlias = true

        outArrowPaint.color = Color.argb(255, 255, 0, 0)
        outArrowPaint.style = Paint.Style.FILL
        outArrowPaint.strokeWidth = 5.0f
        outArrowPaint.isAntiAlias = true

        outButtonTurnOnPaint.color = Color.RED
        outButtonTurnOnPaint.style = Paint.Style.FILL
        outButtonTurnOnPaint.strokeWidth = 4.0f
        outButtonTurnOnPaint.isAntiAlias = true

        outButtonTurnOffPaint.color = Color.argb(255, 220, 220, 220)
        outButtonTurnOffPaint.style = Paint.Style.FILL
        outButtonTurnOffPaint.strokeWidth = 4.0f
        outButtonTurnOffPaint.isAntiAlias = true

        outButtonTextPaint.color = Color.WHITE
        outButtonTextPaint.isAntiAlias = true

        clearButtonTextPaint.color = Color.argb(255, 128, 128, 255)
        clearButtonTextPaint.isAntiAlias = true

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.RunnerProgressInputView, defStyleAttr, 0)
        defaultBase = typedArray.getInt(R.styleable.RunnerProgressInputView_default_base, 0)
        runnersMovement = FieldPlayInputViewModel.RunnerMovement(defaultBase, false)
        typedArray.recycle()
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        backgroundRect.left = 0f
        backgroundRect.top = 0f
        backgroundRect.right = w.toFloat()
        backgroundRect.bottom = h.toFloat()

        firstX = w * 0.75f
        firstY = h * 0.5f
        secondX = w * 0.5f
        secondY = h * 0.25f
        thirdX = w * 0.25f
        thirdY = h * 0.5f
        homeX = w * 0.5f
        homeY = h * 0.75f

        firstBasePath.let {
            it.reset()
            it.moveTo(w * 0.5f, h * 0.75f - 4f)
            it.lineTo(w * 0.75f - 4, h * 0.5f)
            it.lineTo(w * 0.5f, h * 0.25f + 4)
            it.lineTo(w * 0.25f + 4, h * 0.5f)
            it.lineTo(w * 0.5f, h * 0.75f - 4f)
        }
        secondBasePath.set(firstBasePath)
        thirdBasePath.set(firstBasePath)
        homeBasePath.set(firstBasePath)

        firstBasePath.offset(w * 0.25f, 0f)
        secondBasePath.offset(0f, h * -0.25f)
        thirdBasePath.offset(w * -0.25f, 0f)
        homeBasePath.offset(0f, h * 0.25f)

        baseRunArrowPath.let {
            it.reset()
            it.moveTo(0f, 0f)
            it.lineTo(w * -0.08f, h * 0.12f)
            it.lineTo(w * 0.08f, h * 0.12f)
            it.close()
        }

        outButtonPath.let {
            it.reset()
            it.moveTo(w * 0.5f + 4f, h - 2f)
            it.lineTo(w - 2f, h - 2f)
            it.lineTo(w - 2f, h * 0.5f + 4f)
            it.close()
        }
        outButtonTextPaint.textSize = w * 0.125f
        outButtonTextX = w * 0.75f
        outButtonTextY = h * 0.75f + 4f

        clearButtonPath.let {
            it.reset()
            it.moveTo(0f, h * 1f)
            it.lineTo(w * 0.5f, h * 1f)
            it.lineTo(0f, h * 0.5f)
            it.close()
        }
        clearButtonTextPaint.textSize = w * 0.15f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw background
        canvas?.drawRect(backgroundRect, backgroundPaint)

        // Draw background of bases
        if (isEnabled) {
            canvas?.drawPath(firstBasePath, if (defaultBase > 1) passedBasePaint else unreachedBasePaint)
            canvas?.drawPath(firstBasePath, baseBorderPaint)
            canvas?.drawPath(secondBasePath, if (defaultBase > 2) passedBasePaint else unreachedBasePaint)
            canvas?.drawPath(secondBasePath, baseBorderPaint)
            canvas?.drawPath(thirdBasePath, if (defaultBase > 3) passedBasePaint else unreachedBasePaint)
            canvas?.drawPath(thirdBasePath, baseBorderPaint)
            canvas?.drawPath(homeBasePath, unreachedBasePaint)
            canvas?.drawPath(homeBasePath, baseBorderPaint)
        } else {
            canvas?.drawPath(firstBasePath, disableBasePaint)
            canvas?.drawPath(secondBasePath, disableBasePaint)
            canvas?.drawPath(thirdBasePath, disableBasePaint)
            canvas?.drawPath(homeBasePath, disableBasePaint)
        }

        // Draw starting base sign
        if (isEnabled) {
            if (baseLastPlayedAt >= 1 && defaultBase < 1) {
                canvas?.drawLine(homeX, homeY, firstX, firstY, startBasePaint)
            }
            if (baseLastPlayedAt >= 2 && defaultBase < 2) {
                canvas?.drawLine(firstX, firstY, secondX, secondY, startBasePaint)
            }
            if (baseLastPlayedAt >= 3 && defaultBase < 3) {
                canvas?.drawLine(secondX, secondY, thirdX, thirdY, startBasePaint)
            }
            if (baseLastPlayedAt >= 4 && defaultBase < 4) {
                canvas?.drawLine(thirdX, thirdY, homeX, homeY, startBasePaint)
            }

            if (!hasBeenPutOut) {
                when (baseLastPlayedAt) {
                    0 -> canvas?.drawCircle(homeX, homeY, width * 0.0625f, startBasePaint)
                    1 -> canvas?.drawCircle(firstX, firstY, width * 0.0625f, startBasePaint)
                    2 -> canvas?.drawCircle(secondX, secondY, width * 0.0625f, startBasePaint)
                    3 -> canvas?.drawCircle(thirdX, thirdY, width * 0.0625f, startBasePaint)
                }
            } else {
                canvas?.save()
                when (baseLastPlayedAt) {
                    1 -> canvas?.translate(firstX, firstY)
                    2 -> canvas?.translate(secondX, secondY)
                    3 -> canvas?.translate(thirdX, thirdY)
                    4 -> canvas?.translate(homeX, homeY)
                }
                canvas?.drawLine(width * -0.125f, height * -0.125f, width * 0.125f, height * 0.125f, outSignPaint)
                canvas?.drawLine(width * -0.125f, height * 0.125f, width * 0.125f, height * -0.125f, outSignPaint)
                canvas?.restore()
            }
        }

        // Draw trace line of runners
        if (runnersMovement?.putout == true) {
            when (baseLastPlayedAt) {
                0 -> canvas?.drawLine(homeX, homeY, firstX, firstY, outTracePaint)
                1 -> canvas?.drawLine(firstX, firstY, secondX, secondY, outTracePaint)
                2 -> canvas?.drawLine(secondX, secondY, thirdX, thirdY, outTracePaint)
                3 -> canvas?.drawLine(thirdX, thirdY, homeX, homeY, outTracePaint)
            }
        } else if (runnersMovement != null) {
            if (baseLastPlayedAt <= 0 && runnersMovement!!.basePlayed >= 1) {
                canvas?.drawLine(homeX, homeY, firstX, firstY, baseRunTracePaint)
            }
            if (baseLastPlayedAt <= 1 && runnersMovement!!.basePlayed >= 2) {
                canvas?.drawLine(firstX, firstY, secondX, secondY, baseRunTracePaint)
            }
            if (baseLastPlayedAt <= 2 && runnersMovement!!.basePlayed >= 3) {
                canvas?.drawLine(secondX, secondY, thirdX, thirdY, baseRunTracePaint)
            }
            if (baseLastPlayedAt <= 3 && runnersMovement!!.basePlayed >= 4) {
                canvas?.drawLine(thirdX, thirdY, homeX, homeY, baseRunTracePaint)
            }
        }

        // Draw arrows
        if (runnersMovement != null && (runnersMovement!!.putout || baseLastPlayedAt < runnersMovement!!.basePlayed)) {
            canvas?.save()
            when (runnersMovement?.basePlayed) {
                1 -> {
                    canvas?.translate(firstX, firstY)
                    canvas?.rotate(if (baseLastPlayedAt < runnersMovement!!.basePlayed) 45f else 135f, 0f, 0f)
                }
                2 -> {
                    canvas?.translate(secondX, secondY)
                    canvas?.rotate(if (baseLastPlayedAt < runnersMovement!!.basePlayed) 315f else 45f, 0f, 0f)
                }
                3 -> {
                    canvas?.translate(thirdX, thirdY)
                    canvas?.rotate(if (baseLastPlayedAt < runnersMovement!!.basePlayed) 225f else 315f, 0f, 0f)
                }
                4 -> {
                    canvas?.translate(homeX, homeY)
                    canvas?.rotate(135f, 0f, 0f)
                }
            }
            canvas?.drawPath(baseRunArrowPath, if (runnersMovement!!.putout) outArrowPaint else baseRunArrowPaint)
            canvas?.restore()
        }

        // Draw an out button
        canvas?.drawPath(outButtonPath, if (runnersMovement?.putout == true) outButtonTurnOnPaint else outButtonTurnOffPaint)
        canvas?.save()
        canvas?.translate(outButtonTextX, outButtonTextX)
        canvas?.rotate(315f, 0f, 0f)
        canvas?.drawText(
            "OUT",
            -outButtonTextPaint.measureText("OUT") / 2f,
            -outButtonTextPaint.fontMetrics.ascent,
            outButtonTextPaint
        )
        canvas?.restore()

        // Draw a clear button
        clearButtonTextPaint.color = if (isEnabled) Color.argb(255, 128, 128, 255) else Color.LTGRAY
        canvas?.drawText("CLR", 4f, height - clearButtonTextPaint.fontMetrics.descent - 4f, clearButtonTextPaint)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (!isEnabled) return true
        if (hasBeenPutOut || baseLastPlayedAt == 4) return true

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val which = when {
                    firstBasePath.contains(event.x, event.y) -> 1
                    secondBasePath.contains(event.x, event.y) -> 2
                    thirdBasePath.contains(event.x, event.y) -> 3
                    homeBasePath.contains(event.x, event.y) -> 4
                    outButtonPath.contains(event.x, event.y) -> 5
                    clearButtonPath.contains(event.x, event.y) -> 6
                    else -> 0
                }

                var newBase: Int = runnersMovement?.basePlayed ?: 0
                var newPutOut: Boolean = runnersMovement?.putout ?: false
                when (which) {
                    in 1..4 -> {
                        if (runnersMovement?.putout == true) {
                            if (which == baseLastPlayedAt || which == baseLastPlayedAt + 1) newBase = which
                        } else {
                            if (which >= baseLastPlayedAt) newBase = which
                        }
                    }
                    5 -> {
                        newPutOut = !(runnersMovement?.putout ?: false)
                        newBase = baseLastPlayedAt + 1
                    }
                    6 -> {
                        newPutOut = false
                        newBase = baseLastPlayedAt
                    }
                }
                if (which > 0) VibrateService.makeVibrate(context)

                runnersMovement = FieldPlayInputViewModel.RunnerMovement(newBase, newPutOut)
            }
        }

        return true
    }
}