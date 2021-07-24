package app.simple.buyer.fragments.main

import android.app.Application
import androidx.lifecycle.LiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent
import app.simple.buyer.util.database.Database
import io.realm.RealmObjectChangeListener

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { changedUser, changeSet ->
            if (changeSet?.isFieldChanged(User::currentListId.name) == true) {
                _currentListChanged.postValue(changedUser.currentListId)
            }
        })
    }

    val isDarkThemeOn get() = user.darkTheme

    private val _currentListChanged = SingleLiveEvent<Long>()
    val currentListChanged: LiveData<Long> get() = _currentListChanged


    fun checkFirstInit(){
        Database.firstInit(context, realm)
    }
}