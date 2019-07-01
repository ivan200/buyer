package app.simple.buyer

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import io.realm.Realm
import java.util.logging.Level
import java.util.logging.Logger

abstract class BaseFragment : Fragment(), IEmptyView {
    companion object {
        private val logger = Logger.getLogger(BaseFragment::class.java.name)
    }

    abstract val title: Int
    abstract val layoutId: Int

    lateinit var mActivity: BaseActivity

    override var emptyImageView: ImageView? = null
    override var emptyTextTitle: TextView? = null
    override var emptyTextSubTitle: TextView? = null
    override var emptyView: View? = null
    override val emptyData: EmptyData? = null

    var toolbar: Toolbar? = null

    lateinit var realm: Realm

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(layoutId, container, false)

        mActivity = activity as BaseActivity
        realm = mActivity.realm

        toolbar = view.findViewById(R.id.toolbar)
        if (toolbar != null) {
            mActivity.setSupportActionBar(toolbar)
            mActivity.title = getText(title)
            toolbar!!.setNavigationOnClickListener { mActivity.onBackPressed() }
        }

        emptyView = view.findViewById(R.id.emptyView)
        emptyImageView = view.findViewById(R.id.emptyImageView)
        emptyTextTitle = view.findViewById(R.id.emptyTextTitle)
        emptyTextSubTitle = view.findViewById(R.id.emptyTextSubTitle)

        return view
    }

    override fun toggleEmptyScreen(show: Boolean) {
        if (emptyView != null) {
            emptyView?.clearAnimation()
            if (show) {
                if (emptyData != null) {
                    emptyImageView?.setImageResource(emptyData!!.imageResource)
                    emptyTextTitle?.text = getString(emptyData!!.textTitleResource)
                    emptyTextSubTitle?.text = getString(emptyData!!.textSubTitleResource)
                }

                emptyView?.clearAnimation()
                emptyView?.animate()?.alpha(1.0f)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationStart(animation: Animator?) {
                            super.onAnimationStart(animation)
                            emptyView?.visibility = View.VISIBLE
                        }
                    })
            } else {
                emptyView?.clearAnimation()
                emptyView?.animate()?.alpha(0.0f)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            super.onAnimationEnd(animation)
                            emptyView?.visibility = View.GONE
                        }
                    })
            }
        } else {
            logger.log(Level.WARNING,"NO EMPTY VIEW FOUND")
        }
    }

    fun showError(throwable: Throwable) {
        mActivity.showError(throwable)
    }

    fun isSectionVisible(): Boolean = (((view?.parent as? ViewGroup)?.parent as? ViewGroup)?.visibility == View.VISIBLE)
}