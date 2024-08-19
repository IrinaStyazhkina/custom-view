package irastyazhkina.customview.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import irastyazhkina.customview.R
import irastyazhkina.customview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class NotFilledCustomView@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0,
) : View(
    context,
    attributeSet,
    defStyleAttr,
    defStyleRes
) {
    private var textSize = AndroidUtils.dp(context, 20).toFloat()
    private var lineWidth = AndroidUtils.dp(context, 5)
    private var colors = emptyList<Int>()

    init {
        context.withStyledAttributes(attributeSet, R.styleable.CustomView) {
            textSize = getDimension(R.styleable.CustomView_textSize, textSize)
            lineWidth = getDimension(R.styleable.CustomView_lineWidth, lineWidth.toFloat()).toInt()
            colors = listOf(
                getColor(R.styleable.CustomView_color1, randomColor()),
                getColor(R.styleable.CustomView_color2, randomColor()),
                getColor(R.styleable.CustomView_color3, randomColor()),
                getColor(R.styleable.CustomView_color4, randomColor()),
            )
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            invalidate()
        }
    private var radius = 0F
    private var center = PointF()
    private var oval = RectF()
    private val paint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.STROKE
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@NotFilledCustomView.textSize
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
    }

    private val dotPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        strokeWidth = lineWidth.toFloat()
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        color = colors.first()
    }


    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        radius = min(w, h) / 2F - lineWidth
        center = PointF(w / 2F, h / 2F)
        oval = RectF(
            center.x - radius,
            center.y - radius,
            center.x + radius,
            center.y + radius,
        )
    }

    override fun onDraw(canvas: Canvas) {
        if (data.isEmpty()) return
        paint.color = 0xFFD3D3D3.toInt()

        canvas.drawCircle(center.x, center.y, radius, paint)
        var startAngle = -90F

        data.forEachIndexed { index, it ->
            val angle = it * 360
            paint.color = colors.getOrElse(index) {randomColor()}
            canvas.drawArc(oval, startAngle, angle, false, paint)
            startAngle += angle
        }

        canvas.drawText(
            "%.2f%%".format(data.sum() * 100),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )

        canvas.drawPoint(center.x, center.y - radius, dotPaint)
    }

    private fun randomColor() = Random.nextInt(0xFF0000000.toInt(), 0xFFFFFFFF.toInt())
}