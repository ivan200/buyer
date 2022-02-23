package app.simple.buyer.interactor

import app.simple.buyer.entities.User
import app.simple.buyer.entities.enums.CheckedPosition
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.util.getById
import app.simple.buyer.util.update
import app.simple.buyer.util.updateRealmObjectFieldAsync
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

    private fun Realm.updateUserField(condition: User.() -> Boolean, block: User.() -> Unit) =
        updateRealmObjectFieldAsync({ getUser(it) }, condition, block)

    fun selectListAsync(realm: Realm, listId: Long) = realm.executeTransactionAsync {
        getUser(it).apply {
            if (currentListId != listId) {
                currentListId = listId
                currentList = it.getById(listId)
                update(it)
            }
        }
    }

    fun selectItemAsync(realm: Realm, listItemId: Long) = realm.updateUserField(
        { currentItemId != listItemId },
        { currentItemId = listItemId }
    )

    fun selectItem(realm: Realm, listItemId: Long) {
        val user = getUser(realm)
        if(user.currentItemId != listItemId){
            user.currentItemId = listItemId
            user.update(realm)
        }
    }

    fun deselectItemAsync(realm: Realm) = realm.updateUserField(
        { currentItemId != 0L },
        { currentItemId = 0L }
    )



    fun updateMainMenuStateAsync(realm: Realm, scrollState: ByteArray) = realm.updateUserField(
        { !mainMenuScrollState.contentEquals(scrollState) },
        { mainMenuScrollState = scrollState }
    )

    fun updateOrderTypeAsync(realm: Realm, orderType: OrderType) = realm.updateUserField(
        { listsOrderType != orderType.value },
        { listsOrderType = orderType.value }
    )


    fun updateSortAscendingAsync(realm: Realm, sortType: SortType) = realm.updateUserField(
        { listsSortAscending != sortType.value },
        { listsSortAscending = sortType.value }
    )


    fun updateDarkThemeAsync(realm: Realm, dark: Boolean) = realm.updateUserField(
        { darkTheme != dark },
        { darkTheme = dark }
    )

    fun updateListItemsAsync(realm: Realm, orderType: OrderType, sortType: SortType) = realm.updateUserField(
        { listItemsOrderType != orderType.value || listItemsSortAscending != sortType.value },
        {
            listItemsOrderType = orderType.value
            listItemsSortAscending = sortType.value
        }
    )

    fun updateListItemsAsync(realm: Realm, sortType: SortType) = realm.updateUserField(
        { listItemsSortAscending != sortType.value },
        { listItemsSortAscending = sortType.value }
    )


    fun updateListItemsAsync(realm: Realm, checkedPosition: CheckedPosition) = realm.updateUserField(
        { listItemsCheckedPosition != checkedPosition.value },
        { listItemsCheckedPosition = checkedPosition.value }
    )

    fun updateShowCheckedAsync(realm: Realm, showChecked: Boolean) = realm.updateUserField(
        { showCheckedItems != showChecked },
        { showCheckedItems = showChecked }
    )
}