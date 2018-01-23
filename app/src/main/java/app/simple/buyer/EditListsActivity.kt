package app.simple.buyer

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
        setContentView(R.layout.activity_add_item)

        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val adapter = AddItemRecyclerViewAdapter(realm.where(BuyItem::class.java).findAll())

        rv_edit_lists.adapter = adapter
        rv_edit_lists.layoutManager = LinearLayoutManager(this)
//        adapter.update(listItems)

        rv_edit_lists.invalidate()
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