package app.simple.buyer.entities

import io.realm.RealmObject
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
    private var id: Long? = null

    //Название списка
    private var name: String? = null

    //Позиция при ручной сортировке
    private var sortPosition: Long = 0

    //Дата создания списка
    private var created: Date? = null

    //Дата модификации списка
    private var modified: Date? = null

    //Сколько раз просматривался, или популярность
    private var populatity: Long = 0
}

