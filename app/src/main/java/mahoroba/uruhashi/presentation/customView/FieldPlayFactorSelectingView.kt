package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.FieldPlayFactor
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class FieldPlayFactorSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private class ButtonStatus(val factor: FieldPlayFactor?, val toFixed: Boolean) {
        var left: Float = 0f
        var right: Float = 0f
        var top: Float = 0f
        var bottom: Float = 0f
        var caption: String = ""
        var enabled: Boolean = true
        var isSelected: Boolean = false
    }

    private val buttonBorderPaint = Paint()
    private val buttonBackGroundPaint = Paint()
    private val selectedButtonBackGroundPaint = Paint()
    private val disabledButtonBackGroundPaint = Paint()
    private val buttonCaptionPaint = Paint()

    private val rowSize = 2
    private val colSize = 4

    private val buttonStatuses = arrayOf(
        ButtonStatus(FieldPlayFactor.STEALING, false),
        ButtonStatus(FieldPlayFactor.PICK_OFF_PLAY, false),
        ButtonStatus(FieldPlayFactor.WILD_PITCH, false),
        ButtonStatus(FieldPlayFactor.PASSED_BALL, false),
        ButtonStatus(FieldPlayFactor.OBSTRUCTION, false),
        ButtonStatus(FieldPlayFactor.INTERFERENCE_FIELDING, false),
        ButtonStatus(FieldPlayFactor.INTERFERENCE_BATTING, false),
        ButtonStatus(null, true)
    )

    var fixedFieldPlayFactor: FieldPlayFactor? = null
        set(value) {
            buttonStatuses.find { n -> n.factor == null }?.let {
                it.caption = getFieldPlayFactorString(value)
                it.isSelected = value != null
                it.enabled = value != null
            }
            buttonStatuses.filter { n -> n.factor != null }.forEach {
                it.enabled = value == null
            }
            field = value
        }

    var selectedValue: FieldPlayFactor?
        get() {
            buttonStatuses.forEach { if (it.isSelected) return it.factor ?: FieldPlayFactor.OTHER }
            return fixedFieldPlayFactor ?: FieldPlayFactor.OTHER
        }
        set(value) {
            buttonStatuses.forEach { it.isSelected = it.factor == value }
            invalidate()
        }

    var onValueSelectedListener: (() -> Unit)? = null

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
        disabledButtonBackGroundPaint.isAntiAlias = true
        disabledButtonBackGroundPaint.style = Paint.Style.FILL
        disabledButtonBackGroundPaint.color = Color.argb(255, 160, 160, 160)
        buttonCaptionPaint.isAntiAlias = true
        buttonCaptionPaint.style = Paint.Style.STROKE
        buttonCaptionPaint.color = Color.BLACK

        for (b in buttonStatuses) {
            b.caption = getFieldPlayFactorString(b.factor)
            b.enabled = (b.factor == null) != (fixedFieldPlayFactor == null)
        }
    }

    private fun getFieldPlayFactorString(factor: FieldPlayFactor?): String {
        return when (factor) {
            FieldPlayFactor.STEALING -> context.getString(R.string.field_play_factor_stealing)
            FieldPlayFactor.PICK_OFF_PLAY -> context.getString(R.string.field_play_factor_pick_off_play)
            FieldPlayFactor.WILD_PITCH -> context.getString(R.string.field_play_factor_wild_pitch)
            FieldPlayFactor.PASSED_BALL -> context.getString(R.string.field_play_factor_passed_ball)
            FieldPlayFactor.OBSTRUCTION -> context.getString(R.string.field_play_factor_obstruction)
            FieldPlayFactor.INTERFERENCE_FIELDING -> context.getString(R.string.field_play_factor_interference_fielding)
            FieldPlayFactor.INTERFERENCE_BATTING -> context.getString(R.string.field_play_factor_interference_batting)
            FieldPlayFactor.BATTING -> context.getString(R.string.field_play_factor_batting)
            FieldPlayFactor.WALK -> context.getString(R.string.field_play_factor_walk)
            FieldPlayFactor.HIT_BY_PITCH -> context.getString(R.string.field_play_factor_hit_by_pitch)
            else -> ""
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
                when {
                    b.isSelected -> selectedButtonBackGroundPaint
                    b.enabled -> buttonBackGroundPaint
                    else -> disabledButtonBackGroundPaint
                }
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
                    if (!b.toFixed && b.enabled && b.left < event.x && event.x < b.right && b.top < event.y && event.y < b.bottom) {
                        touchedButton = b
                        makeVibrate(context)
                        break
                    }
                }

                if (touchedButton != null) {
                    buttonStatuses.forEach {
                        if (it == touchedButton) it.isSelected = !it.isSelected else it.isSelected = false
                    }

                    onValueSelectedListener?.invoke()
                }
                invalidate()
            }
        }
        return true
    }
}