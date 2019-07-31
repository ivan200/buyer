package app.simple.buyer.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView


class FragmentMain : BaseFragment(R.layout.fragment_main), Toolbar.OnMenuItemClickListener {

    override val title: Int
        get() = R.string.app_name

    private val main_base_layout by lazy { mView.findViewById<LinearLayout>(R.id.main_base_layout) }

    private val drawer_layout by lazy { mView.findViewById<DrawerLayout>(R.id.drawer_layout) }

    private val menu_toolbar by lazy { mView.findViewById<Toolbar>(R.id.menu_toolbar) }
    private val menu_recycler by lazy { mView.findViewById<RecyclerView>(R.id.menu_recycler) }
    private val menu_toolbar_super by lazy { mView.findViewById<AppBarLayout>(R.id.menu_toolbar_super) }
    private val menu_recycler_super by lazy { mView.findViewById<FrameLayout>(R.id.menu_recycler_super) }

    private val recyclerView by lazy { mView.findViewById<RecyclerView>(R.id.main_recycler) }
    private val shadow by lazy { mView.findViewById<View>(R.id.shadow_view) }
    private var mainShadowToggler: ShadowRecyclerSwitcher? = null
    private var menuShadowToggler: ShadowRecyclerSwitcher? = null

    private val nav_view by lazy { mView.findViewById<NavigationView>(R.id.nav_view) }
    private val fab by lazy { mView.findViewById<FloatingActionButton>(R.id.fab) }
    private val menu_shadow by lazy { mView.findViewById<View>(R.id.menu_shadow_view) }

    private val navigateAddItem = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentAddItem, Bundle())
    private val navigateEditLists = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentEditLists, Bundle())

    var adapter: MultiCellTypeAdapter? = null

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(false)
        }

        val toggle = ActionBarDrawerToggle(mActivity, drawer_layout, toolbar, R.string.app_name, R.string.app_name)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        //Выставление отступов у вьюх так как на вью прозрачный тулбар, статусбар, и навигейшенбар
        ViewCompat.setOnApplyWindowInsetsListener(main_base_layout) { _, insets ->
            setRecyclersPaddings(insets)
            app_bar_layout?.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)

            if (ViewCompat.getLayoutDirection(drawer_layout) == ViewCompat.LAYOUT_DIRECTION_LTR) {
                menu_toolbar_super.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, 0, 0)
                menu_recycler_super.setPadding(insets.systemWindowInsetLeft, 0, 0, 0)
            } else {
                menu_toolbar_super.setPadding(0, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
                menu_recycler_super.setPadding(0, 0, insets.systemWindowInsetRight, 0)
            }

            val ll = fab.layoutParams as ViewGroup.MarginLayoutParams
            val margin = resources.getDimensionPixelOffset(R.dimen.margin_default)
            ll.bottomMargin = margin + insets.systemWindowInsetBottom
            ll.rightMargin = margin + insets.systemWindowInsetRight
            ll.leftMargin = margin + insets.systemWindowInsetLeft
            fab.layoutParams = ll
            insets.consumeSystemWindowInsets()
        }

        setRecyclersPaddings()

        fab.setOnClickListener { v ->
            navigateAddItem.onClick(v)

//            Database.deleteAll(mActivity)
        }

        setHasOptionsMenu(true)
        menu_toolbar.title = getText(title)
        menu_toolbar.setOnClickListener(navigateEditLists)
        menu_toolbar?.setOnMenuItemClickListener(this)

        adapter = MultiCellTypeAdapter(mActivity, this::showError)
        recyclerView.layoutManager = LinearLayoutManager(mActivity)
        recyclerView.adapter = adapter
        adapter!!.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        val adapterMenu = MultiCellTypeAdapter(mActivity, this::showError)
        menu_recycler.layoutManager = LinearLayoutManager(mActivity)
        menu_recycler.adapter = adapterMenu
        adapterMenu.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        mainShadowToggler = ShadowRecyclerSwitcher(recyclerView, shadow, Prefs(mActivity).mainScrollPosition) { pos -> Prefs(mActivity).mainScrollPosition = pos }
        menuShadowToggler = ShadowRecyclerSwitcher(menu_recycler, menu_shadow, Prefs(mActivity).mainMenuScrollPosition) { pos -> Prefs(mActivity).mainMenuScrollPosition = pos }
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
            menu_recycler.setPadding(menu_recycler.paddingLeft, toolbarHeight, menu_recycler.paddingRight, menu_recycler.paddingBottom)
        } else if(insets != null){
            recyclerView.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop + toolbarHeight,
                    insets.systemWindowInsetRight, insets.systemWindowInsetBottom + resources.getDimensionPixelOffset(R.dimen.size_fab) + margin * 2)
            menu_recycler.setPadding(0, insets.systemWindowInsetTop + toolbarHeight, 0, insets.systemWindowInsetBottom)
        }
    }

    override fun onResume() {
        super.onResume()
//        Utils.hideKeyboardFrom(drawer_layout)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if(menu_toolbar?.menu?.size() == 0){
            menu_toolbar?.inflateMenu(R.menu.main)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.darkTheme ->{
                Prefs(mActivity).darkTheme = !Prefs(mActivity).darkTheme

                mActivity.recreate()
                //OpenActivityManager.showPrefferences(mActivity)
            }
        }
        return true
    }
}