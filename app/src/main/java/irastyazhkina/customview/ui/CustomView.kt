package irastyazhkina.customview.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.withStyledAttributes
import irastyazhkina.customview.R
import irastyazhkina.customview.model.AnimationType
import irastyazhkina.customview.utils.AndroidUtils
import kotlin.math.min
import kotlin.random.Random

class CustomView @JvmOverloads constructor(
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

    private var progress = 0F
    private var valueAnimator: ValueAnimator? = null
    private var animationType = 0


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

        context.theme.obtainStyledAttributes(
            attributeSet,
            R.styleable.CustomView,
            defStyleAttr,
            defStyleRes
        ).apply {
            try {
                animationType = getInteger(R.styleable.CustomView_animationType, 0)
            } finally {
                recycle()
            }
        }
    }

    var data: List<Float> = emptyList()
        set(value) {
            field = value
            update()
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
        isAntiAlias = false
    }

    private val textPaint = Paint(
        Paint.ANTI_ALIAS_FLAG
    ).apply {
        textSize = this@CustomView.textSize
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

        canvas.drawText(
            "%.2f%%".format(100F),
            center.x,
            center.y + textPaint.textSize / 4,
            textPaint
        )
        this.drawProgress(canvas)
    }

    private fun drawProgress(canvas: Canvas) {
        val sum = data.sum()
        var startAngle = -90F

        when (AnimationType.fromInt(animationType)) {
            AnimationType.ROTATION -> {
                data.forEachIndexed { index, it ->
                    val angle = it * 360 / sum
                    paint.color = colors.getOrElse(index) { randomColor() }
                    canvas.drawArc(
                        oval,
                        startAngle + progress * 360,
                        angle * progress,
                        false,
                        paint
                    )
                    startAngle += angle
                }
            }

            AnimationType.SEQUENTIAL -> {
                val rotationAngle = 360 * progress + startAngle

                data.forEachIndexed { index, it ->
                    if (startAngle > rotationAngle) return
                    val angle = it * 360 / sum
                    paint.color = colors.getOrElse(index) { randomColor() }
                    canvas.drawArc(oval, startAngle, rotationAngle - startAngle, false, paint)
                    startAngle += angle
                }
            }

            AnimationType.BIDIRECTIONAL -> {
                data.forEachIndexed { index, it ->
                    val angle = it * 360 / sum
                    paint.color = colors.getOrElse(index) { randomColor() }
                    val halfAngle = angle * progress / 2
                    canvas.drawArc(oval, startAngle - halfAngle, angle * progress, false, paint)
                    startAngle += angle
                }
            }
        }
    }

    private fun update() {
        valueAnimator?.let {
            it.removeAllListeners()
            it.cancel()
        }
        progress = 0F

        valueAnimator = ValueAnimator.ofFloat(0F, 1F).apply {
            addUpdateListener { anim ->
                progress = anim.animatedValue as Float
                invalidate()
            }
            duration = 2000
            interpolator = LinearInterpolator()
        }.also {
            it.start()
        }
    }

    private fun randomColor() = Random.nextInt(0xFF0000000.toInt(), 0xFFFFFFFF.toInt())
}