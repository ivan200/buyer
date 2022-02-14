package app.simple.buyer.fragments.mainlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.databinding.CellMainListBinding
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainListAdapter(
    data: OrderedRealmCollection<BuyListItem>,
    val onItemSelected: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyListItem, MainListAdapter.MainListHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val binding = CellMainListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MainListHolder(binding)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainListHolder(val binding: CellMainListBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var itemListId: Long = 0

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(data: BuyListItem) {
            itemListId = data.id

            binding.apply {
                tvTitle.text = data.buyItem?.name

                if (data.count <= 1L) {
                    tvCount.hide()
                } else {
                    tvCount.show()
                    tvCount.text = data.count.toString()
                }

                checkbox.isChecked = data.isBuyed
            }
        }

        override fun onClick(v: View?) {
            onItemSelected.invoke(itemListId)
        }
    }
}