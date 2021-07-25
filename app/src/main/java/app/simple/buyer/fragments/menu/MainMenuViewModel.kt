package app.simple.buyer.fragments.menu

import android.app.Application
import app.simple.buyer.BaseViewModel
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.User
import app.simple.buyer.interactor.UserInteractor
import app.simple.buyer.util.RealmObjectFieldLiveData
import io.realm.OrderedRealmCollection

class MainMenuViewModel(application: Application) : BaseViewModel(application) {
    private val user: User = UserInteractor.getUser(realm)

    val currentListId = RealmObjectFieldLiveData(user, User::currentListId.name)
    val listsOrderChanged = RealmObjectFieldLiveData(user, User::listsOrderType.name, User::listsSortAscending.name)

    var mainMenuState: ByteArray
        get() = user.mainMenuScrollState
        set(value) = UserInteractor.updateMainMenuStateAsync(realm, value)

    fun toggleDarkTheme() {
        UserInteractor.updateDarkThemeAsync(realm, !user.darkTheme)
    }

    fun getItems(): OrderedRealmCollection<BuyList> {
        return BuyList.getAllOrderedAsync(realm, user.order, user.sort)
    }

    fun onMenuItemSelected(itemId: Long) {
        UserInteractor.selectListAsync(realm, itemId)
    }
}