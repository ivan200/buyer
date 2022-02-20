package app.simple.buyer.entities

import app.simple.buyer.entities.enums.CheckedPosition
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.util.update
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField

/**
 * User
 */
@RealmClass(name = User.KEY_TABLE_NAME)
open class User : RealmObject() {
    /** Уникальный Id юзера. Так как он пока один, то всегда 0 */
    @PrimaryKey
    @RealmField(name = KEY_ID)
    var id: Long = 0

    /** Текущий выбранный список */
    @RealmField(name = KEY_CURRENT_LIST_ID)
    var currentListId: Long = 0L

    /** Текущий выбранный список */
    @RealmField(name = KEY_CURRENT_LIST)
    var currentList: BuyList? = null

    /** Сортировка списков в левой панели */
    @RealmField(name = KEY_LISTS_ORDER_TYPE)
    var listsOrderType: Int = OrderType.CREATED.value

    /** Сортировка списков в левой панели по возрастанию/убыванию */
    @RealmField(name = KEY_LISTS_SORT_ASCENDING)
    var listsSortAscending: Boolean = SortType.ASCENDING.value

    /** Тёмная тема */
    @RealmField(name = KEY_DARK_THEME)
    var darkTheme: Boolean = DARK_THEME_DEFAULT

    /** Текущее состояние левой панели для восстановления скролла */
    @RealmField(name = KEY_MAIN_MENU_SCROLL_STATE)
    var mainMenuScrollState: ByteArray = ByteArray(0)

    /** Тип стандартной сортировки покупок  */
    @RealmField(name = KEY_LIST_ITEMS_ORDER_TYPE)
    var listItemsOrderType: Int = OrderType.CREATED.value

    /** Направление стандартной сортировки покупок (по возрастанию/ по убыванию) */
    @RealmField(name = KEY_LIST_ITEMS_SORT_ASCENDING)
    var listItemsSortAscending: Boolean = SortType.DESCENDING.value

    /** Позиция прочеканых элементов относительно остальных */
    @RealmField(name = KEY_LIST_ITEMS_CHECKED_POSITION)
    var listItemsCheckedPosition: Int = CheckedPosition.BOTTOM.value

    /** Показывать или нет прочеканые элементы */
    @RealmField(name = KEY_SHOW_CHECKED_ITEMS)
    var showCheckedItems: Boolean = true

    val order get() = OrderType.getByValue(listsOrderType)
    val sort get() = SortType.getByValue(listsSortAscending)

    val itemsOrder get() = OrderType.getByValue(listItemsOrderType)
    val itemsSort get() = SortType.getByValue(listItemsSortAscending)
    val itemsCheck get() = CheckedPosition.getByValue(listItemsCheckedPosition)

    companion object {
        fun new(realm: Realm): User = realm.createObject(
            User::class.java, 0
        ).also {
            it.update(realm)
        }

        private fun getQuery(realm: Realm): RealmQuery<User> {
            return realm.where(User::class.java)
        }

        fun get(realm: Realm): User? {
            return getQuery(realm).findFirst()
        }

        fun getAsync(realm: Realm): User {
            return getQuery(realm).findFirstAsync()
        }

        const val KEY_TABLE_NAME = "User"
        const val KEY_ID = "id"
        const val KEY_CURRENT_LIST_ID = "currentListId"
        const val KEY_CURRENT_LIST = "currentList"
        const val KEY_LISTS_ORDER_TYPE = "listsOrderType"
        const val KEY_LISTS_SORT_ASCENDING = "listsSortAscending"
        const val KEY_DARK_THEME = "darkTheme"
        const val KEY_MAIN_MENU_SCROLL_STATE = "mainMenuScrollState"
        const val KEY_LIST_ITEMS_ORDER_TYPE = "listItemsOrderType"
        const val KEY_LIST_ITEMS_SORT_ASCENDING = "listItemsSortAscending"
        const val KEY_LIST_ITEMS_CHECKED_POSITION = "listItemsCheckedPosition"
        const val KEY_SHOW_CHECKED_ITEMS = "showCheckedItems"

        const val DARK_THEME_DEFAULT = true //также возможно нужно смотреть на android:theme в манифесте
    }
}

