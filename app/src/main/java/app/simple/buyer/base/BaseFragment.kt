package app.simple.buyer.base

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsCompat.Type.ime
import androidx.core.view.WindowInsetsCompat.Type.systemBars
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.MainActivity
import app.simple.buyer.R
import app.simple.buyer.util.ColorUtils
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.max

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IEmptyView, OnApplyWindowInsetsListener {
    @StringRes
    open val title = R.string.app_name
    open val titleString: String? = null

    val mActivity get() = activity as MainActivity

    override val emptyData: EmptyData? = null

    open val toolbar get() = view?.findViewById<Toolbar?>(R.id.toolbar)
    open val appBarLayout get() = view?.findViewById<AppBarLayout?>(R.id.app_bar_layout)

    override val emptyViewRoot: View? get() = view

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar?.let {
            mActivity.setSupportActionBar(it)
            mActivity.title = titleString ?: getString(title)
            it.setNavigationOnClickListener { mActivity.onBackPressed() }
        }
        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setOnApplyWindowInsetsListener(requireView(), this)
        } else {
            //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
            reApplyInsets(null)
        }
        ColorUtils.changeOverScrollGlowColor(resources, requireContext().getColorResCompat(R.attr.colorFabShadow))
    }

    fun updateTitle(newTitle: String?) {
        val t = newTitle ?: titleString ?: getString(title)
        toolbar?.title = t
        mActivity.title = t
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Build.VERSION.SDK_INT < 21) {
            //На телефонах со старыми api при изменении лейаута onApplyWindowInsetsListener не сработает, потому вызываем ручками
            reApplyInsets(null)
        }
    }

    open fun reApplyInsets(insets: WindowInsetsCompat?){}

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        reApplyInsets(insets)
        return insets
    }

    override fun toggleEmptyScreen(show: Boolean) {
        emptyView?.let {
            it.clearAnimation()
            if (show) {
                emptyData?.apply {
                    emptyImageView?.setImageResource(imageResource)
                    emptyTextTitle?.text = getString(textTitleResource)
                    emptyTextSubTitle?.text = getString(textSubTitleResource)
                }
                it.animate()
                    .alpha(1.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            it.show()
                        }
                    })
            } else {
                it.animate()
                    .alpha(0.0f)
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            it.hide()
                        }
                    })
            }
        }
    }

    companion object {

        private fun getToolbarHeight(activity: Activity): Int {
            var toolbarHeight = 0
            val tv = TypedValue()
            if (activity.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
                toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, activity.resources.displayMetrics)
            }
            return toolbarHeight
        }

        fun setRecyclerPaddings(
            activity: Activity,
            rView: RecyclerView?,
            appBar: AppBarLayout?,
            fab: FloatingActionButton?,
            insets: WindowInsetsCompat? = null,
            usePaddingLeft: Boolean = true,
            usePaddingRight: Boolean = true,
            navBarBg: View? = null
        ) {
            val toolbarHeight = getToolbarHeight(activity)
            val margin = activity.resources.getDimensionPixelOffset(R.dimen.margin_default)

            val fabOffset = if (fab == null) 0 else activity.resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2

            if (insets != null) {
                val rtl = activity.resources.getBoolean(R.bool.is_rtl)
                val useLeft = if (rtl) usePaddingRight else usePaddingLeft
                val useRight = if (rtl) usePaddingLeft else usePaddingRight

                val systemInsets = insets.getInsets(systemBars())

                val insetLeft = if (useLeft) systemInsets.left else 0
                val insetRight = if (useRight) systemInsets.right else 0

                appBar?.setPadding(insetLeft, systemInsets.top, insetRight, 0)

                if (fab != null) {
                    val imeInsets = insets.getInsets(ime())

                    val ll = fab.layoutParams as ViewGroup.MarginLayoutParams
                    ll.bottomMargin = margin + max(systemInsets.bottom, imeInsets.bottom)
                    ll.rightMargin = margin + insetRight
                    ll.leftMargin = margin + insetLeft
                    fab.layoutParams = ll
                }

                rView?.setPadding(
                    insetLeft, systemInsets.top + toolbarHeight,
                    insetRight, systemInsets.bottom + fabOffset
                )

                navBarBg?.layoutParams?.height = systemInsets.bottom
            } else {
                //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
                rView?.setPadding(rView.paddingLeft, toolbarHeight, rView.paddingRight, fabOffset)
            }
        }
    }
}


