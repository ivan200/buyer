package app.simple.buyer.fragments

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentEditListsBinding
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import app.simple.buyer.util.views.viewBinding
import io.realm.Sort

class FragmentEditLists : BaseFragment(R.layout.fragment_edit_lists), Toolbar.OnMenuItemClickListener {
    override val title: Int
        get() = R.string.app_name

    private val shadowView get() = requireView().findViewById<View>(R.id.shadow_view)
    private val binding by viewBinding(FragmentEditListsBinding::bind)
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

        binding.rvEditLists.layoutManager = layoutManager
        binding.rvEditLists.adapter = adapter
        adapter.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        shadowToggler = ShadowRecyclerSwitcher(binding.rvEditLists, shadowView, Prefs(mActivity).mainMenuScrollPosition)

        setHasOptionsMenu(true)
        toolbar?.setOnMenuItemClickListener(this)
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
        setRecyclerPaddings(binding.rvEditLists, appBarLayout, binding.listsFab, insets)
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
        val sortType = if(Prefs(requireContext()).listsSortAscending)  Sort.ASCENDING else Sort.DESCENDING

        when (item.itemId) {
            R.id.item_sort_type -> {
                val invertSort = if(sortType == Sort.ASCENDING)  Sort.DESCENDING else Sort.ASCENDING
                BuyList.orderBy(realm, requireContext(), Prefs(requireContext()).listsOrderType, invertSort)
                if (invertSort == Sort.ASCENDING) {
                    item.setIcon(R.drawable.ic_sort_ascending)
                } else {
                    item.setIcon(R.drawable.ic_sort_descending)
                }
            }
            R.id.item_order_alphabet -> {
                BuyList.orderBy(realm, requireContext(), OrderType.ALPHABET, sortType)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_popularity -> {
                BuyList.orderBy(realm, requireContext(), OrderType.POPULARITY, sortType)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_size -> {
                BuyList.orderBy(realm, requireContext(), OrderType.SIZE, sortType)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_create -> {
                BuyList.orderBy(realm, requireContext(), OrderType.CREATED, sortType)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_modify -> {
                BuyList.orderBy(realm, requireContext(), OrderType.MODIFIED, sortType)
                checkItem(item, toolbar.menu)
            }
            R.id.item_order_price -> {
                BuyList.orderBy(realm, requireContext(), OrderType.PRICE, sortType)
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
}