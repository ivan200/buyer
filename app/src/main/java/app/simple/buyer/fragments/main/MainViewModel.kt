package app.simple.buyer.fragments.main

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import io.realm.OrderedRealmCollection

class MainViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val currentListId = RealmObjectFieldLiveData(user, User::currentListId.name)

    var scrollState: ByteArray
        get() = BuyList.getScrollState(realm, user.currentListId)
        set(value) = ListsInteractor.updateListScrollStateAsync(realm, user.currentListId, value)

    fun getItems(): OrderedRealmCollection<BuyListItem> {
        return BuyListItem.getAllOrdered(realm, user.currentListId, user.itemsOrder, user.itemsSort, user.itemsCheck)
    }

    fun onItemSelected(itemId: Long){



    }
}