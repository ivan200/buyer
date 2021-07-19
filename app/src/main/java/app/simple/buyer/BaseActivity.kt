package app.simple.buyer

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import app.simple.buyer.util.Utils
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.DialogHelper
import io.realm.Realm


abstract class BaseActivity(val layoutId: Int) : AppCompatActivity() {

    val realm: Realm = Realm.getDefaultInstance()
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

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Utils.themeStatusBar(window, Color.TRANSPARENT, !Prefs(this).darkTheme, false )
        Utils.themeNavBar(window, Color.TRANSPARENT, !Prefs(this).darkTheme, false )
        return super.onCreateView(name, context, attrs)
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
