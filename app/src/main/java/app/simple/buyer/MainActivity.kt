package app.simple.buyer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.adapters.MainMenuRecyclerViewAdapter
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.crash.LogModule
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.text.MessageFormat


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val fab by lazy { findViewById<FloatingActionButton>(R.id.fab) }
    private val toolbar by lazy { findViewById<Toolbar>(R.id.toolbar) }
    private val drawer_layout by lazy { findViewById<DrawerLayout>(R.id.drawer_layout) }
    private val nav_view by lazy { findViewById<NavigationView>(R.id.nav_view) }
    private val menu_tv_title by lazy { findViewById<TextView>(R.id.menu_tv_title) }
    private val menu_rv_list by lazy { findViewById<RecyclerView>(R.id.menu_rv_list) }
    private val menu_ll_status_bar by lazy { findViewById<LinearLayout>(R.id.menu_ll_status_bar) }

    //    var menu_list: RecyclerView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)
        fab.setOnClickListener { view ->
            val intent = Intent(this, AddItemActivity::class.java)
            startActivityForResult(intent, AddItemActivity.ActivityCode)

//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }


        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            menu_ll_status_bar.visibility = View.GONE
            menu_rv_list.setPadding(0, 0, 0, 0)
        }

        menu_tv_title.setOnClickListener { v ->
            val intent = Intent(this, EditListsActivity::class.java)
            startActivityForResult(intent, EditListsActivity.ActivityCode)
        }

        menu_rv_list.adapter = MainMenuRecyclerViewAdapter(BuyList.getAllOrdered())
        menu_rv_list.setHasFixedSize(true)
        menu_rv_list.layoutManager = LinearLayoutManager(this)
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_camera -> {
//                val intent = Intent(this, AddItemActivity::class.java)
//                startActivityForResult(intent, AddItemActivity.ActivityCode)

                // Handle the camera action
            }
            R.id.nav_gallery -> {

            }
            R.id.nav_slideshow -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_share -> {

            }
            R.id.nav_send -> {

            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        LogModule.printToLog(MessageFormat.format("PERMISSIONS onActivityResult: {0}, {1}", requestCode.toString(), resultCode.toString()))

        menu_rv_list.adapter?.notifyDataSetChanged()

        if (resultCode == RESULT_OK) {
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


//    private fun loadCities(): List<City>? {
//        // In this case we're loading from local assets.
//        // NOTE: could alternatively easily load from network
//        val stream: InputStream
//        try {
//            stream = assets.open("cities.json")
//        } catch (e: IOException) {
//            return null
//        }
//
//        val gson = GsonBuilder().create()
//
//        val json = JsonParser().parse(InputStreamReader(stream))
//        val cities = gson.fromJson(json, object : TypeToken<List<City>>() {
//
//        }.getType())
//
//        // Open a transaction to store items into the realm
//        // Use copyToRealm() to convert the objects into proper RealmObjects managed by Realm.
//        realm.beginTransaction()
//        val realmCities = realm.copyToRealm(cities)
//        realm.commitTransaction()
//
//        return ArrayList<City>(realmCities)
//    }

}
