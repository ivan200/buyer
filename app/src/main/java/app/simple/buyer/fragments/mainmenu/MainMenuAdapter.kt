package app.simple.buyer.fragments.mainmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
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

    var selectListId: Long = 0

    fun selectList(listId: Long){
        this.selectListId = listId
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
        private val tvTitle: TextView = view.findViewById(R.id.tv_title)
        private var itemListId: Long = 0

        init {
            view.setOnClickListener(this)
        }

        fun bind(data: BuyList) {
            view.isSelected = data.id == selectListId
            tvTitle.text = data.name
            itemListId = data.id
        }

        override fun onClick(v: View?) {
            onMenuItemSelected.invoke(itemListId)
        }
    }
}