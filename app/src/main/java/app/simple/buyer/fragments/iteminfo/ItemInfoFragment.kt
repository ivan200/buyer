package app.simple.buyer.fragments.iteminfo

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentItemInfoBinding
import app.simple.buyer.util.views.viewBinding


class ItemInfoFragment : BaseFragment(R.layout.fragment_item_info), Toolbar.OnMenuItemClickListener {

    override val title: Int get() = R.string.lists_edit_title

    private val binding by viewBinding(FragmentItemInfoBinding::bind)

    private val navbarBg get() = binding.viewNavbarBg.navBarLayoutBg

    override val toolbar: Toolbar get() = binding.viewToolbar.toolbar

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        setHasOptionsMenu(true)
        toolbar.setOnMenuItemClickListener(this)
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(null, appBarLayout, null, insets, navBarBg = navbarBg)
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (toolbar.menu?.size() == 0) {
            toolbar.inflateMenu(R.menu.item_info)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_item_edit -> {
                //
            }
        }
        return true
    }
}