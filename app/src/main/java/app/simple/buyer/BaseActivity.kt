package app.simple.buyer

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import app.simple.buyer.util.views.DialogHelper
import io.realm.Realm


abstract class BaseActivity : AppCompatActivity() {
    abstract val layoutId: Int

    val realm: Realm = Realm.getDefaultInstance()

    var onResumeHandler: Function0<Unit>? = null

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(layoutId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //using styles.xml(v26) with it
            window.decorView.systemUiVisibility = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
        }
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

    open fun showException(throwable: Throwable) {
        DialogHelper(this).withThrowable(throwable).show()
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
