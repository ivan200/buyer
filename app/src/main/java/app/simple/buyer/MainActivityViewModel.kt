package app.simple.buyer

import android.app.Application
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldSingleLiveEvent
import app.simple.buyer.util.database.Database

class MainActivityViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val darkThemeChanged = RealmObjectFieldSingleLiveEvent(user, User::darkTheme.name)

    val isDarkThemeOn get() = user.darkTheme

    fun checkFirstInit() {
        Database.firstInit(context, realm)
    }
}