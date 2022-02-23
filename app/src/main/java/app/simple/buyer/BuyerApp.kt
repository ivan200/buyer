package app.simple.buyer

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
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
class BuyerApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        ACRA.init(this)
        initRealm(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

//        ColorUtils.changeOverScrollGlowColor(resources, R.color.ColorBackgroundGrey800)
//        val consoleHandler = ConsoleHandler()
//        consoleHandler.level = Level.FINEST
//        Logger.getLogger("").apply {
//            level = Level.FINEST
//            addHandler(consoleHandler)
//        }




//        LogManager.getLogManager().reset()
//        val rootLogger = LogManager.getLogManager().getLogger("")
//        val consoleHandler = ConsoleHandler()
//        consoleHandler.level = Level.FINER
//        rootLogger.addHandler(consoleHandler)
    }

    private fun initRealm(context: Context) {
        Realm.init(context)
        val realmConfiguration = RealmConfiguration.Builder()
                .schemaVersion(DBMigration.schemaVersion)
//                .deleteRealmIfMigrationNeeded() //THIS IS ONLY FOR DEVELOPMENT
                .migration(DBMigration())
                .build()
        Realm.setDefaultConfiguration(realmConfiguration)
        PrimaryKeyFactory.initialize(Realm.getInstance(realmConfiguration))
    }
}

