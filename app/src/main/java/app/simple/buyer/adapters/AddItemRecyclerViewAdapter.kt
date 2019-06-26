package app.simple.buyer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Zakharovi on 22.01.2018.
 */

class AddItemRecyclerViewAdapter(data: OrderedRealmCollection<BuyItem>) : RealmRecyclerViewAdapter<BuyItem, AddItemRecyclerViewAdapter.BuyItemViewHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_add_item, parent, false)
        return BuyItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuyItemViewHolder, position: Int) {
        val obj = getItem(position)
        holder.buyItem = obj
        holder.title.text = obj?.name
        holder.count.text = obj?.populatity.toString()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id!!
    }

    inner class BuyItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_title)
        var count: TextView = view.findViewById(R.id.tv_count)
        var button_detete: ImageButton = view.findViewById(R.id.btn_detete)
        var buyItem: BuyItem? = null

        init {
            //            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}
