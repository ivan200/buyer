package app.simple.buyer.fragments.main

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import io.realm.RealmObjectChangeListener

class MainViewModel(application: Application) : BaseViewModel(application) {
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

    fun saveScrollState(scrollState: ByteArray) {
        ListsInteractor.updateListScrollState(realm, user.currentListId, scrollState)
    }

    fun getScrollState(): ByteArray {
        return user.mainMenuScrollState
    }

//    fun getItems(): OrderedRealmCollection<BuyListItem> {
//        return realm.getById<BuyList>()?.items
//    }
//

}