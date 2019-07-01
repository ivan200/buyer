package app.simple.buyer.entities

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Элемент списка покупок, которые мы составляем
open class BuyListItem : RealmObject() {
    //Уникальный id
    @PrimaryKey
    @Required
    var id: Long? = null

    //id вещи, на которую ссылается этот элемент списка
    var itemId: Long? = null

    //id списка, в котором этот элемент
    var listId: Long? = null

    //Количество (если пользователь вводил)
    var count: Long = 1

    //Комментарий к покупке
    var comment: String? = null

    //Куплена уже, или нет
    var isBuyed: Boolean = false

    //Позиция при ручной сортировке
    var sortPosition: Long = 0

    //Позиция при ручной сортировке
    var handSortPosition: Long = 0

    //Дата модификации элемента
    var modified: Date? = null

    //Дата создания элемента
    var created: Date? = null

    companion object {
        private fun getQuery() : RealmQuery<BuyListItem> {
            return  Realm.getDefaultInstance().where(BuyListItem::class.java)
        }

        fun getAll(): RealmResults<BuyListItem> {
            return getQuery().findAll()
        }

        fun count(): Long {
            return getQuery().count()
        }

        fun countInList(listId: Long): Long {
            return getQuery().equalTo("listId", listId).count()
        }

        fun price(listId: Long): Float {
            var sumPrice = 0f
            getQuery()
                    .equalTo("listId", listId)
                    .findAll().forEach { x -> x.itemId?.let { sumPrice += BuyItem.getByID(it)?.price!! } }
            return sumPrice
        }

        fun copyOrUpdateItem(item: BuyListItem) {
            var realm = Realm.getDefaultInstance()
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }
    }
}

