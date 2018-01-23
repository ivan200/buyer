package app.simple.buyer.entities

import io.realm.RealmObject
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
}
