package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.count
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.Realm

/**
 * @author ivan200
 * @since 23.07.2021
 */
object ListsInteractor {

    /**
     * Создать список покупок с заголовком
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
    fun deleteListAsync(realm: Realm, listId: Long) {
        realm.executeTransactionAsync {
            val user = UserInteractor.getUser(it)
            val selectedListId = user.currentListId
            if (listId == selectedListId) {
                val menuList = BuyList.getAllOrdered(it, user.order, user.sort)
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

    fun updateListScrollStateAsync(realm: Realm, listId: Long, scrollState: ByteArray) {
        realm.executeTransactionAsync {
            val list = it.getById<BuyList>(listId)
            list?.scrollState = scrollState
            list?.update(it)
        }
    }

}