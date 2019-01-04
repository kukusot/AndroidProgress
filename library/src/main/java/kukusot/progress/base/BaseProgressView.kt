package kukusot.progress.base

import android.animation.AnimatorSet
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.util.Log
import android.view.View


abstract class BaseProgressView(context: Context, attrs: AttributeSet? = null) : View(context, attrs) {

    protected val animatorSet: AnimatorSet = AnimatorSet()
    protected val dots = arrayListOf<Dot>()
    protected val paint: Paint = Paint(Paint.ANTI_ALIAS_FLAG)


    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animatorSet.cancel()
    }


    override fun setVisibility(visibility: Int) {
        super.setVisibility(visibility)
        if (visibility == VISIBLE) {
            animatorSet.start()
        } else {
            animatorSet.cancel()
            dots.forEach {
                it.reset()
            }
        }
    }

    var drawCount = 0
    var lastDrawTime = System.currentTimeMillis()

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        dots.forEach { it.draw(canvas, paint) }
        drawCount++
        if (System.currentTimeMillis() - lastDrawTime >= 1000) {
            lastDrawTime = System.currentTimeMillis()
            Log.e("DrawCalls", " " + drawCount + " " + javaClass.canonicalName)
            drawCount = 0
        }
    }

}

class Dot(
    var x: Float,
    var y: Float,
    var radius: Float
) {

    private val startX: Float = x
    private val startY: Float = y

    fun draw(canvas: Canvas?, paint: Paint) {
        canvas?.drawCircle(x, y, radius, paint)
    }

    fun reset() {
        x = startX
        y = startY
    }
}