package app.simple.buyer.util

import android.content.Context
import android.content.SharedPreferences
import app.simple.buyer.BuyerApp

/**
 * Created by Zakharovi on 30.01.2018.
 */

object AppPreff {
    private const val APP_PREFFERENCES = "global_settings"

    var listsOrderType: Int
        get() {
            return getPreff()?.getInt("listsOrderType", 0) ?: 0
        }
        set(value) {
            getEditor()?.putInt("listsOrderType", value)?.apply()
        }

    var listsSortType: Int
        get() {
            return getPreff()?.getInt("listsSortType", 0) ?: 0
        }
        set(value) {
            getEditor()?.putInt("listsSortType", value)?.apply()
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
