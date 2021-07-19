package app.simple.buyer.entities

import app.simple.buyer.util.database.PrimaryKeyFactory
import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey
import java.util.*

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Элемент списка покупок, которые мы составляем
open class BuyListItem() : RealmObject() {
    constructor(buyItem: BuyItem, buyList: BuyList) : this() {
        this.id = PrimaryKeyFactory.nextKey<BuyListItem>()
        this.buyItem = buyItem
        this.buyList = buyList
        this.listId = buyList.id
    }

    /** Уникальный id */
    @PrimaryKey
    var id: Long = 0

    /** Вещь, на которую ссылается этот элемент списка */
    var buyItem: BuyItem? = null

    /** id списка, в котором этот элемент */
    var buyList: BuyList? = null

    /** id списка, в котором этот элемент */
    var listId: Long = 0

    /** Дата создания элемента */
    var created: Date = Date()

    /** Дата модификации элемента */
    var modified: Date = Date()

    /** Количество (если пользователь вводил) */
    var count: Long = 1

    /** Комментарий к покупке */
    var comment: String? = null

    /** Куплена уже, или нет */
    var isBuyed: Boolean = false

    /** Позиция при ручной сортировке */
    var handSortPosition: Long = 0


    companion object {
        private fun getQuery(realm: Realm): RealmQuery<BuyListItem> {
            return realm.where(BuyListItem::class.java)
        }

        fun countInList(realm: Realm, listId: Long): Long {
            return getQuery(realm).equalTo("listId", listId).count()
        }
    }
}

