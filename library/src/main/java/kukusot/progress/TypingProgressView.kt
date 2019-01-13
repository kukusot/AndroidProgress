package kukusot.progress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import kukusot.progress.base.BaseProgressView
import kukusot.progress.base.Dot

const val DEFAULT_ANIMATION_DURATION = 2500
const val DEFAULT_NUM_DOTS = 3
const val DEFAULT_CIRCLE_COLOR = Color.BLACK
const val DEFAULT_CIRCLE_RADIUS = 24f
const val DEFAULT_CIRCLE_SPACING = 10f
const val DEFAULT_CIRCLE_TRAVEL = 30f

class TypingProgressView(context: Context, attrs: AttributeSet? = null) : BaseProgressView(context, attrs) {


    var circleRadius = DEFAULT_CIRCLE_RADIUS
        set(value) {
            field = value
            requestLayout()
        }
    var circleSpacing = DEFAULT_CIRCLE_SPACING
        set(value) {
            field = value
            requestLayout()
        }

    var circleTravel = DEFAULT_CIRCLE_TRAVEL
        set(value) {
            field = value
            requestLayout()
        }

    var animationDuration = DEFAULT_ANIMATION_DURATION
        set(value) {
            field = value
            dotAnimationDuration = animationDuration / numDots
        }

    var circleColor = DEFAULT_CIRCLE_COLOR
        set(value) {
            field = value
            paint.color = value
        }

    var numDots = DEFAULT_NUM_DOTS
        set(value) {
            field = value
            dotAnimationDuration = animationDuration / numDots
            initiateDots()
            requestLayout()
        }

    private var dotAnimationDuration: Int
    private val calculatedHeight: Int

    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.TypingProgressView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.TypingProgressView_circleRadius, DEFAULT_CIRCLE_RADIUS)
            circleSpacing = getDimension(R.styleable.TypingProgressView_circleSpacing, DEFAULT_CIRCLE_SPACING)
            circleTravel = getDimension(R.styleable.TypingProgressView_circleTravel, DEFAULT_CIRCLE_TRAVEL)
            circleColor = getColor(R.styleable.TypingProgressView_circleColor, DEFAULT_CIRCLE_COLOR)
            numDots = getInt(R.styleable.TypingProgressView_numDots, DEFAULT_NUM_DOTS)
            animationDuration =
                    getInt(R.styleable.TypingProgressView_animationDuration, DEFAULT_ANIMATION_DURATION)
            dotAnimationDuration = animationDuration / numDots

            recycle()
        }

        calculatedHeight = (circleRadius * 2 + circleTravel).toInt()
        initiateDots()
    }

    private fun initiateDots() {
        dots.clear()
        val startY = calculatedHeight - circleRadius
        var dotX = circleRadius
        for (i in 0 until numDots) {
            dots.add(Dot(dotX, startY, circleRadius))
            dotX += circleSpacing + (circleRadius * 2)
        }

        initiateDotsAnimators()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedWidth = (dots.last().x + circleRadius).toInt()

        setMeasuredDimension(calculatedWidth, calculatedHeight)
    }


    private fun initiateDotsAnimators() {
        var starOffset = 0L
        val animators = arrayListOf<ValueAnimator>()
        for (i in 0 until numDots) {
            val dot = dots[i]
            animators.add(ValueAnimator.ofFloat(dot.y, circleRadius, dot.y).apply {
                duration = (dotAnimationDuration * 0.8).toLong()
                startDelay = starOffset
                starOffset += 100
                addUpdateListener {
                    dot.y = it.animatedValue as Float
                    postInvalidate()
                }
            })
        }

        animatorSet.apply {
            playTogether(animators as Collection<Animator>?)
            start()
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    start()
                }
            })
        }
    }
}