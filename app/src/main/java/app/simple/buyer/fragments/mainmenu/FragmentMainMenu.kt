package app.simple.buyer.fragments.mainmenu

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
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentMainMenuBinding
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.toParcelable
import app.simple.buyer.util.views.viewBinding


class FragmentMainMenu : BaseFragment(R.layout.fragment_main_menu), Toolbar.OnMenuItemClickListener {

    private val model: MainMenuViewModel by viewModels()

    private val binding by viewBinding(FragmentMainMenuBinding::bind)

    override val title: Int
        get() = R.string.lists_title

    private var menuShadowToggler: ShadowRecyclerSwitcher? = null

    private val navigateEditLists = Navigation.createNavigateOnClickListener(R.id.action_fragmentMain_to_fragmentEditLists, Bundle())

    private lateinit var layoutManager: LinearLayoutManager

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)
        binding.menuToolbar.title = getText(title)
        binding.menuToolbar.setOnClickListener(navigateEditLists)
        binding.menuToolbar.setOnMenuItemClickListener(this)

        val adapterMenu = MainMenuAdapter(model.getItems(), model::onMenuItemSelected)
        adapterMenu.stateRestorationPolicy = RecyclerView.Adapter.StateRestorationPolicy.PREVENT
        binding.menuRecycler.adapter = adapterMenu

        layoutManager = LinearLayoutManager(mActivity)
        binding.menuRecycler.layoutManager = layoutManager
        val menuState = model.getMainMenuState().toParcelable(LinearLayoutManager.SavedState.CREATOR)
        layoutManager.onRestoreInstanceState(menuState)

        menuShadowToggler = ShadowRecyclerSwitcher(binding.menuRecycler, binding.menuShadowView, model::saveMainMenuState)

        model.currentListId.observe(viewLifecycleOwner){
            adapterMenu.selectList(it)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(binding.menuRecycler, binding.menuToolbarSuper, null, insets, usePaddingLeft = true, usePaddingRight = false)
        return super.onApplyWindowInsets(v, insets)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (binding.menuToolbar.menu?.size() == 0) {
            binding.menuToolbar.inflateMenu(R.menu.main)
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