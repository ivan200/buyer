package app.simple.buyer.entities

import app.simple.buyer.util.database.PrimaryKeyFactory
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

    /** элементы списка покупок */
    var items: RealmList<BuyListItem> = RealmList()

    /** Количеество элементов, для сортировки */
    var itemsCount: Long = 0

    /** Суммарная стоимость элементов, для сортировки */
    var itemsSumPrice: Long = 0

    /** Позиция при ручной сортировке */
    var handSortPosition: Long = 0

    /** Сколько раз просматривался, или популярность */
    var populatity: Long = 0

    /** Скрыт список, или нет (перед удалением) */
    var isHidden: Boolean = false

    /** тип персональной сортировки данного списка (обычно сортировка внутри списка распространяется на все списки) */
    var personalOrderType: Int = OrderType.CREATED.value

    /** Текущее состояние скролла каждого из списков */
    var scrollState: ByteArray = ByteArray(0)

    companion object {
        private fun getQuery(realm: Realm) : RealmQuery<BuyList> {
            return realm.where(BuyList::class.java)
        }

        fun getAllOrdered(realm: Realm, orderType: OrderType, sortType: SortType): OrderedRealmCollection<BuyList> {
            val sortOrder = when (sortType) {
                SortType.ASCENDING -> Sort.ASCENDING
                SortType.DESCENDING -> Sort.DESCENDING
            }
            val fieldName = when (orderType) {
                OrderType.ALPHABET -> BuyList::name.name
                OrderType.POPULARITY -> BuyList::populatity.name
                OrderType.CREATED -> BuyList::created.name
                OrderType.MODIFIED -> BuyList::modified.name
                OrderType.PRICE -> BuyList::itemsSumPrice.name
                OrderType.HAND -> BuyList::handSortPosition.name
                OrderType.SIZE -> BuyList::itemsCount.name
            }
            return getQuery(realm).findAll().sort(fieldName, sortOrder)
        }

        fun clearHandOrder(realm: Realm) {
//            realm.executeTransactionAsync {
//                getAllOrdered(realm).forEach { buyList -> buyList.handSortPosition = buyList.sortPosition }
//            }
        }

        fun getByName(realm: Realm, name: String) : BuyList? {
            return getQuery(realm).equalTo(BuyList::name.name, name).findFirst()
        }

    }
}

