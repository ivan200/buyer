package app.simple.buyer.fragments.editlists

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import app.simple.buyer.base.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.interactor.ListsInteractor
import app.simple.buyer.interactor.UserInteractor

/**
 * @author ivan200
 * @since 22.07.2021
 */
class AddListViewModel(application: Application) : BaseViewModel(application) {

    private var currentNewListName = ""

    private val user = UserInteractor.getUser(realm)

    val isDarkThemeOn get() = user.darkTheme

    private val _listAlreadyExist = MutableLiveData(false)
    val listAlreadyExist: LiveData<Boolean> get() = _listAlreadyExist

    private val _okEnabled = MutableLiveData(false)
    val okEnabled: LiveData<Boolean> get() = _okEnabled

    fun onInputTextChanged(result: String){
        currentNewListName = result.trim()
        if(result.isEmpty()){
            _okEnabled.postValue(false)
            _listAlreadyExist.postValue(false)
        } else {
            val byName = BuyList.getByName(realm, currentNewListName)
            if(byName == null){
                _okEnabled.postValue(true)
                _listAlreadyExist.postValue(false)
            } else{
                _listAlreadyExist.postValue(true)
                _okEnabled.postValue(false)
            }
        }
    }

    fun onAddNewList(){
        if(currentNewListName.isNotEmpty()) {
            ListsInteractor.createList(realm, currentNewListName)
            currentNewListName = ""
        }
    }
}