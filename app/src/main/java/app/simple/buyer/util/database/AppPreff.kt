package app.simple.buyer.util.database

import android.content.Context
import android.content.SharedPreferences
import app.simple.buyer.BuyerApp
import app.simple.buyer.entities.OrderType
import io.realm.Sort

/**
 * Created by Zakharovi on 30.01.2018.
 */

object AppPreff {
    private const val APP_PREFFERENCES = "global_settings"

    var listsOrderType: Int
        get() {
            return getPreff()?.getInt("listsOrderType", OrderType.CREATED) ?: OrderType.CREATED
        }
        set(value) {
            getEditor()?.putInt("listsOrderType", value)?.apply()
        }

    var listsSortType: Sort
        get() {
            val s = getPreff()?.getBoolean("listsSortType", true) ?: true
            return if(s) Sort.ASCENDING else Sort.DESCENDING
        }
        set(value) {
            getEditor()?.putBoolean("listsSortType", value.value)?.apply()
        }



    var token: String?
        get() {
            return getPreff()?.getString("token", null)
        }
        set(value) {
            getEditor()?.putString("token", value)?.apply()
        }

    private fun getPreff(): SharedPreferences? {
        return BuyerApp.appContext?.getSharedPreferences(APP_PREFFERENCES, Context.MODE_PRIVATE)
    }

    private fun getEditor(): SharedPreferences.Editor? {
        return getPreff()?.edit()
    }
}
