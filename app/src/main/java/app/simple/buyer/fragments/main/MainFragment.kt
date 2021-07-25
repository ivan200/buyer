package app.simple.buyer.fragments.main

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentMainBinding
import app.simple.buyer.fragments.additem.DrawerStateConsumer
import app.simple.buyer.fragments.main.DrawerState.*
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.asScrollState
import app.simple.buyer.util.savedState
import app.simple.buyer.util.views.viewBinding


class MainFragment : BaseFragment(R.layout.fragment_main) {
    override val title: Int
        get() = R.string.app_name

    private val model: MainViewModel by viewModels()
    private val binding by viewBinding(FragmentMainBinding::bind)

    private lateinit var mDrawerToggle: ActionBarDrawerToggle
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: MainAdapter
    private lateinit var mainShadowToggler: ShadowRecyclerSwitcher

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

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)

                if (newState == rDrawerState) return
                val state = getDrawerState(binding.drawer, false)
                if (state == rDrawerState) return
                rDrawerState = state

                val drawerOpen = binding.drawer.isDrawerOpen(GravityCompat.END)
                val pos = DrawerState.getDrawerPos(rDrawerState, drawerOpen)
                this@MainFragment
                    .childFragmentManager
                    .fragments
                    .firstOrNull { it is DrawerStateConsumer }
                    ?.let { it as? DrawerStateConsumer }
                    ?.onDrawerPositionChanged(pos)
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

        layoutManager = LinearLayoutManager(mActivity)
        layoutManager.onRestoreInstanceState(model.scrollState.asScrollState)

        adapter = MainAdapter(model.getItems(), model::onItemSelected)
        adapter.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        binding.contentMain.mainRecycler.let {
            it.layoutManager = layoutManager
            it.adapter = adapter
        }

        mainShadowToggler = ShadowRecyclerSwitcher(binding.contentMain.mainRecycler, binding.contentMain.viewToolbar.shadowView) {
            model.scrollState = binding.contentMain.mainRecycler.savedState
        }

        model.listChanged.observe(viewLifecycleOwner){
            adapter.updateData(model.getItems())
            updateTitle()
            binding.drawer.closeDrawer(GravityCompat.START)
        }
        model.listOrderChanged.observe(viewLifecycleOwner){
            adapter.updateData(model.getItems())
        }
        updateTitle()

        //TODO Доприкрутить менюшки
        //TODO Добавить экспорт списка
    }

    private fun updateTitle(){
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

    private fun getDrawerState(mDrawerLayout: DrawerLayout, isLeftDrawer: Boolean = true): Int {
        try {
            val viewDragHelper = mDrawerLayout.javaClass
                .getDeclaredField(if (isLeftDrawer) "mLeftDragger" else "mRightDragger")
                .apply { isAccessible = true }
                .run { get(mDrawerLayout) as ViewDragHelper }

            return viewDragHelper.viewDragState
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

}