package app.simple.buyer.util.database

import android.content.Context
import android.os.AsyncTask
import app.simple.buyer.Constants
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.count
import app.simple.buyer.util.update
import io.realm.Realm
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


object Database {
    fun addItem(itemName: String, realm: Realm){
        realm.executeTransactionAsync {
            val item = BuyItem.getByName(it, itemName)
            if (item != null) {
                item.apply {
                    populatity += 1
                    update(it)
                }
            } else {
                BuyItem().apply {
                    id = PrimaryKeyFactory.nextKey<BuyItem>()
                    name = itemName
                    wordCount = itemName.count { c -> c.isWhitespace() } + 1
                    update(it)
                }
            }
        }
    }

    fun firstInit(context: Context, realm: Realm){
        if(realm.count<BuyItem>() > 0){
            return
        }

        AsyncTask.execute {
            initItems(context)
        }
    }

    fun deleteAll(context: Context){
        Realm.getDefaultInstance().executeTransactionAsync {
            it.deleteAll()
        }
    }

    fun initItems(context: Context){
        val realm = Realm.getDefaultInstance()

        val items = arrayListOf<BuyItem>()
        val inputStream = context.resources.openRawResource(app.simple.buyer.R.raw.food)
        val inputReader = InputStreamReader(inputStream)
        val bufReader = BufferedReader(inputReader)
        var line = bufReader.readLine()

        var catId = 0L
        while (!line.isNullOrBlank()) {

            val newItem = createBuyItem(line.trim())
            if(!line.startsWith('\t')){
                catId = newItem.id
            } else {
                newItem.parentId = catId
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

    fun checkAddItem(realm: Realm, currentListId: Long, itemName: String){
        val item = BuyItem.getByName(realm, itemName)
        if(item != null){
            checkAddItem(realm, currentListId, item.id)
        } else{
            val newBuyItem = createBuyItem(itemName)
            newBuyItem.parentId = Constants.CUSTOM_CAT_ID
            realm.executeTransaction {
                newBuyItem.update(it)
            }
            checkAddItem(realm, currentListId, newBuyItem.id)
        }
    }

    fun checkAddItem(realm: Realm, currentListId: Long, buyItemId: Long){
        val createBuyItem = createBuyItem(itemName)

    }



    fun initLists(){
        createBuyList("Продукты")




    }

    fun createBuyItem(foodName: String): BuyItem {
        return BuyItem().apply {
            id = PrimaryKeyFactory.nextKey<BuyItem>()
            name = foodName
            searchName = BuyItem.smoothName(foodName)
            wordCount = foodName.count { c -> c.isWhitespace() } + 1
            orderCombineString = getOrderString()
        }
    }

    fun createBuyList(listName: String): BuyList {
        return BuyList().apply {
            id = PrimaryKeyFactory.nextKey<BuyList>()
            name = listName
            modified = Date()
            created = Date()
        }
    }

    fun createBuyListItem(buyItemId: Long, buyListId: Long): BuyListItem {
        return BuyListItem().apply {
            id = PrimaryKeyFactory.nextKey<BuyListItem>()
            itemId = buyItemId
            listId = buyListId
            modified = Date()
            created = Date()
        }


    }
}