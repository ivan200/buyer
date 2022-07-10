package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import app.simple.buyer.util.updateRealmObjectFieldAsync
import io.realm.Realm
import java.util.Date

/**
 *
 *
 * @author ivan200
 * @since 16.02.2022
 */
object ListItemInteractor {


    private fun Realm.updateListItemField(buyListItemId: Long, block: BuyListItem.() -> Unit) =
        updateRealmObjectFieldAsync({ it.getById(buyListItemId) }, null, block)

    private fun Realm.updateListItemField(buyListItemId: Long, condition: BuyListItem.() -> Boolean, block: BuyListItem.() -> Unit) =
        updateRealmObjectFieldAsync({ it.getById(buyListItemId) }, condition, block)


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
     * Удалить выделенные элементы списка покупок [listId] асинхнонно в бд [realm]
     */
    fun deleteSelectedAsync(realm: Realm, listId: Long) {
        realm.executeTransactionAsync {
            val listItems = BuyListItem.getAllSelectedByList(it, listId)
            listItems.deleteAllFromRealm()
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
        if(isBuyed){
            buyed = Date()
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
    fun toggleActionAsync(realm: Realm, listItemId: Long, onSuccess: Realm.Transaction.OnSuccess){
        realm.executeTransactionAsync ({
            it.getById<BuyListItem>(listItemId)?.apply {
                isSelected = !isSelected
                update(it)
            }
        }, onSuccess)
    }

    /**
     * Выделить несколько элементов списка покупок (в экшенмоде) [listItemId] в бд [realm]
     */
    fun selectRangeAsync(realm: Realm, listItemId: Long, onSuccess: Realm.Transaction.OnSuccess) {
        realm.executeTransactionAsync({
            val user = UserInteractor.getUser(it)
            val allOrdered = BuyListItem.getAllOrdered(it, user)
            val first = allOrdered.indexOfFirst { listItem -> listItem.isSelected || listItem.id == listItemId }
            val last = allOrdered.indexOfLast { listItem -> listItem.isSelected || listItem.id == listItemId }
            for (i in first..last) {
                allOrdered[i]?.apply {
                    if (!isSelected) {
                        isSelected = true
                        update(it)
                    }
                }
            }
        }, onSuccess)
    }

    /**
     * Прочекать несколько элементов списка покупок [listItemId] в бд [realm]
     */
    fun doneSelectedAsync(realm: Realm, listItemId: Long) {
        realm.executeTransactionAsync {
            val selected = BuyListItem.getSelectedItems(it, listItemId)
            val hasUndone = selected.any { item -> !item.isBuyed }
            if(hasUndone){
                selected.forEach { item ->
                    if(!item.isBuyed) {
                        item.isBuyed = true
                        item.update(it)
                    }
                }
            } else {
                selected.forEach { item ->
                    item.isBuyed = false
                    item.update(it)
                }
            }
        }
    }

    fun setItemInfo(realm: Realm, listItemId: Long, itemCount: Long, itemComment: String) = realm.updateListItemField(
        listItemId,
        { count != itemCount || comment != itemComment },
        {
            count = itemCount
            comment = itemComment
            modified = Date()
        }
    )
}