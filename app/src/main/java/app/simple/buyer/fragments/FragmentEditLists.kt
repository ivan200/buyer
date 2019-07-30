package app.simple.buyer.fragments

import android.content.res.Configuration
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentEditLists : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_edit_lists

    override val title: Int
        get() = R.string.app_name

    private val lists_fab by lazy { view!!.findViewById<FloatingActionButton>(R.id.lists_fab) }
    private val lists_base_layout by lazy { view!!.findViewById<LinearLayout>(R.id.lists_base_layout) }
    private val rv_edit_lists by lazy { view!!.findViewById<RecyclerView>(R.id.rv_edit_lists) }

    private val shadow by lazy { view!!.findViewById<View>(R.id.shadow_view) }
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(lists_base_layout) { _, insets ->
            val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)

            app_bar_layout?.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
            setRecyclerPaddings(rv_edit_lists, insets)

            val ll = lists_fab.layoutParams as ViewGroup.MarginLayoutParams

            ll.bottomMargin = margin + insets.systemWindowInsetBottom
            ll.rightMargin = margin + insets.systemWindowInsetRight
            ll.leftMargin = margin + insets.systemWindowInsetLeft
            lists_fab.layoutParams = ll

            insets.consumeSystemWindowInsets()
        }
        setRecyclerPaddings(rv_edit_lists)

        val adapter = MultiCellTypeAdapter(mActivity, this::showError)
        rv_edit_lists.layoutManager = LinearLayoutManager(mActivity)
        rv_edit_lists.adapter = adapter
        adapter.update((1..50).map { x-> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        shadowToggler = ShadowRecyclerSwitcher(rv_edit_lists, shadow, Prefs(mActivity).mainMenuScrollPosition) { pos -> Prefs(mActivity).mainMenuScrollPosition = pos }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerPaddings(rv_edit_lists)
    }

}