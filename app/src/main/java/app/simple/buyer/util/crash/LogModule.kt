package app.simple.buyer.util.crash

import android.text.TextUtils
import android.util.Log
import app.simple.buyer.Constants
import org.acra.ACRA
import java.text.MessageFormat
import java.util.*

/**
 * Created by Zakharovi on 24.01.2018.
 */

object LogModule : Throwable() {
    private val preErrorText = "= Err: = "
    private val preErrorTextDetailed = "= Info = "
    private val preMessageText = "= Msg = "

    private fun detailedLog(throwable: Throwable?, stackTraceLength: Int): String {
        var message = ""
        if (throwable != null) {
            if(!TextUtils.isEmpty(throwable.message)){
                message = throwable.message!!
            }
            if (TextUtils.isEmpty(message)) {
                message = throwable.toString()
            }
            if (stackTraceLength == 0) {
                return message
            }

            val stackTrace = throwable.stackTrace
            val lines = ArrayList<String>()
            var i = 2
            while (i < stackTrace.size && lines.size <= stackTraceLength) {
                val stackTraceElement = stackTrace[i]

                if (!stackTraceElement.className.contains("CustomLogModule") && stackTraceElement.className.contains("max_moscow")) {
                    val fullClassName = stackTraceElement.className
                    val className = fullClassName.substring(fullClassName.lastIndexOf(".") + 1)
                    val methodName = stackTraceElement.methodName
                    val lineNumber = stackTraceElement.lineNumber

                    lines.add(MessageFormat.format("{0}.{1}():{2}", className, methodName, lineNumber))
                }
                i++
            }

            message = MessageFormat.format("{0}\n{1}", message, TextUtils.join("\n", lines))
        }
        return message
    }


    private fun getDetailedLog(throwable: Throwable?, message: String?, stackTraceLength: Int): String {
        var exMessage: String
        if (throwable == null) {
            exMessage = MessageFormat.format("{0} {1}", preMessageText, message ?: "")
        } else {
            if (TextUtils.isEmpty(message)) {
                exMessage = MessageFormat.format("{0} {1}",
                        preErrorText,
                        detailedLog(throwable, stackTraceLength))
            } else {
                exMessage = MessageFormat.format("{0} {1}\n{2}\n{3}",
                        preErrorText,
                        message,
                        preErrorTextDetailed,
                        detailedLog(throwable, stackTraceLength))
            }
            if (throwable.cause != null) {
                exMessage = MessageFormat.format("{0}\ncaused by:\n{1}", exMessage, detailedLog(throwable.cause, stackTraceLength))
            }
        }
        return exMessage
    }

    /**
     * Print log.
     *
     * @param message the mark
     * @param throwable the e
     */
    @JvmOverloads
    fun printToLog(message: String?, throwable: Throwable? = null) {
        val detailedLog = getDetailedLog(throwable, message, Constants.LOG_LEVEL)
        Log.d("BUYER", detailedLog)
        sendAcra(detailedLog)
    }

    fun printToLog(throwable: Throwable) {
        printToLog(null, throwable)
    }

    private fun sendAcra(err: String) {
        ACRA.getErrorReporter().putCustomData(System.currentTimeMillis().toString(), err)
    }
}