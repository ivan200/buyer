package app.simple.buyer.fragments.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.databinding.CellMainBinding
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainAdapter(
    data: OrderedRealmCollection<BuyListItem>,
    val onItemSelected: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyListItem, MainAdapter.MainMenuHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_main, parent, false)
        return MainMenuHolder(itemView)
    }

    override fun onBindViewHolder(holder: MainMenuHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainMenuHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        val binding = CellMainBinding.bind(view)
        private var itemListId: Long = 0

        init {
            view.setOnClickListener(this)
        }

        fun bind(data: BuyListItem) {
            itemListId = data.id

            binding.tvTitle.text = data.buyItem?.name
            view.isSelected = data.id == itemListId
        }

        override fun onClick(v: View?) {
            onItemSelected.invoke(itemListId)
        }
    }
}