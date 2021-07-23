package app.simple.buyer.fragments.editlists

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentEditListsBinding
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.toParcelable
import app.simple.buyer.util.views.viewBinding


class FragmentEditLists : BaseFragment(R.layout.fragment_edit_lists), Toolbar.OnMenuItemClickListener {

    private val model: EditListsViewModel by viewModels()

    override val title: Int
        get() = R.string.lists_edit_title

    private val shadowView get() = requireView().findViewById<View>(R.id.shadow_view)
    private val navbarBg get() = requireView().findViewById<LinearLayout>(R.id.nav_bar_layout_bg)

    private val binding by viewBinding(FragmentEditListsBinding::bind)
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: EditListsAdapter

    private val orderMapping = listOf(
//        Pair(OrderType.HAND, R.id.item_order_hand),
        Pair(OrderType.ALPHABET, R.id.item_order_alphabet),
        Pair(OrderType.POPULARITY, R.id.item_order_popularity),
        Pair(OrderType.SIZE, R.id.item_order_size),
        Pair(OrderType.CREATED, R.id.item_order_create),
        Pair(OrderType.MODIFIED, R.id.item_order_modify),
        Pair(OrderType.PRICE, R.id.item_order_price),
    )

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        adapter = EditListsAdapter(model.getItems())
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
        layoutManager = LinearLayoutManager(mActivity)
        binding.rvEditLists.layoutManager = layoutManager
        binding.rvEditLists.adapter = adapter
        binding.rvEditLists.itemAnimator?.moveDuration = 0
        binding.rvEditLists.itemAnimator?.changeDuration = 0

        val menuState = model.mainMenuState.toParcelable(LinearLayoutManager.SavedState.CREATOR)
        layoutManager.onRestoreInstanceState(menuState)
        shadowToggler = ShadowRecyclerSwitcher(binding.rvEditLists, shadowView, model::saveMainMenuState)

        setHasOptionsMenu(true)
        toolbar?.setOnMenuItemClickListener(this)

        binding.listsFab.setOnClickListener {
            AddListDialogFragment().show(childFragmentManager, AddListDialogFragment.TAG)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(binding.rvEditLists, appBarLayout, binding.listsFab, insets, navBarBg = navbarBg)
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (toolbar?.menu?.size() == 0) {
            toolbar.inflateMenu(R.menu.order_edit_lists)
            menu.setGroupVisible(R.id.group_normal_mode, true)
            menu.setGroupVisible(R.id.group_reorder_mode, false)
        }
        model.orderTypeChanged.observe(viewLifecycleOwner) { orderType: OrderType ->
            val orderItem = orderMapping.firstOrNull { it.first == orderType }
            if (orderItem != null) {
                checkItem(menu.findItem(orderItem.second), toolbar.menu)
            }
        }
        model.sortTypeChanged.observe(viewLifecycleOwner) {
            val icon = when (it!!) {
                SortType.ASCENDING -> R.drawable.ic_sort_ascending
                SortType.DESCENDING -> R.drawable.ic_sort_descending
            }
            menu.findItem(R.id.item_sort_type)?.setIcon(icon)
        }
    }

    private fun checkItem(item: MenuItem, menu: Menu) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)!!
            menuItem.isCheckable = (menuItem.itemId == item.itemId)
            menuItem.isChecked = (menuItem.itemId == item.itemId)
            if (menuItem.hasSubMenu()) {
                checkItem(item, menuItem.subMenu)
            }
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        val orderItem = orderMapping.firstOrNull { it.second == item.itemId }
        if (orderItem != null) {
            model.updateOrderType(orderItem.first)
        } else {
            when (item.itemId) {
                R.id.item_sort_type -> {
                    model.toggleSortAscending()
                }
                R.id.item_order_hand -> {
                    toolbar.menu.setGroupVisible(R.id.group_normal_mode, false)
                    toolbar.menu.setGroupVisible(R.id.group_reorder_mode, true)
//                adapter?.enableReorderMode(true)
                    item.isChecked = true
                }
                R.id.item_action_clear -> {
                    toolbar.menu.setGroupVisible(R.id.group_reorder_mode, false)
                    toolbar.menu.setGroupVisible(R.id.group_normal_mode, true)
//                adapter?.enableReorderMode(false)
                }
            }
        }
        return true
    }
}