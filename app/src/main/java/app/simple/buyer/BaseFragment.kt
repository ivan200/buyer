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
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import com.google.android.material.appbar.AppBarLayout

abstract class BaseFragment : Fragment(), IEmptyView {
    abstract val title: Int
    abstract val layoutId: Int
    abstract fun initialize(view: View)

    val mActivity  by lazy { activity as BaseActivity }
    val realm by lazy { mActivity.realm }

    override val emptyData: EmptyData? = null
    override val emptyView by lazy { view!!.findViewById<View?>(R.id.emptyView) }
    override val emptyImageView by lazy { view!!.findViewById<ImageView?>(R.id.emptyImageView) }
    override val emptyTextTitle by lazy { view!!.findViewById<TextView?>(R.id.emptyTextTitle) }
    override val emptyTextSubTitle by lazy { view!!.findViewById<TextView?>(R.id.emptyTextSubTitle) }

    val toolbar by lazy { view!!.findViewById<Toolbar?>(R.id.toolbar) }
    val app_bar_layout by lazy { view!!.findViewById<AppBarLayout?>(R.id.app_bar_layout) }
    var linkToView: View? = null
    var initCount = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        if(linkToView == null){
            linkToView = inflater.inflate(layoutId, container, false)
        }
        initCount++
        return linkToView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(initCount <2) {
            if (toolbar != null) {
                mActivity.setSupportActionBar(toolbar)
                mActivity.title = getText(title)
                toolbar!!.setNavigationOnClickListener { mActivity.onBackPressed() }
            }
            initialize(linkToView!!)
        }
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