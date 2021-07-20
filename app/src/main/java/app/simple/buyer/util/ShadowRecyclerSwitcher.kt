package app.simple.buyer.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ShadowRecyclerSwitcher(
        recyclerView: RecyclerView,
        val shadowView: View,
        val onScrollChanged: Function1<ByteArray, Unit>? = null) {
    private var scrollPosItem = 0
    private var shadowVisible = false

    init {
        scrollPosItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
        shadowVisible = shadowView.visibility == View.VISIBLE
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                scrollPosItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
                checkAndToggleShadow(true)

                onScrollChanged?.invoke(
                    (recyclerView.layoutManager as LinearLayoutManager).onSaveInstanceState().toByteArray() ?: ByteArray(0)
                )
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
            changeViewVisibilityWithAnimation(shadowView, show)
        } else {
            shadowView.visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }

    fun changeViewVisibilityWithAnimation(view: View,  show: Boolean){
        view.clearAnimation()
        if (show) {
            view.animate().alpha(1.0f)
                ?.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationStart(animation: Animator?) {
                        super.onAnimationStart(animation)
                        view.visibility = View.VISIBLE
                    }
                })
        } else {
            view.animate().alpha(0.0f)
                ?.setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.INVISIBLE
                    }
                })
        }
    }
}
