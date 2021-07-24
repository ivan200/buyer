package app.simple.buyer.fragments.editlists

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent
import io.realm.OrderedRealmCollection
import io.realm.RealmObjectChangeListener

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception> get() = _error

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { changedUser, changeSet ->
            if (changeSet?.isFieldChanged(User::listsOrderType.name) == true) {
                _orderTypeChanged.postValue(changedUser.order)
            }
            if (changeSet?.isFieldChanged(User::listsSortAscending.name) == true) {
                _sortTypeChanged.postValue(changedUser.sort)
            }
        })
    }

    private val _orderTypeChanged = MutableLiveData(user.order)
    val orderTypeChanged: LiveData<OrderType> get() = _orderTypeChanged

    private val _sortTypeChanged = MutableLiveData(user.sort)
    val sortTypeChanged: LiveData<SortType> get() = _sortTypeChanged

    fun saveMainMenuState(scrollState: ByteArray) {
        try {
            UserInteractor.updateMainMenuState(realm, scrollState)
        } catch (ex: Exception) {
            ex.printStackTrace()
            _error.postValue(ex)
        }
    }

    val mainMenuState: ByteArray get() = user.mainMenuScrollState

    fun updateOrderType(order: OrderType) {
        UserInteractor.updateOrderType(realm, order)
    }

    fun toggleSortAscending() {
        val newSort = SortType.getByValue(!user.listsSortAscending)
        UserInteractor.updateSortAscending(realm, newSort)
    }


    fun getItems(): OrderedRealmCollection<BuyList> {
        return BuyList.getAllOrdered(realm, user.order, user.sort)
    }

    fun onItemClicked(itemId: Long){
    }

    fun onItemDeleted(itemId: Long){
        ListsInteractor.deleteList(realm, itemId)
    }
}