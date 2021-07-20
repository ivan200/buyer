package app.simple.buyer.fragments

import android.app.Application
import androidx.lifecycle.LiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.LiveRealmObject
import app.simple.buyer.util.SingleLiveEvent

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val _user: LiveRealmObject<User> = LiveRealmObject(UserInteractor.getUser(realm))
    val user: LiveData<User> get() = _user

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
        return _user.value!!.mainMenuState
    }
}