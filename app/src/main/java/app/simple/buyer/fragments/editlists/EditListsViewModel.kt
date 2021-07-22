package app.simple.buyer.fragments.editlists

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.SingleLiveEvent
import io.realm.RealmObjectChangeListener

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception> get() = _error

    init {
        user.addChangeListener(RealmObjectChangeListener<User> { changedUser, changeSet ->
            if (changeSet?.isFieldChanged(User::listsOrderType.name) == true) {
                val order = OrderType.getByValue(changedUser.listsOrderType)
                _orderTypeChanged.postValue(order)
            }
            if (changeSet?.isFieldChanged(User::listsSortAscending.name) == true) {
                val sort = SortType.getByValue(changedUser.listsSortAscending)
                _sortTypeChanged.postValue(sort)
            }
        })
    }

    private val _orderTypeChanged = MutableLiveData(OrderType.getByValue(user.listsOrderType))
    val orderTypeChanged: LiveData<OrderType> get() = _orderTypeChanged

    private val _sortTypeChanged = MutableLiveData(SortType.getByValue(user.listsSortAscending))
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

    val listsOrderType: OrderType get() = OrderType.getByValue(user.listsOrderType)

    val listsSortAscending: Boolean get() = user.listsSortAscending

    fun updateOrderType(order: OrderType) {
        UserInteractor.updateOrderType(realm, order)
//        BuyList.orderBy(realm, requireContext(), OrderType.ALPHABET, sortType)
    }

    fun toggleSortAscending() {
        val newSort = SortType.getByValue(!user.listsSortAscending)
        UserInteractor.updateSortAscending(realm, newSort)
    }

}