package app.simple.buyer.fragments.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.databinding.CellMainMenuBinding
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainMenuAdapter(
    data: OrderedRealmCollection<BuyList>,
    var selectedListId: Long,
    val onMenuItemSelected: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyList, MainMenuAdapter.MainMenuHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    fun selectList(listId: Long?) {
        this.selectedListId = listId ?: 0L
        notifyDataSetChanged()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuHolder {
        val binding = CellMainMenuBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return MainMenuHolder(binding)
    }

    override fun onBindViewHolder(holder: MainMenuHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainMenuHolder(val binding: CellMainMenuBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        private var itemListId: Long = 0

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(data: BuyList) {
            itemListId = data.id
            binding.tvTitle.text = data.name
            binding.root.isSelected = (itemListId == selectedListId)
        }

        override fun onClick(v: View?) {
            onMenuItemSelected.invoke(itemListId)
        }
    }
}