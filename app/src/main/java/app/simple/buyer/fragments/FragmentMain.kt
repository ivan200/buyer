package app.simple.buyer.fragments

import android.content.Context
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.customview.widget.ViewDragHelper
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.fragments.FragmentMain.DrawerState.*
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.TAG
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton


class FragmentMain : BaseFragment(R.layout.fragment_main) {

    enum class DrawerState {
        START_OPENING,
        FINISH_OPENING,
        START_CLOSING,
        FINISH_CLOSING
    }

    override val title: Int
        get() = R.string.app_name

    private val mDrawer get() = mView.findViewById<DrawerLayout>(R.id.drawer_layout)

    private val recyclerView get() = mView.findViewById<RecyclerView>(R.id.main_recycler)
    private val shadow get() = mView.findViewById<View>(R.id.shadow_view)
    private var mainShadowToggler: ShadowRecyclerSwitcher? = null
    private val fab get() = mView.findViewById<FloatingActionButton>(R.id.fab)

    private lateinit var mDrawerToggle: ActionBarDrawerToggle

    var adapter: MultiCellTypeAdapter? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        mDrawerToggle = object : ActionBarDrawerToggle(mActivity, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close){
            var rDrawerState: Int = ViewDragHelper.STATE_IDLE

            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                if (newState == rDrawerState) return

                val state = getDrawerState(mDrawer, false)
                if (state == rDrawerState) return

                rDrawerState = state

                val drawerOpen = mDrawer.isDrawerOpen(GravityCompat.END)
                val pos = getDrawerPos(rDrawerState, drawerOpen)

//                logger.log(Level.INFO, "RightDrawerState: ${pos.name}")
                val fragmentAddItem = (this@FragmentMain).childFragmentManager.fragments
                        .find { it.TAG == FragmentAddItem::class.java.simpleName } as? FragmentAddItem
                fragmentAddItem?.rightDrawerPositionChanged(pos)
            }

            override fun onDrawerOpened(drawerView: View) {
                val isSecondaryOpened = drawerView.id == R.id.nav_view_right
                val lockGravity = if (isSecondaryOpened) GravityCompat.START else GravityCompat.END
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, lockGravity)
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                val isSecondaryClosed = drawerView.id == R.id.nav_view_right
                val lockGravity = if (isSecondaryClosed) GravityCompat.START else GravityCompat.END
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, lockGravity)
                super.onDrawerClosed(drawerView)
            }

        }

        mDrawer.addDrawerListener(mDrawerToggle)
        mDrawerToggle.syncState()

        fab.setOnClickListener { v ->
            mDrawer.openDrawer(GravityCompat.END)
        }

        setDrawerEdge(mDrawer, true)
        setDrawerEdge(mDrawer, false)

        adapter = MultiCellTypeAdapter(mActivity, this::showError)
        recyclerView.layoutManager = LinearLayoutManager(mActivity)
        recyclerView.adapter = adapter
        adapter!!.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        mainShadowToggler = ShadowRecyclerSwitcher(recyclerView, shadow, Prefs(mActivity).mainScrollPosition) { pos -> Prefs(mActivity).mainScrollPosition = pos }
    }

    fun getDrawerPos(newState: Int, drawerOpen: Boolean): DrawerState {
        return if (newState == DrawerLayout.STATE_IDLE) {
            if (drawerOpen) FINISH_OPENING else FINISH_CLOSING
        } else if (drawerOpen) START_CLOSING else START_OPENING
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(recyclerView, app_bar_layout, fab, insets)
        childFragmentManager.fragments.forEach {
            (it as? BaseFragment)?.onApplyWindowInsets(v, insets)
        }
        return super.onApplyWindowInsets(v, insets)
    }

    private fun setDrawerEdge(mDrawerLayout: DrawerLayout, isLeftDrawer: Boolean = true) {
        try {
            val manager = mActivity.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            val width = DisplayMetrics().also { manager.defaultDisplay.getMetrics(it) }.widthPixels

            val viewDragHelper = mDrawerLayout.javaClass
                    .getDeclaredField(if (isLeftDrawer) "mLeftDragger" else "mRightDragger")
                    .apply { isAccessible = true }
                    .run { get(mDrawerLayout) as ViewDragHelper }

            viewDragHelper.let { it::class.java.getDeclaredField("mEdgeSize") }
                    .apply {
                        isAccessible = true
                        setInt(viewDragHelper, width/2)
                    }
        } catch (e: Exception) {
            e.printStackTrace()
        }
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