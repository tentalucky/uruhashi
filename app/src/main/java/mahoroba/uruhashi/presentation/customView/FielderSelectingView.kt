package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import mahoroba.uruhashi.domain.game.FieldPosition
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class FielderSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val position: FieldPosition, val caption: String) {
        var left: Float = 0f
        var width: Float = 0f
        var top: Float = 0f
        var height: Float = 0f
        val right: Float
            get() = left + width
        val bottom: Float
            get() = top + height
    }

    private val windowBackGroundPaint = Paint()
    private val buttonBorderPaint = Paint()
    private val buttonBackgroundPaint = Paint()
    private val pressedButtonBackgroundPaint = Paint()
    private val buttonCaptionPaint = Paint()

    private val buttons = mapOf(
        FieldPosition.PITCHER to ButtonStatus(FieldPosition.PITCHER, "1"),
        FieldPosition.CATCHER to ButtonStatus(FieldPosition.CATCHER, "2"),
        FieldPosition.FIRST_BASEMAN to ButtonStatus(FieldPosition.FIRST_BASEMAN, "3"),
        FieldPosition.SECOND_BASEMAN to ButtonStatus(FieldPosition.SECOND_BASEMAN, "4"),
        FieldPosition.THIRD_BASEMAN to ButtonStatus(FieldPosition.THIRD_BASEMAN, "5"),
        FieldPosition.SHORT_STOP to ButtonStatus(FieldPosition.SHORT_STOP, "6"),
        FieldPosition.LEFT_FIELDER to ButtonStatus(FieldPosition.LEFT_FIELDER, "7"),
        FieldPosition.CENTER_FIELDER to ButtonStatus(FieldPosition.CENTER_FIELDER, "8"),
        FieldPosition.RIGHT_FIELDER to ButtonStatus(FieldPosition.RIGHT_FIELDER, "9")
    )
    private var pressedButton: ButtonStatus? = null

    interface OnFielderButtonClickListener {
        fun onFielderButtonClicked(position: FieldPosition)
    }
    var onFieldButtonClickedListener: OnFielderButtonClickListener? = null

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
        buttonBackgroundPaint.isAntiAlias = true
        buttonBackgroundPaint.style = Paint.Style.FILL
        buttonBackgroundPaint.color = Color.argb(255, 220, 220, 220)
        pressedButtonBackgroundPaint.isAntiAlias = true
        pressedButtonBackgroundPaint.style = Paint.Style.FILL
        pressedButtonBackgroundPaint.color = Color.argb(255, 192, 192, 192)
        buttonCaptionPaint.isAntiAlias = true
        buttonCaptionPaint.style = Paint.Style.STROKE
        buttonCaptionPaint.color = Color.BLACK
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * 4) / 3f
        val buttonHeight: Float = (h - margin * 5) / 4f

        buttons[FieldPosition.LEFT_FIELDER]?.let {
            it.left = margin
            it.width = buttonWidth
            it.top = margin
            it.height = buttonHeight
        }

        buttons[FieldPosition.CENTER_FIELDER]?.let {
            it.left = (buttons[FieldPosition.LEFT_FIELDER]?.right ?: 0f) + margin
            it.width = buttonWidth
            it.top = buttons[FieldPosition.LEFT_FIELDER]?.top ?: 0f
            it.height = buttonHeight
        }

        buttons[FieldPosition.RIGHT_FIELDER]?.let {
            it.left = (buttons[FieldPosition.CENTER_FIELDER]?.right ?: 0f) + margin
            it.width = buttonWidth
            it.top = buttons[FieldPosition.CENTER_FIELDER]?.top ?: 0f
            it.height = buttonHeight
        }

        buttons[FieldPosition.SHORT_STOP]?.let {
            it.left = margin * 1.5f + buttonWidth / 2
            it.width = buttonWidth
            it.top = (buttons[FieldPosition.LEFT_FIELDER]?.bottom ?: 0f) + margin
            it.height = buttonHeight
        }

        buttons[FieldPosition.SECOND_BASEMAN]?.let {
            it.left = (buttons[FieldPosition.SHORT_STOP]?.right ?: 0f) + margin
            it.width = buttonWidth
            it.top = buttons[FieldPosition.SHORT_STOP]?.top ?: 0f
            it.height = buttonHeight
        }

        buttons[FieldPosition.THIRD_BASEMAN]?.let {
            it.left = margin
            it.width = buttonWidth
            it.top = (buttons[FieldPosition.SHORT_STOP]?.bottom ?: 0f) + margin
            it.height = buttonHeight
        }

        buttons[FieldPosition.PITCHER]?.let {
            it.left = (buttons[FieldPosition.THIRD_BASEMAN]?.right ?: 0f) + margin
            it.width = buttonWidth
            it.top = buttons[FieldPosition.THIRD_BASEMAN]?.top ?: 0f
            it.height = buttonHeight
        }

        buttons[FieldPosition.FIRST_BASEMAN]?.let {
            it.left = (buttons[FieldPosition.PITCHER]?.right ?: 0f) + margin
            it.width = buttonWidth
            it.top = buttons[FieldPosition.THIRD_BASEMAN]?.top ?: 0f
            it.height = buttonHeight
        }

        buttons[FieldPosition.CATCHER]?.let {
            it.left = w / 2 - buttonWidth / 2
            it.width = buttonWidth
            it.top = (buttons[FieldPosition.THIRD_BASEMAN]?.bottom ?: 0f) + margin
            it.height = buttonHeight
        }

        buttonCaptionPaint.textSize = buttonHeight / 2
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        buttons.values.forEach {
            canvas?.drawRoundRect(it.left, it.top, it.right, it.bottom, 10f, 10f,
                if (it == pressedButton) pressedButtonBackgroundPaint else buttonBackgroundPaint)
            canvas?.drawRoundRect(it.left, it.top, it.right, it.bottom, 10f, 10f, buttonBorderPaint)
            canvas?.drawTextWithinArea(it.caption, it.left, it.right, (it.top + it.bottom) / 2, 2f, buttonCaptionPaint)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                for (b in buttons.values) {
                    if (b.left < event.x && event.x < b.right && b.top < event.y && event.y < b.bottom) {
                        pressedButton = b
                        makeVibrate(context)
                        break
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
                    pressedButton = null
                    onFieldButtonClickedListener?.onFielderButtonClicked(it.position)
                }
            }
        }
        invalidate()

        return true
    }
}