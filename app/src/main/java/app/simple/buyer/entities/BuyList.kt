package app.simple.buyer.entities

import app.simple.buyer.util.database.DBHelper.realm
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
        fun getAll() : RealmResults<BuyList> {
            return realm.where(BuyList::class.java).findAll()
        }
        fun getByName(name: String) : BuyList? {
            return realm.where(BuyList::class.java).equalTo("name", name).findFirst()
        }
        fun count(): Long {
            return realm.where(BuyList::class.java).count()
        }
        fun addAsync(name: String) {
            var newList = BuyList()
            newList.id = BuyList.count()+1
            newList.name = name
            newList.created = Date()
            newList.modified = Date()
            realm.executeTransactionAsync {
                realm.copyToRealm(newList)
            }
        }
    }
}

