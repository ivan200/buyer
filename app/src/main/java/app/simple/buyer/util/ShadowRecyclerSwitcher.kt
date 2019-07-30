package app.simple.buyer.util

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShadowRecyclerSwitcher(recyclerView: RecyclerView, val shadowView: View, scrollPos: Int = RecyclerView.NO_POSITION, val onScrollChanged: Function1<Int, Unit>? = null ) {
    private var scrollPosItem = 0
    private var shadowVisible = false
    init {
        scrollPosItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        shadowVisible = shadowView.visibility == View.VISIBLE
        if(scrollPos != RecyclerView.NO_POSITION) {
            recyclerView.scrollToPosition(scrollPos)
        }
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                scrollPosItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                checkAndToggleShadow(true)
                onScrollChanged?.invoke(scrollPosItem)
            }
        })
        checkAndToggleShadow(false)
    }

    fun checkAndToggleShadow(withAnimation: Boolean) {
        when {
            scrollPosItem <= 0 && shadowVisible -> {
                toggleShadow(false, withAnimation)
            }
            scrollPosItem > 0 && !shadowVisible -> {
                toggleShadow(true, withAnimation)
            }
        }
    }

    fun toggleShadow(show: Boolean, withAnimation: Boolean) {
        shadowVisible = show
        if (withAnimation) {
            Utils.changeViewVisibilityWithAnimation(shadowView, show)
        } else {
            shadowView.visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }
}
