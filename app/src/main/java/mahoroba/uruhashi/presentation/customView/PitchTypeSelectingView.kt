package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.PitchType
import mahoroba.uruhashi.presentation.utility.VibrateService
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class PitchTypeSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val type: PitchType) {
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

    private val rowSize = 4
    private val colSize = 3

    private val buttonStatuses = arrayOf(
        ButtonStatus(PitchType.FOUR_SEAM_FASTBALL),
        ButtonStatus(PitchType.TWO_SEAM_FASTBALL),
        ButtonStatus(PitchType.SHOOT_BALL),
        ButtonStatus(PitchType.CUTTER),
        ButtonStatus(PitchType.SLIDER),
        ButtonStatus(PitchType.CURVE_BALL),
        ButtonStatus(PitchType.FORK_BALL),
        ButtonStatus(PitchType.SPLITTER),
        ButtonStatus(PitchType.CHANGE_UP),
        ButtonStatus(PitchType.SINKER),
        ButtonStatus(PitchType.PALM_BALL),
        ButtonStatus(PitchType.KNUCKLE_BALL)
    )

    var selectedValue: PitchType?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.type }
            return null
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.type == value }
            invalidate()
        }

    var onSelectedListener: (() -> Unit)? = null

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
            b.caption = when (b.type) {
                PitchType.FOUR_SEAM_FASTBALL -> context.getString(R.string.pitch_type_four_seam_fastball)
                PitchType.TWO_SEAM_FASTBALL -> context.getString(R.string.pitch_type_two_seam_fastball)
                PitchType.CURVE_BALL -> context.getString(R.string.pitch_type_curve_ball)
                PitchType.SLIDER -> context.getString(R.string.pitch_type_slider)
                PitchType.FORK_BALL -> context.getString(R.string.pitch_type_fork_ball)
                PitchType.SHOOT_BALL -> context.getString(R.string.pitch_type_shoot_ball)
                PitchType.SINKER -> context.getString(R.string.pitch_type_sinker)
                PitchType.CHANGE_UP -> context.getString(R.string.pitch_type_change_up)
                PitchType.KNUCKLE_BALL -> context.getString(R.string.pitch_type_knuckle)
                PitchType.SPLITTER -> context.getString(R.string.pitch_type_splitter)
                PitchType.CUTTER -> context.getString(R.string.pitch_type_cutter)
                PitchType.PALM_BALL -> context.getString(R.string.pitch_type_palm_ball)
                PitchType.NO_ENTRY -> ""
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val margin = w / 64.0f
        val buttonWidth: Float = (w - margin * (colSize + 1)) / colSize
        val buttonHeight: Float = (h - margin * (rowSize + 1)) / rowSize

        for (i in 0 until buttonStatuses.size) {
            buttonStatuses[i].let {
                val colPos = i % colSize
                val rowPos = i / colSize
                it.left = (colPos + 1) * margin + colPos * buttonWidth
                it.top = (rowPos + 1) * margin + rowPos * buttonHeight
                it.right = it.left + buttonWidth
                it.bottom = it.top + buttonHeight
            }
        }

        buttonCaptionPaint.textSize = buttonHeight / 3f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

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
                        if (it == touchedButton) it.isSelected = !it.isSelected else it.isSelected = false
                    }
                    onSelectedListener?.invoke()
                }
                invalidate()
            }
        }
        return true
    }
}