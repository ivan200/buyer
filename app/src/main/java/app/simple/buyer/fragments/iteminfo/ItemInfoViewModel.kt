package app.simple.buyer.fragments.iteminfo

import android.app.Application
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.ListItemInteractor
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.getById
import java.util.Date

/**
 * @author ivan200
 * @since 22.02.2022
 */
class ItemInfoViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val item = realm.getById<BuyListItem>(user.currentItemId)

    private var _itemComment = item?.comment ?: ""

    fun getTitle(): String? {
        return item?.buyItem?.name
    }

    private val _count = MutableLiveData(item?.count ?: BuyListItem.MIN_COUNT)
    val count: LiveData<Long> get() = _count

    val comment: String get() = _itemComment

    fun commentChanged(newComment: String){
        _itemComment = newComment
    }

    fun countOnTextChanged(newCount: Long){
        checkNewCount(newCount)
    }

    fun incrementCount(){
        checkNewCount(_count.value!! + 1)
    }

    fun decrementCount(){
        checkNewCount(_count.value!! - 1)
    }

    fun checkNewCount(newCount: Long) {
        if (newCount <= BuyListItem.MIN_COUNT) {
            _isDecrementButtonEnable.postValue(false)
        } else {
            _isDecrementButtonEnable.postValue(true)
        }
        if (newCount >= BuyListItem.MIN_COUNT) {
            _count.postValue(newCount)
        }
    }

    private val _isDecrementButtonEnable = MutableLiveData(_count.value!! > BuyListItem.MIN_COUNT)
    val isDecrementButtonEnable: LiveData<Boolean> get() = _isDecrementButtonEnable


    fun onBackPressed(){
        UserInteractor.deselectItemAsync(realm)
    }

    fun onPause(){
        item?.id?.let {
            ListItemInteractor.setItemInfo(realm, it, count.value!!, comment)
        }
    }

    fun getCreatedDate(): String? {
        return item?.created?.let { dateToString(it) }
    }
    fun getModifiedDate(): String? {
        return item?.modified?.let {
            if(it == item.created) null else dateToString(it)
        }
    }
    fun getBuyedDate(): String? {
        return item?.buyed?.let {
            if(it == item.modified || it == item.created) null else dateToString(it)
        }
    }

    fun dateToString(date: Date): String? {
        if(date.time <= 0) return null
        return DateUtils.getRelativeTimeSpanString(date.time, Date().time, 1, DateUtils.FORMAT_ABBREV_RELATIVE).toString()
    }

}
