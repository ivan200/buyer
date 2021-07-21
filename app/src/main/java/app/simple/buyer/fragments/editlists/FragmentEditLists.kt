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
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.fragments.ViewHolderSample
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.toParcelable
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import app.simple.buyer.util.views.viewBinding
import io.realm.Sort

class FragmentEditLists : BaseFragment(R.layout.fragment_edit_lists), Toolbar.OnMenuItemClickListener {

    private val model: EditListsViewModel by viewModels()

    override val title: Int
        get() = R.string.app_name

    private val shadowView get() = requireView().findViewById<View>(R.id.shadow_view)
    private val navbarBg get() = requireView().findViewById<LinearLayout>(R.id.nav_bar_layout_bg)

    private val binding by viewBinding(FragmentEditListsBinding::bind)
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        val adapter = MultiCellTypeAdapter(this::showError)
        layoutManager = LinearLayoutManager(mActivity)
        binding.rvEditLists.layoutManager = layoutManager
        binding.rvEditLists.adapter = adapter
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
        adapter.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        val menuState = model.getMainMenuState().toParcelable(LinearLayoutManager.SavedState.CREATOR)
        layoutManager.onRestoreInstanceState(menuState)

        shadowToggler = ShadowRecyclerSwitcher(binding.rvEditLists, shadowView, model::saveMainMenuState)

        setHasOptionsMenu(true)
        toolbar?.setOnMenuItemClickListener(this)
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
    }

    fun checkItem(item: MenuItem, menu: Menu) {
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
        val sortType = true// if(Prefs(requireContext()).listsSortAscending)  Sort.ASCENDING else Sort.DESCENDING

        when (item.itemId) {
            R.id.item_sort_type -> {
//                val invertSort = if(sortType == Sort.ASCENDING)  Sort.DESCENDING else Sort.ASCENDING
//                BuyList.orderBy(realm, Prefs(requireContext()).listsOrderType, invertSort)
//                if (invertSort == Sort.ASCENDING) {
//                    item.setIcon(R.drawable.ic_sort_ascending)
//                } else {
//                    item.setIcon(R.drawable.ic_sort_descending)
//                }
            }
            R.id.item_order_alphabet -> {
                model.onOrderSelected(OrderType.ALPHABET)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_popularity -> {
                model.onOrderSelected(OrderType.POPULARITY)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_size -> {
                model.onOrderSelected(OrderType.SIZE)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_create -> {
                model.onOrderSelected(OrderType.CREATED)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_modify -> {
                model.onOrderSelected(OrderType.MODIFIED)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_price -> {
                model.onOrderSelected(OrderType.PRICE)
                checkItem(item, toolbar.menu)
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
//            android.R.id.home -> {
//                onBackPressed()
//                return true
//            }
        }


//        val id = item.itemId
//        if (id == android.R.id.home) {
//            onBackPressed()
//            overridePendingTransition(0, 0)
//            return true
//        }
        return true
    }

    companion object{
        private const val KEY_SCROLL_STATE = "SCROLL_STATE"
        private fun getScrollState(savedInstanceState: Bundle): LinearLayoutManager.SavedState? =
            savedInstanceState.getParcelable(KEY_SCROLL_STATE)
        private fun saveScrollState(outState: Bundle, linearLayoutManager: LinearLayoutManager) =
            outState.putParcelable(KEY_SCROLL_STATE,  linearLayoutManager.onSaveInstanceState())


    }
}