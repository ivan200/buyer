package app.simple.buyer.util

import androidx.annotation.MainThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.realm.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

//
// Created by Ivan200 on 26.11.2019.
//

//https://proandroiddev.com/the-realm-of-kotlin-and-live-data-using-mvp-architecture-f04fc41c914e
class RealmLiveData<T : RealmModel>(val realmResults: RealmResults<T>) : LiveData<RealmResults<T>>(), RealmChangeListener<RealmResults<T>> {
    override fun onChange(results: RealmResults<T>) {
        value = results
    }

    override fun onActive() = realmResults.addChangeListener(this)

    override fun onInactive() = realmResults.removeChangeListener(this)
}

class RealmObjectLiveData<T : RealmObject>(val realmObject: T) : LiveData<T?>(), RealmChangeListener<T> {
    override fun onActive() {
        if (!realmObject.isLoaded) {
            realmObject.addChangeListener(this)
        }
    }

    override fun onInactive() {
        if (realmObject.isLoaded) {
            realmObject.removeChangeListener(this)
        }
    }

    override fun onChange(result: T) {
        value = if (result.isLoaded && result.isValid) result else null
    }
}


class RealmResultsLiveData<M : RealmModel>(
    var realmResults: OrderedRealmCollection<M>?
) : LiveData<OrderedRealmCollection<M>>(), OrderedRealmCollectionChangeListener<OrderedRealmCollection<M>> {
    var active = false

    fun update(newResults: OrderedRealmCollection<M>?) {
        if (active) {
            realmResults?.removeListener(this)
            realmResults = newResults
            realmResults?.addListener(this)
        } else {
            realmResults = newResults
        }
        value = realmResults
    }

    override fun onActive() {
        active = true
        realmResults?.addListener(this)
    }

    override fun onInactive() {
        realmResults?.removeListener(this)
        active = false
    }

    override fun onChange(realmResults: OrderedRealmCollection<M>, changeSet: OrderedCollectionChangeSet) {
        value = realmResults
    }
}

class RealmObjectFieldLiveData<T : RealmObject>(
    var realmObject: T?,
    private vararg val fieldName: String
) : LiveData<T?>(), RealmObjectChangeListener<T> {
    var active = false

    fun update(newRealmObject: T) {
        if (active) {
            realmObject?.removeChangeListener(this)
            realmObject = newRealmObject
            realmObject?.addChangeListener(this)
        } else {
            realmObject = newRealmObject
        }
    }

    override fun onActive() {
        active = true
        realmObject?.addChangeListener(this)
    }

    override fun onInactive() {
        realmObject?.removeChangeListener(this)
        active = false
    }

    override fun onChange(result: T, changeSet: ObjectChangeSet?) {
        if (fieldName.any { changeSet?.isFieldChanged(it) == true }) { postValue(result) }
    }
}


class RealmObjectFieldSingleLiveEvent<T : RealmObject>(
    val realmObject: T,
    private vararg val fieldName: String
) : SingleLiveEvent<T?>(), RealmObjectChangeListener<T> {

    override fun onActive() = realmObject.addChangeListener(this)

    override fun onInactive() = realmObject.removeChangeListener(this)

    override fun onChange(result: T, changeSet: ObjectChangeSet?) {
        if (fieldName.any { changeSet?.isFieldChanged(it) == true }) { postValue(result) }
    }
}






class LiveRealmObject<T : RealmModel?> @MainThread constructor(obj: T?) : MutableLiveData<T>() {
    private val listener = RealmObjectChangeListener<T> { obj, objectChangeSet ->
        if (!objectChangeSet!!.isDeleted) {
            setValue(obj)
        } else { // Because invalidated objects are unsafe to set in LiveData, pass null instead.
            setValue(null)
        }
    }

    /**
     * Starts observing the RealmObject if we have observers and the object is still valid.
     */
    override fun onActive() {
        super.onActive()
        val obj = value
        if (obj != null && RealmObject.isValid(obj)) {
            RealmObject.addChangeListener(obj, listener)
        }
    }

    /**
     * Stops observing the RealmObject.
     */
    override fun onInactive() {
        super.onInactive()
        val obj = value
        if (obj != null && RealmObject.isValid(obj)) {
            RealmObject.removeChangeListener(obj, listener)
        }
    }

    var value: T? = obj
}


fun <T : RealmModel> RealmResults<T>.asLiveData() = RealmLiveData(this)

fun <T : RealmObject> T.asLiveData() = RealmObjectLiveData(this)


