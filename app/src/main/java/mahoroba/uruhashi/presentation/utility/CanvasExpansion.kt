package mahoroba.uruhashi.presentation.utility

import android.graphics.Canvas
import android.graphics.Paint

fun Canvas.drawTextWithinArea(
    text: String?,
    left: Float,
    right: Float,
    y: Float,
    padding: Float,
    paint: Paint,
    align: Paint.Align = Paint.Align.CENTER
) {
    val textWidth = paint.measureText(text ?: "")
    val scale =
        if (right - left - padding * 2 < textWidth) (right - left - padding * 2) / textWidth else 1f
    val metrics = paint.fontMetrics

    this.save()

    when (align) {
        Paint.Align.CENTER -> translate((right + left) / 2, 0f)
        Paint.Align.LEFT -> translate(left + padding, 0f)
        Paint.Align.RIGHT -> translate(right - padding, 0f)
    }
    this.scale(scale, 1f)
    val xOffset: Float = when (align) {
        Paint.Align.CENTER -> -textWidth / 2
        Paint.Align.LEFT -> 0f
        Paint.Align.RIGHT -> -textWidth
    }
    this.drawText(text ?: "", xOffset, y - (metrics.ascent + metrics.descent) / 2, paint)

    this.restore()
}

fun Canvas.drawTextRight(
    text: String,
    x: Float,
    y: Float,
    paint: Paint
) {
    val textWidth = paint.measureText(text)
    this.drawText(text, x - textWidth, y, paint)
}