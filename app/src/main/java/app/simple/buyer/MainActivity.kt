package app.simple.buyer


import android.os.Bundle
import androidx.navigation.findNavController


class MainActivity : BaseActivity() {

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        ViewCompat.setOnApplyWindowInsetsListener(section_wrapper_main) { _, insets ->
//            onInsetsChanged()
//            insets.consumeSystemWindowInsets()
//        }
    }

//    fun onInsetsChanged(){
//        logger.log(Level.FINE, "changed")
//    }

    override fun onBackPressed() {
        findNavController(R.id.fragment_main).let { if (!it.popBackStack()) finish() }
    }
}