//https://gist.github.com/michaelbukachi/0c7f18bdeaf4bdb12b5527304d6cdbc0
suspend fun Realm.executeAsync(block: (Realm) -> Unit): Unit =
    suspendCancellableCoroutine { continuation ->
        this.executeTransactionAsync(
            { block(it) },
            { continuation.resume(Unit) },
            { continuation.resumeWithException(it) })
    }

suspend fun <S : RealmObject> RealmQuery<S>.await(): RealmResults<S> =
    suspendCancellableCoroutine { continuation ->
        this.findAllAsync().addChangeListener { t -> continuation.resume(t) }
    }

suspend fun <S : RealmObject> RealmQuery<S>.awaitFirst(): S? =
    suspendCancellableCoroutine { continuation ->
        this.findFirstAsync().addChangeListener { t: S? -> continuation.resume(t) }
    }


fun <T : RealmObject> RealmResults<out T>.copy(): List<T> {
    return this.realm.copyFromRealm(this)
}

fun <T : RealmObject> T.copy(): T {
    return this.realm.copyFromRealm(this)
}

inline fun <T> Iterable<T>.contains(function: (T) -> Boolean): Boolean {
    for (item in this) if (function(item)) return true
    return false
}

fun <T : RealmModel> T.update(realm: Realm) {
    realm.insertOrUpdate(this)
}

fun <T : RealmModel> RealmResults<T>.update() {
    this.realm.insertOrUpdate(this)
}

fun <T : RealmModel> Collection<T>.update(realm: Realm) {
    realm.insertOrUpdate(this)
}

//при обновлении ячейки, убираем слушатель со старого объекта, добавляем его на новый, и возвращаем новый для переинициаизации
fun <T : RealmObject> T?.reInitListener(obj: T?, listener: RealmChangeListener<T>): T? {
    this?.removeChangeListener(listener)
    obj?.addChangeListener(listener)
    return obj
}

fun <T : RealmObject> T?.reInitListenerNonNull(obj: T, listener: RealmChangeListener<T>): T {
    this?.removeChangeListener(listener)
    obj.addChangeListener(listener)
    return obj
}


fun <T : RealmModel> RealmResults<T>?.reInitListener(
    obj: RealmResults<T>?,
    listener: RealmChangeListener<RealmResults<T>>
): RealmResults<T>? {
    this?.removeChangeListener(listener)
    obj?.addChangeListener(listener)
    return obj
}

fun <T : RealmModel> RealmList<T>?.reInitListener(obj: RealmList<T>?, listener: RealmChangeListener<RealmList<T>>): RealmList<T>? {
    this?.removeChangeListener(listener)
    obj?.addChangeListener(listener)
    return obj
}

inline fun <reified T : RealmModel> Realm.getById(id: Long): T? {
    return this.where(T::class.java).equalTo("id", id).findFirst()
}

inline fun <reified T : RealmModel> Realm.getByIdAsync(id: Long): T {
    return this.where(T::class.java).equalTo("id", id).findFirstAsync()
}

inline fun <reified T : RealmModel> Realm.getListByIds(ids: Array<Long>): RealmResults<T> {
    return this.where(T::class.java).`in`("id", ids).findAll()
}

inline fun <reified T : RealmModel> Realm.getAll(): RealmResults<T> {
    return this.where(T::class.java).findAll()
}

inline fun <reified T : RealmModel> Realm.count(): Long {
    return this.where(T::class.java).count()
}

inline fun <reified T : RealmModel> Realm.getAllAsync(): RealmResults<T> {
    return this.where(T::class.java).findAllAsync()
}


@Suppress("UNCHECKED_CAST")
fun <T : RealmModel> OrderedRealmCollection<T>.addListener(
    listener: OrderedRealmCollectionChangeListener<OrderedRealmCollection<T>>
) = when (this) {
    is RealmResults<T> -> this.addChangeListener(listener as OrderedRealmCollectionChangeListener<RealmResults<T>>)
    is RealmList<T> -> this.addChangeListener(listener as OrderedRealmCollectionChangeListener<RealmList<T>>)
    else -> throw IllegalArgumentException("RealmCollection not supported: " + this.javaClass)
}

@Suppress("UNCHECKED_CAST")
fun <T : RealmModel> OrderedRealmCollection<T>.removeListener(
    listener: OrderedRealmCollectionChangeListener<OrderedRealmCollection<T>>
) = when (this) {
    is RealmResults<T> -> this.removeChangeListener(listener as OrderedRealmCollectionChangeListener<RealmResults<T>>)
    is RealmList<T> -> this.removeChangeListener(listener as OrderedRealmCollectionChangeListener<RealmList<T>>)
    else -> throw IllegalArgumentException("RealmCollection not supported: " + this.javaClass)
}
