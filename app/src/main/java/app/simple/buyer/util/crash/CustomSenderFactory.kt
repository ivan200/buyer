package app.simple.buyer.util.crash

import android.content.Context

import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderFactory

/**
 * Created by Zakharovi on 10.01.2018.
 */

class CustomSenderFactory : ReportSenderFactory {

    override fun create(context: Context, config: ACRAConfiguration): ReportSender {
        return CustomReportSender(context, config)
    }
}
