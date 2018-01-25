package app.simple.buyer.entities

import app.simple.buyer.util.database.DBHelper
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

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


    companion object {
        fun getAll(): RealmResults<BuyListItem> {
            return DBHelper.realm.where(BuyListItem::class.java).findAll()
        }

        fun count(): Long {
            return DBHelper.realm.where(BuyListItem::class.java).count()
        }

        fun copyOrUpdateItem(item: BuyListItem) {
            var realm = DBHelper.realm
            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()
        }
    }
}

