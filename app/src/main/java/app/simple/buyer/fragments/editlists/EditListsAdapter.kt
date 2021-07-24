package app.simple.buyer.fragments.editlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
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
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_edit_list, parent, false)
        return EditListsHolder(itemView)
    }

    override fun onBindViewHolder(holder: EditListsHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class EditListsHolder(val view: View) : RecyclerView.ViewHolder(view) {
        private val tvTitle: TextView = view.findViewById(R.id.tv_title)
        private val ibDelete: ImageButton = view.findViewById(R.id.ib_delete)
        private var itemListId: Long = 0

        init {
            view.setOnClickListener {
                onItemClicked.invoke(itemListId)
            }
            ibDelete.setOnClickListener {
                onDelClicked.invoke(itemListId)
            }
        }

        fun bind(data: BuyList) {
            itemListId = data.id
            tvTitle.text = data.name
        }
    }
}