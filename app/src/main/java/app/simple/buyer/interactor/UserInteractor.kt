package app.simple.buyer.interactor

import app.simple.buyer.entities.User
import app.simple.buyer.util.logger
import app.simple.buyer.util.update
import io.realm.Realm
import java.util.logging.Level

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

    fun updateMainMenuState(realm: Realm, scrollState: ByteArray) {
        realm.executeTransactionAsync {
            val user = getUser(it)
            user.apply {
                if (!mainMenuState.contentEquals(scrollState)) {
                    mainMenuState = scrollState
                    update(it)
                    logger.log(Level.INFO, scrollState.contentToString())
                }
            }
        }
    }
}