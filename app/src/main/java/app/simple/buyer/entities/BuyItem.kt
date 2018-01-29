package app.simple.buyer.entities

import app.simple.buyer.util.database.DBHelper
import app.simple.buyer.util.database.DBHelper.realm
import io.realm.OrderedRealmCollection
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required



/**
 * Created by Zakharovi on 10.01.2018.
 */

//Вещь, которую можно купить
open class BuyItem : RealmObject() {
    //Уникальный id
    @PrimaryKey
    @Required
    var id: Long? = null

    //id суперкатегории, куда она относится
    var superID: Long? = null

    //Название
    var name: String? = null

    //Сколько раз добавлялась, или популярность
    var populatity = 0L

    //Цена, запоминается
    var price = 0.0f

    companion object {
        private fun getQuery() : RealmQuery<BuyItem> {
            return DBHelper.realm.where(BuyItem::class.java)
        }

        fun getAll(): OrderedRealmCollection<BuyItem> {
            return getQuery().findAll()
        }

        fun getByID(id: Long) : BuyItem? {
            return getQuery().equalTo("id", id).findFirst()
        }

        fun getByName(name: String) : BuyItem? {
            return getQuery().equalTo("name", name).findFirst()
        }

        fun count(): Long {
            return getQuery().count()
        }

        fun addItem(name: String){
            realm.executeTransactionAsync {
                var item = getByName(name)
                if (item == null) {
                    item = BuyItem()
                    item.id = BuyItem.count() + 1
                    item.name = name
                } else {
                    item.populatity += 1
                }
            }
//            realm.beginTransaction()
//            realm.copyToRealmOrUpdate(item)
//            realm.commitTransaction()

        }
    }
}
