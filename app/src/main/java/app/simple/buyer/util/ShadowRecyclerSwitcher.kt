package app.simple.buyer.util

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class ShadowRecyclerSwitcher(
        recyclerView: RecyclerView,
        val shadowView: View,
        val onScrollStateChanged: Function0<Unit>? = null) {
    private var canScrollUp = false
    private var shadowVisible = false

    init {
        canScrollUp = recyclerView.canScrollVertically(-1)
        shadowVisible = shadowView.visibility == View.VISIBLE
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                onScrollStateChanged?.invoke()
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                canScrollUp = recyclerView.canScrollVertically(-1)
                checkAndToggleShadow(true)
                super.onScrolled(recyclerView, dx, dy)
            }
        })
        checkAndToggleShadow(false)
    }

    fun checkAndToggleShadow(withAnimation: Boolean) {
        when {
            !canScrollUp && shadowVisible -> toggleShadow(false, withAnimation)
            canScrollUp && !shadowVisible -> toggleShadow(true, withAnimation)
        }
    }

    private fun toggleShadow(show: Boolean, withAnimation: Boolean) {
        shadowVisible = show
        if (withAnimation) {
            changeViewVisibilityWithAnimation(shadowView, show)
        } else {
            shadowView.visibility = if (show) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun changeViewVisibilityWithAnimation(view: View, show: Boolean){
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
