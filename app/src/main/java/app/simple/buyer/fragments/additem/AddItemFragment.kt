package app.simple.buyer.fragments.additem

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import app.simple.buyer.BaseFragment
import app.simple.buyer.R
import app.simple.buyer.databinding.FragmentAddItemBinding
import app.simple.buyer.fragments.mainlist.DrawerState
import app.simple.buyer.fragments.mainlist.DrawerState.FINISH_CLOSING
import app.simple.buyer.fragments.mainlist.DrawerState.FINISH_OPENING
import app.simple.buyer.fragments.mainlist.DrawerState.START_CLOSING
import app.simple.buyer.fragments.mainlist.DrawerState.START_OPENING
import app.simple.buyer.util.ShadowRecyclerSwitcher
import app.simple.buyer.util.Utils
import app.simple.buyer.util.views.viewBinding

class AddItemFragment : BaseFragment(R.layout.fragment_add_item), DrawerStateConsumer {

    override val title: Int
        get() = R.string.app_name

    private var shadowSwitcher: ShadowRecyclerSwitcher? = null

    private val binding by viewBinding(FragmentAddItemBinding::bind)

    private val model: AddItemViewModel by viewModels()

    private lateinit var adapter: AddItemAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = AddItemAdapter(model.getItems(), this::onItemClicked, model::onItemDeleted)
        binding.apply {
            recyclerList.setHasFixedSize(true)
            recyclerList.itemAnimator = null
            recyclerList.layoutManager = LinearLayoutManager(mActivity)
            recyclerList.adapter = adapter

            editText.addTextChangedListener(Utils.simpleTextWatcher (this@AddItemFragment::onTextChanged))
            editText.setOnEditorActionListener(this@AddItemFragment::onEditorAction)
            shadowSwitcher = ShadowRecyclerSwitcher(recyclerList, shadowView)
        }

        model.ltemsLiveData.observe(viewLifecycleOwner){
            adapter.itemsUpdated(it)
        }
    }

    override fun onApplyWindowInsets(v: View, insets: WindowInsetsCompat?): WindowInsetsCompat? {
        setRecyclerPaddings(binding.recyclerList, binding.addAppbar, null, insets, usePaddingLeft = false, usePaddingRight = true)
        return super.onApplyWindowInsets(v, insets)
    }

    var blockItemUpdateOnce = false

    fun onItemClicked(itemId: Long){
        blockItemUpdateOnce = true
        binding.editText.text?.clear()
        model.onItemClicked(itemId)
    }

    fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_NEXT) {
            (v as EditText).text.let {
                blockItemUpdateOnce = true
                model.onNewItem(it.toString())
                it.clear()
            }
            return true
        }
        return false
    }

    fun onTextChanged(text: String){
        if(blockItemUpdateOnce){
            blockItemUpdateOnce = false
            return
        }
        if(binding.recyclerList.canScrollVertically(-1)){
            binding.recyclerList.scrollToPosition(0)
        }
        adapter.updateDataNoClear(model.getItems(text))
    }

    override fun onDrawerPositionChanged(pos: DrawerState){
        when(pos){
            START_OPENING -> Utils.showKeyBoard2(binding.editText)
            FINISH_OPENING -> Utils.showKeyBoard2(binding.editText)
            START_CLOSING -> Utils.hideKeyboardFrom(requireView())
            FINISH_CLOSING -> {
                binding.editText.text?.clear()
                Utils.hideKeyboardFrom(requireView())
            }
        }
    }
}