package app.simple.buyer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.DBHelper
import app.simple.buyer.util.views.BindHolder
import app.simple.buyer.util.views.MultiCellObject
import app.simple.buyer.util.views.MultiCellTypeAdapter
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_add_item.*
import rx.functions.Func1
import java.util.*


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

//        val findAll = realm.where(BuyItem::class.java).findAll()


        val count = realm.where(BuyItem::class.java).count()


//        var equalTo = realm.where(BuyItem::class.java).equalTo("name", "ffdsfdsf").findAll()

        doneButton.setOnClickListener {
            val item = BuyItem()
            item.id = count + 1
            item.name = editText.text.toString()

            realm.beginTransaction()
            realm.copyToRealmOrUpdate(item)
            realm.commitTransaction()

            editText.setText("")
        }

        val listItems = ArrayList<MultiCellObject<*>>()
        if (count > 0) {
            listItems.add(MultiCellObject("test", Func1 { ViewHolderHeader(it) }, R.layout.cell_add_item))
        }


        val adapter = MultiCellTypeAdapter()
        recyclerList.adapter = adapter
        recyclerList.layoutManager = LinearLayoutManager(this)
        adapter.update(listItems)

        recyclerList.invalidate()
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

    private class ViewHolderHeader internal constructor(itemView: View) : BindHolder<String>(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)

        override fun bind(obj: String) {
            title.text = obj
        }
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
