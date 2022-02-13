package app.simple.buyer.entities

import app.simple.buyer.util.database.PrimaryKeyFactory
import app.simple.buyer.util.update
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import io.realm.annotations.RealmField
import java.util.Locale


/**
 * Created by Zakharovi on 10.01.2018.
 */

//Вещь, которую можно купить
@RealmClass(name = BuyItem.KEY_TABLE_NAME)
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
    @RealmField(name=KEY_ID)
    var id: Long = 0

    /** Название */
    @RealmField(name=KEY_NAME)
    var name: String = ""

    /** Сколько раз добавлялась, или популярность */
    @RealmField(name=KEY_POPULARITY)
    var popularity: Long = 0L

    /** Ссылка на верхнюю категорию (если есть) */
    @RealmField(name=KEY_PARENT_ITEM)
    var parentItem: BuyItem? = null

    /** Цена, запоминается (к сожалению храним во флоте)*/
    @RealmField(name=KEY_PRICE)
    var price: Float = 0f

    /** Название без заглавных и букв ё */
    @RealmField(name=KEY_SEARCH_NAME)
    var searchName: String = ""
        private set

    /** Количество слов в названии (количество пробелов + 1) */
    @RealmField(name=KEY_WORD_COUNT)
    var wordCount: Int = 0
        private set

    /** строка с данными для сортировки по ней (сортируется сначала по популярности, затем по количеству слов, затем по алфавиту */
    @RealmField(name=KEY_ORDER_COMBINE_STRING)
    var orderCombineString: String? = null
        private set

    private fun getOrderString() = String.format("%05d", popularity) + wordCount.toString() + searchName

    /** все подкатегории */
    @RealmField(name=KEY_SUB_ITEMS)
    var subItems: RealmList<BuyItem> = RealmList()

    companion object {
        fun new(realm: Realm, name: String, parentItem: BuyItem? = null): BuyItem = realm.createObject(
            BuyItem::class.java, PrimaryKeyFactory.nextKey<BuyItem>()
        ).also {
            it.name = name
            it.parentItem = parentItem
            it.searchName = smoothName(name)
            it.wordCount = name.count { c -> c.isWhitespace() } + 1
            it.orderCombineString = it.getOrderString()
            it.update(realm)
        }

        //сглаживание имени (заменяем буквы ё и уменьшаем регистр для поиска)
        fun smoothName(name: String): String {
            //TODO Прикрутить поддержку английского и возможно других языков.
            return name.replace('ё', 'е')
                .replace('Ё', 'Е')
                .lowercase(Locale("ru", "RU"))
        }

        private fun getQuery(realm: Realm): RealmQuery<BuyItem> {
            return realm.where(BuyItem::class.java)
        }

        fun getByName(realm: Realm, name: String): BuyItem? {
            return getQuery(realm).equalTo(KEY_SEARCH_NAME, smoothName(name)).findFirst()
        }

        fun getListAsync(realm: Realm, name: String): RealmResults<BuyItem> {
            val split = name.split(' ')
            var query = getQuery(realm)

            for (s in split) {
                query = query.contains(KEY_SEARCH_NAME, smoothName(s))
            }

            return query
                .sort(KEY_ORDER_COMBINE_STRING)
                .findAllAsync()
        }

        const val KEY_TABLE_NAME = "BuyItem"
        const val KEY_ID = "id"
        const val KEY_NAME = "name"
        const val KEY_POPULARITY = "popularity"
        const val KEY_PARENT_ITEM = "parentItem"
        const val KEY_PRICE = "price"
        const val KEY_SEARCH_NAME = "searchName"
        const val KEY_WORD_COUNT = "wordCount"
        const val KEY_ORDER_COMBINE_STRING = "orderCombineString"
        const val KEY_SUB_ITEMS = "subItems"
    }
}
