package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.BattingOption
import mahoroba.uruhashi.domain.game.BattingOption.*
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.dpToPx
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class BattingOptionSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private val buntPath = Path()
    private val slashPath = Path()
    private val borderPaint = Paint()
    private val backGroundPaint = Paint()
    private val selectedBackGroundPaint = Paint()
    private val captionPaint = Paint()

    private val buntRect = RectF()
    private val slashRect = RectF()

    private var buntCaption = ""
    private var slashCaption = ""

    var selectedValue: BattingOption? = NONE
    var onSelectedListener: (() -> Unit)? = null

    enum class Orientation { VERTICAL, HORIZONTAL }

    var orientation: Orientation = Orientation.VERTICAL

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context) : this(context, null)

    init {
        borderPaint.isAntiAlias = true
        borderPaint.style = Paint.Style.STROKE
        borderPaint.strokeWidth = 2.0f
        borderPaint.color = Color.argb(255, 64, 64, 64)
        backGroundPaint.isAntiAlias = true
        backGroundPaint.style = Paint.Style.FILL
        backGroundPaint.strokeWidth = 2.0f
        backGroundPaint.color = Color.argb(255, 160, 160, 160)
        selectedBackGroundPaint.isAntiAlias = true
        selectedBackGroundPaint.style = Paint.Style.FILL
        selectedBackGroundPaint.strokeWidth = 2.0f
        selectedBackGroundPaint.color = Color.argb(255, 255, 224, 160)

        context.theme.obtainStyledAttributes(
            attrs, R.styleable.BattingOptionSelectingView, 0, 0
        ).let {
            when (it.getInteger(R.styleable.BattingOptionSelectingView_orientation, 0)) {
                0 -> orientation = Orientation.VERTICAL
                1 -> orientation = Orientation.HORIZONTAL
            }
        }

        buntCaption = context.getString(R.string.batting_option_bunt)
        slashCaption = context.getString(R.string.batting_option_slash)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        val r = 20f
        val margin: Float = context.dpToPx(4f)

        if (orientation == Orientation.VERTICAL) {
            buntRect.let {
                it.top = margin
                it.bottom = height / 2f
                it.left = margin
                it.right = width - margin
                buntPath.rewind()
                buntPath.moveTo(it.left, it.bottom)
                buntPath.arcTo(
                    it.left, it.top, it.left + r, it.top + r, 180f, 90f, false
                )
                buntPath.arcTo(
                    it.right - r, it.top, it.right, it.top + r, 270f, 90f, false
                )
                buntPath.lineTo(it.right, it.bottom)
                buntPath.close()
            }

            slashRect.let {
                it.top = height / 2f
                it.bottom = height - margin
                it.left = margin
                it.right = width - margin
                slashPath.rewind()
                slashPath.moveTo(it.left, it.top)
                slashPath.arcTo(
                    it.left, it.bottom - r, it.left + r, it.bottom, 180f, -90f, false
                )
                slashPath.arcTo(
                    it.right - r, it.bottom - r, it.right, it.bottom, 90f, -90f, false
                )
                slashPath.lineTo(it.right, it.top)
                slashPath.close()
            }

            captionPaint.textSize = h / 6f

        } else {
            buntRect.let {
                it.top = margin
                it.bottom = height - margin
                it.left = margin
                it.right = width / 2f
                buntPath.rewind()
                buntPath.moveTo(it.right, it.bottom)
                buntPath.arcTo(
                    it.left, it.bottom - r, it.left + r, it.bottom, 90f, 90f, false
                )
                buntPath.arcTo(
                    it.left, it.top, it.left + r, it.top + r, 180f, 90f, false
                )
                buntPath.lineTo(it.right, it.top)
                buntPath.close()
            }

            slashRect.let {
                it.top = margin
                it.bottom = height - margin
                it.left = width / 2f
                it.right = width - margin
                slashPath.rewind()
                slashPath.moveTo(it.left, it.bottom)
                slashPath.arcTo(
                    it.right - r, it.bottom - r, it.right, it.bottom, 90f, -90f, false
                )
                slashPath.arcTo(
                    it.right - r, it.top, it.right, it.top + r, 0f, -90f, false
                )
                slashPath.lineTo(it.left, it.top)
                slashPath.close()
            }

            captionPaint.textSize = h / 3f
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawPath(buntPath, borderPaint)
        canvas?.drawPath(buntPath, if (selectedValue == BUNT) selectedBackGroundPaint else backGroundPaint)
        canvas?.drawTextWithinArea(
            buntCaption,
            buntRect.left,
            buntRect.right,
            (buntRect.top + buntRect.bottom) / 2,
            (buntRect.right - buntRect.left) / 20f,
            captionPaint
        )

        canvas?.drawPath(slashPath, borderPaint)
        canvas?.drawPath(slashPath, if (selectedValue == SLASH_BUNT) selectedBackGroundPaint else backGroundPaint)
        canvas?.drawTextWithinArea(
            slashCaption,
            slashRect.left,
            slashRect.right,
            (slashRect.top + slashRect.bottom) / 2,
            (slashRect.right - slashRect.left) / 20f,
            captionPaint
        )
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                if (buntRect.contains(event.x, event.y)) {
                    selectedValue = if (selectedValue == BUNT) NONE else BUNT
                    makeVibrate(context)
                    onSelectedListener?.invoke()
                } else if (slashRect.contains(event.x, event.y)) {
                    selectedValue = if (selectedValue == SLASH_BUNT) NONE else SLASH_BUNT
                    makeVibrate(context)
                    onSelectedListener?.invoke()
                }
                invalidate()
            }
        }
        return true
    }
}