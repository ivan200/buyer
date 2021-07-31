package app.simple.buyer.interactor

import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.entities.User
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import io.realm.Realm

object UserInteractor {

    fun getUser(realm: Realm): User {
        return User.get(realm) ?: createUser(realm)
    }

    fun createUser(realm: Realm): User {
        realm.executeTransactionAsync {
            User.new(it)
        }
        return User.getAsync(realm)
    }

    fun getUserSync(realm: Realm): User {
        return User.get(realm) ?: User.new(realm)
    }

    fun selectListAsync(realm: Realm, listId: Long) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (currentListId != listId) {
                currentListId = listId
                currentList = it.getById(listId)
                update(it)
            }
        }
    }

    fun updateMainMenuStateAsync(realm: Realm, scrollState: ByteArray){
        realm.executeTransactionAsync {
            getUser(it).apply {
                if (!mainMenuScrollState.contentEquals(scrollState)) {
                    mainMenuScrollState = scrollState
                    update(it)
                }
            }
        }
    }

    fun updateOrderTypeAsync(realm: Realm, orderType: OrderType) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (listsOrderType != orderType.value) {
                listsOrderType = orderType.value
                update(it)
            }
        }
    }


    fun updateSortAscendingAsync(realm: Realm, sortType: SortType) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (listsSortAscending != sortType.value) {
                listsSortAscending = sortType.value
                update(it)
            }
        }
    }


    fun updateDarkThemeAsync(realm: Realm, dark: Boolean) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (darkTheme != dark) {
                darkTheme = dark
                update(it)
            }
        }
    }

}