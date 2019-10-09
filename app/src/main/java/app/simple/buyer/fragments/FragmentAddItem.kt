package app.simple.buyer.fragments

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.RelativeLayout
import androidx.core.view.WindowInsetsCompat
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

    private val base_layout get() = mView.findViewById<RelativeLayout>(R.id.base_layout)
    private val add_appbar get() = mView.findViewById<AppBarLayout>(R.id.add_appbar)
    private val editText get() = mView.findViewById<EditText>(R.id.editText)
    private val recyclerList get() = mView.findViewById<RecyclerView>(R.id.recyclerList)
    private val shadow get() = mView.findViewById<View>(R.id.shadow_view)
    private var shadowToggler: ShadowRecyclerSwitcher? = null

    private lateinit var adapter: FoodAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = FoodAdapter(BuyItem.getListAsync(realm, ""), this::onItemAdded)
        recyclerList.setHasFixedSize(true)
        recyclerList.itemAnimator = null
        recyclerList.layoutManager = LinearLayoutManager(mActivity)
        recyclerList.adapter = adapter

        editText.addTextChangedListener(Utils.simpleTextWatcher (this::onTextChanged))

        editText.setOnEditorActionListener { v, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
                (v as EditText).text.clear()
            }
            false
        }
        shadowToggler = ShadowRecyclerSwitcher(recyclerList, shadow)
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(recyclerList, add_appbar, null, insets, usePaddingLeft = false, usePaddingRight = true)
        return super.onApplyWindowInsets(v, insets)
    }

    var blockItemUpdateOnce = false

    fun onItemAdded(){
        blockItemUpdateOnce = true
        editText.text.clear()
    }

    fun onTextChanged(text: String){
        if(blockItemUpdateOnce){
            blockItemUpdateOnce = false
            return
        }
        if((recyclerList.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition() > 0){
            recyclerList.scrollToPosition(0)
        }
        adapter.updateDataNoClear(BuyItem.getListAsync(realm, text.trim()))
    }

    fun rightDrawerPositionChanged(pos: FragmentMain.DrawerState){
        when(pos){
            FragmentMain.DrawerState.START_OPENING -> Utils.showKeyBoard2(editText)
            FragmentMain.DrawerState.FINISH_OPENING -> Utils.showKeyBoard2(editText)
            FragmentMain.DrawerState.START_CLOSING -> Utils.hideKeyboard2(mActivity, editText)
            FragmentMain.DrawerState.FINISH_CLOSING -> {
                editText.text.clear()
                Utils.hideKeyboard2(mActivity, editText)
            }
        }
    }
}