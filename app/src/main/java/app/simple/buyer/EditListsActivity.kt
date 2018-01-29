package app.simple.buyer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import app.simple.buyer.adapters.EditListsRecyclerViewAdapter
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.database.DBHelper
import app.simple.buyer.util.views.DialogHelper
import io.reactivex.functions.Consumer
import io.realm.Realm
import io.realm.Sort
import kotlinx.android.synthetic.main.activity_edit_lists.*

/**
 * Created by Zakharovi on 23.01.2018.
 */

class EditListsActivity : AppCompatActivity() {
    companion object {
        const val ActivityCode = 124
    }

    val realm: Realm = DBHelper.realm

    var adapter: EditListsRecyclerViewAdapter? = null

    private var menu: Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lists)
        setSupportActionBar(edit_lists_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        edit_lists_toolbar.title = "Edit lists"

        supportActionBar?.title = "Edit lists"

        lists_fab.setOnClickListener { view ->
            DialogHelper.showInputDialog(this,
                    baseContext.getString(R.string.dialog_add_list_title),
                    baseContext.getString(R.string.dialog_add_list_hint),
                    baseContext.getString(R.string.button_add),
                    Consumer { this.addList(it) })


//            val intent = Intent(this, AddItemActivity::class.java)
//            startActivityForResult(intent, AddItemActivity.ActivityCode)

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }

        adapter = EditListsRecyclerViewAdapter(BuyList.getAllOrdered())
        rv_edit_lists.adapter = adapter
        rv_edit_lists.setHasFixedSize(true)
        rv_edit_lists.layoutManager = LinearLayoutManager(this)
    }


    private fun addList(name: String) {
        var listByName = BuyList.getByName(name)
        if(listByName != null){
            throw RuntimeException(baseContext.getString(R.string.dialog_add_list_error))
        } else{
            BuyList.addAsync(name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.edit_lists, menu)
        menu.setGroupVisible(R.id.group_normal_mode, true)
        menu.setGroupVisible(R.id.group_reorder_mode, false)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            R.id.action_settings -> {
                return true
            }
            R.id.order_alphabet_az ->{
                BuyList.orderBy(BuyList.OrderType.ALPHABET, Sort.ASCENDING)
            }
            R.id.order_alphabet_za ->{
                BuyList.orderBy(BuyList.OrderType.ALPHABET, Sort.DESCENDING)
            }
            R.id.order_popularity_az ->{
                BuyList.orderBy(BuyList.OrderType.POPULARITY, Sort.ASCENDING)
            }
            R.id.order_popularity_za ->{
                BuyList.orderBy(BuyList.OrderType.POPULARITY, Sort.DESCENDING)
            }
            R.id.order_size_az ->{
                BuyList.orderBy(BuyList.OrderType.SIZE, Sort.ASCENDING)
            }
            R.id.order_size_za ->{
                BuyList.orderBy(BuyList.OrderType.SIZE, Sort.DESCENDING)
            }
            R.id.order_by_hand ->{
                menu?.setGroupVisible(R.id.group_normal_mode, false)
                menu?.setGroupVisible(R.id.group_reorder_mode, true)
                adapter?.enableReorderMode(true)
            }
            R.id.action_clear ->{
                menu?.setGroupVisible(R.id.group_reorder_mode, false)
                menu?.setGroupVisible(R.id.group_normal_mode, true)
                adapter?.enableReorderMode(false)
            }

            android.R.id.home -> {
                onBackPressed()
                overridePendingTransition(0, 0)
                return true
            }
        }


//        val id = item.itemId
//        if (id == android.R.id.home) {
//            onBackPressed()
//            overridePendingTransition(0, 0)
//            return true
//        }

        return super.onOptionsItemSelected(item)
    }

}