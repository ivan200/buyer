package app.simple.buyer


import android.os.Bundle
import androidx.navigation.findNavController
import app.simple.buyer.util.database.Database


class MainActivity : BaseActivity(R.layout.activity_main) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            Database.firstInit(this, realm)
        } catch (e: Throwable) {
            showError(e)
        }
    }

    override fun onBackPressed() {
        findNavController(R.id.fragment_main).let {
            if (!it.popBackStack()) finish()
        }
    }
}
