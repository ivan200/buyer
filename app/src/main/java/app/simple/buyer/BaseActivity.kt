package app.simple.buyer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.DialogHelper
import io.realm.Realm


abstract class BaseActivity : AppCompatActivity() {
    abstract val layoutId: Int

    var realm: Realm = Realm.getDefaultInstance()
    private var onResumeHandler: Function0<Unit>? = null

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if(Prefs(this).darkTheme) R.style.AppThemeDark_Translucent else R.style.AppThemeLight_Translucent)
        super.onCreate(savedInstanceState)
        setContentView(layoutId)
    }

    override fun onResume() {
        super.onResume()

        onResumeHandler?.invoke()
        onResumeHandler = null
    }

    fun tryResumeAction(resumeHandler: Function0<Unit>) {
        if (lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED)) {
            resumeHandler.invoke()
        } else {
            onResumeHandler = resumeHandler
        }
    }

    override fun onDestroy() {
        realm.close()
        super.onDestroy()
    }

    fun showError(throwable: Throwable) {
        tryResumeAction {
            runOnUiThread {
                DialogHelper(this).withThrowable(throwable).show()
            }
        }
    }
}
