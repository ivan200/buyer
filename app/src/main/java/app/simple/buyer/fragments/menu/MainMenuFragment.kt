package app.simple.buyer.fragments.menu

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.appcompat.widget.Toolbar
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.base.BaseFragment
import app.simple.buyer.databinding.FragmentMainMenuBinding
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.asScrollState
import app.simple.buyer.util.savedState
import app.simple.buyer.util.views.viewBinding


class MainMenuFragment : BaseFragment(R.layout.fragment_main_menu), Toolbar.OnMenuItemClickListener {

    private val model: MainMenuViewModel by viewModels()

    private val binding by viewBinding(FragmentMainMenuBinding::bind)

    override val title: Int
        get() = R.string.lists_title

    private var menuShadowToggler: ShadowRecyclerSwitcher? = null

    private val navigateEditLists = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentEditLists, Bundle())

    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapterMenu: MainMenuAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        binding.viewToolbar.toolbar.title = getText(title)
        binding.viewToolbar.toolbar.setOnClickListener(navigateEditLists)
        binding.viewToolbar.toolbar.setOnMenuItemClickListener(this)

        //TODO Добавить отображение emptyView если удалены все списки

        layoutManager = LinearLayoutManager(mActivity)
        layoutManager.onRestoreInstanceState(model.mainMenuState.asScrollState)

        adapterMenu = MainMenuAdapter(model.getItems(), model.getSelectedListId(), model::onMenuItemSelected)
        adapterMenu.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT

        binding.menuRecycler.let {
            it.layoutManager = layoutManager
            it.adapter = adapterMenu
        }

        menuShadowToggler = ShadowRecyclerSwitcher(binding.menuRecycler, binding.viewToolbar.shadowView){
            model.mainMenuState = binding.menuRecycler.savedState
        }

        model.currentListId.observe(viewLifecycleOwner){
            adapterMenu.selectList(it?.currentListId)
        }
        model.listsOrderChanged.observe(viewLifecycleOwner){
            adapterMenu.updateData(model.getItems())
        }
    }

    override fun reApplyInsets(insets: WindowInsetsCompat?) {
        setRecyclerPaddings(mActivity, binding.menuRecycler, binding.viewToolbar.appBarLayout, null, insets, usePaddingLeft = true, usePaddingRight = false)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (binding.viewToolbar.toolbar.menu?.size() == 0) {
            binding.viewToolbar.toolbar.inflateMenu(R.menu.main_menu_list)
        }
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.darkTheme -> {
                model.toggleDarkTheme()
            }
        }
        return true
    }

}