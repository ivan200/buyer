package app.simple.buyer

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

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

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        if (android.os.Build.VERSION.SDK_INT <= android.os.Build.VERSION_CODES.KITKAT) {
            nav_view.findViewById<LinearLayout>(R.id.ll_status_bar).visibility = View.GONE
        }

        var listName = nav_view.getHeaderView(0).findViewById<TextView>(R.id.tv_title)
        listName.setOnClickListener( { v->
            val intent = Intent(this, EditListsActivity::class.java)
            startActivityForResult(intent, EditListsActivity.ActivityCode)
        })
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
