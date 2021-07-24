package app.simple.buyer.fragments.menu

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import io.realm.OrderedRealmCollection
import io.realm.RealmObjectChangeListener

class MainMenuViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { changedUser, changeSet ->
            if (changeSet?.isFieldChanged(User::currentListId.name) == true) {
                _currentListId.postValue(changedUser.currentListId)
            }
        })
    }

    private val _currentListId = MutableLiveData(user.currentListId)
    val currentListId: LiveData<Long> get() = _currentListId

    fun saveMainMenuState(scrollState: ByteArray) {
        UserInteractor.updateMainMenuState(realm, scrollState)
    }

    fun getMainMenuState(): ByteArray {
        return user.mainMenuScrollState
    }

    fun toggleDarkTheme() {
        UserInteractor.updateDarkTheme(realm, !user.darkTheme)
    }

    fun getItems(): OrderedRealmCollection<BuyList> {
        return BuyList.getAllOrdered(realm)
    }

    fun onMenuItemSelected(itemId: Long){
        UserInteractor.selectList(realm, itemId)
    }
}