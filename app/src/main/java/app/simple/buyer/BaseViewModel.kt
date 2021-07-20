package app.simple.buyer

import android.app.Application
import androidx.lifecycle.*
import io.realm.Realm
import kotlinx.coroutines.*

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private lateinit var _realm: Realm
    val realm: Realm
        get() {
            if (!::_realm.isInitialized) {
                _realm = Realm.getDefaultInstance()
            }
            return _realm
        }

    // Do work in IO
    fun <P> doWork(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }

    // Do work in Main
    fun <P> doWorkInMainThread(doOnAsyncBlock: suspend CoroutineScope.() -> P) {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
                doOnAsyncBlock.invoke(this)
            }
        }
    }

    fun cancelJob() {
        viewModelScope.coroutineContext.cancelChildren()
    }

    override fun onCleared() {
        if (::_realm.isInitialized) {
            _realm.close()
        }
        super.onCleared()
    }

    val context get() = getApplication<Application>().applicationContext!!
}