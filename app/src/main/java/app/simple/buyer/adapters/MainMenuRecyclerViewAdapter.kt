package app.simple.buyer.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyList
import io.realm.OrderedRealmCollection
import io.realm.RealmRecyclerViewAdapter

/**
 * Created by Zakharovi on 26.01.2018.
 */

class MainMenuRecyclerViewAdapter(data: OrderedRealmCollection<BuyList>) : RealmRecyclerViewAdapter<BuyList, MainMenuRecyclerViewAdapter.BuyListItemViewHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyListItemViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_main_menu, parent, false)
        return BuyListItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: BuyListItemViewHolder, position: Int) {
        val obj = getItem(position)
        holder.buyList = obj
        holder.title.text = obj?.name
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id!!
    }

    inner class BuyListItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var title: TextView = view.findViewById(R.id.tv_title)
        var buyList: BuyList? = null
        init {
            //            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}

