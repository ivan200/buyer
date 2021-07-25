@file:Suppress("unused")

package app.simple.buyer.util

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Handler
import android.os.Looper
import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator
import android.view.LayoutInflater
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.IntegerRes
import androidx.collection.LongSparseArray
import androidx.collection.SparseArrayCompat
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.collections.HashMap


//
//Logger
//
inline val <T : Any> T.TAG: String get() = this::class.java.simpleName
inline val <T : Any> T.logger: Logger get() = Logger.getLogger(this.TAG)
inline fun <T : Any> T.log(value: Any) = logger.log(Level.INFO, value.toString())

//
//Collections
//
fun <T: Number> T?.orElse(number: T) =
        if (this != null && this != 0) this else number

fun <T> T?.oneOf(vararg elements: T): Boolean {
    return elements.contains(this)
}

inline fun <T> Iterable<T>.splitBy(compute: Function1<T, Boolean>): Pair<List<T>, List<T>> {
    val group = this.groupBy(compute)
    return Pair(group.getValue(true), group.getValue(false))
}


fun <K,V> Map<out K,V>?.firstValueOrNull(): V? {
    if (this == null || this.isEmpty()) return null
    return this[this.keys.first()]
}

inline fun <K, V> Map<out K, V>.firstValueOrNull(predicate: (V) -> Boolean): V? {
    for (entry in this) {
        if (predicate(entry.value)) {
            return entry.value
        }
    }
    return null
}

inline fun <T> Iterable<T>.toLongSparseArrayGrouped(getKey: Function1<T, Long?>): LongSparseArray<MutableList<T>> {
    val array = LongSparseArray<MutableList<T>>()
    for (item in this) {
        val key: Long = getKey(item) ?: 0L
        var vList = array.get(key)
        if (vList == null) {
            vList = ArrayList()
            array.put(key, vList)
        }
        vList.add(item)
    }
    return array
}

inline fun <T> Iterable<T>.toLongSparseArray(getKey: Function1<T, Long?>): LongSparseArray<T> {
    val array = LongSparseArray<T>()
    for (item in this) {
        array.append(getKey(item) ?: 0L, item)
    }
    return array
}
inline fun <T> Iterable<T>.toSparseArray(getKey: Function1<T, Int?>): SparseArrayCompat<T> {
    val array = SparseArrayCompat<T>()
    for (item in this) {
        array.append(getKey(item) ?: 0, item)
    }
    return array
}

inline fun <T,K,V> Iterable<T>.toHashMap(getKey: Function1<T, K>, getValue: Function1<T, V>): HashMap<K, V> {
    val map = HashMap<K,V>()
    for (item in this) {
        map[getKey(item)] = getValue(item)
    }
    return map
}
inline fun <T,K,V> Iterable<T>.toHashMapNullable(getKey: Function1<T, K>, getValue: Function1<T, V?>): HashMap<K, V?> {
    val map = HashMap<K,V?>()
    for (item in this) {
        map[getKey(item)] = getValue(item)
    }
    return map
}

inline fun <reified T> List<T>.asMutableList(): MutableList<T> {
    return if(this is MutableCollection<*>) this as MutableList<T> else this.toMutableList()
}

fun <T> LongSparseArray<T>.getKeys(): List<Long> {
    val result = ArrayList<Long>()
    for (i in 0 until this.size()) {
        result.add(this.keyAt(i))
    }
    return result
}

//
//Views
//

/**
 * Show the view  (visibility = View.VISIBLE)
 */
fun <T:View> T.show() : T {
    if (visibility != View.VISIBLE) visibility = View.VISIBLE
    return this
}

fun <T : View> T.hide(): T {
    if (visibility != View.GONE) visibility = View.GONE
    return this
}

fun <T : View> T.invisible() : T {
    if (visibility != View.INVISIBLE) visibility = View.INVISIBLE
    return this
}

fun <T : View> T.showIf(condition: () -> Boolean): T {
    return if (condition()) show() else hide()
}

fun <T : View> T.hideIf(condition: () -> Boolean): T {
    return if (condition()) hide() else show()
}

fun <T : View> T.invisibleIf(condition: () -> Boolean): T {
    return if (condition()) invisible() else show()
}

fun <T : View> T.onClick(function: () -> Unit): T {
    setOnClickListener { function() }
    return this
}

fun <T : CompoundButton> T.onCheckedChanged(function: (CompoundButton, Boolean) -> Unit): T {
    setOnCheckedChangeListener(function)
    return this
}


/**
 * Extension method to get a view as bitmap.
 */
fun View.getBitmap(): Bitmap {
    val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bmp)
    draw(canvas)
    canvas.save()
    return bmp
}

fun ImageView.tint(@ColorRes colorId: Int, context: Context? = null) {
    ImageViewCompat.setImageTintList(this, ColorStateList.valueOf(ContextCompat.getColor(context ?: this.context, colorId)))
}

/**
 * Extension method to get LayoutInflater
 */
inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

/**
 * Extension methods to Get resources for Context.
 */

fun Context.getInteger(@IntegerRes id: Int) = resources.getInteger(id)
fun Context.getDimensionPx(@DimenRes id: Int) = resources.getDimension(id)
fun Context.getDimensionDp(@DimenRes id: Int) = resources.getDimension(id) / resources.displayMetrics.density


/**
 * Extension method to check String equalsIgnoreCase
 */
fun String.equalsIgnoreCase(other: String) = this.toLowerCase(Locale.getDefault()).contentEquals(other.toLowerCase(Locale.getDefault()))

fun String.remove(substring: String) = replace(substring, "")

/**
 * Extension method to run block of code on UI Thread.
 */
fun runOnUiThread(action: () -> Unit){
    if (Looper.getMainLooper().thread == Thread.currentThread()) action() else Handler(Looper.getMainLooper()).post { action() }
}


fun Parcelable?.toByteArray(): ByteArray? {
    if(this == null) return null
    val parcel = Parcel.obtain()
    this.writeToParcel(parcel, 0)
    val bytes = parcel.marshall()
    parcel.recycle() // not sure if needed or a good idea
    return bytes
}

fun <T : Parcelable> ByteArray.toParcelable(creator: Creator<T>): T {
    val parcel = Parcel.obtain()
    parcel.unmarshall(this, 0, this.size)
    parcel.setDataPosition(0) // this is extremely important!
    return creator.createFromParcel(parcel)
}

val RecyclerView.savedState: ByteArray
    get() = (layoutManager as LinearLayoutManager).onSaveInstanceState().toByteArray() ?: ByteArray(0)

val ByteArray.asScrollState: LinearLayoutManager.SavedState
    get() = this.toParcelable(LinearLayoutManager.SavedState.CREATOR)
