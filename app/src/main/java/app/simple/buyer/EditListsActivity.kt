package app.simple.buyer

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.EditListsRecyclerViewAdapter
import app.simple.buyer.util.database.DBHelper
import app.simple.buyer.util.views.DialogHelper
import io.reactivex.functions.Consumer
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit_lists.*

/**
 * Created by Zakharovi on 23.01.2018.
 */

class EditListsActivity : AppCompatActivity() {
    companion object {
        const val ActivityCode = 124
    }

    val realm: Realm = DBHelper.realm

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

        val adapter = EditListsRecyclerViewAdapter(BuyList.getAll())
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            onBackPressed()
            overridePendingTransition(0, 0)
            return true
        }

        return super.onOptionsItemSelected(item)
    }

}