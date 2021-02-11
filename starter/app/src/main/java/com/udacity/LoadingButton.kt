package com.udacity

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import androidx.core.animation.doOnEnd
import androidx.core.animation.doOnStart
import androidx.core.content.withStyledAttributes
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0

    private var rectArea = Rect()
    private var progressArc = RectF()

    private var loadingProgress: Int = 0

    private var buttonBackgroundColor: Int = 0
    private var buttonText: String = ""
    private var buttonTextColor: Int = Color.WHITE
    private var circleColor: Int = 0
    private var displayedText: String = ""
    private var buttonLoadingBackgroundColor: Int = 0
    private var loadingText: String = ""

    private var valueAnimator = ValueAnimator()

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 55.0f
        typeface = Typeface.DEFAULT_BOLD
    }

    private var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { _, _, new ->
        when (new) {
            ButtonState.Clicked -> {
                buttonState = ButtonState.Clicked
                isEnabled = false // Avoid clicking multiple times
            }
            ButtonState.Completed -> {
                buttonState = ButtonState.Completed
                isEnabled = true // Enabling button again
            }
            ButtonState.Loading -> {
                buttonState = ButtonState.Loading
                valueAnimator = ValueAnimator.ofInt(
                    LOADING_PROGRESS_START,
                    LOADING_PROGRESS_END
                ).apply {
                    duration = 25_000 // 25 seconds
                    addUpdateListener {
                        loadingProgress = animatedValue as Int
                        invalidate()
                    }
                    doOnStart {
                        isEnabled = false
                        displayedText = loadingText
                    }
                    doOnEnd {
                        loadingProgress = LOADING_PROGRESS_END
                        isEnabled = true
                        displayedText = buttonText
                    }
                    start()
                }
            }
        }
        invalidate()
    }

    init {
        isClickable = true
        context.withStyledAttributes(attrs, R.styleable.LoadingButton) {
            buttonBackgroundColor = getColor(
                R.styleable.LoadingButton_buttonBackgroundColor,
                Color.GREEN
            )
            buttonText = getString(R.styleable.LoadingButton_text) ?: ""
            buttonTextColor = getColor(R.styleable.LoadingButton_textColor, Color.WHITE)
            circleColor = getColor(R.styleable.LoadingButton_circleColor, Color.MAGENTA)
            displayedText = buttonText
            buttonLoadingBackgroundColor = getColor(
                R.styleable.LoadingButton_buttonLoadingBackgroundColor,
                Color.GREEN
            )
            loadingText = getString(R.styleable.LoadingButton_loadingText) ?: ""
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        paint.color = buttonBackgroundColor
        canvas.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)

        if (buttonState == ButtonState.Loading) {
            paint.color = buttonLoadingBackgroundColor
            val progressRect = loadingProgress / 1000f * 360f
            canvas.drawRect(0f, 0f, progressRect, heightSize.toFloat(), paint)

            val sweepAngle = loadingProgress / 1000f * 360f
            paint.color = circleColor
            canvas.drawArc(progressArc, 0f, sweepAngle, true, paint)
        }

        paint.color = buttonTextColor
        paint.getTextBounds(displayedText, 0, displayedText.length, rectArea)
        val centerButton = (measuredHeight.toFloat() / 2) - rectArea.centerY()
        canvas.drawText(displayedText, widthSize / 2f, centerButton, paint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(MeasureSpec.getSize(w), heightMeasureSpec, 0)

        widthSize = w
        heightSize = h

        setMeasuredDimension(w, h)
    }

    fun downloadStarted() {
        buttonState = ButtonState.Loading
    }

    fun downloadCompleted() {
        with(valueAnimator) {
            val fraction = animatedFraction
            cancel()
            setCurrentFraction(fraction + 0.1f)
            duration = LOADING_PROGRESS_END.toLong()
            start()
        }
    }

    companion object {
        private const val LOADING_PROGRESS_START: Int = 0
        private const val LOADING_PROGRESS_END: Int = 1000
    }

}