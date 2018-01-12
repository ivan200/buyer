package app.simple.buyer.util

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.StatFs
import android.provider.Settings
import android.telephony.CellInfoGsm
import android.telephony.CellInfoLte
import android.telephony.CellInfoWcdma
import android.telephony.TelephonyManager
import android.text.TextUtils
import android.util.Log
import android.view.Surface
import android.view.WindowManager
import java.io.*
import java.math.BigInteger
import java.net.InetSocketAddress
import java.net.NetworkInterface
import java.net.Socket
import java.text.MessageFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

/**
 * Created by Zakharovi on 10.01.2018.
 */

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
            val cpu_usage = cpuUsageStatistic
            return if (cpu_usage == null) "" else cpu_usage[0].toString()
        }

    val deviceCpuUsageSystem: String
        get() {
            val cpu_sys = cpuUsageStatistic
            return if (cpu_sys == null) "" else cpu_sys[1].toString()
        }

    val deviceCpuIDLE: String
        get() {
            val cpu_usage = cpuUsageStatistic
            return if (cpu_usage == null) "" else cpu_usage[2].toString()
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
            try {
                val p = Runtime.getRuntime().exec("uname -a")
                var `is`: InputStream? = null
                if (p.waitFor() == 0) {
                    `is` = p.inputStream
                } else {
                    `is` = p.errorStream
                }
                val br = BufferedReader(InputStreamReader(`is`!!), 1024)
                val line = br.readLine()
                br.close()
                return line
            } catch (ex: Exception) {
                return "ERROR: " + ex.message
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
    private val IPV4_BASIC_PATTERN_STRING = "(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}" + // initial 3 fields, 0-255 followed by .
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
                    tempString = tempString!!.replace("  ".toRegex(), " ")
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
    val isDeviceHasInternet: Boolean
        get() {
            try {
                val timeoutMs = 1500
                val sock = Socket()
                val sockaddr = InetSocketAddress("8.8.8.8", 53)
                sock.connect(sockaddr, timeoutMs)
                sock.close()
                return true
            } catch (e: IOException) {
                return false
            }

        }

    fun getDeviceLocalCountryCode(context: Context): String {
        return context.resources.configuration.locale.country
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

    @SuppressLint("MissingPermission")
    fun getDeviceTelephonyInfo(context: Context): String {
        val m_telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val DeviceId: String
        val SubscriberId: String
        val NetworkOperator: String
        val OsVersion: String
        val SimOperatorName: String
        DeviceId = m_telephonyManager.deviceId
        SubscriberId = m_telephonyManager.subscriberId
        NetworkOperator = m_telephonyManager.networkOperator
        OsVersion = m_telephonyManager.deviceSoftwareVersion
        SimOperatorName = m_telephonyManager.simOperatorName

        return "DeviceId : " + DeviceId +
                "\nSubscriberId : " + SubscriberId +
                "\nNetworkOperator : " + NetworkOperator +
                "\nSoftwareVersion : " + OsVersion +
                "\nSimOperatorName : " + SimOperatorName
    }

    @SuppressLint("MissingPermission", "NewApi")
    private fun getCellInfo(context: Context): String? {
        var additional_info = ""
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {

            val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager ?: return null
            //User must have Manifest.permission.ACCESS_COARSE_LOCATION

            val cellInfos = tm.allCellInfo
            val cellInfo = cellInfos[0]

            if (cellInfo is CellInfoGsm) {
                val cellInfoGsm: CellInfoGsm? = null
                val cellIdentityGsm = cellInfoGsm!!.cellIdentity
                additional_info = ("cell identity " + cellIdentityGsm.cid + "\n"
                        + "Mobile country code " + cellIdentityGsm.mcc + "\n"
                        + "Mobile network code " + cellIdentityGsm.mnc + "\n"
                        + "local area " + cellIdentityGsm.lac + "\n")
            } else if (cellInfo is CellInfoLte) {
                val cellIdentityLte = cellInfo.cellIdentity
                additional_info = ("cell identity " + cellIdentityLte.ci + "\n"
                        + "Mobile country code " + cellIdentityLte.mcc + "\n"
                        + "Mobile network code " + cellIdentityLte.mnc + "\n"
                        + "physical cell " + cellIdentityLte.pci + "\n"
                        + "Tracking area code " + cellIdentityLte.tac + "\n")
            } else if (cellInfo is CellInfoWcdma) {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    val cellIdentityWcdma = cellInfo.cellIdentity
                    additional_info = ("cell identity " + cellIdentityWcdma.cid + "\n"
                            + "Mobile country code " + cellIdentityWcdma.mcc + "\n"
                            + "Mobile network code " + cellIdentityWcdma.mnc + "\n"
                            + "local area " + cellIdentityWcdma.lac + "\n")
                }

            }
        }
        return additional_info
    }

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
                var _data = deviceUid.toByteArray()
                val _digest = java.security.MessageDigest.getInstance("MD5")
                _digest.update(_data)
                _data = _digest.digest()
                val _bi = BigInteger(_data).abs()
                deviceUid = _bi.toString(36)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return deviceUid
    }

    private fun capitalize(s: String?): String {
        if (s == null || s.isEmpty()) {
            return ""
        }
        val first = s[0]
        return if (Character.isUpperCase(first)) {
            s
        } else {
            Character.toUpperCase(first) + s.substring(1)
        }
    }

    @SuppressLint("NewApi")
    fun getDeviceMemoryTotal(activity: Context): Long {
        try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            return mi.totalMem / 1048576L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }

    }

    fun getDeviceMemoryFree(activity: Context): Long {
        try {
            val mi = ActivityManager.MemoryInfo()
            val activityManager = activity.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
            activityManager.getMemoryInfo(mi)

            return mi.availMem / 1048576L
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
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

            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                if (interfaceName != null) {
                    if (!intf.name.equals(interfaceName, ignoreCase = true))
                        continue
                }
                val mac = intf.hardwareAddress ?: return ""
                val buf = StringBuilder()
                for (idx in mac.indices)
                    buf.append(String.format("%02X:", mac[idx]))
                if (buf.length > 0)
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
            val interfaces = Collections.list(NetworkInterface.getNetworkInterfaces())
            for (intf in interfaces) {
                val addrs = Collections.list(intf.inetAddresses)
                for (addr in addrs) {
                    if (!addr.isLoopbackAddress) {
                        val sAddr = addr.hostAddress.toUpperCase()
                        //TODO 3.0.0
                        val isIPv4 = isIPv4Address(sAddr)
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%') // drop ip6 port
                                // suffix
                                return if (delim < 0) sAddr else sAddr.substring(0, delim)
                            }
                        }
                    }
                }
            }
        } catch (ex: Exception) {
        }
        // for now eat exceptions
        return ""
    }

    private fun executeTop(): String? {
        var p: java.lang.Process? = null
        var `in`: BufferedReader? = null
        var returnString: String? = null
        try {
            p = Runtime.getRuntime().exec("top -n 1")
            `in` = BufferedReader(InputStreamReader(p!!.inputStream))
            while (returnString == null || returnString.contentEquals("")) {
                returnString = `in`.readLine()
            }
        } catch (e: IOException) {
            Log.e("executeTop", "error in getting first line of top")
            e.printStackTrace()
        } finally {
            try {
                `in`!!.close()
                p!!.destroy()
            } catch (e: IOException) {
                Log.e("executeTop", "error in closing and destroying top process")
                e.printStackTrace()
            }

        }
        return returnString
    }

    @SuppressLint("MissingPermission")
    fun getDeviceNetworkType(activity: Context): String {
        var networkStatus = ""

        val connMgr = activity.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // check for wifi

        val wifi = connMgr?.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        // check for mobile data
        val mobile = connMgr?.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)

        if (wifi != null && wifi.isAvailable) {
            networkStatus = "Wifi"
        } else if (mobile != null && mobile.isAvailable) {
            networkStatus = getDataType(activity)
        } else {
            networkStatus = "noNetwork"
        }
        return networkStatus
    }

    //<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    fun getDeviceTelephonyNetworkType(context: Context): String? {
        val mTelephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val networkType = mTelephonyManager.networkType
        when (networkType) {
            TelephonyManager.NETWORK_TYPE_GSM -> return "gsm"
            TelephonyManager.NETWORK_TYPE_GPRS, TelephonyManager.NETWORK_TYPE_EDGE, TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> return "cdma" //"2g";
            TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0, TelephonyManager.NETWORK_TYPE_EVDO_A, TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA, TelephonyManager.NETWORK_TYPE_HSPA, TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD, TelephonyManager.NETWORK_TYPE_HSPAP, TelephonyManager.NETWORK_TYPE_TD_SCDMA -> return "wcdma" //"3g";
            TelephonyManager.NETWORK_TYPE_LTE -> return "lte" //"4g";
            else -> return null
        }
    }

    private fun isTablet(context: Context): Boolean {
        return context.resources.configuration.screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK >= Configuration.SCREENLAYOUT_SIZE_LARGE
    }

    fun getDeviceInch(activity: Context): Double {
        try {
            val displayMetrics = activity.resources.displayMetrics

            val yInches = displayMetrics.heightPixels / displayMetrics.ydpi
            val xInches = displayMetrics.widthPixels / displayMetrics.xdpi
            val diagonalInches = Math.sqrt((xInches * xInches + yInches * yInches).toDouble())
            return diagonalInches
        } catch (e: Exception) {
            return -1.0
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

    fun getDeviceOrientation(context: Context): String {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val rotation = wm.defaultDisplay.rotation
        when (rotation) {
            Surface.ROTATION_0 -> return "Portrait"
            Surface.ROTATION_90 -> return "Landscape left"
            Surface.ROTATION_180 -> return "Upside down"
            Surface.ROTATION_270 -> return "Landscape right"
        }
        return ""
    }

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

    fun getDeviceAvailableSpaceMB(f: File): Float {
        val stat = StatFs(f.path)
        val bytesAvailable: Long
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bytesAvailable = stat.blockSizeLong * stat.availableBlocksLong
        } else {
            bytesAvailable = stat.blockSize.toLong() * stat.availableBlocks.toLong()
        }
        return bytesAvailable / (1024f * 1024f)
    }

    //Получение вообще всех установленных приложений
    fun getDeviceInstalledApps(context: Context, includeSystem: Boolean): List<String> {
        val packages = context.packageManager.getInstalledApplications(PackageManager.GET_META_DATA)

        val allApps = ArrayList<String>()
        for (pkg in packages) {
            allApps.add(pkg.packageName)
        }
        if (includeSystem) {
            return allApps
        }
        //убираем системные
        val appsNoSystem = ArrayList<String>()
        for (app in allApps) {
            if (!isSystemPackage(context, app)) {
                appsNoSystem.add(app)
            }
        }
        return appsNoSystem
    }

    //Получение всех приложений, работающих в данный момент (включая или не включая системные)
    fun getDeviceRunningApps(context: Context, includeSystem: Boolean): List<String> {
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager

        var runningApps: MutableList<String> = ArrayList()

        //получаем работающие приложения
        try {
            val runAppsList = activityManager.runningAppProcesses
            for (process in runAppsList) {
                for (pkg in process.pkgList) {
                    runningApps.add(pkg)
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        //Из всех работающих задач получаем приложения, которые их запустили
        //Может вызвать securityException на api<18 (требуется "android.permission.GET_TASKS")
        try {
            val runningTasks = activityManager.getRunningTasks(1000)
            for (task in runningTasks) {
                runningApps.add(task.topActivity.packageName)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        //получаем все работающие сервисы
        try {
            val runningServices = activityManager.getRunningServices(1000)
            for (runningService in runningServices) {
                runningApps.add(runningService.service.packageName)
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        //убираем повторяющиеся
        runningApps = ArrayList(HashSet(runningApps))

        //Убираем системные
        if (!includeSystem) {
            for (i in runningApps.indices.reversed()) {
                val app = runningApps[i]
                if (isSystemPackage(context, app)) {
                    runningApps.removeAt(i)
                }
            }
        }
        return runningApps
    }

    //Проверка, является ли переданное приложение системным, или нет
    private fun isSystemPackage(context: Context, app: String): Boolean {
        val packageManager = context.packageManager
        try {
            val pkgInfo = packageManager.getPackageInfo(app, 0)
            return pkgInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }

        return false
    }
}
