package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import app.simple.buyer.util.updateRealmObjectField
import io.realm.Realm

/**
 *
 *
 * @author ivan200
 * @since 16.02.2022
 */
object ListItemInteractor {


    private fun Realm.updateListItemField(buyListItemId: Long, block: BuyListItem.() -> Unit) =
        updateRealmObjectField({ it.getById(buyListItemId) }, null, block)

    private fun Realm.updateListItemField(buyListItemId: Long, condition: BuyListItem.() -> Boolean, block: BuyListItem.() -> Unit) =
        updateRealmObjectField({ it.getById(buyListItemId) }, condition, block)


    /**
     * Добавить покупаемую вещь [buyItem] в список покупок [list], или увеличить покупаемое количество в бд [realm]
     * @return true если создали новую, false если такой элемент уже был
     */
    fun createOrIncrease(realm: Realm, list: BuyList, buyItem: BuyItem): Boolean {
        var listItem = BuyListItem.getByListAndItem(realm, list.id, buyItem.id)
        val isNew = listItem == null
        if (listItem == null) {
            listItem = BuyListItem.new(realm, buyItem, list)
        } else {
            if (listItem.isBuyed) {
                listItem.isBuyed = false
            } else {
                listItem.count = listItem.count + 1
            }
        }
        listItem.update(realm)
        return isNew
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
        val listItems = BuyListItem.getAllSelectedByList(realm, listId)
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
    fun toggleCheckItemAsync(realm: Realm, listItemId: Long) = realm.updateListItemField(listItemId){
        isBuyed = !isBuyed
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
    fun toggleActionAsync(realm: Realm, listItemId: Long) = realm.updateListItemField(listItemId){
        isSelected = !isSelected
    }

    fun setItemInfo(realm: Realm, listItemId: Long, itemCount: Long, itemComment: String) = realm.updateListItemField(
        listItemId,
        { count != itemCount || comment != itemComment },
        {
            count = itemCount
            comment = itemComment
        }
    )
}