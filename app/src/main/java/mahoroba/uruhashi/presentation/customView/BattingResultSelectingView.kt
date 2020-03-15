package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.BattingResult
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class BattingResultSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val result: BattingResult, val row: Int, val col: Int) {
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

    private val rowSize = 3
    private val colSize = 4

    private val buttonStatuses = arrayOf(
        ButtonStatus(BattingResult.SINGLE, 0, 0),
        ButtonStatus(BattingResult.DOUBLE, 0, 1),
        ButtonStatus(BattingResult.TRIPLE, 0, 2),
        ButtonStatus(BattingResult.HOME_RUN, 0, 3),
        ButtonStatus(BattingResult.GROUND_OUT, 1, 0),
        ButtonStatus(BattingResult.LINE_OUT, 1, 1),
        ButtonStatus(BattingResult.FLY_OUT, 1, 2),
        ButtonStatus(BattingResult.FOUL_LINE_OUT, 2, 1),
        ButtonStatus(BattingResult.FOUL_FLY_OUT, 2, 2),
        ButtonStatus(BattingResult.SACRIFICE_HIT, 1, 3),
        ButtonStatus(BattingResult.SACRIFICE_FLY, 2, 3)
    )

    var selectedValue: BattingResult?
        get() {
            return buttonStatuses.find { n -> n.isSelected }?.result
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.result == value }
        }

    private val selectedValueChangedListener = ArrayList<((BattingResult?) -> Unit)>()
    fun addOnSelectedValueChangedListener(listener: (BattingResult?) -> Unit) {
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
            it.caption = when (it.result) {
                BattingResult.SINGLE -> context.getString(R.string.batting_result_single)
                BattingResult.DOUBLE -> context.getString(R.string.batting_result_double)
                BattingResult.TRIPLE -> context.getString(R.string.batting_result_triple)
                BattingResult.HOME_RUN -> context.getString(R.string.batting_result_home_run)
                BattingResult.GROUND_OUT -> context.getString(R.string.batting_result_ground_out)
                BattingResult.LINE_OUT -> context.getString(R.string.batting_result_line_out)
                BattingResult.FLY_OUT -> context.getString(R.string.batting_result_fly_out)
                BattingResult.FOUL_LINE_OUT -> context.getString(R.string.batting_result_foul_line_out)
                BattingResult.FOUL_FLY_OUT -> context.getString(R.string.batting_result_foul_fly_out)
                BattingResult.SACRIFICE_HIT -> context.getString(R.string.batting_result_sacrifice_hit)
                BattingResult.SACRIFICE_FLY -> context.getString(R.string.batting_result_sacrifice_fly)
                else -> ""
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * (colSize + 1)) / colSize
        val buttonHeight: Float = (h - margin * (rowSize + 1)) / rowSize

        buttonStatuses.forEach {
            it.left = (it.col + 1) * margin + (it.col * buttonWidth)
            it.top = (it.row + 1) * margin + (it.row * buttonHeight)
            it.right = it.left + buttonWidth
            it.bottom = it.top + buttonHeight
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
                    selectedValueChangedListener.forEach { it.invoke(touchedButton.result) }
                }
                invalidate()
            }
        }

        return true
    }
}