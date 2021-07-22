package app.simple.buyer.fragments.editlists

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.interactor.UserInteractor

/**
 * @author ivan200
 * @since 22.07.2021
 */
class AddListViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    val isDarkThemeOn get() = user.darkTheme

}