package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.widget.CheckBox
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class CustomToggleButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    CheckBox(context, attrs) {

    private val rect = RectF()
    private val borderPaint = Paint()
    private val turnOffBackGroundPaint = Paint()
    private val turnOnBackGroundPaint = Paint()
    private val captionPaint = Paint()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2.0f
        borderPaint.color = Color.argb(255, 64, 64, 64)
        turnOnBackGroundPaint.isAntiAlias = true
        turnOnBackGroundPaint.style = Paint.Style.FILL
        turnOnBackGroundPaint.strokeWidth = 2.0f
        turnOnBackGroundPaint.color = Color.argb(255, 255, 224, 160)
        turnOffBackGroundPaint.isAntiAlias = true
        turnOffBackGroundPaint.style = Paint.Style.FILL
        turnOffBackGroundPaint.strokeWidth = 2.0f
        turnOffBackGroundPaint.color = Color.argb(255, 160, 160, 160)
        captionPaint.isAntiAlias = true
        captionPaint.style = Paint.Style.STROKE
        captionPaint.color = Color.BLACK
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        rect.left = context.dpToPx(paddingLeft.toFloat())
        rect.right = width - context.dpToPx(paddingRight.toFloat())
        rect.top = context.dpToPx(paddingTop.toFloat())
        rect.bottom = height - context.dpToPx(paddingBottom.toFloat())

        captionPaint.textSize = height / 3f
    }

    override fun onDraw(canvas: Canvas?) {
        canvas?.drawRoundRect(
            rect,
            if (isEnabled) 15f else 0f,
            if (isEnabled) 15f else 0f,
            if (isChecked) turnOnBackGroundPaint else turnOffBackGroundPaint)
        canvas?.drawRoundRect(
            rect,
            if (isEnabled) 15f else 0f,
            if (isEnabled) 15f else 0f,
            borderPaint)
        canvas?.drawTextWithinArea(
            text.toString(),
            rect.left,
            rect.right,
            (rect.top + rect.bottom) / 2,
            (rect.right - rect.left) / 20f,
            captionPaint
        )
    }
}