package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.Realm

/**
 * @author ivan200
 * @since 24.07.2021
 */
object ItemInteractor {

    private fun getByName(realm: Realm, buyItemName: String): BuyItem {
        val trimmedName = buyItemName.trim()
        var buyItem = BuyItem.getByName(realm, trimmedName)
        if (buyItem == null) {
            buyItem = BuyItem.new(realm, trimmedName)
        }
        return buyItem
    }

    private fun getCurrentList(realm: Realm, defaultListTitle: String): BuyList {
        val user = UserInteractor.getUserSync(realm)
        var list = realm.getById<BuyList>(user.currentListId)
        if (list == null) {
            list = BuyList.new(realm, defaultListTitle)

            user.currentListId = list.id
            user.currentList = list
            user.update(realm)
        }
        return list
    }

    /**
     * Добавить элемент списка покупок, по имеющемуся предмету
     */
    fun addNewItemAsync(realm: Realm, buyItemId: Long, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyItem>(buyItemId)
            if(buyItem != null){
                val list = getCurrentList(it, defaultListTitle)
                val mkNew = ListItemInteractor.createOrIncrease(it, list, buyItem)
                if(mkNew){
                    buyItem.popularity = buyItem.popularity + 1
                    buyItem.update(it)
                }
            }
        }
    }

    /**
     * Удалить элемент списка покупок, по имеющемуся предмету
     */
    fun deleteItemAsync(realm: Realm, buyItemId: Long, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyItem>(buyItemId)!!
            val list = getCurrentList(it, defaultListTitle)
            ListItemInteractor.deleteOrDecrease(it, list, buyItem)
        }
    }

    /**
     * Добавить элемент списка покупок, имея только названия предмета
     */
    fun addNewItemAsync(realm: Realm, buyItemName: String, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = getByName(it, buyItemName)
            val list = getCurrentList(it, defaultListTitle)
            val mkNew = ListItemInteractor.createOrIncrease(it, list, buyItem)
            if(mkNew){
                buyItem.popularity = buyItem.popularity + 1
                buyItem.update(it)
            }
        }
    }
}