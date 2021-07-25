package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
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

    private fun createOrIncrease(realm: Realm, list: BuyList, buyItem: BuyItem) {
        var item = BuyListItem.getByListAndItem(realm, list.id, buyItem.id)
        if (item == null) {
            item = BuyListItem.new(realm, buyItem, list)
//            list.items.add(item)
        } else {
            if (item.isBuyed) {
                item.isBuyed = false
            } else {
                item.count = item.count + 1
            }
        }
        item.update(realm)
    }

    private fun deleteOrDecrease(realm: Realm, list: BuyList, buyItem: BuyItem) {
        val item = BuyListItem.getByListAndItem(realm, list.id, buyItem.id)
        if (item != null) {
            if (item.count > 1) {
                item.count = item.count - 1
                item.update(realm)
            } else {
                item.deleteFromRealm()
            }
        }
    }

    /**
     * Добавить элемент списка покупок, по имеющемуся предмету
     */
    fun addNewItemAsync(realm: Realm, buyItemId: Long, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyItem>(buyItemId)!!
            val list = getCurrentList(it, defaultListTitle)
            createOrIncrease(it, list, buyItem)
        }
    }

    /**
     * Добавить элемент списка покупок, по имеющемуся предмету
     */
    fun deleteItemAsync(realm: Realm, buyItemId: Long, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyItem>(buyItemId)!!
            val list = getCurrentList(it, defaultListTitle)
            deleteOrDecrease(it, list, buyItem)
        }
    }

    /**
     * Добавить элемент списка покупок, по имеющемуся предмету
     */
    fun toggleItemAsync(realm: Realm, listItemId: Long) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyListItem>(listItemId)!!
            buyItem.isBuyed = !buyItem.isBuyed
            buyItem.update(it)
        }
    }

    /**
     * Добавить элемент списка покупок, имея только названия предмета
     */
    fun addNewItemAsync(realm: Realm, buyItemName: String, defaultListTitle: String) {
        realm.executeTransactionAsync {
            val buyItem = getByName(it, buyItemName)
            val list = getCurrentList(it, defaultListTitle)
            createOrIncrease(it, list, buyItem)
        }
    }
}