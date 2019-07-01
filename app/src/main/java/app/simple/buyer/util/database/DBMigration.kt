package app.simple.buyer.util.database

import io.realm.DynamicRealm
import io.realm.RealmMigration

/**
 * Created by Zakharovi on 23.01.2018.
 */

class DBMigration : RealmMigration {
    companion object {
        const val schemaVersion: Long = 3
    }

    override fun migrate(realm: DynamicRealm, oldVersion: Long, newVersion: Long) {
        var oldVersion = oldVersion

        val schema = realm.schema

        for (version in oldVersion..newVersion) when (version) {
            0L -> {
//                schema.get("BuyItem")?.removeField("count")
//                schema.get("BuyList")?.addField("isHidden", Boolean::class.java)
            }
        }
    }
}