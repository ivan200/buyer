@file:Suppress("DEPRECATION")

package app.simple.buyer.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrixColorFilter
import android.graphics.PorterDuff
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.graphics.drawable.VectorDrawable
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import android.util.DisplayMetrics
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ProgressBar
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.hardware.fingerprint.FingerprintManagerCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import app.simple.buyer.BuildConfig
import app.simple.buyer.R
import java.util.Random
import kotlin.math.max
import kotlin.math.min

@Suppress("unused", "MemberVisibilityCanBePrivate")
object Utils {
    /**
     * Keyboard
     **/

    fun hasNoHardwareKeyboard(view: View) = view.context.resources.configuration.keyboard == Configuration.KEYBOARD_NOKEYS

    fun hideKeyboard(activity: Activity) {
        val focusedView = activity.currentFocus
        if (focusedView != null) {
            val inputMethodManager = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(focusedView.windowToken, InputMethodManager.RESULT_UNCHANGED_SHOWN)
        }
    }

    fun hideKeyboard2(activity: Activity, editText: EditText) {
        editText.clearFocus()
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

    fun showKeyBoard3(editText: View?) {
        if (editText != null && editText.requestFocus()) {
            val inputMethodManager = ContextCompat.getSystemService(editText.context, InputMethodManager::class.java)
            inputMethodManager?.showSoftInput(editText, InputMethodManager.SHOW_FORCED)
        }
    }

    /**
     * Resources
     **/
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
            0f, 0f, 0f, 1f, 0f
        )

        val mFilter = ColorMatrixColorFilter(matrix)
        drawable.colorFilter = mFilter
    }

    //Перекрашивание цвета прогрессбара на api<21
    fun tintIndeterminateProgress(
        progress: ProgressBar,
        @ColorInt color: Int = ContextCompat.getColor(progress.context, R.color.colorPrimary)
    ) {
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

    fun tintHorizontalProgress(
        progress: ProgressBar,
        @ColorInt color: Int = ContextCompat.getColor(progress.context, R.color.colorPrimary)
    ) {
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
            appName = packageManager.getApplicationLabel(
                packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            ).toString()
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
     * Other
     **/
    //Проверка на возможность использования отпечатка пальца на устройстве
    @SuppressLint("MissingPermission")
    fun isFingerprintAvailable(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
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

    private fun resolveTransparentStatusBarFlag(flag: String): Int {
        try {
            val field = View::class.java.getField(flag)
            if (field.type == Integer.TYPE) return field.getInt(null)
        } catch (ignored: java.lang.Exception) {
        }
        return 0
    }

    fun setWindowFlag(win: Window, bits: Int, state: Boolean) {
        val flags = win.attributes.flags
        win.attributes.flags = if (state) flags or bits else flags and bits.inv()
    }

    fun setDecorFlag(win: Window, bits: Int, state: Boolean) {
        val flags = win.decorView.systemUiVisibility
        win.decorView.systemUiVisibility = if (state) flags or bits else flags and bits.inv()
    }

    fun transparentSystemBars(
        window: Window,
        transparentStatus: Boolean = true,
        transparentNavigation: Boolean = true,
        statusLightTheme: Boolean = false,
        navigationLightTheme: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        }
        var windowFlag = 0
        if (transparentStatus) windowFlag = windowFlag or WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        if (transparentNavigation) windowFlag = windowFlag or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION

        var decorFlag = 0
        if (transparentStatus) decorFlag = decorFlag or View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        if (transparentNavigation) decorFlag = decorFlag or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION

        if (statusLightTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            decorFlag = decorFlag or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        if (navigationLightTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            decorFlag = decorFlag or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(window, windowFlag, false)
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        } else {
            setWindowFlag(window, windowFlag, true)
        }
        window.decorView.systemUiVisibility = decorFlag
    }


    fun transparentStatus(window: Window, statusLightTheme: Boolean = false, drawSystemBar: Boolean = false) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, true)

            setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, drawSystemBar)
            if (!drawSystemBar) {
                window.statusBarColor = Color.TRANSPARENT
            }
        } else {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
        }

        setDecorFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_STABLE, true)
        setDecorFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, true)
        if (!drawSystemBar && statusLightTheme && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            setDecorFlag(window, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, statusLightTheme)
        }
    }

    fun themeStatusBar(window: Window, @ColorInt color: Int, statusLightTheme: Boolean = false, drawSystemBar: Boolean = false) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, true)
        }

        if (color == Color.TRANSPARENT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, drawSystemBar)
                if (!drawSystemBar) {
                    window.statusBarColor = Color.TRANSPARENT
                }
            } else {
                setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true)
            }
            setDecorFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_STABLE, true)
            setDecorFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN, true)
            if (!drawSystemBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                setDecorFlag(window, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, statusLightTheme)
            }
        } else {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.statusBarColor = color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    setDecorFlag(window, View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR, statusLightTheme)
                }
            }
        }
    }

    fun themeNavBar(
        window: Window,
        @ColorInt color: Int,
        navigationLightTheme: Boolean = false,
        drawSystemBar: Boolean = false
    ) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS, true)
        }

        if (color == Color.TRANSPARENT) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, drawSystemBar)
                if (!drawSystemBar) {
                    window.navigationBarColor = Color.TRANSPARENT
                }
            } else {
                setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, true)
            }

            setDecorFlag(window, View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION, true)
            if (!drawSystemBar && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setDecorFlag(window, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, navigationLightTheme)
            }
        } else {
            setWindowFlag(window, WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION, false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.navigationBarColor = color
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    setDecorFlag(window, View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR, navigationLightTheme)
                }
            }
        }
    }


    fun getColorBrightness(@ColorInt color: Int): Double {
        val outLab = DoubleArray(3)
        ColorUtils.colorToLAB(color, outLab)
        return outLab[0]
    }

    fun isColorBright(@ColorInt color: Int) = getColorBrightness(color) > 50

    /**
     * Получить приватное поле из объекта
     * @param fieldName Название поля
     * @param clazz - класс, к которому этот объект относится.
     * Так как объекты наследники абстрактных классов могут в разных местах содержать поля,
     * то надо обязательно понимать, в каком из классов находится поле
     */
    inline fun <reified O> O.getPrivateFieldOrNull(fieldName: String, clazz: Class<*>? = null): Any? = try {
        (clazz ?: this!!::class.java).getDeclaredField(fieldName).let {
            it.isAccessible = true
            it.get(this)
        }
    } catch (t: Throwable) {
        null
    }
}
