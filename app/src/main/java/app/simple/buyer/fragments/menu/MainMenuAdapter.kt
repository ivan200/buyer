package app.simple.buyer.fragments.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.databinding.CellMainMenuBinding
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainMenuAdapter(
    data: OrderedRealmCollection<BuyList>,
    val onMenuItemSelected: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyList, MainMenuAdapter.MainMenuHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    private var selectedListId: Long = 0L

    fun selectList(listId: Long?) {
        this.selectedListId = listId ?: 0L
        notifyDataSetChanged()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainMenuHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_main_menu, parent, false)
        return MainMenuHolder(itemView)
    }

    override fun onBindViewHolder(holder: MainMenuHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainMenuHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val binding = CellMainMenuBinding.bind(view)
        private var itemListId: Long = 0

        init {
            view.setOnClickListener(this)
        }

        fun bind(data: BuyList) {
            itemListId = data.id
            binding.tvTitle.text = data.name
            view.isSelected = data.id == selectedListId
        }

        override fun onClick(v: View?) {
            onMenuItemSelected.invoke(itemListId)
        }
    }
}