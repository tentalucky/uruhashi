package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View

class OutlineTextView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    AppCompatTextView(context, attrs) {

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        canvas?.drawColor(Color.TRANSPARENT)

        val paint = Paint()
        paint.isAntiAlias = true
        paint.textSize = textSize
        paint.textAlign = when(textAlignment) {
            View.TEXT_ALIGNMENT_TEXT_START -> Paint.Align.LEFT
            View.TEXT_ALIGNMENT_CENTER -> Paint.Align.CENTER
            else -> Paint.Align.LEFT
        }

        paint.strokeWidth = 4.0f
        paint.strokeJoin = Paint.Join.ROUND
        paint.strokeCap = Paint.Cap.ROUND
        paint.color = Color.WHITE
        paint.style = Paint.Style.STROKE

        val textBounds = Rect()
        paint.getTextBounds(text.toString(), 0, text.length, textBounds)

        val posX: Float = when(paint.textAlign) {
            Paint.Align.LEFT -> 0f
            Paint.Align.CENTER -> width / 2f
            Paint.Align.RIGHT -> width.toFloat()
            null -> 0f
        }
        val posY: Float = (height / 2 + textBounds.height() / 2 - textBounds.bottom).toFloat()
        canvas?.drawText(text.toString(), posX, posY, paint)

        paint.strokeWidth = 0f
        paint.color = currentTextColor
        paint.style = Paint.Style.FILL
        canvas?.drawText(text.toString(), posX, posY, paint)
    }
}