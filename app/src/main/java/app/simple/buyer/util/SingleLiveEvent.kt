package app.simple.buyer.util

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import java.util.concurrent.atomic.AtomicBoolean

class SingleLiveEvent<T> : MutableLiveData<T> {

    constructor() : super()
    constructor(value: T) : super(value)

    private val handled = AtomicBoolean(false)
    val isHandled: Boolean get() = handled.get()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        // Observe the internal MutableLiveData
        super.observe(owner) { t ->
            if (handled.compareAndSet(false, true)) {
                observer.onChanged(t)
            }
        }
    }

    override fun setValue(t: T?) {
        handled.set(false)
        super.setValue(t)
    }

    /**
     * Used for cases where T is Void, to make calls cleaner.
     */
    fun call() {
        value = null
    }
}