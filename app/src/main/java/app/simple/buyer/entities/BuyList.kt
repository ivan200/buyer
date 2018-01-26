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

    //Позиция при нестандартной сортировке
    var sortPosition: Long = 0

    //Позиция при ручной сортировке
    var handSortPosition: Long = 0

    //Дата создания списка
    var created: Date? = null

    //Дата модификации списка
    var modified: Date? = null

    //Сколько раз просматривался, или популярность
    var populatity: Long = 0

    //Скрыт список, или нет (перед удалением)
    var isHidden: Boolean = false

    companion object {
        enum class OrderType {
            ALPHABET,
            POPULARITY,
            SIZE,
            CREATED,
            MODIFIED
        }

        fun getAll() : RealmResults<BuyList> {
            return realm.where(BuyList::class.java).findAll()
        }
        fun getAllOrdered() : OrderedRealmCollection<BuyList> {
            return realm.where(BuyList::class.java).findAll().sort("sortPosition")
        }
        fun getAllOrderedByHand() : OrderedRealmCollection<BuyList> {
            return realm.where(BuyList::class.java).findAll().sort("handSortPosition")
        }

        fun clearHandOrder(){
            realm.executeTransactionAsync {
                getAllOrdered().forEach { buyList -> buyList.handSortPosition = buyList.sortPosition }
            }
        }

        fun orderBy(orderType: OrderType, sortOrder: Sort) {
            when (orderType) {
                OrderType.ALPHABET -> {
                    orderByField("name", sortOrder)
                }
                OrderType.POPULARITY -> {
                    orderByField("populatity", sortOrder)
                }
                OrderType.CREATED -> {
                    orderByField("created", sortOrder)
                }
                OrderType.MODIFIED -> {
                    orderByField("modified", sortOrder)
                }
                OrderType.SIZE -> {
                    realm.executeTransactionAsync {
                        val sort = getAllOrdered().sortedBy { l -> BuyListItem.countInList(l.id!!) }
                        val indices = if (sortOrder == Sort.ASCENDING) sort.indices else sort.indices.reversed()
                        for ((k, i) in indices.withIndex()) {
                            sort[i]?.sortPosition = k.toLong()
                        }
                    }
                }
            }
        }

        private fun orderByField(fieldName: String, sortOrder: Sort) {
            realm.executeTransactionAsync {
                val sort = getAllOrdered().sort(fieldName, sortOrder)
                for (i in sort.indices) {
                    sort[i]?.sortPosition = i.toLong()
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

