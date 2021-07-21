package app.simple.buyer.interactor

import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.User
import app.simple.buyer.util.log
import app.simple.buyer.util.update
import io.realm.Realm

object UserInteractor {

    fun getUser(realm: Realm): User {
        return User.get(realm) ?: createUser(realm)
    }

    fun createUser(realm: Realm): User {
        val firstUser = User()
        realm.executeTransactionAsync {
            firstUser.update(it)
        }
        return firstUser
    }

    fun updateMainMenuState(realm: Realm, scrollState: ByteArray) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (!mainMenuScrollState.contentEquals(scrollState)) {
                mainMenuScrollState = scrollState
                update(it)
                log(scrollState.contentToString())
            }
        }
    }


    fun updateOrderType(realm: Realm, orderType: OrderType) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (listsOrderType != orderType.value) {
                listsOrderType = orderType.value
                update(it)
                log("OrderType: ${orderType.name}")
            }
        }
    }


    fun updateSortAscending(realm: Realm, ascending: Boolean) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (listsSortAscending != ascending) {
                listsSortAscending = ascending
                update(it)
                log("SortAscending: $ascending")
            }
        }
    }


    fun updateDarkTheme(realm: Realm, dark: Boolean) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (darkTheme != dark) {
                darkTheme = dark
                update(it)
                log("DarkTheme: $dark")
            }
        }
    }

}