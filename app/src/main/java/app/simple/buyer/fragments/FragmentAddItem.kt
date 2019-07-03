package app.simple.buyer.fragments

import android.view.View
import app.simple.buyer.BaseFragment
import app.simple.buyer.R

class FragmentAddItem : BaseFragment() {
    override val layoutId: Int
        get() = R.layout.fragment_add_item

    override val title: Int
        get() = R.string.app_name

//    private val doneButton by lazy { mActivity.findViewById<Button>(R.id.doneButton) }

    //    private val recyclerList by lazy { mActivity.findViewById<RecyclerView>(R.id.recyclerList) }
//    private val editText by lazy { mActivity.findViewById<EditText>(R.id.editText) }

    override fun initialize(view: View) {
        mActivity.supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
    }
}