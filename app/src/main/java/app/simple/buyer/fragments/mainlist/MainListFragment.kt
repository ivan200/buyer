package app.simple.buyer.fragments.mainlist

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.marginLeft
import androidx.customview.widget.ViewDragHelper
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentMainListBinding
import app.simple.buyer.databinding.ViewMainSortBinding
import app.simple.buyer.fragments.additem.DrawerStateConsumer
import app.simple.buyer.util.ColorUtils
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.asScrollState
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.getDimensionPx
import app.simple.buyer.util.savedState
import app.simple.buyer.util.views.drawer.ActionBarDrawerToggle
import app.simple.buyer.util.views.drawer.DrawerLayout
import app.simple.buyer.util.views.viewBinding


/**
 *
 */
class MainListFragment : BaseFragment(R.layout.fragment_main_list), Toolbar.OnMenuItemClickListener {
    override val title: Int
        get() = R.string.app_name

    private val model: MainListViewModel by viewModels()
    private val binding by viewBinding(FragmentMainListBinding::bind)

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MainListAdapter
    private lateinit var mainShadowToggler: ShadowRecyclerSwitcher

    var actionMode: ActionMode? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        mDrawerToggle = object : ActionBarDrawerToggle(
            mActivity,
            binding.drawer,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            var rDrawerState: Int = ViewDragHelper.STATE_IDLE

            override fun onDrawerStateChanged(newState: Int, drawerView: View?) {
                super.onDrawerStateChanged(newState, drawerView)
                val isSecondaryDrawer = drawerView?.id == R.id.nav_view_right
                if (isSecondaryDrawer) {
                    if (newState == rDrawerState) return
                    rDrawerState = newState
                    val drawerOpen = binding.drawer.isDrawerOpen(GravityCompat.END)
                    val pos = DrawerState.getDrawerPos(rDrawerState, drawerOpen)
                    this@MainListFragment
                        .childFragmentManager
                        .fragments
                        .firstOrNull { it is DrawerStateConsumer }
                        ?.let { it as? DrawerStateConsumer }
                        ?.onDrawerPositionChanged(pos)
                }
            }

            override fun onDrawerOpened(drawerView: View) {
                val isSecondaryOpened = drawerView.id == R.id.nav_view_right
                val lockGravity = if (isSecondaryOpened) GravityCompat.START else GravityCompat.END
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, lockGravity)
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                val isSecondaryClosed = drawerView.id == R.id.nav_view_right
                val lockGravity = if (isSecondaryClosed) GravityCompat.START else GravityCompat.END
                binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, lockGravity)
                super.onDrawerClosed(drawerView)
            }
        }

        binding.drawer.addDrawerListener(mDrawerToggle)

        mDrawerToggle.syncState()

        binding.contentMain.fab.setOnClickListener { v ->
            binding.drawer.openDrawer(GravityCompat.END)
        }

        layoutManager = object : LinearLayoutManager(activity, VERTICAL, false) {
            override fun onLayoutCompleted(state: RecyclerView.State?) {
                super.onLayoutCompleted(state)
                mainShadowToggler.checkAndToggleShadow(binding.contentMain.mainRecycler, true)
            }
        }
        layoutManager.onRestoreInstanceState(model.scrollState.asScrollState)

        adapter = MainListAdapter(model.getItems(), model::onItemClick, model::onItemLongClick, false)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        binding.contentMain.mainRecycler.let {
            it.layoutManager = layoutManager
            it.adapter = adapter
            (it.itemAnimator as? SimpleItemAnimator)?.supportsChangeAnimations = false
            setRecyclerMargin(model.getShowCheckedItems(), false)
        }

        mainShadowToggler = ShadowRecyclerSwitcher(binding.contentMain.mainRecycler, binding.contentMain.viewToolbar.shadowView) {
            model.scrollState = binding.contentMain.mainRecycler.savedState
        }

        model.listChanged.observe(viewLifecycleOwner) {
            adapter.updateDataNoClear(model.getItems())
            updateTitle()
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        model.listOrderChanged.observe(viewLifecycleOwner) {
            setRecyclerMargin(model.getShowCheckedItems(), true)
            adapter.updateDataNoClear(model.getItems())
        }
        updateTitle()

        setHasOptionsMenu(true)
        toolbar?.setOnMenuItemClickListener(this)

        ColorUtils.changeFabShadowColor(binding.contentMain.fab, requireContext().getColorResCompat(R.attr.colorFabShadow))

        model.actionModeStart.observe(viewLifecycleOwner) {
            actionMode = mActivity.startSupportActionMode(callback)
        }

        model.actionModeStop.observe(viewLifecycleOwner) {
            actionMode?.finish()
        }
        //TODO Добавить экспорт списка
    }


    /**
     * Установить отступ у ресайклера (в отрицательный или 0),
     * чтобы если мы выберем не показывать завершенные таски часть ячейки с чекбоксом уехала налево за границу экрана
     *
     * @param showChecked показывать ли прочеканые элементы
     * @param withAnimation делать ли это с анимацией (при действии), или без (при старте экрана)
     */
    fun setRecyclerMargin(showChecked: Boolean, withAnimation: Boolean) {
        val newLeftMargin = if (showChecked) 0 else -1 * requireContext().getDimensionPx(R.dimen.size_icon_bounding).toInt()
        val recycler = binding.contentMain.mainRecycler
        if (recycler.marginLeft != newLeftMargin) {
            if (!withAnimation) {
                val params = recycler.layoutParams as RelativeLayout.LayoutParams
                params.leftMargin = newLeftMargin
                recycler.layoutParams = params
            } else {
                val params = recycler.layoutParams as RelativeLayout.LayoutParams
                val animator = ValueAnimator.ofInt(params.leftMargin, newLeftMargin)
                animator.addUpdateListener { valueAnimator ->
                    params.leftMargin = valueAnimator.animatedValue as Int
                    recycler.requestLayout()
                }
                animator.duration = resources.getInteger(android.R.integer.config_shortAnimTime).toLong()
                animator.start()
            }
        }
    }

    private fun updateTitle() {
        mActivity.title = model.getTitle() ?: getString(title)
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(
            binding.contentMain.mainRecycler,
            appBarLayout,
            binding.contentMain.fab,
            insets,
            navBarBg = binding.navbar.navBarLayoutBg
        )
        childFragmentManager.fragments.forEach {
            (it as? BaseFragment)?.onApplyWindowInsets(v, insets)
        }
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_list_order -> {
                val binding = ViewMainSortBinding.inflate(LayoutInflater.from(context))
                val popup = MainPopupWindow(binding, model, viewLifecycleOwner)
                popup.showAsDropDown(requireView().findViewById(R.id.action_list_order))
            }
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (binding.contentMain.viewToolbar.toolbar.menu?.size() == 0) {
            binding.contentMain.viewToolbar.toolbar.inflateMenu(R.menu.main_list)
        }
    }

    private val callback = object : ActionMode.Callback {
        override fun onCreateActionMode(mode: ActionMode, menu: Menu?): Boolean {
            mode.menuInflater.inflate(R.menu.main_list_edit, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            adapter.isActionMode = true
            binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.action_item_delete -> {
                    model.deleteItem()
                    return true
                }
                //TODO Добавить экспорт и импорт списков

                //Экшен действия:
                //переместить выбранное в новый список
                //экспортировать
                //Архивировать
                //Удалить насовсем??
            }
            return false
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            model.onExitFromActionMode()
            binding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            adapter.isActionMode = false
            actionMode = null
        }
    }
}
