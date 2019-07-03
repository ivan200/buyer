package app.simple.buyer.util.database

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.logger
import io.realm.Realm
import io.realm.RealmObject
import java.util.*
import java.util.concurrent.atomic.AtomicLong
import java.util.logging.Level

/**
 * Created by Zakharovi on 06.03.2018.
 */

//Определение первичных ключей для моделей таблиц, где ключи требуется генерировать.
object PrimaryKeyFactory {
    private val PRIMARY_KEY_FIELD = "id"
    private var keys: MutableMap<Class<out RealmObject>, AtomicLong> = HashMap()

    fun initialize(realm: Realm) {
        val model = ArrayList<Class<out RealmObject>>()
        model.add(BuyItem::class.java)
        model.add(BuyList::class.java)
        model.add(BuyListItem::class.java)

        keys = HashMap()

        for (c in model) {
            var keyValue: Number? = null
            try {
                if (realm.where(c).count() > 0) {
                    keyValue = realm.where(c).max(PRIMARY_KEY_FIELD)
                }
            } catch (ex: Exception) {
                logger.log(Level.WARNING, "error while getting number primary key for " + c.simpleName, ex)
            }

            if (keyValue != null) {
                keys[c] = AtomicLong(keyValue.toLong())
            }
        }
    }

    // Automatically create next key
    @Synchronized
    fun nextKey(clazz: Class<out RealmObject>): Long {
        var l = keys[clazz]
        if (l == null) {
//            Log.d("KEYS", "There was no primary keys for " + clazz.simpleName)
            //RealmConfiguration#getRealmObjectClasses() returns only classes with existing instances
            //so we need to store value for the first instance created
            l = AtomicLong(0)
            keys[clazz] = l
        }
        return l.incrementAndGet()
    }

    inline fun <reified T: RealmObject> nextKey(): Long {
        return nextKey(T::class.java)
    }
}
