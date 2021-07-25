package app.simple.buyer.fragments.editlists

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import io.realm.OrderedRealmCollection

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val currentListId = RealmObjectFieldLiveData(user, User::currentListId.name)
    val orderTypeChanged = RealmObjectFieldLiveData(user, User::listsOrderType.name)
    val sortTypeChanged = RealmObjectFieldLiveData(user, User::listsSortAscending.name)

    var mainMenuState: ByteArray
        get() = user.mainMenuScrollState
        set(value) = UserInteractor.updateMainMenuStateAsync(realm, value)

    fun updateOrderType(order: OrderType) {
        UserInteractor.updateOrderTypeAsync(realm, order)
    }

    fun toggleSortAscending() {
        val newSort = SortType.getByValue(!user.listsSortAscending)
        UserInteractor.updateSortAscendingAsync(realm, newSort)
    }

    fun getItems(): OrderedRealmCollection<BuyList> {
        return BuyList.getAllOrderedAsync(realm, user.order, user.sort)
    }

    fun onItemClicked(itemId: Long){
    }

    fun onItemDeleted(itemId: Long){
        ListsInteractor.deleteListAsync(realm, itemId)
    }
}