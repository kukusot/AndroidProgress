package kukusot.progress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import kukusot.progress.base.BaseProgressView
import kukusot.progress.base.Dot

class TypingProgressView(context: Context, attrs: AttributeSet? = null) : BaseProgressView(context, attrs) {

    private var circleRadius: Float
    private var circleSpacing: Float
    private var circleTravel: Float
    private val animationDuration: Long
    private val dotAnimationDuration: Long
    private val numDots: Int

    private val calculatedHeight: Int

    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.TypingProgressView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.TypingProgressView_circleRadius, 24f)
            circleSpacing = getDimension(R.styleable.TypingProgressView_circleSpacing, 10f)
            circleTravel = getDimension(R.styleable.TypingProgressView_circleTravel, 30f)
            paint.color = getColor(R.styleable.TypingProgressView_circleColor, Color.BLACK)
            numDots = getInt(R.styleable.TypingProgressView_numDots, 3)
            animationDuration = getInt(R.styleable.TypingProgressView_animationDuration, 2500).toLong()
            dotAnimationDuration = animationDuration / numDots

            recycle()
        }

        calculatedHeight = (circleRadius * 2 + circleTravel).toInt()
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