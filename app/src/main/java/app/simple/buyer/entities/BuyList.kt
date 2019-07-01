package app.simple.buyer.entities

import android.content.Context
import app.simple.buyer.util.database.Prefs
import io.realm.*
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

    //тип персональной сортировки данного списка (обычно сортировка внутри списка распространяется на все списки)
    var personalOrderType: Int = OrderType.CREATED

    companion object {
        private fun getQuery() : RealmQuery<BuyList> {
            return Realm.getDefaultInstance().where(BuyList::class.java)
        }

        fun getAll() : RealmResults<BuyList> {
            return getQuery().findAll()
        }
        fun getAllOrdered() : OrderedRealmCollection<BuyList> {
            return getQuery().findAll().sort("sortPosition")
        }
        fun getAllOrderedByHand() : OrderedRealmCollection<BuyList> {
            return getQuery().findAll().sort("handSortPosition")
        }

        fun clearHandOrder(){
            Realm.getDefaultInstance().executeTransactionAsync {
                getAllOrdered().forEach { buyList -> buyList.handSortPosition = buyList.sortPosition }
            }
        }

        fun orderBy(context: Context, orderType: Int, sortOrder: Sort) {
            Prefs(context).listsSortAscending = if(sortOrder == Sort.ASCENDING) Sort.ASCENDING.value else Sort.DESCENDING.value
            Prefs(context).listsOrderType = orderType
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
                    Realm.getDefaultInstance().executeTransactionAsync {
                        val sort = getAllOrdered().sortedBy { l -> BuyListItem.countInList(l.id!!) }
                        val indices = if (sortOrder == Sort.ASCENDING) sort.indices else sort.indices.reversed()
                        for ((k, i) in indices.withIndex()) {
                            sort[i]?.sortPosition = k.toLong()
                        }
                    }
                }
            }
        }

        //Сортировка по одному из полей
        private fun orderByField(fieldName: String, sortOrder: Sort) {
            Realm.getDefaultInstance().executeTransactionAsync {
                val sort = getAllOrdered().sort(fieldName, sortOrder)
                for (i in sort.indices) {
                    sort[i]?.sortPosition = i.toLong()
                }
            }
        }

        fun getByName(name: String) : BuyList? {
            return getQuery().equalTo("name", name).findFirst()
        }
        fun count(): Long {
            return getQuery().count()
        }
        fun addAsync(name: String) {
            var newList = BuyList()
            newList.id = BuyList.count()+1
            newList.name = name
            newList.created = Date()
            newList.modified = Date()
            newList.sortPosition = BuyList.count()+1
            Realm.getDefaultInstance().executeTransactionAsync {
                Realm.getDefaultInstance().copyToRealm(newList)
            }
        }
    }
}

