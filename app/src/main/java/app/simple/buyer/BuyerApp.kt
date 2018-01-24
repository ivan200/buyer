package app.simple.buyer

import android.app.Application
import android.content.Context
import android.support.v7.app.AppCompatDelegate
import app.simple.buyer.util.crash.CustomSenderFactory
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
        appContext = this
    }

    companion object {
        var appContext: Context? = null
    }
}
