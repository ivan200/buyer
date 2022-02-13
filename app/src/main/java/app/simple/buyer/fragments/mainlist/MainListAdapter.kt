package app.simple.buyer.fragments.mainlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
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
) : RealmRecyclerViewAdapter2<BuyListItem, MainListAdapter.MainMenuHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuHolder {
        val binding = CellMainListBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return MainMenuHolder(binding)
    }

    override fun onBindViewHolder(holder: MainMenuHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainMenuHolder(val binding: CellMainListBinding) : RecyclerView.ViewHolder(binding.root), View.OnClickListener,
        CompoundButton.OnCheckedChangeListener {
        private var itemListId: Long = 0

        init {
            binding.root.setOnClickListener(this)
        }

        fun bind(data: BuyListItem) {
            itemListId = data.id

            binding.tvTitle.text = data.buyItem?.name

            if (data.count == 1L) {
                binding.tvCount.hide()
            } else {
                binding.tvCount.show()
                binding.tvCount.text = data.count.toString()
            }

            binding.checkbox.let {
                it.setOnCheckedChangeListener(null)
                it.isChecked = data.isBuyed
                it.setOnCheckedChangeListener(this)
            }
        }

        override fun onClick(v: View?) {
            onItemSelected.invoke(itemListId)
        }

        override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
            onItemSelected.invoke(itemListId)
        }
    }
}