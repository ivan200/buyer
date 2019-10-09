package app.simple.buyer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.appcompat.widget.Toolbar
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IEmptyView, OnApplyWindowInsetsListener {
    @StringRes
    open val title = R.string.app_name
    open val titleString: String? = null

    val mActivity get() = activity as BaseActivity
    val realm get() = mActivity.realm

    override val emptyData: EmptyData? = null

    override val emptyView get() = mView.findViewById<View?>(R.id.emptyView)
    override val emptyImageView get() = mView.findViewById<ImageView?>(R.id.emptyImageView)
    override val emptyTextTitle get() = mView.findViewById<TextView?>(R.id.emptyTextTitle)
    override val emptyTextSubTitle get() = mView.findViewById<TextView?>(R.id.emptyTextSubTitle)
    val toolbar get() = mView.findViewById<Toolbar?>(R.id.toolbar)

    val app_bar_layout get() = mView.findViewById<AppBarLayout?>(R.id.app_bar_layout)
    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mView = super.onCreateView(inflater, container, savedInstanceState)!!

        toolbar?.let {
            mActivity.setSupportActionBar(it)
            mActivity.title = titleString ?: getString(title)
            it.setNavigationOnClickListener { mActivity.onBackPressed() }
        }

        if (Build.VERSION.SDK_INT >= 21) {
            ViewCompat.setOnApplyWindowInsetsListener(mView, this)
        } else {
            //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
            onApplyWindowInsets(mView, null)
        }
        return mView
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        if (Build.VERSION.SDK_INT < 21) {
            //На телефонах со старыми api при изменении лейаута onApplyWindowInsetsListener не сработает, потому вызываем ручками
            onApplyWindowInsets(mView, null)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        return insets?.consumeSystemWindowInsets()
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

    fun getToolbarHeight(): Int {
        var toolbarHeight = 0
        val tv = TypedValue()
        if (mActivity.theme.resolveAttribute(androidx.appcompat.R.attr.actionBarSize, tv, true)) {
            toolbarHeight = TypedValue.complexToDimensionPixelSize(tv.data, resources.displayMetrics)
        }
        return toolbarHeight
    }

    fun showError(throwable: Throwable) {
        mActivity.showError(throwable)
    }



    fun setRecyclerPaddings(rView: RecyclerView?, appBar: AppBarLayout?, fab: FloatingActionButton?,
                            insets: WindowInsetsCompat? = null, usePaddingLeft: Boolean = true, usePaddingRight: Boolean = true){
        val toolbarHeight = getToolbarHeight()
        val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)

        val fabOffset = if(fab == null) 0 else resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2

        if(insets != null) {
            val rtl = resources.getBoolean(R.bool.is_rtl)
            val useLeft = if(rtl) usePaddingRight else usePaddingLeft
            val useRight = if(rtl) usePaddingLeft else usePaddingRight

            val insetLeft = if(useLeft) insets.systemWindowInsetLeft else 0
            val insetRight =  if(useRight) insets.systemWindowInsetRight else 0

            appBar?.setPadding(insetLeft, insets.systemWindowInsetTop, insetRight, 0)

            if(fab != null){
                val ll = fab.layoutParams as ViewGroup.MarginLayoutParams
                ll.bottomMargin = margin + insets.systemWindowInsetBottom
                ll.rightMargin = margin + insetRight
                ll.leftMargin = margin + insetLeft
                fab.layoutParams = ll
            }

            rView?.setPadding(insetLeft, insets.systemWindowInsetTop + toolbarHeight,
                    insetRight, insets.systemWindowInsetBottom + fabOffset)
        } else{
            //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
            rView?.setPadding(rView.paddingLeft, toolbarHeight, rView.paddingRight, fabOffset)
        }
    }
}