package app.simple.buyer

import android.app.Application
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.Database
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldSingleLiveEvent

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val darkThemeChanged = RealmObjectFieldSingleLiveEvent(user, User.KEY_DARK_THEME)

    val isDarkThemeOn: Boolean
        get() {
            return try {
                user.darkTheme
            } catch (ex: Exception){
                User.DARK_THEME_DEFAULT
            }
        }

    fun checkFirstInit() {
        Database.firstInit(context, realm)
    }
}