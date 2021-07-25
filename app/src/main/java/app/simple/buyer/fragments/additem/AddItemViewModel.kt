package app.simple.buyer.fragments.additem

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ItemInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import app.simple.buyer.util.RealmResultsLiveData
import io.realm.RealmResults

class AddItemViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val userLiveData = RealmObjectFieldLiveData(user, User::currentList.name)

    val ltemsLiveData = RealmResultsLiveData(user.currentList?.let { BuyListItem.getAllByListAsync(realm, it.id) })

    init {
        userLiveData.observeForever {
            ltemsLiveData.update(BuyListItem.getAllByListAsync(realm, user.currentList!!.id))
        }
    }

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
}