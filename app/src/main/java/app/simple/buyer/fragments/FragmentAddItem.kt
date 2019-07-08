package app.simple.buyer.fragments

import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.Utils
import com.google.android.material.appbar.AppBarLayout

class FragmentAddItem : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_add_item

    override val title: Int
        get() = R.string.app_name

    private val base_layout by lazy { mActivity.findViewById<LinearLayout>(R.id.base_layout) }
    private val add_appbar by lazy { mActivity.findViewById<AppBarLayout>(R.id.add_appbar) }
    private val editText by lazy { mActivity.findViewById<EditText>(R.id.editText) }
    private val recyclerList by lazy { mActivity.findViewById<RecyclerView>(R.id.recyclerList) }

    private lateinit var adapter: FoodAdapter

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(base_layout) { _, insets ->
            add_appbar.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
            recyclerList.setPadding(insets.systemWindowInsetLeft, 0, insets.systemWindowInsetRight, insets.systemWindowInsetBottom)
            insets.consumeSystemWindowInsets()
        }

//        val adapter = MultiCellTypeAdapter(mActivity, this::showError)
//        recyclerList.layoutManager = LinearLayoutManager(mActivity)
//        recyclerList.adapter = adapter
//        adapter.update((1..50).map { x -> MultiCellObject(ViewHolderSample.holderData, "Example string $x") })


        adapter = FoodAdapter(BuyItem.getListAsync(realm, ""))
        recyclerList.layoutManager = LinearLayoutManager(mActivity)
        recyclerList.adapter = adapter

        editText.addTextChangedListener(Utils.simpleTextWatcher (this::onTextChanged))
    }

    fun onTextChanged(text: String){
        adapter.updateData(BuyItem.getListAsync(realm, text.trim()))
    }

    override fun onResume() {
        super.onResume()
        editText.post {
            Utils.showKeyboard(mActivity, editText)
        }
    }

    override fun onPause() {
        Utils.hideKeyboard2(mActivity, editText)
        super.onPause()
    }
}