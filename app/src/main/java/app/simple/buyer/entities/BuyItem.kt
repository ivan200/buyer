package app.simple.buyer.entities

import app.simple.buyer.util.database.Database
import io.realm.RealmObject
import io.realm.RealmQuery
import io.realm.annotations.PrimaryKey



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
    var nameWords: Int = 0

    //Сколько раз добавлялась, или популярность
    var populatity = 0L

    //Ссылка на верхнюю категорию (если есть)
    var parentId = 0L

    //Уровень категории
    var level = 0.0f

    //Цена, запоминается
    var price = 0.0f

    var searchCombineString: String? = null

    fun getSearchString() =  String.format("%05d" , populatity) + nameWords.toString() + searchName

    companion object {
        private fun getQuery(db: Database) : RealmQuery<BuyItem> {
            return db.realm.where(BuyItem::class.java)
        }

        fun getByName(db: Database, name: String) : BuyItem? {
            return getQuery(db).equalTo("name", name).findFirst()
        }
    }
}
