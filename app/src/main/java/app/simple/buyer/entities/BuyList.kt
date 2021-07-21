package app.simple.buyer.entities

import android.content.Context
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.database.PrimaryKeyFactory
import app.simple.buyer.util.update
import io.realm.*
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Список покупок, который мы составляем
open class BuyList() : RealmObject() {
    constructor(name: String) : this() {
        this.id = PrimaryKeyFactory.nextKey<BuyList>()
        this.name = name
    }

    /** Уникальный id */
    @PrimaryKey
    var id: Long = 0

    /** Название списка */
    var name: String = ""

    /** Дата создания списка */
    var created: Date = Date()

    /** Дата модификации списка */
    var modified: Date = Date()

    /** Позиция при нестандартной сортировке */
    var sortPosition: Long = 0

    /** Позиция при ручной сортировке */
    var handSortPosition: Long = 0

    /** Сколько раз просматривался, или популярность */
    var populatity: Long = 0

    /** Скрыт список, или нет (перед удалением) */
    var isHidden: Boolean = false

    /** тип персональной сортировки данного списка (обычно сортировка внутри списка распространяется на все списки) */
    var personalOrderType: Int = OrderType.CREATED.value

    /** элементы списка покупок */
    var items: RealmList<BuyListItem> = RealmList()

    /** Текущее состояние скролла каждого из списков */
    var scrollState: ByteArray = ByteArray(0)

    companion object {
        private fun getQuery(realm: Realm) : RealmQuery<BuyList> {
            return realm.where(BuyList::class.java)
        }

        fun getAll(realm: Realm) : RealmResults<BuyList> {
            return getQuery(realm).findAll()
        }

        fun getAllOrdered(realm: Realm): OrderedRealmCollection<BuyList> {
            return getQuery(realm).findAll().sort(BuyList::sortPosition.name)
        }

        fun getAllOrderedByHand(realm: Realm): OrderedRealmCollection<BuyList> {
            return getQuery(realm).findAll().sort(BuyList::handSortPosition.name)
        }

        fun clearHandOrder(realm: Realm) {
            realm.executeTransactionAsync {
                getAllOrdered(realm).forEach { buyList -> buyList.handSortPosition = buyList.sortPosition }
            }
        }

        fun orderBy(realm: Realm, orderType: OrderType, sortAscending: Boolean) {
//            Prefs(context).listsSortAscending = if(sortOrder == Sort.ASCENDING) Sort.ASCENDING.value else Sort.DESCENDING.value
//            Prefs(context).listsOrderType = orderType
            val sortOrder = if(sortAscending) Sort.ASCENDING else Sort.DESCENDING

            when (orderType) {
                OrderType.ALPHABET -> orderByField(realm, BuyList::name.name, sortOrder)
                OrderType.POPULARITY -> orderByField(realm, BuyList::populatity.name, sortOrder)
                OrderType.CREATED -> orderByField(realm,  BuyList::created.name, sortOrder)
                OrderType.MODIFIED -> orderByField(realm, BuyList::modified.name, sortOrder)
                OrderType.SIZE -> {
                    realm.executeTransactionAsync {
                        val sort = getAllOrdered(realm).sortedBy { l -> BuyListItem.countInList(it, l.id) }
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
                val sort = getAllOrdered(realm).sort(fieldName, sortOrder)
                for (i in sort.indices) {
                    sort[i]?.sortPosition = i.toLong()
                }
            }
        }

        fun getByName(realm: Realm, name: String) : BuyList? {
            return getQuery(realm).equalTo( BuyList::name.name, name).findFirst()
        }

        fun addAsync(realm: Realm, name: String) {
            val newList = BuyList(name)
            realm.executeTransactionAsync {
                newList.update(it)
            }
        }
    }
}

