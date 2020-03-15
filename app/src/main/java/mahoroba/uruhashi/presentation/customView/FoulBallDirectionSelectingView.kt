package mahoroba.uruhashi.presentation.customView

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import mahoroba.uruhashi.R
import mahoroba.uruhashi.domain.game.FoulBallDirection
import mahoroba.uruhashi.domain.game.FoulBallDirection.*
import mahoroba.uruhashi.presentation.utility.VibrateService.makeVibrate
import mahoroba.uruhashi.presentation.utility.drawTextWithinArea

class FoulBallDirectionSelectingView(context: Context, attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {

    private open class ButtonStatus {
        var left: Float = 0f
        var top: Float = 0f
        var right: Float = 0f
        var bottom: Float = 0f
        var caption: String = ""
    }

    private class DirectionButtonStatus(val direction: FoulBallDirection) : ButtonStatus()

    private val toBackStopButton = DirectionButtonStatus(TO_BACKSTOP).apply {
        this.caption = context.getString(R.string.foul_ball_direction_to_backstop)
    }
    private val toFirstSideButton = DirectionButtonStatus(TO_FIRST_SIDE).apply {
        this.caption = context.getString(R.string.foul_ball_direction_to_first_side)
    }
    private val toThirdSideButton = DirectionButtonStatus(TO_THIRD_SIDE).apply {
        this.caption = context.getString(R.string.foul_ball_direction_to_third_side)
    }
    private val toRightFieldButton = DirectionButtonStatus(TO_RIGHT_FIELD).apply {
        this.caption = context.getString(R.string.foul_ball_direction_to_right_field)
    }
    private val toLeftFieldButton = DirectionButtonStatus(TO_LEFT_FIELD).apply {
        this.caption = context.getString(R.string.foul_ball_direction_to_left_field)
    }
    private val isAtLineButton = ButtonStatus().apply {
        this.caption = context.getString(R.string.foul_ball_is_at_line)
    }
    private val buttonCollection = listOf(
        toBackStopButton,
        toFirstSideButton,
        toThirdSideButton,
        toRightFieldButton,
        toLeftFieldButton,
        isAtLineButton
    )

    private val buttonBorderPaint = Paint()
    private val buttonBackGroundPaint = Paint()
    private val selectedButtonBackGroundPaint = Paint()
    private val buttonCaptionPaint = Paint()

    private val baseRect = Rect()

    var selectedValue: FoulBallDirection? = NO_ENTRY
    var isAtLine: Boolean = false

    private val selectedValueChangedListener = ArrayList<((FoulBallDirection?) -> Unit)>()
    fun addOnSelectedValueChangedListener(listener: (FoulBallDirection?) -> Unit) {
        selectedValueChangedListener.add(listener)
    }

    private val isAtLineChangedListener = ArrayList<(Boolean) -> Unit>()
    fun addIsAtLineChangedListener(listener: (Boolean) -> Unit) {
        isAtLineChangedListener.add(listener)
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
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        if (h * 9 > w * 6) {
            baseRect.left = 0
            baseRect.right = w
            baseRect.top = (h - w * 2 / 3) / 2
            baseRect.bottom = baseRect.top + (w * 2 / 3)
        } else {
            baseRect.top = 0
            baseRect.bottom = h
            baseRect.left = (w - h * 3 / 2) / 2
            baseRect.right = baseRect.left + (h * 3 / 2)
        }

        val buttonWidth = baseRect.width().toFloat() * 2 / 9
        val buttonHeight = baseRect.height().toFloat() / 6

        toBackStopButton.let {
            it.left = baseRect.left + baseRect.width() / 2 - buttonWidth / 2
            it.right = it.left + buttonWidth
            it.top = baseRect.top + buttonHeight * 4.5f
            it.bottom = it.top + buttonHeight
        }
        toFirstSideButton.let {
            it.left = toBackStopButton.right + buttonWidth * 0.05f
            it.right = it.left + buttonWidth
            it.top = baseRect.top + buttonHeight * 3f
            it.bottom = it.top + buttonHeight
        }
        toThirdSideButton.let {
            it.right = toBackStopButton.left - buttonWidth * 0.05f
            it.left = it.right - buttonWidth
            it.top = baseRect.top + buttonHeight * 3f
            it.bottom = it.top + buttonHeight
        }
        toRightFieldButton.let {
            it.left = toFirstSideButton.left + buttonWidth / 2
            it.right = it.left + buttonWidth
            it.top = baseRect.top + buttonHeight * 1f
            it.bottom = it.top + buttonHeight
        }
        toLeftFieldButton.let {
            it.right = toThirdSideButton.right - buttonWidth / 2
            it.left = it.right - buttonWidth
            it.top = baseRect.top + buttonHeight * 1f
            it.bottom = it.top + buttonHeight
        }
        isAtLineButton.let {
            it.left = baseRect.left + baseRect.width() / 2 - buttonWidth / 2
            it.right = it.left + buttonWidth
            it.top = baseRect.top + buttonHeight * 0.5f
            it.bottom = it.top + buttonHeight * 0.75f
        }

        buttonCaptionPaint.textSize = buttonHeight / 2f
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        buttonCollection.forEach {
            val bgPaint =
                if (it is DirectionButtonStatus)
                    if (it.direction == selectedValue) selectedButtonBackGroundPaint else buttonBackGroundPaint
                else
                    if (isAtLine) selectedButtonBackGroundPaint else buttonBackGroundPaint

            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f, bgPaint
            )
            canvas?.drawRoundRect(
                it.left, it.top, it.right, it.bottom, 10f, 10f, buttonBorderPaint
            )
            canvas?.drawTextWithinArea(
                it.caption,
                it.left,
                it.right,
                (it.top + it.bottom) / 2,
                (it.right - it.left) / 20f,
                buttonCaptionPaint
            )
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                for (b in buttonCollection.filterIsInstance<DirectionButtonStatus>()) {
                    if (b.left < event.x && event.x < b.right && b.top < event.y && event.y < b.bottom) {
                        selectedValue = if (selectedValue == b.direction) NO_ENTRY else b.direction
                        selectedValueChangedListener.forEach { it.invoke(selectedValue) }
                        makeVibrate(context)
                        invalidate()
                        break
                    }
                }

                isAtLineButton.let {
                    if (it.left < event.x && event.x < it.right && it.top < event.y && event.y < it.bottom) {
                        isAtLine = !isAtLine
                        isAtLineChangedListener.forEach { it.invoke(isAtLine) }
                        makeVibrate(context)
                        invalidate()
                    }
                }
            }
        }
        return true
    }
}