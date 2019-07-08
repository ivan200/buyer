package app.simple.buyer.entities

import io.realm.Realm
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * Created by Zakharovi on 10.01.2018.
 */

//Вещь, которую можно купить
open class BuyItem : RealmObject() {
    //Уникальный id
    @PrimaryKey
    var id: Long = 0

    //Название
    var name: String? = null

    //Название без заглавных и букв ё
    var searchName: String? = null

    //Количество слов в названии (количество пробелов + 1)
    var wordCount: Int = 0

    //Сколько раз добавлялась, или популярность
    var populatity = 0L

    //Ссылка на верхнюю категорию (если есть)
    var parentId = 0L

    //Уровень категории
    var level = 0.0f

    //Цена, запоминается
    var price = 0.0f

    //строка с данными для сортировки по ней (сортируется сначала по популярности, затем по количеству слов, затем по алфавиту
    var orderCombineString: String? = null

    fun getOrderString() =  String.format("%05d" , populatity) + wordCount.toString() + searchName

    companion object {

        //сглаживание имени (земеняем буквы ё, и ументшаем регистр, для поиска)
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

            if(split.count() > 1){
                for (s in split) {
                    query = query.contains("searchName", smoothName(s))
                }
            } else {
                query = query.contains("searchName", smoothName(name))
            }

            return query
                    .sort("orderCombineString")
                    .findAllAsync()
        }
    }
}
