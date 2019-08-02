package app.simple.buyer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import com.google.android.material.appbar.AppBarLayout

abstract class BaseFragment(contentLayoutId: Int) : Fragment(contentLayoutId), IEmptyView, OnApplyWindowInsetsListener {
    abstract val title: Int

    abstract fun initialize(view: View)
    val mActivity  by lazy { activity as BaseActivity }

    val realm by lazy { mActivity.realm }
    override val emptyData: EmptyData? = null

    override val emptyView by lazy { mView.findViewById<View?>(R.id.emptyView) }
    override val emptyImageView by lazy { mView.findViewById<ImageView?>(R.id.emptyImageView) }
    override val emptyTextTitle by lazy { mView.findViewById<TextView?>(R.id.emptyTextTitle) }
    override val emptyTextSubTitle by lazy { mView.findViewById<TextView?>(R.id.emptyTextSubTitle) }
    val toolbar by lazy { mView.findViewById<Toolbar?>(R.id.toolbar) }

    val app_bar_layout by lazy { mView.findViewById<AppBarLayout?>(R.id.app_bar_layout) }
    lateinit var mView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if(!::mView.isInitialized){
            mView = super.onCreateView(inflater, container, savedInstanceState)!!

            if (toolbar != null) {
                mActivity.setSupportActionBar(toolbar)
                mActivity.title = getText(title)
                toolbar?.setNavigationOnClickListener { mActivity.onBackPressed() }
            }

            ViewCompat.setOnApplyWindowInsetsListener(mView, this)
            initialize(mView)
        }
        return mView
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        return insets.consumeSystemWindowInsets()
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

    fun setRecyclerPaddings(rView: RecyclerView, insets: WindowInsetsCompat? = null){
        val toolbarHeight = getToolbarHeight()
        val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)

        //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
        if (Build.VERSION.SDK_INT < 21) {
            rView.setPadding(rView.paddingLeft, toolbarHeight, rView.paddingRight, resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2)
        } else if(insets != null){
            rView.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop + toolbarHeight,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom + resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2)
        }
    }

}