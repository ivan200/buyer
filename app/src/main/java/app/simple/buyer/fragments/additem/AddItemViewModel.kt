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
import io.realm.RealmResults

class AddItemViewModel(application: Application) : BaseViewModel(application) {

    private val user: User = UserInteractor.getUser(realm)

    val listItems get() = BuyListItem.getAllByList(realm, user.currentListId)
    var buyListItems: RealmLiveData<BuyListItem> = listItems.asLiveData()

    private val onCurrentListChanged = Observer<User?> {
        buyListItems.onChange(listItems)
    }
    private val currentListId = RealmObjectFieldLiveData(user, User::currentListId.name).apply { observeForever(onCurrentListChanged) }

    fun getItems(): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, "")
    }

    fun getItems(text: String): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, text.trim())
    }


    fun onNewItem(buyItemName: String) {
        ItemInteractor.addNewItemAsync(realm, buyItemName, context.getString(R.string.default_first_list_name))
    }

    fun onItemClicked(itemId: Long) {
        ItemInteractor.addNewItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }

    fun onItemDeleted(itemId: Long) {
        ItemInteractor.deleteItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }


    override fun onCleared() {
        currentListId.removeObserver(onCurrentListChanged)
        super.onCleared()
    }
}