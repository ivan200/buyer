@file:Suppress("DEPRECATION")

package app.simple.buyer.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateUtils
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.AnyRes
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.core.view.ViewCompat.setBackground
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.simple.buyer.BuildConfig
import app.simple.buyer.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.max
import kotlin.math.min

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Utils {
    /**
     * Keyboard
     **/

    fun hideKeyboard(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }

    fun hideKeyboardFrom(view: View) {
        val imm = view.context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }

    fun showKeyboard(activity: Activity, editText: EditText) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }

    fun showKeyBoard2(editText: View?) {
        if (editText != null && editText.requestFocus()) {
            val inputMethodManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    /**
     * Resources
     **/

    //Перекрашивание цвета оверскролла на всех RecyclerView на api<21. Достаточно вызвать 1 раз в onCreate приложения
    fun changeOverScrollGlowColor(res: Resources, colorID: Int) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            changeResColor(res, "overscroll_glow", colorID)
            changeResColor(res, "overscroll_edge", colorID)
        }
    }

    //Перекрашивание системного ресурса используемого в приложении в определённый цвет
    @Suppress("DEPRECATION")
    private fun changeResColor(res: Resources, resId: String, colorID: Int) {
        try {
            val drawableId = res.getIdentifier(resId, "drawable", "android")
            val drawable = res.getDrawable(drawableId)
            drawable.setColorFilter(res.getColor(colorID), PorterDuff.Mode.SRC_ATOP)
        } catch (ignored: Exception) {
        }
    }

    fun tintDrawable(drawable: Drawable, @ColorInt color: Int) : Drawable{
        val wrappedDrawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(wrappedDrawable, color)
        return DrawableCompat.unwrap(wrappedDrawable)
    }

    fun tintDrawableWithMatrix(drawable: Drawable, @ColorInt iColor: Int) {
        val r = Color.red(iColor)
        val g = Color.green(iColor)
        val b = Color.blue(iColor)
        val x = (255f - r) / 255f
        val y = (255f - g) / 255f
        val z = (255f - b) / 255f
        val rx = 0.3f
        val gy = 0.6f
        val bz = 0.1f
        val offset = 25f

        val matrix = floatArrayOf(
                rx * x, gy * x, bz * x, 0f, r - offset,
                rx * y, gy * y, bz * y, 0f, g - offset,
                rx * z, gy * z, bz * z, 0f, b - offset,
                0f, 0f, 0f, 1f, 0f)

        val mFilter = ColorMatrixColorFilter(matrix)
        drawable.colorFilter = mFilter
    }

    //Перекрашивание цвета прогрессбара на api<21
    fun tintIndeterminateProgress(progress: ProgressBar, @ColorInt color: Int = ContextCompat.getColor(progress.context, R.color.colorPrimary)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progress.indeterminateTintList = ColorStateList.valueOf(color)
        } else {
            (progress.indeterminateDrawable as? LayerDrawable)?.apply {
                if (numberOfLayers == 1) {
                    setId(0, android.R.id.progress)
                    val progressDrawable = findDrawableByLayerId(android.R.id.progress).mutate()
                    tintDrawableWithMatrix(progressDrawable, color)
                } else if (numberOfLayers >= 2) {
                    setId(0, android.R.id.progress)
                    setId(1, android.R.id.secondaryProgress)
                    val progressDrawable = findDrawableByLayerId(android.R.id.progress).mutate()
                    progressDrawable.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
                }
            }
        }
    }

    fun tintHorizontalProgress(progress: ProgressBar, @ColorInt color: Int = ContextCompat.getColor(progress.context, R.color.colorPrimary)) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            progress.progressTintList = ColorStateList.valueOf(color)
        } else {
            val layerDrawable = progress.progressDrawable as? LayerDrawable
            val progressDrawable = layerDrawable?.findDrawableByLayerId(android.R.id.progress)
            progressDrawable?.setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        }
    }

    //Расчёт количества столбцов относительно размеров экрана и ориентации
    fun getGridColumnsCount(context: Context, landscape: Boolean): Int {
        val displayMetrics = context.resources.displayMetrics
        val hi = displayMetrics.heightPixels / displayMetrics.xdpi
        val wi = displayMetrics.widthPixels / displayMetrics.ydpi
        val screenWidthInch = if (landscape) max(wi, hi) else min(wi, hi)
        val screenWidthCm = screenWidthInch * 2.54f
        val columns = (screenWidthCm / 2).toInt()
        return if (columns < 3) 3 else columns
    }

    fun convertPixelsToDpInt(px: Number): Int {
        return convertPixelsToDp(px).toInt()
    }

    fun convertPixelsToDp(px: Number): Float {
        return px.toFloat() / (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun convertDpToPixelInt(dp: Int): Int {
        return convertDpToPixel(dp).toInt()
    }

    fun convertDpToPixel(dp: Number): Float {
        return dp.toFloat() * (Resources.getSystem().displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

    fun getBitmapFromDrawable(context: Context, @DrawableRes drawableId: Int, width: Int = 0, height: Int = 0): Bitmap {
        val drawable = ContextCompat.getDrawable(context, drawableId)
        if (drawable is BitmapDrawable) {
            return if (width > 0 && height > 0) {
                Bitmap.createScaledBitmap(drawable.bitmap, width, height, false)
            } else {
                drawable.bitmap
            }
        } else if (drawable is VectorDrawableCompat || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && drawable is VectorDrawable)) {
            val w: Int
            val h: Int
            if (width > 0 && height > 0) {
                w = width
                h = height
            } else {
                w = drawable.intrinsicWidth
                h = drawable.intrinsicHeight
            }
            val bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            return bitmap
        } else {
            throw IllegalArgumentException("unsupported drawable type")
        }
    }

    //Получение ресурса, привязаного к теме через аттрибуты
    @AnyRes
    fun getResIdFromAttribute(context: Context, @AttrRes attr: Int): Int {
        if (attr == 0)
            return 0
        val typedValueAttr = TypedValue()
        context.theme.resolveAttribute(attr, typedValueAttr, true)
        return typedValueAttr.resourceId
    }

    //Получение ресурса цвета, привязаного к теме через аттрибуты, например android.R.attr.textColorPrimary
    @ColorInt
    fun getColorIdFromAttribute(context: Context, @AttrRes colorAttr: Int): Int {
        val resolvedAttr = TypedValue()
        context.theme.resolveAttribute(colorAttr, resolvedAttr, true)
        val colorRes = resolvedAttr.run { if (resourceId != 0) resourceId else data }
        return ContextCompat.getColor(context, colorRes)
    }

    //Обращение яркости цвета в html строке (color: и background-color:)
    fun invertStyleBrightness(str: String): String {
        val styleName = "color:"
        var index = str.indexOf(styleName)
        if (index < 0) {
            return str
        }

        val colorMapper = hashMapOf<String, String>()
        val fixedString = StringBuilder()
        var i1: Int
        var i2 = 0
        while (index >= 0) {
            fixedString.append(str.substring(i2, index))
            i1 = str.indexOf('#', index)
            i2 = str.indexOf(';', index)
            if (i1 >= 0 && i2 >= 0 && i2 > i1 && i2-i1 < 10) {
                fixedString.append(str.substring(index, i1))
                val subStrColor = str.substring(i1, i2)
                if(!colorMapper.containsKey(subStrColor)){
                    colorMapper[subStrColor] = invertColorBrightness(subStrColor)
                }
                val reversedColor = colorMapper[subStrColor]
                fixedString.append(reversedColor)
            } else {
                fixedString.append(str.substring(index, i1))
            }
            index = str.indexOf(styleName, index + 1)
        }
        fixedString.append(str.substring(if (i2 >= 0) i2 else index, str.length - 1))

        return fixedString.toString()
    }

    //Обращение яркости цвета в формате строки, для использования на тёмной теме цветов светлой
    fun invertColorBrightness(color: String): String {
        return try {
            val oldColor = Color.parseColor(color)
            val newColor = invertColorBrightness(oldColor)
            if (Color.alpha(newColor) == 255) {
                String.format("#%06X", 0xFFFFFF and newColor)
            } else {
                String.format("#%08X", newColor)
            }
        } catch (ex: Exception) {
            color
        }
    }

    //Обращение яркости цвета, для нормального использования на тёмной теме цветов светлой
    fun invertColorBrightness(color: Int): Int {
        return try {
            val outLab = DoubleArray(3)
            ColorUtils.colorToLAB(color, outLab)
            outLab[0] = 100 - outLab[0] * 0.85
            val newColor = ColorUtils.LABToColor(outLab[0], outLab[1], outLab[2])
            if (Color.alpha(color) == 255) {
                newColor
            } else {
                Color.argb(Color.alpha(color), Color.red(newColor), Color.green(newColor), Color.blue(newColor))
            }
        } catch (ex: Exception) {
            color
        }
    }

    /**
     * Realm
     **/

    //Ещё примеры использования реалма
//            val realm = Realm.getDefaultInstance()
//            return getQuery(realm)
//                    .equalTo("id", id)
//                    .findFirstAsync()
//                    .asFlowable<Project>()
//                    .map { x->realm.copyFromRealm(x) }
//                    .single(null)
//                    .doOnSuccess { realm.close() }


//            Realm.getDefaultInstance().use {
//                return getQuery(it)
//                        .equalTo("id", id)
//                        .findFirstAsync()
//                        .asFlowable<Project>()
//                        .map { x -> it.copyFromRealm(x) }
//                        .single(null)
//            }

//            Realm.getDefaultInstance()
//                  .where(Day.class)
//                  .findAll()
//                  .asFlowable()
//                  .toObservable()
//                  .subscribeOn(AndroidSchedulers.mainThread())
//                  .observeOn(AndroidSchedulers.mainThread())
//                  .filter(RealmResults::isLoaded)
//                  .filter(RealmResults::isValid)
//                  .doOnComplete(() -> Realm.getDefaultInstance().close());

    /**
     * Intent
     **/

    //открытие плеймаркета для текущего приложения
    fun openPlayMarketForCurrentApp(context: Context) {
        val appPackageName = context.packageName
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (exception: android.content.ActivityNotFoundException) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    //открытие приложения по его packageName или открытие плеймаркета если приложение не установлено
    fun startNewApplication(context: Context, packageName: String) {
        if (TextUtils.isEmpty(packageName))
            return

        var intent: Intent? = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            intent = Intent(Intent.ACTION_VIEW)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                intent.data = Uri.parse("market://details?id=$packageName")
                context.startActivity(intent)
            } catch (anfe: android.content.ActivityNotFoundException) {
                intent.data = Uri.parse("http://play.google.com/store/apps/details?id=$packageName")
                context.startActivity(intent)
            }
        }
    }

    //открытие детальных настроек приложения
    fun openApplicationDetailSettings(activity: Activity, packageName: String, requestCode: Int): Boolean {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.putExtra(Intent.EXTRA_PACKAGE_NAME, packageName)
        }
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        intent.addCategory(Intent.CATEGORY_DEFAULT)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        return if (intent.resolveActivity(activity.packageManager) != null) {
            try {
                activity.startActivityForResult(intent, requestCode)
                true
            } catch (ex: Exception) {
                ex.printStackTrace()
                false
            }
        } else {
            false
        }
    }

    //Получение читаемого имени приложения по его packagename
    fun getApplicationName(context: Context, packageName: String): String {
        var appName = packageName
        val packageManager = context.packageManager
        try {
            appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return appName
    }

    //Сравнение версий приложения для определения, нужно ли его обновлять
    fun needForUpdate(newVersionString: String): Boolean {
        if (TextUtils.isEmpty(newVersionString)) {
            return false
        }
        val split = "._"
        val curArray = BuildConfig.VERSION_NAME.replace("[^0-9$split]".toRegex(), "").split("[$split]".toRegex())
        val newArray = newVersionString.replace("[^0-9$split]".toRegex(), "").split("[$split]".toRegex())

        val maxIndex = max(curArray.size, newArray.size)
        for (i in 0 until maxIndex) {
            val oldValue = if (i < curArray.size) Integer.parseInt(curArray[i]) else 0
            val newValue = if (i < newArray.size) Integer.parseInt(newArray[i]) else 0
            if (oldValue != newValue) {
                return oldValue < newValue
            }
        }
        return false
    }

    /**
     * Time
     **/

    fun substractSeconds(date: Date): Date {
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun substractHours(date: Date?): Date? {
        if(date == null) return null
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun maximizeHours(date: Date?): Date? {
        if(date == null) return null
        val cal = Calendar.getInstance()
        cal.time = date
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.time
    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance()
        val cal2 = Calendar.getInstance()
        cal1.time = date1
        cal2.time = date2
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun getYesterday(currentDate: Date): Date {
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, -1)
        return calendar.time
    }

    //Получение двухбуквенного названия дня недели (ПН, ВС)
    fun getTwoSymbolDayOfWeekName(cal: Calendar): String {
        var dayName: String
        if (Locale.getDefault().language.toLowerCase() == "en" || Locale.getDefault().language.toLowerCase() == "ru") {
            dayName = cal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.getDefault())
            dayName = if (dayName.length > 2) dayName.substring(0, 2) else dayName
        } else {
            dayName = SimpleDateFormat("EEEEE", Locale.getDefault()).format(cal.time)
        }
        return dayName.toUpperCase()
    }

    //Получение номера дня недели из календаря (со сдвигом на первый день недели используемый в разных локалицациях)
    //пн = 1, вс = 7
    fun getDayOfWeek(cal: Calendar): Int {
        val dayDiff = cal.firstDayOfWeek - 1
        var dayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - dayDiff
        if (dayOfWeek <= 0) {
            dayOfWeek += 7
        }
        return dayOfWeek
    }

    //Получение имени текущего месяца из календаря
    fun getMonthName(context: Context, calendar: Calendar, isShort: Boolean = false): String {
        var flags = DateUtils.FORMAT_SHOW_DATE or DateUtils.FORMAT_NO_MONTH_DAY or DateUtils.FORMAT_NO_YEAR
        if (isShort) {
            flags = flags or DateUtils.FORMAT_ABBREV_MONTH
        }
        var formatDateTime = DateUtils.formatDateTime(context, calendar.timeInMillis, flags)

        formatDateTime = formatDateTime[0].toUpperCase() + formatDateTime.substring(1)
        return formatDateTime
    }

    /**
     * Strings
     **/

    //Сравнение похоже выглядящих строк английской и русской локали
    fun equalsIgnoreLocale(o1: String?, o2: String?): Boolean {
        if (o1 == null && o2 == null) {
            return true
        }
        if (o1 == null || o2 == null) {
            return false
        }
        if (o1.length != o2.length) {
            return false
        }
        val enChars = "еуорахсЕТУОРАНКХСВМ"
        val ruChars = "eyopaxcETYOPAHKXCBM"
        val enRuChars = enChars + ruChars
        val ruEnChars = ruChars + enChars
        val chars1 = o1.toCharArray()
        val chars2 = o2.toCharArray()
        for (i in chars1.indices) {
            val aChar1 = chars1[i]
            val aChar2 = chars2[i]
            if (aChar1 != aChar2) {
                val i1 = enRuChars.indexOf(aChar1)
                val i2 = ruEnChars.indexOf(aChar2)
                if (i1 < 0 || i2 < 0 || i1 != i2) {
                    return false
                }
            }
        }
        return true
    }

    //Оставляет от текства только числа
    fun getDigitText(s: String): String {
        return s.filter { it.isDigit() }
    }

    /**
     * Files
     **/

    //Получение расширения у имени файла
    fun getFileExt(fileName: String?): String {
        var extension = ""
        if(fileName.isNullOrEmpty()){
            return extension
        }
        val dot = fileName.lastIndexOf('.')
        val slash = max(fileName.lastIndexOf('/'), fileName.lastIndexOf('\\'))
        if (dot > slash && dot + 1 < fileName.length) {
            extension = fileName.substring(dot + 1)
            val maxExtLength = 5
            if (extension.length > maxExtLength) {
                extension = extension.substring(0, maxExtLength)
            }
        }
        return extension.toLowerCase()
    }

    //Удаление каталога со всем что есть внутри
    fun deleteDir(dir: File?) {
        if (dir == null) return
        val files = dir.listFiles()
        if (files != null)
            for (file in files) {
                if (file.isDirectory) {
                    deleteDir(file)
                } else {
                    file.delete()
                }
            }
        dir.delete()
    }

    /**
     * Views
     **/

    fun simpleTextWatcher(onTextChanged: Function1<String, Unit>?): TextWatcher {
        return object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

            override fun afterTextChanged(s: Editable) {
                onTextChanged?.invoke(s.toString())
            }
        }
    }

    //Обработчик клавиатуры для поля ввода, который запрещает ввод пробелов, табуляции и т.п
    fun filterSpaceTextWatcher(editText: EditText): TextWatcher {
        return simpleTextWatcher { s ->
            val result = s.replace("\\s".toRegex(), "")
            if (s != result) {
                val pos = editText.selectionStart - (s.length - result.length)
                editText.setText(result)
                editText.setSelection(max(0, min(pos, result.length)))
            }
        }
    }

    //Замостить фон у вью ресурсом
    fun tileBackground(ctx: Context, @DrawableRes resIdOfTile: Int, view: View) {
        try {
            val bmp = BitmapFactory.decodeResource(ctx.resources, resIdOfTile)
            val bitmapDrawable = BitmapDrawable(ctx.resources, bmp)
            bitmapDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT)
            setBackground(view, bitmapDrawable)
        } catch (e: Exception) {
            Log.e(TAG, "Exception while tiling the background of the view")
        }
    }

    /**
     * Other
     **/

    //Проверка на возможность использования отпечатка пальца на устройстве
    @SuppressLint("MissingPermission")
    fun isFingerprintAvailable(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val fingerprintManager = FingerprintManagerCompat.from(context)
            fingerprintManager.isHardwareDetected && fingerprintManager.hasEnrolledFingerprints()
        } else false
    }

    /**
     * Gets random color integer
     */
    @ColorInt
    fun getRandomColor(): Int {
        val random = Random()
        val red = random.nextInt(255)
        val green = random.nextInt(255)
        val blue = random.nextInt(255)

        return Color.argb(255, red, green, blue)
    }
}
