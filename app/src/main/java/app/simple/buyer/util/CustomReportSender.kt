package app.simple.buyer.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import app.simple.buyer.Constants
import com.annimon.stream.ComparatorCompat
import com.annimon.stream.Stream
import org.acra.ReportField
import org.acra.collector.CrashReportData
import org.acra.config.ACRAConfiguration
import org.acra.sender.ReportSender
import org.acra.sender.ReportSenderException
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*

/**
* Created by Zakharovi on 10.01.2018.
*/

class CustomReportSender(private val mContext: Context, private val mConfig: ACRAConfiguration) : ReportSender {

    private val endLine = "\r\n"

    @Throws(ReportSenderException::class)
    override fun send(context: Context, report: CrashReportData) {

        val log = StringBuilder()
        val packageName = context.packageName

        log.append(MessageFormat.format("PACKAGE_NAME: {0}", packageName))

        appendLog(log, report, ReportField.APP_VERSION_NAME)

        log.append(MessageFormat.format("DEVICE: {0}", DeviceInfo.deviceModel)).append(endLine)
        log.append(MessageFormat.format("OS: {0}", DeviceInfo.deviceOSInfo)).append(endLine)
        log.append(MessageFormat.format("SCREEN: {0}, {1}, {2}",
                DeviceInfo.getDeviceType(context),
                DeviceInfo.getDeviceOrientation(context),
                DeviceInfo.getDeviceScreenSize(context)))
        log.append(MessageFormat.format("DATETIME: {0}", DeviceInfo.deviceDateTime)).append(endLine)

        log.append(endLine)
        appendLog(log, report, ReportField.STACK_TRACE)
        log.append(endLine)

        //        appendLog(log,report,ReportField.CUSTOM_DATA);
        val element = report[ReportField.CUSTOM_DATA]
        val splitCustomData = element?.flatten()
        if (splitCustomData != null && splitCustomData.isNotEmpty()) {
            log.append(endLine)
            val strings = Stream.of(*splitCustomData).sorted(ComparatorCompat.comparing { x: String -> x.substring(0, 13) }.reversed()).limit(100).toList()
            val simpleDateFormat = SimpleDateFormat( Constants.REPORT_SEND_DATE_TIME_FORMAT, Locale.getDefault())
            for (string in strings) {
                val substring = string.substring(0, 13)
                try {
                    val aLong = java.lang.Long.valueOf(substring)
                    log.append(simpleDateFormat.format(aLong)).append(string.substring(15)).append(endLine)
                } catch (ex: Exception) {
                    log.append(string).append(endLine)
                }

            }
        }

        log.append(endLine)
        appendLog(log, report, ReportField.LOGCAT)

        val body = log.toString()
        val subject = MessageFormat.format("{0} {1} Crash Report", packageName, report[ReportField.APP_VERSION_NAME])

        val emailIntent = Intent(android.content.Intent.ACTION_SENDTO)
        emailIntent.data = Uri.fromParts("mailto", mConfig.mailTo(), null)
        emailIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        emailIntent.putExtra(Intent.EXTRA_TEXT, body)
        emailIntent.putExtra(Intent.EXTRA_EMAIL, Constants.CRASH_REPORT_MAIL_TO)
        context.startActivity(emailIntent)
    }

    private fun appendLog(log: StringBuilder, report: CrashReportData, reportField: ReportField) {
        log.append(MessageFormat.format("{0}: {1}", reportField.name, report[reportField])).append(endLine)
    }

}
