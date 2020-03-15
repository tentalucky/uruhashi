package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.presentation.utility.VibrateService
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextRight
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class PitchSpeedInputView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private companion object {
        val baseColor = Color.argb(255, 0, 87,  75)
        val whitishColor = Color.argb(255, 236, 236, 236)
    }

    private enum class ButtonType {
        NUMBER,
        CLEAR
    }

    private class ButtonStatus(val type: ButtonType, val number: Int) {
        var left: Float = 0f
        var right: Float = 0f
        var top: Float = 0f
        var bottom: Float = 0f
        val caption: String = when (type) {
            ButtonType.NUMBER -> number.toString(); ButtonType.CLEAR -> "C"
        }
    }

    private val windowBackGroundPaint = Paint()
    private val windowTextPaint = Paint()
    private val buttonBorderPaint = Paint()
    private val buttonBackGroundPaint = Paint()
    private val pressedButtonBackGroundPaint = Paint()
    private val buttonCaptionPaint = Paint()
    private val workPaint = Paint()
    private val windowRect = RectF()

    private val buttonStatuses = arrayOf(
        ButtonStatus(ButtonType.NUMBER, 0),
        ButtonStatus(ButtonType.NUMBER, 1),
        ButtonStatus(ButtonType.NUMBER, 2),
        ButtonStatus(ButtonType.NUMBER, 3),
        ButtonStatus(ButtonType.NUMBER, 4),
        ButtonStatus(ButtonType.NUMBER, 5),
        ButtonStatus(ButtonType.NUMBER, 6),
        ButtonStatus(ButtonType.NUMBER, 7),
        ButtonStatus(ButtonType.NUMBER, 8),
        ButtonStatus(ButtonType.NUMBER, 9),
        ButtonStatus(ButtonType.CLEAR, 0)
    )
    private var pressedButton: ButtonStatus? = null

    var pitchSpeed: Int? = null
    var onPitchSpeedChangedListener: (() -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        windowBackGroundPaint.isAntiAlias = true
        windowBackGroundPaint.style = Paint.Style.FILL
        windowBackGroundPaint.color = Color.TRANSPARENT
        buttonBorderPaint.isAntiAlias = true
        buttonBorderPaint.style = Paint.Style.STROKE
        buttonBorderPaint.strokeWidth = 2f
        buttonBorderPaint.color = Color.WHITE
        buttonBackGroundPaint.isAntiAlias = true
        buttonBackGroundPaint.style = Paint.Style.FILL
        buttonBackGroundPaint.color = Color.argb(255, 220, 220, 220)
        pressedButtonBackGroundPaint.isAntiAlias = true
        pressedButtonBackGroundPaint.style = Paint.Style.FILL
        pressedButtonBackGroundPaint.color = Color.argb(255, 192, 192, 192)
        buttonCaptionPaint.isAntiAlias = true
        buttonCaptionPaint.style = Paint.Style.STROKE
        buttonCaptionPaint.color = Color.BLACK

        workPaint.isAntiAlias = true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val windowWidth: Float = w - margin * 2
        val windowHeight: Float = (h - margin * 2) * 0.15f

        windowRect.left = margin
        windowRect.right = windowRect.left + windowWidth
        windowRect.top = margin
        windowRect.bottom = windowRect.top + windowHeight
        windowTextPaint.textSize = windowHeight * 0.75f
        windowTextPaint.color = whitishColor

        val buttonWidth: Float = (w - margin * 4) / 3
        val buttonHeight: Float = (h - margin * 6 - windowHeight) / 4

        buttonStatuses.forEach {
            if (it.type == ButtonType.NUMBER) {
                val colPos = if (it.number == 0) 0 else (it.number - 1) % 3
                val rowPos = if (it.number == 0) 3 else 2 - (it.number - 1) / 3
                it.left = (colPos + 1) * margin + colPos * buttonWidth
                it.top = (rowPos + 1) * margin + rowPos * buttonHeight + windowHeight
                it.right = it.left + buttonWidth
                it.bottom = it.top + buttonHeight
            } else {
                it.left = 2 * margin + buttonWidth
                it.top = 4 * margin + 3 * buttonHeight + windowHeight
                it.right = it.left + buttonWidth * 2 + margin
                it.bottom = it.top + buttonHeight
            }
        }

        buttonCaptionPaint.textSize = buttonHeight / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        workPaint.color = baseColor
        workPaint.style = Paint.Style.FILL
        canvas?.drawRoundRect(windowRect, 1f, 1f, workPaint)

        canvas?.drawTextRight(
            (if (pitchSpeed == null) "---" else pitchSpeed.toString()) + " km/h",
            windowRect.right - context.dpToPx(4f),
            (windowRect.top + windowRect.bottom) / 2 + windowTextPaint.fontMetrics.descent,
            windowTextPaint
        )

        buttonStatuses.forEach {
            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f,
                if (it == pressedButton) pressedButtonBackGroundPaint else buttonBackGroundPaint
            )
            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f, buttonBorderPaint
            )
            canvas?.drawTextWithinArea(it.caption, it.left, it.right, (it.top + it.bottom) / 2, 2f, buttonCaptionPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (pressedButton == null) {
                    for (b in buttonStatuses) {
                        if (b.left < event.x && event.x < b.right && b.top < event.y && event.y < b.bottom) {
                            pressedButton = b
                            VibrateService.makeVibrate(context)
                            break
                        }
                    }
                }
            }
            MotionEvent.ACTION_MOVE -> {
                pressedButton?.let {
                    if (event.x <= it.left || it.right <= event.x || event.y <= it.top || it.bottom <= event.y) {
                        pressedButton = null
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                pressedButton?.let {
                    when (it.type) {
                        ButtonType.NUMBER -> {
                            if (pitchSpeed == null)
                                pitchSpeed = it.number
                            else if (pitchSpeed!! < 100)
                                pitchSpeed = (pitchSpeed.toString() + it.number.toString()).toInt()
                        }
                        ButtonType.CLEAR -> {
                            pitchSpeed = null
                        }
                    }
                    pressedButton = null
                    onPitchSpeedChangedListener?.invoke()
                }
            }
        }
        invalidate()

        return true
    }
}