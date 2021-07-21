package app.simple.buyer

import android.app.Application
import androidx.lifecycle.LiveData
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent
import app.simple.buyer.util.database.Database
import io.realm.RealmObjectChangeListener

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { t, changeSet ->
            if (changeSet?.isFieldChanged(User::darkTheme.name) == true) {
                _darkThemeChanged.call()
            }
        })
    }

    val isDarkThemeOn get() = user.darkTheme

    private val _darkThemeChanged = SingleLiveEvent<Unit?>()
    val darkThemeChanged: LiveData<Unit?> get() = _darkThemeChanged


    fun checkFirstInit(){
        Database.firstInit(context, realm)
    }
}