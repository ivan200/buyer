package app.simple.buyer.fragments.additem

import android.app.Application
import app.simple.buyer.R
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.BuyItemInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import app.simple.buyer.util.RealmResultsLiveData
import io.realm.RealmResults

class AddItemViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val userLiveData = RealmObjectFieldLiveData(user, User.KEY_CURRENT_LIST)

    val userCurrentItem = RealmObjectFieldLiveData(user, User.KEY_CURRENT_ITEM_ID)

    val ltemsLiveData: RealmResultsLiveData<BuyListItem> = RealmResultsLiveData(null)

    init {
        userLiveData.observeForever {
            ltemsLiveData.update(BuyListItem.getAllByListAsync(realm, user.currentListId))
        }
        ltemsLiveData.update(BuyListItem.getAllByListAsync(realm, user.currentListId))
    }

    fun getItems(): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, "")
    }

    fun getItems(text: String): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, text.trim())
    }


    fun onNewItem(buyItemName: String) {
        BuyItemInteractor.addNewItemAsync(realm, buyItemName, context.getString(R.string.default_first_list_name))
    }

    fun onItemClicked(itemId: Long) {
        BuyItemInteractor.addNewItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }

    fun onItemDeleted(itemId: Long) {
        BuyItemInteractor.deleteItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }


    fun onItemPreview(itemId: Long) {
        BuyItemInteractor.openItemAsync(realm, itemId, context.getString(R.string.default_first_list_name))
    }
}