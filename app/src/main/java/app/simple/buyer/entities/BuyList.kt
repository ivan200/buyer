package app.simple.buyer.entities

import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.util.database.PrimaryKeyFactory
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.OrderedRealmCollection
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import java.util.Date

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Список покупок, который мы составляем
@RealmClass(name = BuyList.KEY_TABLE_NAME)
open class BuyList : RealmObject() {
    /** Уникальный id */
    @PrimaryKey
    @RealmField(name = KEY_ID)
    var id: Long = 0

    /** Название списка */
    @RealmField(name = KEY_NAME)
    var name: String = ""

    /** Дата создания списка */
    @RealmField(name = KEY_CREATED)
    var created: Date = Date()

    /** Дата модификации списка */
    @RealmField(name = KEY_MODIFIED)
    var modified: Date = Date()

//    /** элементы списка покупок */
//    var items: RealmList<BuyListItem> = RealmList()

    /** Количеество элементов, для сортировки */
    @RealmField(name = KEY_ITEMS_COUNT)
    var itemsCount: Long = 0

    /** Суммарная стоимость элементов, для сортировки */
    @RealmField(name = KEY_ITEMS_SUM_PRICE)
    var itemsSumPrice: Long = 0

    /** Позиция при ручной сортировке */
    @RealmField(name = KEY_HAND_SORT_POSITION)
    var handSortPosition: Long = 0

    /** Сколько раз просматривался, или популярность */
    @RealmField(name = KEY_POPULARITY)
    var populatity: Long = 0

    /** Текущее состояние скролла каждого из списков */
    @RealmField(name = KEY_SCROLL_STATE)
    var scrollState: ByteArray = ByteArray(0)

//    TODO Прикрутить персональную сортировку списков
//
//    /** Включена или нет персональная сортировка для данного списка */
//    var isPersonalSortEnabled: Boolean = false
//
//    /** тип персональной сортировки внутри данного списка (обычно сортировка внутри списка распространяется на все списки) */
//    var personalOrderType: Int = OrderType.CREATED.value
//
//    /** тип персональной сортировки внутри данного списка (обычно сортировка внутри списка распространяется на все списки) */
//    var personalSortType: Boolean = SortType.ASCENDING.value

    companion object {
        fun new(realm: Realm, name: String): BuyList = realm.createObject(
            BuyList::class.java, PrimaryKeyFactory.nextKey<BuyList>()
        ).also {
            it.name = name
            it.update(realm)
        }

        private fun getQuery(realm: Realm): RealmQuery<BuyList> {
            return realm.where(BuyList::class.java)
        }

        fun getSortType(sortType: SortType) = when (sortType) {
            SortType.ASCENDING -> Sort.ASCENDING
            SortType.DESCENDING -> Sort.DESCENDING
        }

        fun getFieldName(orderType: OrderType) = when (orderType) {
            OrderType.ALPHABET -> KEY_NAME
            OrderType.POPULARITY -> KEY_POPULARITY
            OrderType.CREATED -> KEY_CREATED
            OrderType.MODIFIED -> KEY_MODIFIED
            OrderType.PRICE -> KEY_ITEMS_SUM_PRICE
            OrderType.HAND -> KEY_HAND_SORT_POSITION
            OrderType.SIZE -> KEY_ITEMS_COUNT
        }


        fun getAllOrdered(realm: Realm, orderType: OrderType, sortType: SortType): OrderedRealmCollection<BuyList> {
            return getQuery(realm).findAll().sort(getFieldName(orderType), getSortType(sortType))
        }

        fun getAllOrderedAsync(realm: Realm, orderType: OrderType, sortType: SortType): OrderedRealmCollection<BuyList> {
            return getQuery(realm).findAllAsync().sort(getFieldName(orderType), getSortType(sortType))
        }


        fun clearHandOrder(realm: Realm) {
//            realm.executeTransactionAsync {
//                getAllOrdered(realm).forEach { buyList -> buyList.handSortPosition = buyList.sortPosition }
//            }
        }

        fun getByName(realm: Realm, name: String): BuyList? {
            return getQuery(realm).equalTo(KEY_NAME, name).findFirst()
        }

        fun getScrollState(realm: Realm, currentListId: Long): ByteArray {
            return realm.getById<BuyList>(currentListId)?.scrollState ?: ByteArray(0)
        }

        const val KEY_TABLE_NAME = "BuyList"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_CREATED = "created"
        const val KEY_MODIFIED = "modified"
        const val KEY_ITEMS_COUNT = "itemsCount"
        const val KEY_ITEMS_SUM_PRICE = "itemsSumPrice"
        const val KEY_HAND_SORT_POSITION = "handSortPosition"
        const val KEY_POPULARITY = "popularity"
        const val KEY_SCROLL_STATE = "scrollState"
    }
}

