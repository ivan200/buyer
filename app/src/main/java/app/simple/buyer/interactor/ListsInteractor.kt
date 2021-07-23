package app.simple.buyer.interactor

import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.SortType
import app.simple.buyer.util.update
import io.realm.Realm
import io.realm.Sort

/**
 * @author ivan200
 * @since 23.07.2021
 */
object ListsInteractor {
    fun createList(realm: Realm, title: String): BuyList {
        val newBuyList = BuyList(title)
        realm.executeTransactionAsync {
            newBuyList.update(it)
        }
        return newBuyList
    }

    fun reorderBy(realm: Realm, orderType: OrderType, sortType: SortType) {
        val sortOrder = when(sortType){
            SortType.ASCENDING -> Sort.ASCENDING
            SortType.DESCENDING -> Sort.DESCENDING
        }

        when (orderType) {
            OrderType.ALPHABET -> orderByField(realm, BuyList::name.name, sortOrder)
            OrderType.POPULARITY -> orderByField(realm, BuyList::populatity.name, sortOrder)
            OrderType.CREATED -> orderByField(realm,  BuyList::created.name, sortOrder)
            OrderType.MODIFIED -> orderByField(realm, BuyList::modified.name, sortOrder)
            OrderType.PRICE -> {}
            OrderType.HAND -> {}
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