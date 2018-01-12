package app.simple.buyer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.DBHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_item.*





/**
 * Created by Zakharovi on 12.01.2018.
 */
class AddItemActivity : AppCompatActivity() {
    companion object {
        const val ActivityCode = 123
    }

    val realm: Realm = DBHelper.getRealm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_item)
        setSupportActionBar(toolbar)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        var findAll = realm.where(BuyItem::class.java).findAll()

        doneButton.setOnClickListener {
            var item = BuyItem()
            item.id = findAll.count().toLong() + 1
            item.name = editText.text.toString()

            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()

            editText.setText("")
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            overridePendingTransition(0, 0)
            return true
        }

        return super.onOptionsItemSelected(item)
    }





//
//    override fun onMenuItemSelected(featureId: Int, item: MenuItem): Boolean {
//
//        val itemId = item.itemId
//        when (itemId) {
//            android.R.id.home -> toggle()
//        }// Toast.makeText(this, "home pressed", Toast.LENGTH_LONG).show();
//
//        return true
//    }

//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        when (item.itemId) {
//            R.id.homeAsUp -> {
//                val intent = this.intent
////                intent.putExtra("SOMETHING", "EXTRAS")
//                this.setResult(Activity.RESULT_OK, intent)
//                finish()
//            }
//        }
//        return super.onOptionsItemSelected(item)
//    }


}
