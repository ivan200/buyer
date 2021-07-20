package app.simple.buyer.entities

import app.simple.buyer.util.awaitFirst
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey

/**
 * User
 */
open class User : RealmObject() {
//    constructor(buyItem: BuyItem, buyList: BuyList) : this() {
//        this.id = PrimaryKeyFactory.nextKey<BuyListItem>()
//        this.buyItem = buyItem
//        this.buyList = buyList
//        this.listId = buyList.id
//    }

    /** Уникальный id */
    @PrimaryKey
    var id: Long = 0

    /** Id юзера. Так как он пока один, то всегда 0 */
    var currentListId: Long = 0L

    /** Сортировка списков в левой панели */
    var listsOrderType: Int = OrderType.CREATED

    /** Сортировка списков в левой панели по возрастанию/убыванию */
    var listsSortAscending: Boolean = true

    /** Тёмная тема */
    var darkTheme: Boolean = true

    /** Текущее состояние левой панели для восстановления скролла */
    var mainMenuState: ByteArray = ByteArray(0)

    companion object {
        private fun getQuery(realm: Realm): RealmQuery<User> {
            return realm.where(User::class.java)
        }

        fun get(realm: Realm): User? {
            return getQuery(realm).findFirst()
        }

        suspend fun getAwait(realm: Realm): User? {
            return getQuery(realm).awaitFirst()
        }

//        fun get(realm: Realm): User {
//            val user = getQuery(realm).findFirst()
//            if(user == null){
//                val firstUser = User()
//                realm.insert(firstUser)
//            }
//            return getQuery(realm).findFirst()!!
//        }
    }
}

