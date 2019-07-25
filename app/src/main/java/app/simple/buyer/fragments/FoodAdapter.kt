package app.simple.buyer.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.views.RealmRecyclerViewAdapter
import io.realm.OrderedRealmCollection

class FoodAdapter (data: OrderedRealmCollection<BuyItem>) :
        RealmRecyclerViewAdapter<BuyItem, FoodAdapter.FoodHolder>(data, true) {

    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_main_menu, parent, false)
        return FoodHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }


    inner class FoodHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val tvTitle: TextView = view.findViewById(R.id.tv_title)

        init {
            view.setOnClickListener(this)
        }

        fun bind(data: BuyItem?) {
            tvTitle.text = data?.name
        }

        override fun onClick(v: View?) {
//            Prefs.currentDocId = dataVar.id
//
//            navigateOnClickListener.onClick(v)
        }
    }

}