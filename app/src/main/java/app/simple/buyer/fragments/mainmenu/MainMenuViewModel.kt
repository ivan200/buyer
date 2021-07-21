package app.simple.buyer.fragments.mainmenu

import android.app.Application
import androidx.lifecycle.LiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent

class MainMenuViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception> get() = _error

    fun saveMainMenuState(scrollState: ByteArray) {
        try {
            UserInteractor.updateMainMenuState(realm, scrollState)
        } catch (ex: Exception) {
            ex.printStackTrace()
            _error.postValue(ex)
        }
    }

    fun getMainMenuState(): ByteArray {
        return user.mainMenuScrollState
    }

    fun toggleDarkTheme() {
        UserInteractor.updateDarkTheme(realm, !user.darkTheme)
    }

}