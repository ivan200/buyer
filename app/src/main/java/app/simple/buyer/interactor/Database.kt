package app.simple.buyer.interactor

import android.content.Context
import android.os.AsyncTask
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.count
import app.simple.buyer.util.update
import io.realm.Realm
import java.io.BufferedReader
import java.io.InputStreamReader


object Database {
    fun firstInit(context: Context, realm: Realm){
        if(realm.count<BuyItem>() > 0){
            return
        }

        AsyncTask.execute {
            Realm.getDefaultInstance().apply {
                initItems(context, this)
                initLists(context, this)
            }
        }
    }

    fun deleteAll(context: Context){
        Realm.getDefaultInstance().executeTransactionAsync {
            it.deleteAll()
        }
    }

    fun initItems(context: Context, realm: Realm) {
        val items = arrayListOf<BuyItem>()
        val inputStream = context.resources.openRawResource(R.raw.food)
        val inputReader = InputStreamReader(inputStream)
        val bufReader = BufferedReader(inputReader)
        var line = bufReader.readLine()

        var parent: BuyItem? = null
        while (!line.isNullOrBlank()) {
            val newItem = BuyItem(line.trim())
            if (!line.startsWith('\t')) {
                parent = newItem
            } else {
                parent?.let {
                    newItem.parentItem = it
                    it.subItems.add(newItem)
                }
            }

            items.add(newItem)
            line = bufReader.readLine()
        }
        bufReader.close()
        inputReader.close()

        realm.executeTransactionAsync {
            items.update(it)
        }
    }

//
//    //Добавление элемента покупок по имени в определённый список
//    fun checkAddItem(realm: Realm, currentList: BuyList, itemName: String, context: Context){
//        //Проверяем, есть ли такой элемент в словаре
//        var item = BuyItem.getByName(realm, itemName)
//        if(item == null){
//            //Ищем категорию других товаров, если нет то создаём
//            val other = context.getString(R.string.cat_other)
//            val otherCat = BuyItem.getByName(realm, other) ?: BuyItem.new(realm, other)
//
//            item = BuyItem(itemName, otherCat)
//            otherCat.subItems.add(item)
//
//            realm.executeTransaction {
//                otherCat.update(it)
//                item.update(it)
//            }
//        }
//        //Если в списке в текущем списке нет элемента с таким же названием, добавляем
//        if(!currentList.items.contains {it.buyItem?.id == item.id}){
//            val buyListItem = BuyListItem(item, currentList)
//            currentList.items.add(buyListItem)
//
//            realm.executeTransaction {
//                buyListItem.update(it)
//                currentList.update(it)
//            }
//        }
//    }

    fun initLists(context: Context, realm: Realm){
        val firstListTitle = context.getString(R.string.default_first_list_name)
        ListsInteractor.createList(realm, firstListTitle)
    }


//    fun createBuyListItem(buyItemId: Long, buyListId: Long): BuyListItem {
//        return BuyListItem().apply {
//            id = PrimaryKeyFactory.nextKey<BuyListItem>()
//            itemId = buyItemId
//            listId = buyListId
//            modified = Date()
//            created = Date()
//        }
//
//
//    }
}