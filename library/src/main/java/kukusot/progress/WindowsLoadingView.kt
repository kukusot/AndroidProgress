package kukusot.progress

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import kukusot.progress.base.BaseProgressView
import kukusot.progress.base.Dot

class WindowsLoadingView(context: Context, attrs: AttributeSet? = null) : BaseProgressView(context, attrs) {

    private val circleRadius: Float
    private val radius: Float
    private val circleColor: Int
    private val size: Float
    private val numDots: Int
    private val animationDuration: Long

    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.WindowsLoadingView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.WindowsLoadingView_circleRadius, 24f)
            radius = getDimension(R.styleable.WindowsLoadingView_radius, 100f)
            circleColor = getColor(R.styleable.WindowsLoadingView_circleColor, Color.BLACK)
            numDots = getInt(R.styleable.WindowsLoadingView_numDots, 6)
            animationDuration = getInt(R.styleable.WindowsLoadingView_animationDuration, 2000).toLong()
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
            animatorList.add(createAnimator(startOffset))
            startOffset += 100
        }

        animatorSet.apply {
            playTogether(animatorList as Collection<Animator>?)
            start()
        }
    }

    private fun createAnimator(startOffset: Long): ValueAnimator {
        val dotY = circleRadius
        val dot = Dot(size / 2, dotY, circleRadius)
        dots.add(dot)

        return createValueAnimator(startOffset, circleRadius, size - circleRadius, circleRadius) { value, fraction ->
            dot.y = value
            dot.x = calculateXorY(dot.y, fraction)
            postInvalidate()
        }
    }

    private fun calculateXorY(y: Float, animFraction: Float): Float {
        val centre = size / 2
        val rSquared = (radius * radius)
        val absX = Math.sqrt((rSquared - (y - centre) * (y - centre)).toDouble()).toFloat()
        return if (animFraction < 0.5f) {
            absX + centre
        } else {
            centre - absX
        }
    }

    private fun createValueAnimator(
        startOffset: Long,
        vararg animationValues: Float,
        updateBlock: (animatedValue: Float, animatedFraction: Float) -> Unit
    ): ValueAnimator {
        return ValueAnimator.ofFloat(*animationValues).apply {
            startDelay = startOffset
            interpolator = AccelerateDecelerateInterpolator()
            duration = animationDuration
            repeatCount = ValueAnimator.INFINITE
            addUpdateListener {
                updateBlock(it.animatedValue as Float, it.animatedFraction)
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size.toInt(), size.toInt())
    }
}