package app.simple.buyer.util.database

import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.User
import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by Zakharovi on 23.01.2018.
 */

class DBMigration : RealmMigration {
    companion object {
        const val schemaVersion: Long = 6
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        val schema = realm.schema
        for (version in oldVersion..newVersion) when (version) {
            0L -> {
//                schema.get("BuyItem")?.removeField("count")
//                schema.get("BuyList")?.addField("isHidden", Boolean::class.java)
            }
            4L -> {
                schema.get(BuyItem.KEY_TABLE_NAME)?.renameField("populatity", BuyItem.KEY_POPULARITY)
                schema.get(BuyList.KEY_TABLE_NAME)?.renameField("populatity", BuyList.KEY_POPULARITY)
            }
            5L -> {
                schema.get(User.KEY_TABLE_NAME)?.addField(User.KEY_SHOW_CHECKED_ITEMS, Boolean::class.java)
            }
        }
    }
}