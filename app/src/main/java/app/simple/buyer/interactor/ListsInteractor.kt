package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.util.count
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.Realm
import io.realm.Sort

/**
 * @author ivan200
 * @since 23.07.2021
 */
object ListsInteractor {

    /**
     * Создать список покупок с загоовком
     * Если у нас нет ни одного списка, то после создания выбираем этот список
     */
    fun createList(realm: Realm, title: String) {
        realm.executeTransactionAsync {
            val isEmpty = it.count<BuyList>() == 0L
            val newBuyList = BuyList(title)
            newBuyList.update(it)
            if(isEmpty) {
                val user = UserInteractor.getUser(it)
                user.currentListId = newBuyList.id
                user.update(it)
            }
        }
    }


    /**
     * Удаление списка продуктов
     * - проверяем какой список сейчас выбран в левом меню
     * - если список, который под удаление сейчас выбран, то перевыбираем следующий за ним
     * - удаляем все элементы списка и сам список
     */
    fun deleteList(realm: Realm, listId: Long) {
        realm.executeTransactionAsync {
            val user = UserInteractor.getUser(it)
            val selectedListId = user.currentListId
            if (listId == selectedListId) {
                val menuList = BuyList.getAllOrdered(it)
                val selectedIndex = menuList.indexOfFirst { list -> list.id == listId }
                val indexAfterDelete = when (selectedIndex) {
                    menuList.count() - 1 -> selectedIndex - 1
                    else -> selectedIndex + 1
                }
                if (menuList.count() <= 1) {
                    user.currentListId = 0
                } else {
                    user.currentListId = menuList[indexAfterDelete].id
                }
                user.update(it)
            }

            val list = it.getById<BuyList>(listId)
            list?.apply {
                items.deleteAllFromRealm()
                deleteFromRealm()
            }
        }
    }


    /**
     * Пересотрировка списков в зависимости от тииа и порядка сортировки
     */
    fun reorderBy(realm: Realm, orderType: OrderType, sortType: SortType) {
        val sortOrder = when (sortType) {
            SortType.ASCENDING -> Sort.ASCENDING
            SortType.DESCENDING -> Sort.DESCENDING
        }

        when (orderType) {
            OrderType.ALPHABET -> orderByField(realm, BuyList::name.name, sortOrder)
            OrderType.POPULARITY -> orderByField(realm, BuyList::populatity.name, sortOrder)
            OrderType.CREATED -> orderByField(realm, BuyList::created.name, sortOrder)
            OrderType.MODIFIED -> orderByField(realm, BuyList::modified.name, sortOrder)
            OrderType.PRICE -> {
            }
            OrderType.HAND -> {
            }
            OrderType.SIZE -> {
                realm.executeTransactionAsync {
                    val sort = BuyList.getAllOrdered(it).sortedBy { l -> BuyListItem.countInList(it, l.id) }
                    val indices = if (sortOrder == Sort.ASCENDING) sort.indices else sort.indices.reversed()
                    for ((k, i) in indices.withIndex()) {
                        sort[i]?.sortPosition = k.toLong()
                    }
                }
            }
        }
    }

    //Сортировка по одному из полей
    private fun orderByField(realm: Realm, fieldName: String, sortOrder: Sort) {
        realm.executeTransactionAsync {
            val sort = BuyList.getAllOrdered(it).sort(fieldName, sortOrder)
            for (i in sort.indices) {
                sort[i]?.sortPosition = i.toLong()
            }
            sort.update()
        }
    }

}