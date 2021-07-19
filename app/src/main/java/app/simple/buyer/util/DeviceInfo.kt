package app.simple.buyer.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.StatFs
import android.provider.Settings
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.DisplayMetrics
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import androidx.annotation.RequiresPermission
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStreamReader
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern
import kotlin.math.sqrt

/**
 * Created by Zakharovi on 10.01.2018.
 */

@Suppress("unused", "MemberVisibilityCanBePrivate")
object DeviceInfo {
    val deviceLanguage: String
        get() = Locale.getDefault().displayLanguage

    val deviceLocale: String
        get() = Locale.getDefault().isO3Country

    //(false, TimeZone.SHORT);
    val deviceTimeZone: String
        get() = TimeZone.getDefault().id

    val deviceDateTime: String
        get() = getTime(Date(), TimeZone.getDefault(), "HH:mm:ss.SSS, dd.MM.yyyy, ZZZZ")

    val deviceDateTimeZeroGmt: String
        get() = getTime(Date(), TimeZone.getTimeZone("GMT+0"), "HH:mm:ss.SSS, dd.MM.yyyy, ZZZZ")

    val deviceNumberOfProcessors: String
        get() = Runtime.getRuntime().availableProcessors().toString() + ""

    val deviceCpuUsageTotal: String
        get() {
            val cpu = cpuUsageStatistic
            if (cpu != null) {
                val total = cpu[0] + cpu[1] + cpu[2] + cpu[3]
                return total.toString()
            }
            return ""
        }

    val deviceCpuUsageUser: String
        get() {
            val cpuUsage = cpuUsageStatistic
            return if (cpuUsage == null) "" else cpuUsage[0].toString()
        }

    val deviceCpuUsageSystem: String
        get() {
            val cpuSys = cpuUsageStatistic
            return if (cpuSys == null) "" else cpuSys[1].toString()
        }

    val deviceCpuIDLE: String
        get() {
            val cpuUsage = cpuUsageStatistic
            return if (cpuUsage == null) "" else cpuUsage[2].toString()
        }

    val deviceOSInfo: String
        get() = MessageFormat.format("Android {0} (API-{1})", Build.VERSION.RELEASE, Build.VERSION.SDK_INT)

    val deviceModel: String
        get() {
            val manufacturer = Build.MANUFACTURER
            val model = Build.MODEL
            return if (model.startsWith(manufacturer)) {
                capitalize(model)
            } else {
                capitalize(manufacturer) + " " + model
            }
        }

    val deviceKernelVersion: String
        get() {
            return try {
                val p = Runtime.getRuntime().exec("uname -a")
                val `is` = if (p.waitFor() == 0) p.inputStream else p.errorStream
                val br = BufferedReader(InputStreamReader(`is`!!), 1024)
                val line = br.readLine()
                br.close()
                line
            } catch (ex: Exception) {
                "ERROR: " + ex.message
            }
        }


    val deviceMacAddress: String
        get() {
            var mac = getMACAddress("wlan0")
            if (TextUtils.isEmpty(mac)) {
                mac = getMACAddress("eth0")
            }
            if (TextUtils.isEmpty(mac)) {
                mac = "00:00:00:00:00:00"
            }
            return mac
        }

    /**
     * Get IP address from first non-localhost interface
     *
     * @return address or empty string
     */
    private const val IPV4_BASIC_PATTERN_STRING = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" + // initial 3 fields, 0-255 followed by .
            "([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])" // final field, 0-255
    private val IPV4_PATTERN = Pattern.compile("^$IPV4_BASIC_PATTERN_STRING$")


    val deviceIpAddressIPv4: String
        get() = getIPAddress(true)
    val deviceIpAddressIPv6: String
        get() = getIPAddress(false)

    /*
     *
     * @return integer Array with 4 elements: user, system, idle and other cpu
     * usage in percentage.
     */
    private val cpuUsageStatistic: IntArray?
        get() {
            try {
                var tempString = executeTop()

                tempString = tempString!!.replace(",".toRegex(), "")
                tempString = tempString.replace("User".toRegex(), "")
                tempString = tempString.replace("System".toRegex(), "")
                tempString = tempString.replace("IOW".toRegex(), "")
                tempString = tempString.replace("IRQ".toRegex(), "")
                tempString = tempString.replace("%".toRegex(), "")
                for (i in 0..9) {
                    tempString = tempString!!.replace(" {2}".toRegex(), " ")
                }
                tempString = tempString!!.trim { it <= ' ' }
                val myString = tempString.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val cpuUsageAsInt = IntArray(myString.size)
                for (i in myString.indices) {
                    myString[i] = myString[i].trim { it <= ' ' }
                    cpuUsageAsInt[i] = Integer.parseInt(myString[i])
                }
                return cpuUsageAsInt

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("executeTop", "error in getting cpu statics")
                return null
            }
        }

