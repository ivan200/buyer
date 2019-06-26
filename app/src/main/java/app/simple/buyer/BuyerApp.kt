package app.simple.buyer

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import app.simple.buyer.util.crash.CustomSenderFactory
import app.simple.buyer.util.crash.LogModule
import io.realm.Realm
import org.acra.ACRA
import org.acra.ReportingInteractionMode
import org.acra.annotation.ReportsCrashes

/**
 * Created by Zakharovi on 10.01.2018.
 */

@ReportsCrashes(
        mailTo = Constants.CRASH_REPORT_MAIL_TO,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_message,
        reportSenderFactoryClasses = [(CustomSenderFactory::class)])

class BuyerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ACRA.init(this)
        Realm.init(this)
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        _appContext = this
    }

    companion object {
        private var _appContext: Context? = null
        val appContext: Context?
        get() {
            // Нужно учитывать, что если этот контекст будет запрашиваться из статического кода,
            // то он может не успеть проинициализироваться
            if(_appContext == null){
                LogModule.printToLog("CONTEXT IS NULL")
            }
            return _appContext
        }
    }


//    private fun initRealm(context: Context) {
//        Realm.init(context)
//        val realmConfiguration = RealmConfiguration.Builder()
//                .schemaVersion(DBMigration.schemaVersion)
//                .deleteRealmIfMigrationNeeded() //TODO THIS IS ONLY FOR DEVELOPMENT
//                .migration(DBMigration())
//                .build()
//        Realm.setDefaultConfiguration(realmConfiguration)
//
//        getRealm(realmConfiguration).use({ realm -> PrimaryKeyFactory.initialize(realm) })
//    }
}
