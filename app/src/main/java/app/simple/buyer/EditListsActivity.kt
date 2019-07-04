package app.simple.buyer

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.util.Consumer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.adapters.EditListsRecyclerViewAdapter
import app.simple.buyer.entities.BuyList
import app.simple.buyer.entities.OrderType
import app.simple.buyer.util.database.Prefs
import app.simple.buyer.util.views.DialogHelper
import app.simple.buyer.util.views.MenuTintUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.realm.Sort


/**
 * Created by Zakharovi on 23.01.2018.
 */

class EditListsActivity : AppCompatActivity() {
    companion object {
        const val ActivityCode = 124
    }

    var adapter: EditListsRecyclerViewAdapter? = null

    private var menu: Menu? = null

    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val fab by lazy { findViewById<FloatingActionButton>(R.id.lists_fab) }
    private val rv by lazy { findViewById<RecyclerView>(R.id.rv_edit_lists) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_edit_lists)

        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        edit_lists_toolbar.title = "Edit lists"

        supportActionBar?.title = "Edit lists"

        fab.setOnClickListener { view ->

            DialogHelper(this).showInputDialog(
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

        rv.adapter = adapter
        rv.setHasFixedSize(true)
        rv.layoutManager = LinearLayoutManager(this)
    }


    private fun addList(name: String) {
        var listByName = BuyList.getByName(name)
        if (listByName != null) {
            throw RuntimeException(baseContext.getString(R.string.dialog_add_list_error))
        } else {
            BuyList.addAsync(name)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.order_edit_lists, menu)
        menu.setGroupVisible(R.id.group_normal_mode, true)
        menu.setGroupVisible(R.id.group_reorder_mode, false)

        MenuTintUtils.tintAllIcons(menu, Color.WHITE)
        return true
    }

    fun checkItem(item: MenuItem, menu: Menu) {
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)!!
            menuItem.isCheckable = (menuItem.itemId == item.itemId)
            menuItem.isChecked = (menuItem.itemId == item.itemId)
            if (menuItem.hasSubMenu()) {
                checkItem(item, menuItem.subMenu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val sortType = if(Prefs(this).listsSortAscending)  Sort.ASCENDING else Sort.DESCENDING

        when (item.itemId) {
            R.id.item_sort_type -> {
                val invertSort = if(sortType == Sort.ASCENDING)  Sort.DESCENDING else Sort.ASCENDING
                BuyList.orderBy(this, Prefs(this).listsOrderType, invertSort)
                if (invertSort == Sort.ASCENDING) {
                    item.setIcon(R.drawable.ic_sort_ascending)
                } else {
                    item.setIcon(R.drawable.ic_sort_descending)
                }

                MenuTintUtils.tintMenuItemIcon(item, Color.WHITE)
            }
            R.id.item_order_alphabet -> {
                BuyList.orderBy(this, OrderType.ALPHABET, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_popularity -> {
                BuyList.orderBy(this, OrderType.POPULARITY, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_size -> {
                BuyList.orderBy(this, OrderType.SIZE, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_create -> {
                BuyList.orderBy(this, OrderType.CREATED, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_modify -> {
                BuyList.orderBy(this, OrderType.MODIFIED, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_price -> {
                BuyList.orderBy(this, OrderType.PRICE, sortType)
                checkItem(item, menu!!)
            }
            R.id.item_order_hand -> {
                menu?.setGroupVisible(R.id.group_normal_mode, false)
                menu?.setGroupVisible(R.id.group_reorder_mode, true)
                adapter?.enableReorderMode(true)
                item.isChecked = true
            }
            R.id.item_action_clear -> {
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


//    fun showPopupWindow(view: View) {
//
//        val mPopupWindow = PopupWindow()
//        val inflater = this.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
//        val customView = inflater.inflate(R.layout.content_order_menu, null)
//
//        mPopupWindow.isFocusable = true
//        mPopupWindow.width = WindowManager.LayoutParams.WRAP_CONTENT
//        mPopupWindow.height = WindowManager.LayoutParams.WRAP_CONTENT
//        mPopupWindow.contentView = customView
//        mPopupWindow.setBackgroundDrawable(BitmapDrawable(resources))
//
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            mPopupWindow.elevation = 20f
//        }
//
//        mPopupWindow.showAsDropDown(view, 0, 0 - view.height)
//    }

}