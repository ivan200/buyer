package app.simple.buyer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import app.simple.buyer.util.database.DBMigration
import app.simple.buyer.util.database.PrimaryKeyFactory
import io.realm.Realm
import io.realm.RealmConfiguration
import org.acra.ACRA
import org.acra.annotation.AcraCore



/**
 * Created by Zakharovi on 10.01.2018.
 */

@AcraCore(buildConfigClass = BuildConfig::class)
class BuyerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ACRA.init(this)
        initRealm(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
    }

    private fun initRealm(context: Context) {
        Realm.init(context)
        val realmConfiguration = RealmConfiguration.Builder()
                .schemaVersion(DBMigration.schemaVersion)
                .deleteRealmIfMigrationNeeded() //TODO THIS IS ONLY FOR DEVELOPMENT
                .migration(DBMigration())
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        PrimaryKeyFactory.initialize(Realm.getInstance(realmConfiguration))
    }
}