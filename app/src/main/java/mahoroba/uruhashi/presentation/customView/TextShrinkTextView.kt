package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class TextShrinkTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs, defStyleAttr) {

    private val workPaint = Paint()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var text: String? = ""

    override fun getText(): CharSequence? {
        return text
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText("", type)
        this.text = text?.toString()
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        workPaint.isAntiAlias = true
        workPaint.style = Paint.Style.FILL
        workPaint.textSize = textSize
        workPaint.color = currentTextColor
        canvas.drawTextWithinArea(
            text,
            0f, width.toFloat(), height / 2f, width * 0.01f, workPaint
        )
    }
}