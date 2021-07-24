package app.simple.buyer.fragments.menu

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent
import io.realm.OrderedRealmCollection
import io.realm.RealmObjectChangeListener

class MainMenuViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { changedUser, changeSet ->
            if (changeSet?.isFieldChanged(User::currentListId.name) == true) {
                _currentListId.postValue(changedUser.currentListId)
            }
            if (changeSet?.isFieldChanged(User::listsOrderType.name) == true || changeSet?.isFieldChanged(User::listsSortAscending.name) == true) {
                _listsOrderChanged.call()
            }
        })
    }

    private val _listsOrderChanged = SingleLiveEvent<Unit?>()
    val listsOrderChanged: LiveData<Unit?> get() = _listsOrderChanged

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
        return BuyList.getAllOrdered(realm, user.order, user.sort)
    }

    fun onMenuItemSelected(itemId: Long){
        UserInteractor.selectList(realm, itemId)
    }
}