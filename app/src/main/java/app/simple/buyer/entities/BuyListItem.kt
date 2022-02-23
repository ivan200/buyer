package app.simple.buyer.entities

import app.simple.buyer.entities.enums.CheckedPosition
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.util.database.PrimaryKeyFactory
import app.simple.buyer.util.update
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.Sort
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import java.util.Date


/**
 * Created by Zakharovi on 10.01.2018.
 */

//Элемент списка покупок, которые мы составляем
@RealmClass(name = BuyListItem.KEY_TABLE_NAME)
open class BuyListItem : RealmObject() {
    /** Уникальный id */
    @PrimaryKey
    @RealmField(name = KEY_ID)
    var id: Long = 0

    /** ID вещи, на которую ссылается этот элемент списка */
    @RealmField(name = KEY_BUY_ITEM_ID)
    var buyItemId: Long = 0

    /** Вещь, на которую ссылается этот элемент списка */
    @RealmField(name = KEY_BUY_ITEM)
    var buyItem: BuyItem? = null

    /** id списка, в котором этот элемент */
    @RealmField(name = KEY_LIST_ID)
    var listId: Long = 0

    /** Дата создания элемента */
    @RealmField(name = KEY_CREATED)
    var created: Date = Date()

    /** Дата модификации элемента */
    @RealmField(name = KEY_MODIFIED)
    var modified: Date = Date()

    /** Дата покупки элемента */
    @RealmField(name = KEY_BUYED)
    var buyed: Date = Date()

    /** Количество (если пользователь вводил) */
    @RealmField(name = KEY_COUNT)
    var count: Long = MIN_COUNT

    /** Сумарная цена (цена вещи, умноженая на количество) */
    @RealmField(name = KEY_SUM_PRICE)
    var sumPrice: Float = 0f

    /** Комментарий к покупке */
    @RealmField(name = KEY_COMMENT)
    var comment: String? = null

    /** Куплена уже, или нет */
    @RealmField(name = KEY_IS_BUYED)
    var isBuyed: Boolean = false

    /** Выделена для удаления или ещё чего нибудь */
    @RealmField(name = KEY_IS_SELECTED)
    var isSelected: Boolean = false

    /** Куплена уже, или нет */
    @RealmField(name = KEY_IS_ARCHIVED)
    var isArchived: Boolean = false

    /** Позиция при ручной сортировке */
    @RealmField(name = KEY_HAND_SORT_POSITION)
    var handSortPosition: Long = 0

    /** Позиция скролла комментария (если он большой и не влез на экран) */
    @RealmField(name = KEY_SCROLL_STATE)
    var scrollState: ByteArray = ByteArray(0)

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
                .equalTo(KEY_LIST_ID, listId)
                .and()
                .equalTo(KEY_BUY_ITEM_ID, buyItemId)
                .findFirst()
        }

        fun getAllByList(realm: Realm, listId: Long): RealmResults<BuyListItem> {
            return getQuery(realm)
                .equalTo(KEY_LIST_ID, listId)
                .findAll()
        }

        fun getAllSelectedByList(realm: Realm, listId: Long): RealmResults<BuyListItem> {
            return getQuery(realm)
                .equalTo(KEY_LIST_ID, listId)
                .equalTo(KEY_IS_SELECTED, true)
                .findAll()
        }

        fun getAllByListAsync(realm: Realm, listId: Long): RealmResults<BuyListItem> {
            return getQuery(realm)
                .equalTo(KEY_LIST_ID, listId)
                .findAllAsync()
        }

        fun getSelectedCount(realm: Realm, listId: Long): Long {
            return getQuery(realm)
                .equalTo(KEY_LIST_ID, listId)
                .and()
                .equalTo(KEY_IS_SELECTED, true)
                .count()
        }

        fun getSelectedItemId(realm: Realm, listId: Long): Long? {
            return getQuery(realm)
                .equalTo(KEY_LIST_ID, listId)
                .and()
                .equalTo(KEY_IS_SELECTED, true)
                .findFirst()
                ?.id
        }

        fun getAllOrdered(
            realm: Realm,
            listId: Long,
            orderType: OrderType,
            sortType: SortType,
            checkedPosition: CheckedPosition,
            showCheckedItems: Boolean
        ): RealmResults<BuyListItem> {
            val sortOrder = when (sortType) {
                SortType.ASCENDING -> Sort.ASCENDING
                SortType.DESCENDING -> Sort.DESCENDING
            }
            val fieldName = when (orderType) {
                OrderType.ALPHABET -> KEY_BUY_ITEM + "." + BuyItem.KEY_SEARCH_NAME
                OrderType.POPULARITY -> KEY_BUY_ITEM + "." + BuyItem.KEY_POPULARITY
                OrderType.CREATED -> KEY_CREATED
                OrderType.MODIFIED -> KEY_MODIFIED
                OrderType.PRICE -> KEY_SUM_PRICE
                OrderType.HAND -> KEY_HAND_SORT_POSITION
                OrderType.SIZE -> KEY_COUNT
            }

            var query = getQuery(realm).equalTo(KEY_LIST_ID, listId)

            query = if (showCheckedItems)
                when (checkedPosition) {
                    CheckedPosition.BOTTOM -> query.sort(
                        arrayOf(KEY_IS_BUYED, fieldName),
                        arrayOf(Sort.ASCENDING, sortOrder)
                    )
                    CheckedPosition.TOP -> query.sort(
                        arrayOf(KEY_IS_BUYED, fieldName),
                        arrayOf(Sort.DESCENDING, sortOrder)
                    )
                    CheckedPosition.BETWEEN -> query
                        .sort(fieldName, sortOrder)
                }
            else {
                query
                    .equalTo(KEY_IS_BUYED, false)
                    .sort(fieldName, sortOrder)
            }
            return query.findAllAsync()
        }

        const val KEY_TABLE_NAME = "BuyListItem"
        const val KEY_ID = "id"
        const val KEY_BUY_ITEM_ID = "buyItemId"
        const val KEY_BUY_ITEM = "buyItem"
        const val KEY_LIST_ID = "listId"
        const val KEY_CREATED = "created"
        const val KEY_MODIFIED = "modified"
        const val KEY_BUYED = "buyed"
        const val KEY_COUNT = "count"
        const val KEY_SUM_PRICE = "sumPrice"
        const val KEY_COMMENT = "comment"
        const val KEY_IS_BUYED = "isBuyed"
        const val KEY_IS_ARCHIVED = "isArchived"
        const val KEY_IS_SELECTED = "isSelected"
        const val KEY_HAND_SORT_POSITION = "handSortPosition"
        const val KEY_SCROLL_STATE = "scrollState"

        const val MIN_COUNT = 1L
    }
}

