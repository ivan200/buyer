package app.simple.buyer.fragments

import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentEditLists : BaseFragment(R.layout.fragment_edit_lists) {
    override val title: Int
        get() = R.string.app_name

    private val lists_fab get() =  mView.findViewById<FloatingActionButton>(R.id.lists_fab)
    private val lists_base_layout get() =  mView.findViewById<RelativeLayout>(R.id.lists_base_layout)
    private val rv_edit_lists get() =  mView.findViewById<RecyclerView>(R.id.rv_edit_lists)

    private val shadow by lazy { mView.findViewById<View>(R.id.shadow_view) }
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }


        val adapter = MultiCellTypeAdapter(mActivity, this::showError)

        layoutManager = LinearLayoutManager(mActivity)

        rv_edit_lists.layoutManager = layoutManager
        rv_edit_lists.adapter = adapter
        adapter.update((1..50).map { x-> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        shadowToggler = ShadowRecyclerSwitcher(rv_edit_lists, shadow, Prefs(mActivity).mainMenuScrollPosition)
    }

    override fun onPause() {
        Prefs(mActivity).mainMenuState = layoutManager.onSaveInstanceState()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        layoutManager.onRestoreInstanceState(Prefs(mActivity).mainMenuState)
    }


    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(rv_edit_lists, app_bar_layout, lists_fab, insets)
        return super.onApplyWindowInsets(v, insets)
    }

}