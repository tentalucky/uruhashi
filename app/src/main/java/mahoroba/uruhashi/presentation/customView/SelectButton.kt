package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.support.v4.content.ContextCompat
import android.support.v7.widget.AppCompatButton
import android.util.AttributeSet
import mahoroba.uruhashi.R
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class SelectButton(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatButton(context, attrs, defStyleAttr) {

    private val textPaint = Paint()

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    private var text: String? = ""

    init {
        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.textSize = textSize
        textPaint.color = ContextCompat.getColor(context, R.color.color_select_button_text)

        super.setElevation(context.dpToPx(if (super.isEnabled()) 5f else 0f))
    }

    override fun getText(): CharSequence? {
        return text
    }

    override fun setText(text: CharSequence?, type: BufferType?) {
        super.setText("", type)
        this.text = text?.toString()
        this.background = context.getDrawable(
            if (text.isNullOrEmpty()) R.drawable.select_button_background_state_not_selected
            else R.drawable.select_button_background_state_selected
        )
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        super.setElevation(context.dpToPx(if (enabled) 5f else 0f))
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        canvas.drawTextWithinArea(
            text,
            0f, width.toFloat(), height / 2f, width * 0.01f, textPaint
        )
    }
}