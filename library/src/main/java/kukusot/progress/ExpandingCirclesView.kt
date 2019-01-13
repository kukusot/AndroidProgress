package kukusot.progress

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.util.AttributeSet
import kukusot.progress.base.BaseProgressView

const val FRAME_TIME = (1000 / 60).toLong()

class ExpandingCirclesView(context: Context, attrs: AttributeSet? = null) : BaseProgressView(context, attrs) {

    val radius = 150

    val radiuses = floatArrayOf(0f, 30f, 60f)

    init {
        paint.color = Color.argb(0.5f, 0.8f, 0.7f, 0f)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        setMeasuredDimension(2 * radius, 2 * radius)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        for (i in 0 until radiuses.size) {
            val cr = radiuses[i]
            canvas?.drawCircle(radius.toFloat(), radius.toFloat(), cr, paint)
            radiuses[i] = incrementRadius(cr)
        }
        postDelayed({
            invalidate()
        }, FRAME_TIME)
    }


    private fun incrementRadius(radius: Float): Float {
        return if (radius < 150) {
            (radius + 3)
        } else {
            0f
        }
    }

}