package app.simple.buyer.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.TAG
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class FragmentMain : BaseFragment(R.layout.fragment_main) {

    enum class DrawerPos {
        START_OPENING,
        FINISH_OPENING,
        START_CLOSING,
        FINISH_CLOSING
    }

    override val title: Int
        get() = R.string.app_name

    private val drawerLayout by lazy { mView.findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val rightDrawer by lazy { mView.findViewById<DrawerLayout>(R.id.drawer_right_layout) }

    private val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.main_recycler) }
    private val shadow by lazy { mView.findViewById<View>(R.id.shadow_view) }
    private var mainShadowToggler: ShadowRecyclerSwitcher? = null
    private val nav_view by lazy { mView.findViewById<NavigationView>(R.id.nav_view) }
    private val fab by lazy { mView.findViewById<FloatingActionButton>(R.id.fab) }

    private val navigateEditLists = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentEditLists, Bundle())

    var adapter: MultiCellTypeAdapter? = null

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        val toggle = ActionBarDrawerToggle(mActivity, drawerLayout, toolbar, R.string.app_name, R.string.app_name)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        setRecyclersPaddings()

        fab.setOnClickListener { v ->
            rightDrawer.openDrawer(GravityCompat.END)
        }

        val rightToggle = object : ActionBarDrawerToggle(mActivity, rightDrawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            override fun onDrawerStateChanged(newState: Int) {
                super.onDrawerStateChanged(newState)
                val drawerOpen = rightDrawer.isDrawerOpen(GravityCompat.END)
                val pos = when {
                    !drawerOpen && (newState == DrawerLayout.STATE_SETTLING || newState == DrawerLayout.STATE_DRAGGING) -> DrawerPos.START_OPENING
                    drawerOpen && newState == DrawerLayout.STATE_IDLE -> DrawerPos.FINISH_OPENING
                    drawerOpen && (newState == DrawerLayout.STATE_SETTLING || newState == DrawerLayout.STATE_DRAGGING) -> DrawerPos.START_CLOSING
                    !drawerOpen && newState == DrawerLayout.STATE_IDLE -> DrawerPos.FINISH_CLOSING
                    else -> null
                }
                if(pos != null) {
                    val fragmentAddItem = (this@FragmentMain).childFragmentManager.fragments.find { it.TAG == FragmentAddItem::class.java.simpleName } as? FragmentAddItem
                    fragmentAddItem?.posChanged(pos)
                }
            }
        }
        rightDrawer.addDrawerListener(rightToggle)

        adapter = MultiCellTypeAdapter(mActivity, this::showError)
        recyclerView.layoutManager = LinearLayoutManager(mActivity)
        recyclerView.adapter = adapter
        adapter!!.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        mainShadowToggler = ShadowRecyclerSwitcher(recyclerView, shadow, Prefs(mActivity).mainScrollPosition) { pos -> Prefs(mActivity).mainScrollPosition = pos }
    }

    //Выставление отступов у вьюх так как на вью прозрачный тулбар, статусбар, и навигейшенбар
    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        childFragmentManager.fragments.forEach {
            (it as? BaseFragment)?.onApplyWindowInsets(v, insets)
        }

        setRecyclersPaddings(insets)
        app_bar_layout?.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)

        val ll = fab.layoutParams as ViewGroup.MarginLayoutParams
        val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)
        ll.bottomMargin = margin + insets.systemWindowInsetBottom
        ll.rightMargin = margin + insets.systemWindowInsetRight
        ll.leftMargin = margin + insets.systemWindowInsetLeft
        fab.layoutParams = ll
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclersPaddings()
    }

    fun setRecyclersPaddings(insets: WindowInsetsCompat? = null){
        val toolbarHeight = getToolbarHeight()
        val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)

        //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
        if (Build.VERSION.SDK_INT < 21) {
            recyclerView.setPadding(recyclerView.paddingLeft, toolbarHeight, recyclerView.paddingRight, resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2)
        } else if(insets != null){
            recyclerView.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop + toolbarHeight,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom + resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2)
        }
    }
}