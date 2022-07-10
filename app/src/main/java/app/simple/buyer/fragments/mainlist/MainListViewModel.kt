package app.simple.buyer.fragments.mainlist

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.entities.enums.ActionModeType
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


    private val _actionModeChange = SingleLiveEvent<ActionModeType>()
    val actionModeChange: LiveData<ActionModeType> get() = _actionModeChange

//    private val _actionModeStart = SingleLiveEvent<Unit>()
//    val actionModeStart: LiveData<Unit> get() = _actionModeStart
//
//    private val _actionModeStop = SingleLiveEvent<Unit>()
//    val actionModeStop: LiveData<Unit> get() = _actionModeStop

    private val _openItemInfo = SingleLiveEvent<Unit>()
    val openItemInfo: LiveData<Unit> get() = _openItemInfo

    private val _showEditIconInActionMode = MutableLiveData(true)
    val showEditIconInActionMode: LiveData<Boolean> get() = _showEditIconInActionMode


    private val _selectedCount = MutableLiveData(0L)
    val selectedCount: LiveData<Long> get() = _selectedCount

    init {
        if (user.currentItemId != 0L) {
            _openItemInfo.call()
        }
    }

    fun onCurrentItemIdChanged() {
        if (user.currentItemId != 0L) {
            _actionModeChange.postValue(ActionModeType.NO)
            _openItemInfo.call()
        }
    }

    fun getTitle(): String? {
        return realm.getById<BuyList>(user.currentListId)?.name
    }

    fun getItems(): OrderedRealmCollection<BuyListItem> {
        return BuyListItem.getAllOrderedAsync(realm, user)
    }

    fun onItemClick(itemId: Long, actionModeType: ActionModeType) {
        if (actionModeType != ActionModeType.NO) {
            operateSelected(itemId, actionModeType, true)
            ListItemInteractor.toggleActionAsync(realm, itemId){
                _selectedCount.postValue(BuyListItem.getSelectedCount(realm, user.currentListId))
            }
        } else {
            ListItemInteractor.toggleCheckItemAsync(realm, itemId)
        }
    }

    fun onItemLongClick(itemId: Long, actionModeType: ActionModeType) {
        if (actionModeType != ActionModeType.NO) {
            operateSelected(itemId, actionModeType, false)
            ListItemInteractor.selectRangeAsync(realm, itemId) {
                _selectedCount.postValue(BuyListItem.getSelectedCount(realm, user.currentListId))
            }
        } else {
            _actionModeChange.postValue(ActionModeType.SINGLE)
            _selectedCount.postValue(1L)
            _showEditIconInActionMode.postValue(true)
            ListItemInteractor.actionFirstItemAsync(realm, user.currentListId, itemId)
        }
    }
//    private fun operateAfterSelected() {
//        val selectedCount = BuyListItem.getSelectedCount(realm, user.currentListId)
//        val newActionMode = when(selectedCount){
//            0L -> ActionModeType.NO
//            1L -> ActionModeType.SINGLE
//            else -> ActionModeType.MULTI
//        }
//        if(_actionModeChange.value != newActionMode){
//            _actionModeChange.postValue(newActionMode)
//        }
//        _showEditIconInActionMode.postValue(newActionMode == ActionModeType.SINGLE)
//        _selectedCount.postValue(selectedCount)
//    }
    private fun operateSelected(itemId: Long, actionModeType: ActionModeType, click: Boolean) {
        val selectedCount = BuyListItem.getSelectedCount(realm, user.currentListId)
        val nextSelectedCount = if (selectedCount == 0L) {
            1L
        } else {
            val isItemAlreadySelected = realm.getById<BuyListItem>(itemId)?.isSelected == true
            if (isItemAlreadySelected) {
                if(click) selectedCount - 1 else selectedCount
            } else {
                selectedCount + 1
            }
        }
        _showEditIconInActionMode.postValue(nextSelectedCount == 1L)
        if(actionModeType == ActionModeType.SINGLE && nextSelectedCount == 0L){
            UserInteractor.selectItemAsync(realm, itemId)
        }
        if(nextSelectedCount == 0L){
            _actionModeChange.postValue(ActionModeType.NO)
        }
        if(nextSelectedCount > 1){
            _actionModeChange.postValue(ActionModeType.MULTI)
        }
        if (click) {
            _selectedCount.postValue(nextSelectedCount)
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
        ListItemInteractor.deleteSelectedAsync(realm, user.currentListId)
        _actionModeChange.postValue(ActionModeType.NO)
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
            _actionModeChange.postValue(ActionModeType.NO)
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
            if(it.count > 1){
                b.append(" ")
                b.append(it.count.toString())
            }
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