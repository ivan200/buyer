package app.simple.buyer.fragments.additem

import android.app.Application
import androidx.lifecycle.Observer
import app.simple.buyer.BaseViewModel
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ItemInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmLiveData
import app.simple.buyer.util.RealmObjectFieldLiveData
import app.simple.buyer.util.asLiveData
import io.realm.OrderedRealmCollection
import io.realm.RealmResults

class AddItemViewModel(application: Application) : BaseViewModel(application) {

    private val user: User = UserInteractor.getUser(realm)

    var buyListItems: RealmLiveData<BuyListItem> = BuyListItem.getAllByList(realm, user.currentListId).asLiveData()
    private val onCurrentListChanged = Observer<User?> { buyListItems.onChange(BuyListItem.getAllByList(realm, it.currentListId)) }
    private val currentListId = RealmObjectFieldLiveData(user, User::currentListId.name).apply { observeForever(onCurrentListChanged) }

    fun getItems(): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, "")
    }

    fun getItems(text: String): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, text.trim())
    }

    fun getCurrentListItems(): OrderedRealmCollection<BuyListItem> {
        val items = BuyListItem.getAllOrdered(realm, user.currentListId, user.itemsOrder, user.itemsSort, user.itemsCheck)
//        (items as RealmResults).addChangeListener({ changedUser: RealmResults<BuyListItem>, changeSet: OrderedCollectionChangeSet ->
//
//        })
        return items
    }

    fun onItemClicked(itemId: Long) {
        ItemInteractor.addNewItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }

    fun onItemDeleted(itemId: Long) {
//        ListsInteractor.deleteListAsync(realm, itemId)
    }


    override fun onCleared() {
        currentListId.removeObserver(onCurrentListChanged)
        super.onCleared()
    }
}