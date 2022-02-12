package app.simple.buyer

import android.app.Application
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldSingleLiveEvent
import app.simple.buyer.util.database.Database

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val darkThemeChanged = RealmObjectFieldSingleLiveEvent(user, User.KEY_DARK_THEME)

    val isDarkThemeOn get() = user.darkTheme

    fun checkFirstInit() {
        Database.firstInit(context, realm)
    }
}