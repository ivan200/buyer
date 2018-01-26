package app.simple.buyer.util.database

import io.realm.Realm
import io.realm.RealmConfiguration

/**
 * Created by Zakharovi on 10.01.2018.
 */

object DBHelper {
    private var realmConfiguration: RealmConfiguration? = null

    val realm: Realm
        get() = Realm.getInstance(getRealmConfiguration())

    private fun getRealmConfiguration(): RealmConfiguration {
        if (realmConfiguration == null) {
            realmConfiguration = RealmConfiguration.Builder()
                    .schemaVersion(2)
                    .migration(DBMigration())
                    .build()
        }
        return realmConfiguration!!
    }

}
