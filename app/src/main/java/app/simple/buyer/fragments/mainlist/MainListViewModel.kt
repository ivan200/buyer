package app.simple.buyer.fragments.mainlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.entities.enums.CheckedPosition
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.interactor.ListItemInteractor
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import app.simple.buyer.util.RealmObjectFieldSingleLiveEvent
import app.simple.buyer.util.SingleLiveEvent
import app.simple.buyer.util.getById
import io.realm.OrderedRealmCollection

class MainListViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)
    val listChanged = RealmObjectFieldLiveData(user, User.KEY_CURRENT_LIST_ID)

    val listOrderChanged = RealmObjectFieldLiveData(
        user,
        User.KEY_LIST_ITEMS_ORDER_TYPE,
        User.KEY_LIST_ITEMS_SORT_ASCENDING,
        User.KEY_LIST_ITEMS_CHECKED_POSITION,
        User.KEY_SHOW_CHECKED_ITEMS
    )

    val currentItemIdChanged = RealmObjectFieldSingleLiveEvent(
        user,
        User.KEY_CURRENT_ITEM_ID
    )

    var scrollState: ByteArray
        get() = BuyList.getScrollState(realm, user.currentListId)
        set(value) = ListsInteractor.updateListScrollStateAsync(realm, user.currentListId, value)

    private val _actionModeStart = SingleLiveEvent<Unit>()
    val actionModeStart: LiveData<Unit> get() = _actionModeStart

    private val _actionModeStop = SingleLiveEvent<Unit>()
    val actionModeStop: LiveData<Unit> get() = _actionModeStop

    private val _openItemInfo = SingleLiveEvent<Unit>()
    val openItemInfo: LiveData<Unit> get() = _openItemInfo

    private val _showEditIconInActionMode = MutableLiveData(true)
    val showEditIconInActionMode: LiveData<Boolean> get() = _showEditIconInActionMode

    init {
        if (user.currentItemId != 0L) {
            _openItemInfo.call()
        }
    }

    fun onCurrentItemIdChanged() {
        if (user.currentItemId != 0L) {
            _actionModeStop.call()
            _openItemInfo.call()
        }
    }

    fun getTitle(): String? {
        return realm.getById<BuyList>(user.currentListId)?.name
    }

    fun getItems(): OrderedRealmCollection<BuyListItem> {
        return BuyListItem.getAllOrderedAsync(realm, user)
    }

    fun onItemClick(itemId: Long, isActionMode: Boolean = false) {
        if (isActionMode) {
            checkShowEditIcon(itemId, true)
            ListItemInteractor.toggleActionAsync(realm, itemId)
        } else {
            ListItemInteractor.toggleCheckItemAsync(realm, itemId)
        }
    }

    fun checkShowEditIcon(itemId: Long, click: Boolean) {
        val selectedCount = BuyListItem.getSelectedCount(realm, user.currentListId)
        when {
            selectedCount == 0L -> {
                _showEditIconInActionMode.postValue(true)
            }
            selectedCount == 1L -> {
                val isItemAlreadySelected = realm.getById<BuyListItem>(itemId)?.isSelected == true
                if (isItemAlreadySelected) {
                    UserInteractor.selectItemAsync(realm, itemId)
                } else {
                    _showEditIconInActionMode.postValue(false)
                }
            }
            click && selectedCount == 2L && realm.getById<BuyListItem>(itemId)?.isSelected == true -> {
                _showEditIconInActionMode.postValue(true)
            }
        }
    }

    fun onItemLongClick(itemId: Long, isActionMode: Boolean = false) {
        if (isActionMode) {
            checkShowEditIcon(itemId, false)
            ListItemInteractor.selectRangeAsync(realm, itemId)
        } else {
            _actionModeStart.call()
            _showEditIconInActionMode.postValue(true)
            ListItemInteractor.actionFirstItemAsync(realm, user.currentListId, itemId)
        }
    }

    fun getItemsOrder() = user.itemsOrder
    fun getItemsSort() = user.itemsSort
    fun getItemsCheck() = user.itemsCheck
    fun getShowCheckedItems() = user.showCheckedItems

    fun updateListItems(order: OrderType) {
        if (user.itemsOrder == order) {
            val newSort = when (user.itemsSort) {
                SortType.ASCENDING -> SortType.DESCENDING
                SortType.DESCENDING -> SortType.ASCENDING
            }
            UserInteractor.updateListItemsAsync(realm, newSort)
        } else {
            UserInteractor.updateListItemsAsync(realm, order, SortType.ASCENDING)
        }
    }

    fun toggleCheckedItems() {
        var showChecked = user.showCheckedItems
        showChecked = !showChecked
        UserInteractor.updateShowCheckedAsync(realm, showChecked)
    }

    fun updateListItems(checkedPosition: CheckedPosition) {
        UserInteractor.updateListItemsAsync(realm, checkedPosition)
    }

    fun deleteItem() {
        //TODO Докрутить удаление

//        ListItemInteractor.deleteAsync(realm, actionItemId)
        _actionModeStop.call()
    }

    fun onExitFromActionMode() {
        ListItemInteractor.deSelectAllAsync(realm, user.currentListId)
    }

    fun editItem() {
        val selectedItemId = BuyListItem.getSelectedItemId(realm, user.currentListId)
        if (selectedItemId != null) {
            UserInteractor.selectItemAsync(realm, selectedItemId)
        }
    }

    fun doneItems() {
        ListItemInteractor.doneSelectedAsync(realm, user.currentListId)
        if (!user.showCheckedItems) {
            _actionModeStop.call()
        }
    }

    fun getSelected(): String {
        val itemsList = BuyListItem.getAllOrderedSelected(realm, user)
        val list: MutableList<String> = mutableListOf()

        itemsList.forEach {
//          val checkSymbol = if(it.isBuyed) "\uD83D\uDDF8" else "\u2610"
            val checkSymbol = if (it.isBuyed) "+" else "-"
            val b = StringBuilder()
            b.append(checkSymbol)
            b.append(" ")
            b.append(it.buyItem?.name)
            val comment = it.comment
            if (!comment.isNullOrBlank()) {
                b.append("\n")
                b.append(comment)
            }
            list.add(b.toString())
        }
        return list.joinToString("\n")
    }
}