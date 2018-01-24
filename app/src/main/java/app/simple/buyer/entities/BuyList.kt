package app.simple.buyer.entities

import app.simple.buyer.util.database.DBHelper
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required
import java.util.*

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Список покупок, который мы составляем
open class BuyList : RealmObject() {
    //Уникальный id
    @PrimaryKey
    @Required
    var id: Long? = null

    //Название списка
    var name: String? = null

    //Позиция при ручной сортировке
    var sortPosition: Long = 0

    //Дата создания списка
    var created: Date? = null

    //Дата модификации списка
    var modified: Date? = null

    //Сколько раз просматривался, или популярность
    var populatity: Long = 0

    companion object {
        fun getAll() : RealmResults<BuyList>? {
            return DBHelper.getRealm().where(BuyList::class.java).findAll()
        }
        fun getListByName(name: String) : BuyList? {
            return DBHelper.getRealm().where(BuyList::class.java).equalTo("name", name).findFirst()
        }
        fun count(): Long {
            return DBHelper.getRealm().where(BuyList::class.java).count()
        }
        fun addList(list: BuyList) {
            var realm = DBHelper.getRealm()
            realm.beginTransaction()
            realm.copyToRealm(list)
            realm.commitTransaction()
        }
    }
}

