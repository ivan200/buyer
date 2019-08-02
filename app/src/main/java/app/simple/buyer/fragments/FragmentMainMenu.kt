package app.simple.buyer.fragments

import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
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


class FragmentMainMenu : BaseFragment(R.layout.fragment_main_menu), Toolbar.OnMenuItemClickListener {

    override val title: Int
        get() = R.string.app_name

    private val menu_base_layout by lazy { mView.findViewById<RelativeLayout>(R.id.menu_base_layout) }
    private val menu_toolbar by lazy { mView.findViewById<Toolbar>(R.id.menu_toolbar) }
    private val menu_recycler by lazy { mView.findViewById<RecyclerView>(R.id.menu_recycler) }
    private val menu_toolbar_super by lazy { mView.findViewById<AppBarLayout>(R.id.menu_toolbar_super) }
    private val menu_recycler_super by lazy { mView.findViewById<FrameLayout>(R.id.menu_recycler_super) }

    private val menu_shadow by lazy { mView.findViewById<View>(R.id.menu_shadow_view) }
    private var menuShadowToggler: ShadowRecyclerSwitcher? = null

    private val navigateEditLists = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentEditLists, Bundle())

    override fun initialize(view: View) {
        setRecyclersPaddings()

        setHasOptionsMenu(true)
        menu_toolbar.title = getText(title)
        menu_toolbar.setOnClickListener(navigateEditLists)
        menu_toolbar?.setOnMenuItemClickListener(this)

        val adapterMenu = MultiCellTypeAdapter(mActivity, this::showError)
        menu_recycler.layoutManager = LinearLayoutManager(mActivity)
        menu_recycler.adapter = adapterMenu
        adapterMenu.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })

        menuShadowToggler = ShadowRecyclerSwitcher(menu_recycler, menu_shadow, Prefs(mActivity).mainMenuScrollPosition) { pos -> Prefs(mActivity).mainMenuScrollPosition = pos }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat): WindowInsetsCompat {
        setRecyclersPaddings(insets)
        if (ViewCompat.getLayoutDirection(v) == ViewCompat.LAYOUT_DIRECTION_LTR) {
            menu_toolbar_super.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, 0, 0)
            menu_recycler_super.setPadding(insets.systemWindowInsetLeft, 0, 0, 0)
        } else {
            menu_toolbar_super.setPadding(0, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
            menu_recycler_super.setPadding(0, 0, insets.systemWindowInsetRight, 0)
        }
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclersPaddings()
    }

    fun setRecyclersPaddings(insets: WindowInsetsCompat? = null){
        val toolbarHeight = getToolbarHeight()

        //На телефонах со старыми api не работает onApplyWindowInsetsListener, потому выставляем ручками паддинг под тулбаром
        if (Build.VERSION.SDK_INT < 21) {
            menu_recycler.setPadding(menu_recycler.paddingLeft, toolbarHeight, menu_recycler.paddingRight, menu_recycler.paddingBottom)
        } else if(insets != null){
            menu_recycler.setPadding(0, insets.systemWindowInsetTop + toolbarHeight, 0, insets.systemWindowInsetBottom)
        }
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