package app.simple.buyer.fragments.mainlist

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ItemInteractor
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import app.simple.buyer.util.getById
import io.realm.OrderedRealmCollection

class MainListViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val listChanged = RealmObjectFieldLiveData(
        user,
        User.KEY_CURRENT_LIST_ID
    )

    val listOrderChanged = RealmObjectFieldLiveData(
        user,
        User.KEY_LIST_ITEMS_ORDER_TYPE,
        User.KEY_LIST_ITEMS_SORT_ASCENDING,
        User.KEY_LIST_ITEMS_CHECKED_POSITION
    )

    var scrollState: ByteArray
        get() = BuyList.getScrollState(realm, user.currentListId)
        set(value) = ListsInteractor.updateListScrollStateAsync(realm, user.currentListId, value)

    fun getTitle(): String? {
        return realm.getById<BuyList>(user.currentListId)?.name
    }

    fun getItems(): OrderedRealmCollection<BuyListItem> {
        return BuyListItem.getAllOrdered(realm, user.currentListId, user.itemsOrder, user.itemsSort, user.itemsCheck)
    }

    fun onItemSelected(itemId: Long){
        ItemInteractor.toggleItemAsync(realm, itemId)
    }
}