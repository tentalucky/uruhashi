package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.FieldPosition
import mahoroba.uruhashi.presentation.utility.VibrateService
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class FoulBallErrorPositionSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val position: FieldPosition, val rowPos: Int, val colPos: Int) {
        var left: Float = 0f
        var right: Float = 0f
        var top: Float = 0f
        var bottom: Float = 0f
        var caption: String = ""
        var isSelected: Boolean = false
    }

    private val buttonBorderPaint = Paint()
    private val buttonBackGroundPaint = Paint()
    private val selectedButtonBackGroundPaint = Paint()
    private val buttonCaptionPaint = Paint()

    private val buttonStatuses = arrayOf(
        ButtonStatus(FieldPosition.PITCHER, 1, 0),
        ButtonStatus(FieldPosition.CATCHER, 1, 1),
        ButtonStatus(FieldPosition.FIRST_BASEMAN, 1, 2),
        ButtonStatus(FieldPosition.SECOND_BASEMAN, 1, 3),
        ButtonStatus(FieldPosition.THIRD_BASEMAN, 1, 4),
        ButtonStatus(FieldPosition.SHORT_STOP, 1, 5),
        ButtonStatus(FieldPosition.LEFT_FIELDER, 0, 3),
        ButtonStatus(FieldPosition.CENTER_FIELDER, 0, 4),
        ButtonStatus(FieldPosition.RIGHT_FIELDER, 0, 5)
    )

    var selectedValue: FieldPosition?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.position }
            return null
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.position == value }
            invalidate()
        }

    private val selectedValueChangedListener = ArrayList<((FieldPosition?) -> Unit)>()
    fun addOnSelectedValueChangedListener(listener: (FieldPosition?) -> Unit) {
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
            it.caption = when (it.position) {
                FieldPosition.PITCHER -> context.getString(R.string.display_position_pitcher)
                FieldPosition.CATCHER -> context.getString(R.string.display_position_catcher)
                FieldPosition.FIRST_BASEMAN -> context.getString(R.string.display_position_first_baseman)
                FieldPosition.SECOND_BASEMAN -> context.getString(R.string.display_position_second_baseman)
                FieldPosition.THIRD_BASEMAN -> context.getString(R.string.display_position_third_baseman)
                FieldPosition.SHORT_STOP -> context.getString(R.string.display_position_shortstop)
                FieldPosition.LEFT_FIELDER -> context.getString(R.string.display_position_left_fielder)
                FieldPosition.CENTER_FIELDER -> context.getString(R.string.display_position_center_fielder)
                FieldPosition.RIGHT_FIELDER -> context.getString(R.string.display_position_right_fielder)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * 7) / 6
        val buttonHeight: Float = (h - margin * 3) / 2

        buttonStatuses.forEach {
            it.left = (it.colPos + 1) * margin + it.colPos * buttonWidth
            it.top = (it.rowPos + 1) * margin + it.rowPos * buttonHeight
            it.right = it.left + buttonWidth
            it.bottom = it.top + buttonHeight
        }

        buttonCaptionPaint.textSize = buttonHeight / 3f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        buttonStatuses.forEach {
            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f,
                if (it.isSelected) selectedButtonBackGroundPaint else buttonBackGroundPaint
            )
            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f, buttonBorderPaint
            )
            canvas?.drawTextWithinArea(
                it.caption,
                it.left,
                it.right,
                (it.top + it.bottom) / 2,
                (it.right - it.left) / 20f,
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
                        VibrateService.makeVibrate(context)
                        break
                    }
                }

                if (touchedButton != null) {
                    buttonStatuses.forEach {
                        if (it == touchedButton) it.isSelected = !it.isSelected else it.isSelected = false
                    }
                    selectedValueChangedListener.forEach { it.invoke(touchedButton.position) }
                }
                invalidate()
            }
        }
        return true
    }
}