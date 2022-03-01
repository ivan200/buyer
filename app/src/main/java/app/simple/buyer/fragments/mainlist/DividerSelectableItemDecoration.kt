package app.simple.buyer.fragments.mainlist

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import kotlin.math.roundToInt

/**
 * Разделитель RecyclerView с возможностью менять цвет когда ячейка становится selectable
 *
 * @param drawableNormal разделитель в нормальном состоянии
 * @param drawableSelected разделитель в выделенном состоянии
 */
class DividerSelectableItemDecoration(
    private val drawableNormal: Drawable?,
    private val drawableSelected: Drawable?
) : ItemDecoration() {

    private val mBounds = Rect()

    override fun onDraw(canvas: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        if (drawableNormal == null || drawableSelected == null) {
            return
        }
        canvas.save()
        val left: Int
        val right: Int
        if (parent.clipToPadding) {
            left = parent.paddingLeft
            right = parent.width - parent.paddingRight
            canvas.clipRect(
                left, parent.paddingTop, right,
                parent.height - parent.paddingBottom
            )
        } else {
            left = 0
            right = parent.width
        }
        val childCount = parent.childCount
        for (i in 0 until childCount) {
            val child = parent.getChildAt(i)
            val nextChild = if (i < childCount - 1) parent.getChildAt(i + 1) else null

            parent.getDecoratedBoundsWithMargins(child, mBounds)

            val bottom = mBounds.bottom + child.translationY.roundToInt()
            val top = bottom - drawableNormal.intrinsicHeight

            val drawable = when {
                nextChild == null -> null
                child.isSelected && nextChild.isSelected -> drawableSelected
                else -> drawableNormal
            }
            drawable?.setBounds(left, top, right, bottom)
            drawable?.alpha = (child.alpha * 255).toInt()
            drawable?.draw(canvas)
        }
        canvas.restore()
    }

    override fun getItemOffsets(
        outRect: Rect, view: View, parent: RecyclerView,
        state: RecyclerView.State
    ) {
        if (drawableNormal == null) {
            outRect.setEmpty()
            return
        }
        val position = (view.layoutParams as RecyclerView.LayoutParams).bindingAdapterPosition
        if (position < state.itemCount) {
            outRect.set(0, 0, 0, drawableNormal.intrinsicHeight)
        } else {
            outRect.setEmpty()
        }
    }
}