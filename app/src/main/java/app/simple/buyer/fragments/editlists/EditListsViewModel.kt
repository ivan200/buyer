package app.simple.buyer.fragments.editlists

import android.app.Application
import androidx.lifecycle.LiveData
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.LiveRealmObject
import app.simple.buyer.util.SingleLiveEvent

class EditListsViewModel(application: Application) : BaseViewModel(application) {
    private val user = UserInteractor.getUser(realm)

    private val _error = SingleLiveEvent<Exception>()
    val error: LiveData<Exception> get() = _error

    fun saveMainMenuState(scrollState: ByteArray) {
        try {
            UserInteractor.updateMainMenuState(realm, scrollState)
        } catch (ex: Exception) {
            ex.printStackTrace()
            _error.postValue(ex)
        }
    }

    fun getMainMenuState(): ByteArray {
        return user.mainMenuScrollState
    }


    fun onOrderSelected(order: OrderType){
        user.listsOrderType
//        BuyList.orderBy(realm, requireContext(), OrderType.ALPHABET, sortType)
    }
}