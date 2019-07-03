package app.simple.buyer.fragments

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.Utils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class FragmentMain : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_main

    override val title: Int
        get() = R.string.app_name

    private val drawer_layout by lazy { mActivity.findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val menu_ll by lazy { mActivity.findViewById<LinearLayout>(R.id.menu_ll) }
    private val menu_toolbar by lazy { mActivity.findViewById<Toolbar>(R.id.menu_toolbar) }
    private val menu_rv_list by lazy { mActivity.findViewById<RecyclerView>(R.id.menu_rv_list) }
    private val nav_view by lazy { mActivity.findViewById<NavigationView>(R.id.nav_view) }
    private val fab by lazy { mActivity.findViewById<FloatingActionButton>(R.id.fab) }
    private val navigateOnClickListener = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentAddItem, Bundle())

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        val toggle = object : ActionBarDrawerToggle(mActivity, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val drawerOpen = drawer_layout.isDrawerOpen(GravityCompat.START)
                if (!drawerOpen && (newState == DrawerLayout.STATE_SETTLING || newState == DrawerLayout.STATE_DRAGGING)) {
                    onActivityDrawerOpened()
                }
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                onActivityDrawerClosed()
            }
        }
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        ViewCompat.setOnApplyWindowInsetsListener(nav_view) { _, insets ->
            menu_rv_list.setPadding(0, 0, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            menu_ll.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, 0, 0)
            insets.consumeSystemWindowInsets()
        }

        fab.setOnClickListener(navigateOnClickListener)
    }

    fun onActivityDrawerOpened() {
        Utils.hideKeyboard(mActivity)
        //reInitDrawer()
    }

    fun onActivityDrawerClosed() {
    }

}