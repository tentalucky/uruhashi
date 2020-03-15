package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.presentation.PitchInputViewModel
import mahoroba.uruhashi.presentation.PitchInputViewModel.UIPitchResult.*
import mahoroba.uruhashi.presentation.utility.VibrateService
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class PitchResultSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(
        val result: PitchInputViewModel.UIPitchResult,
        val row: Int,
        val column: Int
    ) {
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

    private val rowSize = 2
    private val colSize = 6

    private val buttonStatuses = arrayOf(
        ButtonStatus(STRIKE_SWUNG, 0, 0),
        ButtonStatus(STRIKE_CALLED, 0, 1),
        ButtonStatus(BALL, 0, 2),
        ButtonStatus(FOUL, 0, 3),
        ButtonStatus(BATTED, 0, 4),
        ButtonStatus(HIT_BY_PITCH, 0, 5),
        ButtonStatus(NO_PITCH_INTENTIONAL_WALK, 1, 0),
        ButtonStatus(BALK, 1, 1),
        ButtonStatus(OTHER, 1, 2)
    )

    var selectedValue: PitchInputViewModel.UIPitchResult?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.result }
            return null
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.result == value }
        }

    private val selectedValueChangedListener =
        ArrayList<((PitchInputViewModel.UIPitchResult?) -> Unit)>()

    fun addOnSelectedValueChangedListener(listener: (PitchInputViewModel.UIPitchResult?) -> Unit) {
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

        for (b in buttonStatuses) {
            b.caption = when (b.result) {
                STRIKE_SWUNG -> context.getString(R.string.pitch_result_strike_swung)
                STRIKE_CALLED -> context.getString(R.string.pitch_result_strike_called)
                BALL -> context.getString(R.string.pitch_result_ball)
                FOUL -> context.getString(R.string.pitch_result_foul)
                BATTED -> context.getString(R.string.pitch_result_batted)
                HIT_BY_PITCH -> context.getString(R.string.pitch_result_hit_by_pitch)
                NO_PITCH_INTENTIONAL_WALK -> context.getString(R.string.pitch_result_no_pitch_intentional_walk)
                BALK -> context.getString(R.string.pitch_result_balk)
                OTHER -> context.getString(R.string.pitch_result_others)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * (colSize + 1)) / colSize
        val buttonHeight: Float = (h - margin * (rowSize + 1)) / rowSize

        buttonStatuses.forEach {
            it.left = (it.column + 1) * margin + it.column * buttonWidth
            it.top = (it.row + 1) * margin + it.row * buttonHeight
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
                        VibrateService.makeVibrate(context)
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