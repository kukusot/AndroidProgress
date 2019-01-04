package kukusot.progress

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View

class DotsProgressView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    private var latInvalidateTime = 0L
    private var numDots = 3

    private var circleRadius: Float
    private var circleSpacing: Float
    private var circleTravel: Float
    private var startY: Float = 0f
    private val animationDuration: Long


    private val xCenters: Array<Float>
    private val yCenters: Array<Float>

    private val calculatedHeight: Int

    private val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    init {
        val attrSet = context.obtainStyledAttributes(attrs, R.styleable.DotsProgressView)
        with(attrSet) {
            circleRadius = getDimension(R.styleable.DotsProgressView_circleRadius, 24f)
            circleSpacing = getDimension(R.styleable.DotsProgressView_circleSpacing, 10f)
            circleTravel = getDimension(R.styleable.DotsProgressView_circleTravel, 30f)
            paint.color = getColor(R.styleable.DotsProgressView_circleColor, Color.BLACK)
            animationDuration = getInt(R.styleable.DotsProgressView_animationDuration, 1000).toLong()

            recycle()
        }

        val c1x: Float = circleRadius
        val c2x: Float = c1x + circleSpacing + (circleRadius * 2)
        val c3x: Float = c2x + circleSpacing + (circleRadius * 2)
        xCenters = arrayOf(c1x, c2x, c3x)

        calculatedHeight = (circleRadius * 2 + circleTravel).toInt()
        startY = calculatedHeight - circleRadius
        yCenters = arrayOf(startY, startY, startY)
    }

    lateinit var dotsAnimator: AnimatorSet


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val calculatedWidth = ((circleRadius * 2 * numDots) + (circleSpacing * (numDots - 1))).toInt()

        setMeasuredDimension(calculatedWidth, calculatedHeight)
        initAnimator()
    }

    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        for (i in 0 until numDots) {
            yCenters[i] = startY
        }

        if (!::dotsAnimator.isInitialized && !isAttachedToWindow) {
            return
        }

        if (visibility == VISIBLE) {
            dotsAnimator.start()
        } else {
            dotsAnimator.cancel()
        }
    }

    private fun safeInvalidate() {
        val now = System.currentTimeMillis()
        if (now - latInvalidateTime > FRAME_DURATION) {
            latInvalidateTime = now
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(canvas) {
            for (i in 0 until numDots) {
                val cx = xCenters[i]
                val cy = yCenters[i]
                drawCircle(cx, cy, circleRadius, paint)
            }
        }
    }

    private fun initAnimator() {
        val c1yAnimator = createAnimator(0, 0)
        val c2yAnimator = createAnimator(100, 1)
        val c3yAnimator = createAnimator(200, 2)

        dotsAnimator = AnimatorSet()
        dotsAnimator.playTogether(c1yAnimator, c2yAnimator, c3yAnimator)
        dotsAnimator.start()

        dotsAnimator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator?) {
                postDelayed({
                    dotsAnimator.start()
                }, animationDuration)
            }
        })
    }

    private fun createAnimator(startOffSet: Long, circleIndex: Int) =
        ValueAnimator.ofFloat(startY, startY - circleTravel, startY).apply {
            duration = animationDuration
            startDelay = startOffSet
            addUpdateListener {
                yCenters[circleIndex] = it.animatedValue as Float
                safeInvalidate()
            }
        }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        dotsAnimator.cancel()
    }
}