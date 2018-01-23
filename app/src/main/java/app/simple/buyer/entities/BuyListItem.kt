package app.simple.buyer.entities

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.Required

/**
 * Created by Zakharovi on 10.01.2018.
 */

//Элемент списка покупок, которые мы составляем
open class BuyListItem : RealmObject() {
    //Уникальный id
    @PrimaryKey
    @Required
    private var id: Long? = null

    //id вещи, на которую ссылается этот элемент списка
    private var itemId: Long? = null

    //id списка, в котором этот элемент
    private var listId: Long? = null

    //Количество (если пользователь вводил)
    private var count: Long = 1

    //Комментарий к покупке
    private var comment: String? = null

    //Куплена уже, или нет
    private var isBuyed: Boolean = false

    //Позиция при ручной сортировке
    private var sortPosition: Long = 0
}

