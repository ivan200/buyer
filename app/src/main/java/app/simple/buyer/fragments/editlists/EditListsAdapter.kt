package app.simple.buyer.fragments.editlists

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyList
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class EditListsAdapter(
    data: OrderedRealmCollection<BuyList>
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

    inner class EditListsHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        private val tvTitle: TextView = view.findViewById(R.id.tv_title)

        init {
            view.setOnClickListener(this)
        }

        fun bind(data: BuyList) {
            tvTitle.text = data.name
        }

        override fun onClick(v: View?) {
//            updateView()
        }
    }
}