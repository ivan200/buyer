package app.simple.buyer

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.MenuItem
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.AddItemRecyclerViewAdapter
import app.simple.buyer.util.DBHelper
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_edit_lists.*

/**
 * Created by Zakharovi on 23.01.2018.
 */

class EditListsActivity : AppCompatActivity() {
    companion object {
        const val ActivityCode = 124
    }

    val realm: Realm = DBHelper.getRealm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_lists)
        setSupportActionBar(edit_lists_toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        edit_lists_toolbar.title = "Edit lists"

        supportActionBar?.title = "Edit lists"

        lists_fab.setOnClickListener { view ->
            val intent = Intent(this, AddItemActivity::class.java)
            startActivityForResult(intent, AddItemActivity.ActivityCode)

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }

        val adapter = AddItemRecyclerViewAdapter(realm.where(BuyItem::class.java).findAll())

        rv_edit_lists.setHasFixedSize(true)
        rv_edit_lists.adapter = adapter
        rv_edit_lists.layoutManager = LinearLayoutManager(this)
//        adapter.update(listItems)
//        rv_edit_lists.invalidate()
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