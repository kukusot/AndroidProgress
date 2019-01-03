package kukusot.progress

import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class SquareDotsLoadingView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    var circleRadius: Float
    var radius: Float
    var circleColor: Int
    val rSqaured: Double
    var size: Float
    val numDots: Int
    val animationDuration: Long

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private var animatorSet: AnimatorSet = AnimatorSet()

    private var dots = arrayListOf<Dot>()


    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.SquareDotsLoadingView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.SquareDotsLoadingView_circleRadius, 24f)
            radius = getDimension(R.styleable.SquareDotsLoadingView_radius, 100f)
            rSqaured = (radius * radius).toDouble()
            circleColor = getColor(R.styleable.SquareDotsLoadingView_circleColor, Color.BLACK)
            numDots = getInt(R.styleable.SquareDotsLoadingView_numDots, 6)
            animationDuration = getInt(R.styleable.SquareDotsLoadingView_animationDuration, 2000).toLong()
            paint.color = circleColor
            size = radius * 2 + circleRadius * 2
            recycle()

            createDots()
        }
    }

    private fun createDots() {
        var startOffset = 0L
        var animatorList = arrayListOf<ValueAnimator?>()
        var animatorArray = arrayOfNulls<ValueAnimator>(numDots * 2)
        for (i in 0 until numDots) {
            animatorList.addAll(createDots(startOffset))
            startOffset += 100
        }
        animatorArray = animatorList.toArray(animatorArray)
        animatorSet.playTogether(*animatorArray)
        animatorSet.start()
    }

    private fun createDots(startOffset: Long): Array<ValueAnimator?> {
        val animatorArray = arrayOfNulls<ValueAnimator>(2)
        val dotY = circleRadius
        val dot = Dot(size / 2, dotY, circleRadius)
        dots.add(dot)
        val yAnimator = ValueAnimator.ofFloat(circleRadius, size - circleRadius, circleRadius)
        yAnimator.startDelay = startOffset
        yAnimator.interpolator = AccelerateDecelerateInterpolator()
        yAnimator.duration = animationDuration

        yAnimator.repeatCount = ValueAnimator.INFINITE
        yAnimator.addUpdateListener {
            val circleY = it.animatedValue as Float
            dot.y = circleY
            postInvalidate()

        }
        val centerX = size / 2
        val xAnimator = ValueAnimator.ofFloat(centerX, size - circleRadius, centerX, circleRadius, centerX)
        xAnimator.startDelay = startOffset
        xAnimator.interpolator = AccelerateDecelerateInterpolator()
        xAnimator.duration = animationDuration

        xAnimator.repeatCount = ValueAnimator.INFINITE
        xAnimator.addUpdateListener {
            dot.x = it.animatedValue as Float
            postInvalidate()
        }

        animatorArray[0] = yAnimator
        animatorArray[1] = xAnimator
        return animatorArray
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(size.toInt(), size.toInt())
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dots.forEach { dot -> dot.draw(canvas, paint) }
    }

    inner class Dot(
        var x: Float,
        var y: Float,
        var radius: Float
    ) {

        fun draw(canvas: Canvas?, paint: Paint) {
            canvas?.drawCircle(x, y, radius, paint)
        }
    }

}

