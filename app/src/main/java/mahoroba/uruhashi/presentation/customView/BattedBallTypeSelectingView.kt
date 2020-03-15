package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.BattedBallType
import mahoroba.uruhashi.domain.game.BattedBallType.*
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class BattedBallTypeSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val type: BattedBallType) {
        var left: Float = 0f
        var top: Float = 0f
        var right: Float = 0f
        var bottom: Float = 0f
        var caption: String = ""
        var isSelected: Boolean = false
    }

    private val buttonBorderPaint = Paint()
    private val buttonBackGroundPaint = Paint()
    private val selectedButtonBackGroundPaint = Paint()
    private val buttonCaptionPaint = Paint()

    private val buttonStatuses = arrayOf(
        ButtonStatus(GROUND_HIGH_BOUND),
        ButtonStatus(GROUND),
        ButtonStatus(LINE_DRIVE),
        ButtonStatus(FLY_BALL),
        ButtonStatus(HIGH_FLY_BALL)
    )

    var selectedValue: BattedBallType?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.type }
            return NO_ENTRY
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.type == value }
        }

    private val selectedValueChangedListener = ArrayList<((BattedBallType?) -> Unit)>()
    fun addOnSelectedValueChangedListener(listener: (BattedBallType?) -> Unit) {
        selectedValueChangedListener.add(listener)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        buttonBorderPaint.isAntiAlias = true
        buttonBorderPaint.style = Paint.Style.STROKE
        buttonBorderPaint.strokeWidth = 2.0f
        buttonBorderPaint.color = Color.argb(255, 64, 64, 64)
        buttonBackGroundPaint.isAntiAlias = true
        buttonBackGroundPaint.style = Paint.Style.FILL
        buttonBackGroundPaint.color = Color.WHITE
        selectedButtonBackGroundPaint.isAntiAlias = true
        selectedButtonBackGroundPaint.style = Paint.Style.FILL
        selectedButtonBackGroundPaint.color = Color.argb(255, 255, 160, 128)
        buttonCaptionPaint.isAntiAlias = true
        buttonCaptionPaint.style = Paint.Style.STROKE
        buttonCaptionPaint.color = Color.BLACK

        buttonStatuses.forEach {
            it.caption = when (it.type) {
                GROUND_HIGH_BOUND -> context.getString(R.string.batted_ball_type_ground_high_bound)
                GROUND -> context.getString(R.string.batted_ball_type_ground)
                LINE_DRIVE -> context.getString(R.string.batted_ball_type_line_drive)
                FLY_BALL -> context.getString(R.string.batted_ball_type_fly_ball)
                HIGH_FLY_BALL -> context.getString(R.string.batted_ball_type_high_fly_ball)
                else -> ""
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val isLandScape = w >= h

        val margin = (if (isLandScape) w else h) / 64.0f
        val buttonWidth: Float =
            if (isLandScape) (w - margin * (buttonStatuses.size + 1)) / buttonStatuses.size
            else w - (margin * 2)
        val buttonHeight: Float =
            if (isLandScape) h - (margin * 2)
            else (h - margin * (buttonStatuses.size + 1)) / buttonStatuses.size
        for (i in buttonStatuses.indices) {
            buttonStatuses[i].let {
                if (isLandScape) {
                    it.left = (i + 1) * margin + i * buttonWidth
                    it.top = margin
                    it.right = it.left + buttonWidth
                    it.bottom = it.top + buttonHeight
                } else {
                    it.left = margin
                    it.bottom = h - ((i + 1) * margin + i * buttonHeight)
                    it.right = it.left + buttonWidth
                    it.top = it.bottom - buttonHeight
                }
            }
        }

        buttonCaptionPaint.textSize = buttonHeight / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        for (b in buttonStatuses) {
            canvas?.drawRoundRect(
                b.left, b.top, b.right, b.bottom, 10f, 10f,
                if (b.isSelected) selectedButtonBackGroundPaint else buttonBackGroundPaint
            )
            canvas?.drawRoundRect(
                b.left, b.top, b.right, b.bottom, 10f, 10f, buttonBorderPaint
            )
            canvas?.drawTextWithinArea(
                b.caption,
                b.left,
                b.right,
                (b.top + b.bottom) / 2,
                (b.right - b.left) / 20f,
                buttonCaptionPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                var touchedButton: ButtonStatus? = null
                for (b in buttonStatuses) {
                    if (b.left < event.x && event.x < b.right && b.top < event.y && event.y < b.bottom) {
                        touchedButton = b
                        makeVibrate(context)
                        break
                    }
                }

                if (touchedButton != null) {
                    buttonStatuses.forEach {
                        if (it == touchedButton) it.isSelected = !it.isSelected else it.isSelected =
                            false
                    }
                    selectedValueChangedListener.forEach { it.invoke(touchedButton.type) }
                }
                invalidate()
            }
        }

        return true
    }

}