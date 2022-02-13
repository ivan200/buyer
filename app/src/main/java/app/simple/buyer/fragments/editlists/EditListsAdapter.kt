package app.simple.buyer.fragments.editlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.databinding.CellEditListBinding
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class EditListsAdapter(
    data: OrderedRealmCollection<BuyList>,
    val onItemClicked: Function1<Long, Unit>,
    val onDelClicked: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyList, EditListsAdapter.EditListsHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditListsHolder {
        val binding = CellEditListBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return EditListsHolder(binding)
    }

    override fun onBindViewHolder(holder: EditListsHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class EditListsHolder(val binding: CellEditListBinding) : RecyclerView.ViewHolder(binding.root) {
        private var itemListId: Long = 0

        init {
            binding.root.setOnClickListener {
                onItemClicked.invoke(itemListId)
            }
            binding.ibDelete.setOnClickListener {
                onDelClicked.invoke(itemListId)
            }
        }

        fun bind(data: BuyList) {
            itemListId = data.id
            binding.tvTitle.text = data.name
        }
    }
}