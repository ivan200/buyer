package app.simple.buyer.entities

import app.simple.buyer.util.database.PrimaryKeyFactory
import io.realm.*
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * Created by Zakharovi on 10.01.2018.
 */

//Вещь, которую можно купить
open class BuyItem() : RealmObject() {

    constructor(name: String, parentItem: BuyItem? = null) : this() {
        this.id = PrimaryKeyFactory.nextKey<BuyItem>()
        this.name = name
        this.parentItem = parentItem

        searchName = smoothName(name)
        wordCount = name.count { c -> c.isWhitespace() } + 1
        orderCombineString = getOrderString()
    }

    /** Уникальный id */
    @PrimaryKey
    var id: Long = 0

    /** Название */
    var name: String = ""

    /** Сколько раз добавлялась, или популярность */
    var populatity : Long = 0L

    /** Ссылка на верхнюю категорию (если есть) */
    var parentItem: BuyItem? = null

    /** Цена, запоминается */
    var price: String = ""

    /** Название без заглавных и букв ё */
    var searchName: String = ""
        private set

    /** Количество слов в названии (количество пробелов + 1) */
    var wordCount: Int = 0
        private set

    /** строка с данными для сортировки по ней (сортируется сначала по популярности, затем по количеству слов, затем по алфавиту */
    var orderCombineString: String? = null
        private set

    fun getOrderString() =  String.format("%05d" , populatity) + wordCount.toString() + searchName

    /** все подкатегории */
    var subItems: RealmList<BuyItem> = RealmList()

    companion object {

        //сглаживание имени (заменяем буквы ё и уменьшаем регистр для поиска)
        fun smoothName(name: String): String {
            return name.replace('ё', 'е')
                    .replace('Ё', 'Е')
                    .toLowerCase(Locale("ru", "RU"))
        }

        private fun getQuery(realm: Realm) : RealmQuery<BuyItem> {
            return realm.where(BuyItem::class.java)
        }

        fun getByName(realm: Realm, name: String) : BuyItem? {
            return getQuery(realm).equalTo("searchName", smoothName(name)).findFirst()
        }

        fun getListAsync(realm: Realm, name: String) : RealmResults<BuyItem> {
            val split = name.split(' ')
            var query = getQuery(realm)

            for (s in split) {
                query = query.contains("searchName", smoothName(s))
            }

            return query
                    .sort("orderCombineString")
                    .findAllAsync()
        }
    }
}
