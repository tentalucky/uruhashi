package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.BattedBallStrength
import mahoroba.uruhashi.domain.game.BattedBallStrength.*
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class BattedBallStrengthSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val strength: BattedBallStrength) {
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
        ButtonStatus(VERY_WEAK),
        ButtonStatus(WEAK),
        ButtonStatus(MEDIUM),
        ButtonStatus(HARD),
        ButtonStatus(VERY_HARD)
    )

    var selectedValue: BattedBallStrength?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.strength }
            return NO_ENTRY
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.strength == value }
        }

    private val selectedValueChangedListener = ArrayList<((BattedBallStrength?) -> Unit)>()
    fun addOnSelectedValueChangedListener(listener: (BattedBallStrength?) -> Unit) {
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
            it.caption = when (it.strength) {
                VERY_HARD -> context.getString(R.string.batted_ball_strength_very_hard)
                HARD -> context.getString(R.string.batted_ball_strength_hard)
                MEDIUM -> context.getString(R.string.batted_ball_strength_medium)
                WEAK -> context.getString(R.string.batted_ball_strength_weak)
                VERY_WEAK -> context.getString(R.string.batted_ball_strength_very_weak)
                else -> ""
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * (buttonStatuses.size + 1)) / buttonStatuses.size
        val buttonHeight: Float = h - (margin * 2)
        for (i in 0 until buttonStatuses.size) {
            buttonStatuses[i].let {
                it.left = (i + 1) * margin + i * buttonWidth
                it.top = margin
                it.right = it.left + buttonWidth
                it.bottom = it.top + buttonHeight
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
                        if (it == touchedButton) it.isSelected = !it.isSelected else it.isSelected = false
                    }
                    selectedValueChangedListener.forEach { it.invoke(touchedButton.strength) }
                }
                invalidate()
            }
        }

        return true
    }

}