package app.simple.buyer.base

import android.view.View
import androidx.navigation.NavHostController
import androidx.navigation.fragment.DialogFragmentNavigator
import androidx.navigation.fragment.FragmentNavigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.plusAssign
import app.simple.buyer.R
import app.simple.buyer.util.views.AddFragmentNavigator

/**
 * Навхостфрагмент который позволяет добавлять навигацию добавления фрагмента также через add а не только через replace
 *
 * @author ivan200
 * @since 26.02.2022
 */
class FixedNavHostFragment : NavHostFragment() {
    override fun onCreateNavHostController(navHostController: NavHostController) {
        navController.navigatorProvider += DialogFragmentNavigator(requireContext(), childFragmentManager)
        navController.navigatorProvider += FragmentNavigator(requireContext(), childFragmentManager, containerId)
        navController.navigatorProvider += AddFragmentNavigator(requireContext(), childFragmentManager, containerId)
        super.onCreateNavHostController(navHostController)
    }

    private val containerId: Int
        get() {
            val id = id
            return if (id != 0 && id != View.NO_ID) {
                id
            } else R.id.nav_host_fragment_container
        }
}