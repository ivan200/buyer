package app.simple.buyer

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.AddItemRecyclerViewAdapter
import app.simple.buyer.util.database.DBHelper
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

//        val findAll = realm.where(BuyItem::class.java).findAll()


        val count = realm.where(BuyItem::class.java).count()


//        var equalTo = realm.where(BuyItem::class.java).equalTo("name", "ffdsfdsf").findAll()

        doneButton.setOnClickListener {
            addItem(editText.text.toString())
            onBackPressed()
            overridePendingTransition(0, 0)
        }


        editText.setOnEditorActionListener { textView, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                addItem(textView.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

        recyclerList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING){
                    if(editText.hasFocus()) {
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.hideSoftInputFromWindow(editText!!.windowToken, 0)
                    }
                }
            }
        })

//        val listItems = ArrayList<MultiCellObject<*>>()
//        if (count > 0) {
//            listItems.add(MultiCellObject("test", Func1 { ViewHolderHeader(it) }, R.layout.cell_add_item))
//        }
//        val adapter = MultiCellTypeAdapter()

        val adapter = AddItemRecyclerViewAdapter(realm.where(BuyItem::class.java).findAll())

        recyclerList.adapter = adapter
        recyclerList.layoutManager = LinearLayoutManager(this)
//        adapter.update(listItems)

        recyclerList.invalidate()
    }

    fun addItem(name: String?) {

        var item = realm.where(BuyItem::class.java).equalTo("name", name).findFirst()

        realm.beginTransaction()
        if (item == null) {
            item = BuyItem()
            item.id = realm.where(BuyItem::class.java).count() + 1
            item.name = name
        } else {
            item.populatity =+ 1
        }
        realm.copyToRealmOrUpdate(item)
        realm.commitTransaction()

        editText.setText("")
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

//    private class ViewHolderHeader internal constructor(itemView: View) : BindHolder<String>(itemView) {
//        private val title: TextView = itemView.findViewById(R.id.title)
//
//        override fun bind(obj: String) {
//            title.text = obj
//        }
//    }
//


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
