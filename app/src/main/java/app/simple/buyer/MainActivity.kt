package app.simple.buyer


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import app.simple.buyer.base.OnBackPressedListener
import app.simple.buyer.util.Utils


class MainActivity : AppCompatActivity() {

    private val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        val th = if (model.isDarkThemeOn) R.style.AppThemeDark else R.style.AppThemeLight

        setTheme(th)
        Utils.themeStatusBar(window, Color.TRANSPARENT, !model.isDarkThemeOn, false)
        Utils.themeNavBar(window, Color.TRANSPARENT, !model.isDarkThemeOn, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        model.checkFirstInit()
        model.darkThemeChanged.observe(this) {
            recreate()
        }
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        Utils.themeStatusBar(window, Color.TRANSPARENT, !model.isDarkThemeOn, false)
        Utils.themeNavBar(window, Color.TRANSPARENT, !model.isDarkThemeOn, false)
        return super.onCreateView(name, context, attrs)
    }

    override fun onBackPressed() {
        supportFragmentManager
            .fragments
            .firstOrNull()
            ?.childFragmentManager
            ?.fragments
            ?.lastOrNull()
            ?.let {
                if (it is OnBackPressedListener && !it.onBackPressed()) {
                    return
                }
            }

        findNavController(R.id.fragment_main).let {
            if (!it.popBackStack()) finish()
        }
    }
}
