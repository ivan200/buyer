package app.simple.buyer.fragments.editlists

import android.app.Application
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.User
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import io.realm.OrderedRealmCollection

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val currentListId = RealmObjectFieldLiveData(user, User.KEY_CURRENT_LIST_ID)
    val orderTypeChanged = RealmObjectFieldLiveData(user, User.KEY_LISTS_ORDER_TYPE)
    val sortTypeChanged = RealmObjectFieldLiveData(user, User.KEY_LISTS_SORT_ASCENDING)

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