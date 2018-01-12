package app.simple.buyer.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Zakharovi on 10.01.2018.
 */

open class BuyItem : RealmObject() {
    //Уникальный id
    @PrimaryKey
    @Required
    private var id: Long? = null

    //id суперкатегории, куда она относится
    private var superID: Long? = null

    //Название
    private var name: String? = null

    //Сколько раз добавлялась, или популярность
    private var populatity = 0L

    //Цена (Последняя, что была введена)
    private var price = 0.0f

    //Количество (Последняее, что было введено)
    private var count = 1L
}
