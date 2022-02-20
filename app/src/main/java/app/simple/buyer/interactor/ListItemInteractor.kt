package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.Realm

/**
 *
 *
 * @author ivan200
 * @since 16.02.2022
 */
object ListItemInteractor {

    /**
     * Добавить покупаемую вещь [buyItem] в список покупок [list], или увеличить покупаемое количество в бд [realm]
     */
    fun createOrIncrease(realm: Realm, list: BuyList, buyItem: BuyItem) {
        var item = BuyListItem.getByListAndItem(realm, list.id, buyItem.id)
        if (item == null) {
            item = BuyListItem.new(realm, buyItem, list)
        } else {
            if (item.isBuyed) {
                item.isBuyed = false
            } else {
                item.count = item.count + 1
            }
        }
        item.update(realm)
    }

    /**
     * Удалить покупаемую вещь [buyItem] из списка покупок [list], или уменьшить покупаемое количество в бд [realm]
     */
    fun deleteOrDecrease(realm: Realm, list: BuyList, buyItem: BuyItem) {
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
     * Удалить элемент списка покупок [buyListItemId] асинхнонно в бд [realm]
     */
    fun deleteAsync(realm: Realm, buyListItemId: Long) {
        realm.executeTransactionAsync {
            val item = it.getById<BuyListItem>(buyListItemId)
            item?.deleteFromRealm()
        }
    }

    /**
     * Развыделить все предметы из списка [listId] (перед первым лонгкликом на ячейке) в бд [realm]
     */
    fun deSelectAll(realm: Realm, listId: Long) {
        val listItems = BuyListItem.getAllByList(realm, listId)
        listItems.forEach {
            it.isSelected = false
        }
        listItems.update()
    }

    /**
     * Развыделить все предметы из списка [listId] (перед первым лонгкликом на ячейке) в бд [realm]
     */
    fun deSelectAllAsync(realm: Realm, listId: Long) {
        realm.executeTransactionAsync {
            deSelectAll(it, listId)
        }
    }


    /**
     * Завершить покупку предмета (поставить чекбокс) на элемент [listItemId] в бд [realm]
     */
    fun toggleCheckItemAsync(realm: Realm, listItemId: Long) {
        realm.executeTransactionAsync {
            val buyItem = it.getById<BuyListItem>(listItemId)!!
            buyItem.isBuyed = !buyItem.isBuyed
            buyItem.update(it)
        }
    }

    /**
     * Выделить первый элемент списка покупок (включение экшенмода) [listItemId] в бд [realm]
     *
     * @param realm
     * @param listId
     * @param buyListItemId
     */
    fun actionFirstItemAsync(realm: Realm, listId: Long, buyListItemId: Long) {
        realm.executeTransactionAsync {
            deSelectAll(it, listId)
            val item = it.getById<BuyListItem>(buyListItemId)
            if (item != null && !item.isSelected) {
                item.isSelected = true
                item.update(it)
            }
        }
    }

    /**
     * Выделить элемент списка покупок (в экшенмоде) [listItemId] в бд [realm]
     */
    fun toggleActionAsync(realm: Realm, listItemId: Long) {
        realm.executeTransactionAsync {
            val item = it.getById<BuyListItem>(listItemId)
            if (item != null) {
                item.isSelected = !item.isSelected
                item.update(it)
            }
        }
    }
}