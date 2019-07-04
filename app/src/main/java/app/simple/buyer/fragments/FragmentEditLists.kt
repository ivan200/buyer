package app.simple.buyer.fragments

import android.view.View
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentEditLists : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_edit_lists

    override val title: Int
        get() = R.string.app_name

    private val lists_fab by lazy { mActivity.findViewById<FloatingActionButton>(R.id.lists_fab) }
    private val lists_base_layout by lazy { mActivity.findViewById<LinearLayout>(R.id.lists_base_layout) }
    private val rv_edit_lists by lazy { mActivity.findViewById<RecyclerView>(R.id.rv_edit_lists) }

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(lists_base_layout) { _, insets ->
            app_bar_layout?.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
            rv_edit_lists.setPadding(insets.systemWindowInsetLeft, 0, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
//            add_appbar.setPadding(0, 0, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }

        val adapter = MultiCellTypeAdapter(mActivity, this::showError)
        rv_edit_lists.layoutManager = LinearLayoutManager(mActivity)
        rv_edit_lists.adapter = adapter
        adapter.update((1..50).map { x-> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

    }

}