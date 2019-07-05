package app.simple.buyer.util.database

import android.content.Context
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.count
import app.simple.buyer.util.update
import io.realm.Realm
import java.io.BufferedReader
import java.io.InputStreamReader


class Database {
    var realm: Realm = Realm.getDefaultInstance()

    fun close(){
        realm.close()
    }

    fun addItem(itemName: String){
        realm.executeTransactionAsync {
            val item = BuyItem.getByName(this, itemName)
            if (item != null) {
                item.apply {
                    populatity += 1
                    update(it)
                }
            } else {
                BuyItem().apply {
                    id = PrimaryKeyFactory.nextKey<BuyItem>()
                    name = itemName
                    nameWords = itemName.count { c -> c.isWhitespace() } + 1
                    update(it)
                }
            }
        }
    }

    fun firstInit(context: Context){
        if(realm.count<BuyItem>() > 0){
            return
        }
        val items = arrayListOf<BuyItem>()
        val inputStream = context.resources.openRawResource(R.raw.food)
        val inputReader = InputStreamReader(inputStream)
        val bufReader = BufferedReader(inputReader)
        var line = bufReader.readLine()

        var catId = 0L
        while (line != null) {
            val item = BuyItem().apply {
                id = PrimaryKeyFactory.nextKey<BuyItem>()
                if(!line.startsWith('\t')){
                    catId = id
                    parentId = 0L
                } else{
                    parentId = catId
                }
                val foodName = line.trim()
                name = foodName
                searchName = foodName.replace('ё', 'е')
                        .replace('Ё', 'Е')
                        .toLowerCase()
                nameWords = foodName.count { c -> c.isWhitespace() } + 1

                searchCombineString = getSearchString()
            }
            items.add(item)
            line = bufReader.readLine()
        }
        bufReader.close()
        inputReader.close()

        realm.executeTransactionAsync {
            items.update(it)
        }
    }
}