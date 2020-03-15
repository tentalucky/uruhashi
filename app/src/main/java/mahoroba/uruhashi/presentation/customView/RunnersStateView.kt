package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.util.AttributeSet
import android.view.View
import mahoroba.uruhashi.domain.game.secondary.Situation
import kotlin.math.sqrt

class RunnersStateView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val baseStrokePaint = Paint()
    private val baseFillPaint = Paint()
    private val basePath = Path()

    var situation: Situation? = null

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        baseStrokePaint.isAntiAlias = true
        baseStrokePaint.color = Color.argb(255, 255, 255, 255)
        baseStrokePaint.strokeWidth = 5f
        baseStrokePaint.style = Paint.Style.STROKE

        baseFillPaint.isAntiAlias = true
        baseFillPaint.color = Color.argb(255, 255, 128, 0)
        baseFillPaint.strokeWidth = 4f
        baseFillPaint.style = Paint.Style.FILL

        // Define base size as 80 * 80 and centered to (0, 0)
        val r: Float = sqrt(2.0).toFloat() * 5
        basePath.moveTo(-20f, -20f)
        basePath.arcTo(0f - r, -30f - r, 0f + r, -30f + r, 225f, 90f, false)
        basePath.arcTo(30f - r, 0f - r, 30f + r, 0f + r, 315f, 90f, false)
        basePath.arcTo(0f - r, 30f - r, 0f + r, 30f + r, 45f, 90f, false)
        basePath.arcTo(-30f - r, 0f - r, -30f + r, 0f + r, 135f, 90f, false)
        basePath.close()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        // Draw a first base
        canvas?.save()
        canvas?.translate(width * 3 / 4f, height * 2f / 3f)
        canvas?.scale(width / 160f * 0.8f, height / 120f * 0.8f)
        if (situation?.orderOf1R != null) canvas?.drawPath(basePath, baseFillPaint)
        canvas?.drawPath(basePath, baseStrokePaint)
        canvas?.restore()

        // Draw a second base
        canvas?.save()
        canvas?.translate(width / 2f, height * 1f / 3f)
        canvas?.scale(width / 160f * 0.8f, height / 120f * 0.8f)
        if (situation?.orderOf2R != null) canvas?.drawPath(basePath, baseFillPaint)
        canvas?.drawPath(basePath, baseStrokePaint)
        canvas?.restore()

        // Draw a third base
        canvas?.save()
        canvas?.translate(width / 4f, height * 2f / 3f)
        canvas?.scale(width / 160f * 0.8f, height / 120f * 0.8f)
        if (situation?.orderOf3R != null) canvas?.drawPath(basePath, baseFillPaint)
        canvas?.drawPath(basePath, baseStrokePaint)
        canvas?.restore()
    }
}