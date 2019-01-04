package kukusot.progress

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import kukusot.progress.base.BaseProgressView
import kukusot.progress.base.Dot

class SquareDotsLoadingView(context: Context, attrs: AttributeSet? = null) : BaseProgressView(context, attrs) {

    private val circleRadius: Float
    private val radius: Float
    private val circleColor: Int
    private val size: Float
    private val numDots: Int
    private val animationDuration: Long

    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.SquareDotsLoadingView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.SquareDotsLoadingView_circleRadius, 24f)
            radius = getDimension(R.styleable.SquareDotsLoadingView_radius, 100f)
            circleColor = getColor(R.styleable.SquareDotsLoadingView_circleColor, Color.BLACK)
            numDots = getInt(R.styleable.SquareDotsLoadingView_numDots, 6)
            animationDuration = getInt(R.styleable.SquareDotsLoadingView_animationDuration, 2000).toLong()
            paint.color = circleColor
            size = radius * 2 + circleRadius * 2
            recycle()
        }

        createDots()
    }

    @Suppress("UNCHECKED_CAST")
    private fun createDots() {
        var startOffset = 0L
        val animatorList = arrayListOf<ValueAnimator?>()
        for (i in 0 until numDots) {
            animatorList.addAll(createAnimators(startOffset))
            startOffset += 100
        }

        animatorSet.apply {
            playTogether(animatorList as Collection<Animator>?)
            start()
        }
    }

    private fun createAnimators(startOffset: Long): List<ValueAnimator> {
        val dotY = circleRadius
        val dot = Dot(size / 2, dotY, circleRadius)
        dots.add(dot)

        val yAnimator = createValueAnimator(startOffset, circleRadius, size - circleRadius, circleRadius) {
            dot.y = it
            postInvalidate()
        }

        val centerX = size / 2
        val xAnimator = createValueAnimator(startOffset, centerX, size - circleRadius, centerX, circleRadius, centerX) {
            dot.x = it
            postInvalidate()
        }

        return listOf(yAnimator, xAnimator)
    }

    private fun createValueAnimator(
        startOffset: Long,
        vararg animationValues: Float,
        updateBlock: (animatedValue: Float) -> Unit
    ): ValueAnimator {
        return ValueAnimator.ofFloat(*animationValues).apply {
            startDelay = startOffset
            interpolator = AccelerateDecelerateInterpolator()
            duration = animationDuration
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateBlock(it.animatedValue as Float)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size.toInt(), size.toInt())
    }

}


