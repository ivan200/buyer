package app.simple.buyer

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import app.simple.buyer.util.database.Database
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.DialogHelper


abstract class BaseActivity : AppCompatActivity() {
    abstract val layoutId: Int

    val database = Database()
    private var onResumeHandler: Function0<Unit>? = null

    override fun setTitle(title: CharSequence) {
        super.setTitle(title)
        supportActionBar?.title = title
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(if(Prefs(this).darkTheme) R.style.AppThemeDark_Translucent else R.style.AppThemeLight_Translucent)
        super.onCreate(savedInstanceState)

        setContentView(layoutId)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //using styles.xml(v26) with it
            if(Prefs(this).darkTheme){
                window.decorView.systemUiVisibility = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
            } else{
                window.decorView.systemUiVisibility = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
            }
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

    override fun onDestroy() {
        database.close()
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
