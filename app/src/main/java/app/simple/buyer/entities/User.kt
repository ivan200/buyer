package app.simple.buyer.entities

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey

/**
 * User
 */
open class User : RealmObject() {

    /** Уникальный Id юзера. Так как он пока один, то всегда 0 */
    @PrimaryKey
    var id: Long = 0

    /** Текущий выбранный список */
    var currentListId: Long = 0L

    /** Сортировка списков в левой панели */
    var listsOrderType: Int = OrderType.CREATED.value

    /** Сортировка списков в левой панели по возрастанию/убыванию */
    var listsSortAscending: Boolean = SortType.ASCENDING.value

    /** Тёмная тема */
    var darkTheme: Boolean = true

    /** Текущее состояние левой панели для восстановления скролла */
    var mainMenuScrollState: ByteArray = ByteArray(0)

    /** Тип стандартной сортировки покупок  */
    var listItemsOrderType: Int = OrderType.CREATED.value

    /** Направление стандартной сортировки покупок (по возрастанию/ по убыванию) */
    var listItemsSortAscending: Boolean = SortType.DESCENDING.value

    /** Позиция прочеканых элементов относительно остальных */
    var listItemsCheckedPosition: Int = CheckedPosition.BOTTOM.value

    val order get() = OrderType.getByValue(listsOrderType)
    val sort get() = SortType.getByValue(listsSortAscending)

    val itemsOrder get() = OrderType.getByValue(listItemsOrderType)
    val itemsSort get() = SortType.getByValue(listItemsSortAscending)
    val itemsCheck get() = CheckedPosition.getByValue(listItemsCheckedPosition)

    companion object {
        private fun getQuery(realm: Realm): RealmQuery<User> {
            return realm.where(User::class.java)
        }

        fun get(realm: Realm): User? {
            return getQuery(realm).findFirst()
        }
    }
}

