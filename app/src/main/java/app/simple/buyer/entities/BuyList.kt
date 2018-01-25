package app.simple.buyer.entities

import app.simple.buyer.util.database.DBHelper.realm
import io.realm.OrderedRealmCollection
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.Sort
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
        fun getAllOrdered() : RealmResults<BuyList> {
            return realm.where(BuyList::class.java).findAll().sort("sortPosition")
        }

        fun orderByAlphabet(currentList: OrderedRealmCollection<BuyList> = getAll(), sortOrder: Sort) {
            orderByField("name", currentList, sortOrder)
        }

        fun orderByPopularity(currentList: OrderedRealmCollection<BuyList> = getAll(), sortOrder: Sort) {
            orderByField("populatity", currentList, sortOrder)
        }

        fun orderBySize(currentList: OrderedRealmCollection<BuyList> = getAll(), sortOrder: Sort) {
            realm.executeTransactionAsync {
                val sort = currentList.sortedBy { t -> BuyListItem.countInList(t.id!!) }
                var k = 0
                var indices = if (sortOrder == Sort.ASCENDING) sort.indices else sort.indices.reversed()
                for (i in indices) {
                    sort[i]?.sortPosition = i.toLong()
                    k++
                }
            }
        }

        private fun orderByField(fieldName: String, currentList: OrderedRealmCollection<BuyList> = getAll(), sortOrder: Sort) {
            realm.executeTransactionAsync {
                val sort = currentList.sort(fieldName, sortOrder)
                var k = 0
                for (i in sort.indices) {
                    sort[i]?.sortPosition = i.toLong()
                    k++
                }
            }
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
            newList.sortPosition = BuyList.count()+1
            realm.executeTransactionAsync {
                realm.copyToRealm(newList)
            }
        }
    }
}

