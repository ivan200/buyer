package app.simple.buyer.fragments.additem

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.OrderType
import app.simple.buyer.interactor.UserInteractor
import io.realm.RealmResults

class AddItemViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    fun onOrderSelected(order: OrderType) {
        user.listsOrderType

//        BuyList.orderBy(realm, requireContext(), OrderType.ALPHABET, sortType)
    }

    fun getItems(): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, "")
    }

    fun getItems(text: String): RealmResults<BuyItem> {
        return BuyItem.getListAsync(realm, text.trim())
    }
}