package app.simple.buyer.entities

import app.simple.buyer.util.database.PrimaryKeyFactory
import app.simple.buyer.util.update
import io.realm.*
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * Created by Zakharovi on 10.01.2018.
 */

//Элемент списка покупок, которые мы составляем
open class BuyListItem : RealmObject() {
    /** Уникальный id */
    @PrimaryKey
    var id: Long = 0

    /** ID вещи, на которую ссылается этот элемент списка */
    var buyItemId: Long = 0

    /** Вещь, на которую ссылается этот элемент списка */
    var buyItem: BuyItem? = null

    /** id списка, в котором этот элемент */
    var listId: Long = 0

    /** Дата создания элемента */
    var created: Date = Date()

    /** Дата модификации элемента */
    var modified: Date = Date()

    /** Количество (если пользователь вводил) */
    var count: Long = 1

    /** Сумарная цена (цена вещи, умноженая на количество) */
    var sumPrice: Float = 0f

    /** Комментарий к покупке */
    var comment: String? = null

    /** Куплена уже, или нет */
    var isBuyed: Boolean = false

    /** Позиция при ручной сортировке */
    var handSortPosition: Long = 0


    companion object {
        fun new(realm: Realm, buyItem: BuyItem, buyList: BuyList): BuyListItem = realm.createObject(
            BuyListItem::class.java, PrimaryKeyFactory.nextKey<BuyListItem>()
        ).also {
            it.buyItem = buyItem
            it.buyItemId = buyItem.id
            it.listId = buyList.id
            it.update(realm)
        }

        private fun getQuery(realm: Realm): RealmQuery<BuyListItem> {
            return realm.where(BuyListItem::class.java)
        }

        fun getByListAndItem(realm: Realm, listId: Long, buyItemId: Long): BuyListItem? {
            return getQuery(realm)
                .equalTo(BuyListItem::listId.name, listId)
                .and()
                .equalTo(BuyListItem::buyItemId.name, buyItemId)
                .findFirst()
        }

        fun getAllByList(realm: Realm, listId: Long): RealmResults<BuyListItem> {
            return getQuery(realm)
                .equalTo(BuyListItem::listId.name, listId)
                .findAll()
        }
        fun getAllByListAsync(realm: Realm, listId: Long): RealmResults<BuyListItem> {
            return getQuery(realm)
                .equalTo(BuyListItem::listId.name, listId)
                .findAll()
        }

        fun getAllOrdered(
            realm: Realm,
            listId: Long,
            orderType: OrderType,
            sortType: SortType,
            checkedPosition: CheckedPosition
        ): RealmResults<BuyListItem> {
            val sortOrder = when (sortType) {
                SortType.ASCENDING -> Sort.ASCENDING
                SortType.DESCENDING -> Sort.DESCENDING
            }
            val fieldName = when (orderType) {
                OrderType.ALPHABET -> BuyListItem::buyItem.name + "." + BuyItem::searchName.name
                OrderType.POPULARITY -> BuyListItem::buyItem.name + "." + BuyItem::populatity.name
                OrderType.CREATED -> BuyListItem::created.name
                OrderType.MODIFIED -> BuyListItem::modified.name
                OrderType.PRICE -> BuyListItem::sumPrice.name
                OrderType.HAND -> BuyListItem::handSortPosition.name
                OrderType.SIZE -> BuyListItem::count.name
            }

            val query = getQuery(realm).equalTo(BuyListItem::listId.name, listId)

            return when (checkedPosition) {
                CheckedPosition.INVISIBLE -> query
                    .equalTo(BuyListItem::isBuyed.name, false)
                    .findAll()
                    .sort(fieldName, sortOrder)
                CheckedPosition.BOTTOM -> query
                    .findAll()
                    .sort(
                        arrayOf(BuyListItem::isBuyed.name, fieldName),
                        arrayOf(Sort.ASCENDING, sortOrder)
                    )
                CheckedPosition.TOP -> query
                    .findAll()
                    .sort(
                        arrayOf(BuyListItem::isBuyed.name, fieldName),
                        arrayOf(Sort.DESCENDING, sortOrder)
                    )
                CheckedPosition.BETWEEN -> query
                    .findAll()
                    .sort(fieldName, sortOrder)
            }
        }
    }
}

