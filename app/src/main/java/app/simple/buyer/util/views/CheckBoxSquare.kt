package app.simple.buyer.util.views

import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.Keep
import app.simple.buyer.R
import app.simple.buyer.util.ColorUtils
import app.simple.buyer.util.dpToPx
import app.simple.buyer.util.dpToPxInt
import app.simple.buyer.util.getColorResCompat
import kotlin.math.min

/**
 * Вью чекбокса, которое быстрее отрисовывается чем стандартный чекбокс.
 * Влияет на лаги при инициализации фрагмента
 */
class CheckBoxSquare : View {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(context!!, attrs, defStyle)

    private val rectF = RectF()
    private val drawBitmap = Bitmap.createBitmap(18.dpToPxInt(), 18.dpToPxInt(), Bitmap.Config.ARGB_4444)
    private val drawCanvas = Canvas(drawBitmap)
    private var progress = 0f
    private var checkAnimator: ObjectAnimator? = null
    private var attachedToWindow = false
    var isChecked = false
        private set

    @ColorInt
    private var checkboxSquareUnchecked = context.getColorResCompat(R.attr.colorToolbarIcon)

    @ColorInt
    private var checkboxSquareBackground = ColorUtils.getThemeAccentColor(context)

    @ColorInt
    private var checkboxSquareCheck = context.getColorResCompat(R.attr.colorBackground)

    fun setColors(@ColorInt unchecked: Int, @ColorInt checked: Int, @ColorInt check: Int) {
        checkboxSquareUnchecked = unchecked
        checkboxSquareBackground = checked
        checkboxSquareCheck = check
    }

    @Keep
    fun setProgress(value: Float) {
        if (progress == value) {
            return
        }
        progress = value
        invalidate()
    }

    @Keep
    fun getProgress(): Float {
        return progress
    }

    private fun cancelCheckAnimator() {
        if (checkAnimator != null) {
            checkAnimator!!.cancel()
        }
    }

    private fun animateToCheckedState(newCheckedState: Boolean) {
        checkAnimator = ObjectAnimator.ofFloat(this, "progress", if (newCheckedState) 1f else 0f)
        checkAnimator!!.duration = 300
        checkAnimator!!.start()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        attachedToWindow = true
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        attachedToWindow = false
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
    }

    fun setChecked(checked: Boolean, animated: Boolean) {
        if (checked == isChecked) {
            return
        }
        isChecked = checked
        if (attachedToWindow && animated) {
            animateToCheckedState(checked)
        } else {
            cancelCheckAnimator()
            setProgress(if (checked) 1.0f else 0.0f)
        }
    }

    override fun onDraw(canvas: Canvas) {
        if (visibility != VISIBLE) {
            return
        }
        val checkProgress: Float
        val bounceProgress: Float
        val uncheckedColor = checkboxSquareUnchecked
        val color = checkboxSquareBackground
        if (progress <= 0.5f) {
            checkProgress = progress / 0.5f
            bounceProgress = checkProgress
            val rD = ((Color.red(color) - Color.red(uncheckedColor)) * checkProgress).toInt()
            val gD = ((Color.green(color) - Color.green(uncheckedColor)) * checkProgress).toInt()
            val bD = ((Color.blue(color) - Color.blue(uncheckedColor)) * checkProgress).toInt()
            val c = Color.rgb(Color.red(uncheckedColor) + rD, Color.green(uncheckedColor) + gD, Color.blue(uncheckedColor) + bD)
            checkboxSquare_backgroundPaint.color = c
        } else {
            bounceProgress = 2.0f - progress / 0.5f
            checkProgress = 1.0f
            checkboxSquare_backgroundPaint.color = color
        }
        val bounce = 1.dpToPxInt() * bounceProgress
        rectF[bounce, bounce, 18.dpToPxInt() - bounce] = 18.dpToPxInt() - bounce
        drawBitmap.eraseColor(0)
        drawCanvas.drawRoundRect(rectF, 2.dpToPxInt().toFloat(), 2.dpToPxInt().toFloat(), checkboxSquare_backgroundPaint)
        if (checkProgress != 1f) {
            val rad = min(7.dpToPxInt().toFloat(), 7.dpToPxInt() * checkProgress + bounce)
            rectF[2.dpToPxInt() + rad, 2.dpToPxInt() + rad, 16.dpToPxInt() - rad] = 16.dpToPxInt() - rad
            drawCanvas.drawRect(rectF, checkboxSquare_eraserPaint)
        }
        if (progress > 0.5f) {
            checkboxSquare_checkPaint.color = checkboxSquareCheck
            var endX = (7.dpToPxInt() - 3.dpToPxInt() * (1.0f - bounceProgress)).toInt()
            var endY = (13.dpToPx() - 3.dpToPxInt() * (1.0f - bounceProgress)).toInt()
            drawCanvas.drawLine(7.dpToPxInt().toFloat(), 13.dpToPx(), endX.toFloat(), endY.toFloat(), checkboxSquare_checkPaint)
            endX = (7.dpToPx() + 7.dpToPxInt() * (1.0f - bounceProgress)).toInt()
            endY = (13.dpToPx() - 7.dpToPxInt() * (1.0f - bounceProgress)).toInt()
            drawCanvas.drawLine(7.dpToPx(), 13.dpToPx(), endX.toFloat(), endY.toFloat(), checkboxSquare_checkPaint)
        }
        canvas.drawBitmap(drawBitmap, 0f, 0f, null)
    }

    companion object {
        val checkboxSquare_backgroundPaint: Paint by lazy { Paint(Paint.ANTI_ALIAS_FLAG) }
        val checkboxSquare_checkPaint: Paint by lazy {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                style = Paint.Style.STROKE
                strokeWidth = 2.dpToPxInt().toFloat()
                strokeCap = Paint.Cap.ROUND
            }
        }
        val checkboxSquare_eraserPaint: Paint by lazy {
            Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = 0
                xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
            }
        }
    }
}