    // TCP/HTTP/DNS (depending on the port, 53=DNS, 80=HTTP, etc.)
    //need android.permission.INTERNET
    val isDeviceHasInternetBySocket: Boolean
        get() {
            return try {
                val timeoutMs = 1500
                val sock = Socket()
                val sockAddress = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockAddress, timeoutMs)
                sock.close()
                true
            } catch (e: IOException) {
                false
            }
        }


    enum class InternetType(val rating: Int) {
        NO_INTERNET(0),
        OTHER(1),
        MOBILE(2),
        WIFI(3);

        fun max(iType: InternetType): InternetType {
            return if(this.rating < iType.rating) iType else this
        }
    }

    //need android.permission.ACCESS_NETWORK_STATE and android.permission.INTERNET
    @RequiresPermission(allOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.INTERNET])
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun isDeviceHasInternetByManager(context: Context): InternetType {
        var maxType = InternetType.NO_INTERNET

        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            cm.allNetworks
                .mapNotNull { cm.getNetworkCapabilities(it) }
                .filter {
                    it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                            && it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                }.forEach {
                    maxType = when {
                        it.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> maxType.max(InternetType.WIFI)
                        it.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> maxType.max(InternetType.MOBILE)
                        else -> maxType.max(InternetType.OTHER)
                    }
                }
        } else {
            cm.allNetworkInfo
                .filterNotNull()
                .filter { it.isConnected }
                .forEach {
                    maxType = when (it.type) {
                        ConnectivityManager.TYPE_MOBILE -> maxType.max(InternetType.MOBILE)
                        ConnectivityManager.TYPE_WIFI -> maxType.max(InternetType.WIFI)
                        else -> maxType.max(InternetType.OTHER)
                    }
                }
        }
        return maxType
    }

    @Suppress("DEPRECATION")
    fun getDeviceLocalCountryCode(context: Context): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && !context.resources.configuration.locales.isEmpty) {
            context.resources.configuration.locales.get(0).country
        } else {
            context.resources.configuration.locale.country
        }
    }

    fun getDeviceType(context: Context): String {
        return if (isTablet(context)) {
            if (getDeviceMoreThan5Inch(context)) {
                "Tablet"
            } else
                "Mobile"
        } else {
            "Mobile"
        }
    }

    fun getDeviceKeyboardPresence(context: Context): String {
        val keyboardPresent = context.resources.configuration.keyboard != Configuration.KEYBOARD_NOKEYS
        return if (keyboardPresent) "Hardware keyboard" else "Software keyboard"
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceTelephonyInfo(context: Context): String {
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return "DeviceId : " + tm.deviceId +
                "\nSubscriberId : " +  tm.subscriberId +
                "\nNetworkOperator : " + tm.networkOperator +
                "\nSoftwareVersion : " + tm.deviceSoftwareVersion +
                "\nSimOperatorName : " + tm.simOperatorName
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "NewApi")
    private fun getCellInfo(context: Context): String? {
        var additionalInfo = ""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {

            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            //User must have Manifest.permission.ACCESS_COARSE_LOCATION

            val cellInfos = tm.allCellInfo
            val cellInfo = cellInfos[0]

            if (cellInfo is CellInfoGsm) {
                val cellInfoGsm: CellInfoGsm? = null
                val cellIdentityGsm = cellInfoGsm!!.cellIdentity
                additionalInfo = ("cell identity " + cellIdentityGsm.cid + "\n"
                        + "Mobile country code " + cellIdentityGsm.mcc + "\n"
                        + "Mobile network code " + cellIdentityGsm.mnc + "\n"
                        + "local area " + cellIdentityGsm.lac + "\n")
            } else if (cellInfo is CellInfoLte) {
                val cellIdentityLte = cellInfo.cellIdentity
                additionalInfo = ("cell identity " + cellIdentityLte.ci + "\n"
                        + "Mobile country code " + cellIdentityLte.mcc + "\n"
                        + "Mobile network code " + cellIdentityLte.mnc + "\n"
                        + "physical cell " + cellIdentityLte.pci + "\n"
                        + "Tracking area code " + cellIdentityLte.tac + "\n")
            } else if (cellInfo is CellInfoWcdma) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    val cellIdentityWcdma = cellInfo.cellIdentity
                    additionalInfo = ("cell identity " + cellIdentityWcdma.cid + "\n"
                            + "Mobile country code " + cellIdentityWcdma.mcc + "\n"
                            + "Mobile network code " + cellIdentityWcdma.mnc + "\n"
                            + "local area " + cellIdentityWcdma.lac + "\n")
                }

            }
        }
        return additionalInfo
    }

    @Suppress("SameParameterValue")
    private fun getTime(date: Date, timezone: TimeZone, pattern: String): String {
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        sdf.timeZone = timezone
        return sdf.format(date)
    }


    @SuppressLint("HardwareIds")
    fun getDeviceId(context: Context): String {
        var deviceUid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        if (deviceUid.isEmpty()) {
            deviceUid = "12356789" // for emulator testing
        } else {
            try {
                var data = deviceUid.toByteArray()
                val digest = java.security.MessageDigest.getInstance("MD5")
                digest.update(data)
                data = digest.digest()
                val bi = BigInteger(data).abs()
                deviceUid = bi.toString(36)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return deviceUid
    }

    private fun capitalize(s: String?): String {
        if (s.isNullOrEmpty()) return ""
        return  if (Character.isUpperCase(s[0])) s else Character.toUpperCase(s[0]) + s.substring(1)
    }

    @SuppressLint("NewApi")
    fun getDeviceMemoryTotal(activity: Context): Long {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            mi.totalMem / 1048576L
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getDeviceMemoryFree(activity: Context): Long {
        return try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            mi.availMem / 1048576L
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    fun getDeviceMemoryUsed(context: Context): Long {
        return getDeviceMemoryTotal(context) - getDeviceMemoryFree(context)
    }

    /**
     * Returns MAC address of the given interface name.
     *
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return mac address or empty string
     */
    private fun getMACAddress(interfaceName: String?): String {
        try {
            NetworkInterface.getNetworkInterfaces().toList()
                    .filter { it.name == null || it.name.equals(interfaceName, ignoreCase = true)}
                    .forEach {
                        val mac = it.hardwareAddress ?: return ""
                        val buf = StringBuilder()

                        for (idx in mac.indices)
                            buf.append(String.format("%02X:", mac[idx]))
                        if (buf.isNotEmpty())
                            buf.deleteCharAt(buf.length - 1)
                        return buf.toString()
                    }
        } catch (ex: Exception) {
            return ""
        }
        // for now eat exceptions
        return ""
        /*
        * try { // this is so Linux hack return
        * loadFileAsString("/sys/class/net/" +interfaceName +
        * "/address").toUpperCase().trim(); } catch (IOException ex) { return
        * null; }
        */
    }

    private fun isIPv4Address(input: String): Boolean {
        return IPV4_PATTERN.matcher(input).matches()
    }

    private fun getIPAddress(useIPv4: Boolean): String {
        try {
            NetworkInterface.getNetworkInterfaces().toList()
                    .flatMap { it.inetAddresses.toList() }
                    .filter { !it.isLoopbackAddress }
                    .map { it.hostAddress.toUpperCase(Locale.getDefault()) }
                    .forEach {
                        val isIPv4 = isIPv4Address(it)
                        when {
                            isIPv4 && useIPv4 -> return it
                            !isIPv4 && !useIPv4 -> {
                                val delimiter = it.indexOf('%') // drop ip6 port
                                return if (delimiter < 0) it else it.substring(0, delimiter)
                            }
                        }
                    }
        } catch (_: Exception) {
        }// for now eat exceptions
        return ""
    }

    private fun executeTop(): String? {
        var p: Process? = null
        var reader: BufferedReader? = null
        var returnString: String? = null
        try {
            p = Runtime.getRuntime().exec("top -n 1")
            reader = BufferedReader(InputStreamReader(p!!.inputStream))
            while (returnString == null || returnString.contentEquals("")) {
                returnString = reader.readLine()
            }
        } catch (e: IOException) {
            Log.e("executeTop", "error in getting first line of top")
            e.printStackTrace()
        } finally {
            try {
                reader!!.close()
                p!!.destroy()
            } catch (e: IOException) {
                Log.e("executeTop", "error in closing and destroying top process")
                e.printStackTrace()
            }

        }
        return returnString
    }

    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission")
    fun getDeviceNetworkType(activity: Context): String {
        val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        // check for wifi
        val wifi = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        // check for mobile data
        val mobile = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        return if (wifi != null && wifi.isAvailable) {
            "Wifi"
        } else if (mobile != null && mobile.isAvailable) {
            getDataType(activity)
        } else {
            "noNetwork"
        }
    }

    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    @SuppressLint("MissingPermission")
    fun getDeviceTelephonyNetworkType(context: Context): String? {
        val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return when (mTelephonyManager.networkType) {
            TelephonyManager.NETWORK_TYPE_GSM -> "gsm"
            TelephonyManager.NETWORK_TYPE_GPRS,
            TelephonyManager.NETWORK_TYPE_EDGE,
            TelephonyManager.NETWORK_TYPE_CDMA,
            TelephonyManager.NETWORK_TYPE_1xRTT,
            TelephonyManager.NETWORK_TYPE_IDEN -> "cdma" //"2g";
            TelephonyManager.NETWORK_TYPE_UMTS,
            TelephonyManager.NETWORK_TYPE_EVDO_0,
            TelephonyManager.NETWORK_TYPE_EVDO_A,
            TelephonyManager.NETWORK_TYPE_HSDPA,
            TelephonyManager.NETWORK_TYPE_HSUPA,
            TelephonyManager.NETWORK_TYPE_HSPA,
            TelephonyManager.NETWORK_TYPE_EVDO_B,
            TelephonyManager.NETWORK_TYPE_EHRPD,
            TelephonyManager.NETWORK_TYPE_HSPAP,
            TelephonyManager.NETWORK_TYPE_TD_SCDMA -> "wcdma" //"3g";
            TelephonyManager.NETWORK_TYPE_LTE -> "lte" //"4g";
            else -> null
        }
    }

    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun getDeviceInch(activity: Context): Double {
        return try {
            val displayMetrics = activity.resources.displayMetrics

            val yInches = displayMetrics.heightPixels / displayMetrics.ydpi
            val xInches = displayMetrics.widthPixels / displayMetrics.xdpi
            val diagonalInches = sqrt((xInches * xInches + yInches * yInches).toDouble())
            diagonalInches
        } catch (e: Exception) {
            0.0
        }
    }

    fun getDeviceInchString(activity: Context): String {
        return getDeviceInch(activity).toString()
    }

    private fun getDeviceMoreThan5Inch(activity: Context): Boolean {
        return getDeviceInch(activity) >= 5
    }

    fun getDeviceScreenSize(context: Context): String {
        val displayMetrics = context.resources.displayMetrics
        return MessageFormat.format("width:{0}, height:{1}, density:{2}",
                displayMetrics.widthPixels,
                displayMetrics.heightPixels,
                displayMetrics.density)
    }


    fun getDeviceOrientation(context: Context): Int {
        (context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager)
                ?.defaultDisplay
                ?.let {
                    return when {
                        it.width == it.height -> Configuration.ORIENTATION_SQUARE
                        it.width < it.height -> Configuration.ORIENTATION_PORTRAIT
                        else -> Configuration.ORIENTATION_LANDSCAPE
                    }
                }
        return Configuration.ORIENTATION_UNDEFINED
    }


    fun getDeviceOrientationString(context: Context): String {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as? WindowManager
        when (wm?.defaultDisplay?.rotation) {
            Surface.ROTATION_0 -> return "Portrait"
            Surface.ROTATION_90 -> return "Landscape left"
            Surface.ROTATION_180 -> return "Upside down"
            Surface.ROTATION_270 -> return "Landscape right"
        }
        return ""
    }

    fun isDeviceInPortraitMode(context: Context): Boolean {
        val orientation = context.resources.configuration.orientation
        return if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            false
        } else orientation == Configuration.ORIENTATION_PORTRAIT
    }

    @SuppressLint("MissingPermission")
    private fun getDataType(activity: Context): String {
        var type = "Mobile Data"
        val tm = activity.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        when (tm.networkType) {
            TelephonyManager.NETWORK_TYPE_HSDPA -> {
                type = "Mobile Data 3G"
                Log.d("Type", "3g")
            }
            TelephonyManager.NETWORK_TYPE_HSPAP -> {
                type = "Mobile Data 4G"
                Log.d("Type", "4g")
            }
            TelephonyManager.NETWORK_TYPE_GPRS -> {
                type = "Mobile Data GPRS"
                Log.d("Type", "GPRS")
            }
            TelephonyManager.NETWORK_TYPE_EDGE -> {
                type = "Mobile Data EDGE 2G"
                Log.d("Type", "EDGE 2g")
            }
        }// for 3g HSDPA networktype will be return as
        // per testing(real) in device with 3g enable
        // data
        // and speed will also matters to decide 3g network type
        // No specification for the 4g but from wiki
        // i found(HSPAP used in 4g)
        return type
    }

    @Suppress("DEPRECATION")
    fun getDeviceAvailableSpaceMB(f: File): Float {
        val stat = StatFs(f.path)
        val bytesAvailable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            stat.blockSizeLong * stat.availableBlocksLong
        } else {
            stat.blockSize.toLong() * stat.availableBlocks.toLong()
        }
        return bytesAvailable / (1024f * 1024f)
    }

    //Получение вообще всех установленных приложений
    fun getDeviceInstalledApps(context: Context, includeSystem: Boolean = false): List<String> {
        return context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
                .filter { if (includeSystem) true else !isSystemPackage(it) }
                .map { it.packageName }
    }

    //Получение всех приложений, работающих в данный момент
    @Suppress("DEPRECATION")
    fun getDeviceRunningApps(context: Context, includeSystem: Boolean = false): List<String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApps: MutableSet<String> = mutableSetOf()

        try { activityManager.runningAppProcesses } catch (ex: Exception) { null }
                ?.flatMap { it.pkgList.asList() }
                ?.let { runningApps.addAll(it) }

        try { activityManager.getRunningTasks(1000) } catch (ex: Exception) { null }
                ?.mapNotNull { it.topActivity?.packageName }
                ?.let { runningApps.addAll(it) }

        try { activityManager.getRunningServices(1000) } catch (ex: Exception) { null }
                ?.map { it.service.packageName }
                ?.let { runningApps.addAll(it) }

        return if(includeSystem) runningApps.toList() else runningApps.filter { !isSystemPackage(context, it) }
    }

    //Проверка, является ли переданное приложение системным, или нет
    fun isSystemPackage(context: Context, app: String): Boolean {
        return try {context.packageManager.getPackageInfo(app, 0)} catch (ex: Exception) {null}
                ?.let { isSystemPackage(it.applicationInfo) } == true
    }

    fun isSystemPackage(applicationInfo: ApplicationInfo): Boolean {
        return applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
    }


    //Получение всех приложений, что откликаются на определённое действие
    //например, получение всех несистемных приложений камеры:
    //DeviceInfo.getDeviceAppsForAction(context, MediaStore.ACTION_IMAGE_CAPTURE, false);
    fun getDeviceAppsForAction(context: Context, action: String, includeSystem: Boolean = false): List<String> {
        val apps: MutableSet<String> = mutableSetOf()

        val listApps = context.packageManager.queryIntentActivities(Intent(action), 0)
        apps.addAll(listApps.map { it.activityInfo.packageName })

        return if(includeSystem) apps.toList() else apps.filter { !isSystemPackage(context, it) }
    }

    fun getDeviceDisplaySizePixels(activity: Activity): Point {
        val d = activity.windowManager.defaultDisplay
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val realDisplayMetrics = DisplayMetrics()
            d.getRealMetrics(realDisplayMetrics)
            Point(realDisplayMetrics.widthPixels, realDisplayMetrics.heightPixels)
        } else {
            val displayMetrics = DisplayMetrics()
            d.getMetrics(displayMetrics)
            Point(displayMetrics.widthPixels, displayMetrics.heightPixels)
        }
    }

    val deviceGUID: String
        get() {
            val deviceData = Build.FINGERPRINT
            // Only devices with API >= 9 have android.os.Build.SERIAL
            // http://developer.android.com/reference/android/os/Build.html#SERIAL
            // If a user upgrades software or roots their device, there will be a duplicate entry
            var serial = ""
            return try {
                serial = Build::class.java.getField("SERIAL").get(null).toString()
                UUID(deviceData.hashCode().toLong(), serial.hashCode().toLong()).toString()
            } catch (exception: Exception) {
                exception.printStackTrace()
                UUID(deviceData.hashCode().toLong(), serial.hashCode().toLong()).toString()
            }

        }

    /**
     * Gets the device unique id called IMEI. Sometimes, this returns 00000000000000000 for the
     * rooted devices.
     */
    @Suppress("DEPRECATION")
    @SuppressLint("MissingPermission", "HardwareIds")
    fun getDeviceImei(ctx: Context): String {
        // use application level context to avoid unnecessary leaks.
        val telephonyManager = ctx.applicationContext.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        return telephonyManager.deviceId
    }
}
