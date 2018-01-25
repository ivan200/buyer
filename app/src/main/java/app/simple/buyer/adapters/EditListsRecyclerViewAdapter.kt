package app.simple.buyer.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyList
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Zakharovi on 25.01.2018.
 */

class EditListsRecyclerViewAdapter (data: OrderedRealmCollection<BuyList>) : RealmRecyclerViewAdapter<BuyList, EditListsRecyclerViewAdapter.BuyListViewHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyListViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_add_item, parent, false)
        return BuyListViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuyListViewHolder, position: Int) {
        val obj = getItem(position)
        holder.buyList = obj
        holder.title.text = obj?.name
        holder.count.text = obj?.populatity.toString()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id!!
    }

    inner class BuyListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_title)
        var count: TextView = view.findViewById(R.id.tv_count)
        var button_detete: ImageButton = view.findViewById(R.id.btn_detete)
        var buyList: BuyList? = null

        init {
            //            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}
