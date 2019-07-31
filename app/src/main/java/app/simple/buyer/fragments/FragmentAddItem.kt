package app.simple.buyer.fragments

import android.content.res.Configuration
import android.view.View
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.Utils
import com.google.android.material.appbar.AppBarLayout



class FragmentAddItem : BaseFragment(R.layout.fragment_add_item) {

    override val title: Int
        get() = R.string.app_name

    private val base_layout by lazy { mView.findViewById<RelativeLayout>(R.id.base_layout) }
    private val add_appbar by lazy { mView.findViewById<AppBarLayout>(R.id.add_appbar) }
    private val editText by lazy { mView.findViewById<EditText>(R.id.editText) }
    private val recyclerList by lazy { mView.findViewById<RecyclerView>(R.id.recyclerList) }
    private val shadow by lazy { mView.findViewById<View>(R.id.shadow_view) }
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    private lateinit var adapter: FoodAdapter

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        ViewCompat.setOnApplyWindowInsetsListener(base_layout) { _, insets ->
            setRecyclerPaddings(recyclerList, insets)
            add_appbar.setPadding(insets.systemWindowInsetLeft, insets.systemWindowInsetTop, insets.systemWindowInsetRight, 0)
            insets.consumeSystemWindowInsets()
        }
        setRecyclerPaddings(recyclerList)

        adapter = FoodAdapter(BuyItem.getListAsync(realm, ""))
        recyclerList.itemAnimator = null
        recyclerList.layoutManager = LinearLayoutManager(mActivity)
        recyclerList.adapter = adapter

        editText.addTextChangedListener(Utils.simpleTextWatcher (this::onTextChanged))

        shadowToggler = ShadowRecyclerSwitcher(recyclerList, shadow)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        setRecyclerPaddings(recyclerList)
    }

    fun onTextChanged(text: String){
        if((recyclerList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 0){
            recyclerList.scrollToPosition(0)
        }
        adapter.updateDataNoClear(BuyItem.getListAsync(realm, text.trim()))
    }

    override fun onResume() {
        super.onResume()
        editText.post {
            Utils.showKeyBoard2(editText)
        }
    }

    override fun onPause() {
        Utils.hideKeyboard2(mActivity, editText)
        super.onPause()
    }
}