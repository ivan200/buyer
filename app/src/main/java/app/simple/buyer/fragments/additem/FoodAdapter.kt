package app.simple.buyer.fragments.additem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.util.getColorCompat
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.hide
import app.simple.buyer.util.showIf
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class FoodAdapter(data: OrderedRealmCollection<BuyItem>,
                  val onItemClicked: Function0<Unit>) : RealmRecyclerViewAdapter2<BuyItem, FoodAdapter.FoodHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.cell_add_item, parent, false)
        return FoodHolder(itemView)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class FoodHolder(val view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {

        var count = 0

        val tvTitle: TextView = view.findViewById(R.id.tv_title)
        val tvSubtitle: TextView = view.findViewById(R.id.tv_subtitle)
        val tvCount: TextView = view.findViewById(R.id.tv_count)
//        val ivAdd: ImageView = view.findViewById(R.id.btn_add)
        val ivDel: ImageButton = view.findViewById(R.id.btn_detete)

        init {
            view.setOnClickListener(this)
            ivDel.setOnClickListener(this::onDelClick)
        }

        fun bind(data: BuyItem?) {
            tvTitle.text = data?.name
            updateView()
        }

        fun onDelClick(v: View?){
            if(count > 0) count--
            updateView()
        }

        override fun onClick(v: View?) {
            count++
            updateView()
            this@FoodAdapter.onItemClicked.invoke()
//            Prefs.currentDocId = dataVar.id
//
//            navigateOnClickListener.onClick(v)
        }

        fun updateView(){
            ivDel.showIf { count > 0 }
            tvCount.showIf { count > 1 }
            tvSubtitle.hide()

            if(count == 0) {
                tvTitle.setTextColor(view.context.getColorResCompat(R.attr.colorText))
            }
            if(count > 0) {
                tvTitle.setTextColor(view.context.getColorCompat(R.color.colorCheckboxGreen))
            }
            if(count == 1){
                ivDel.setImageResource(R.drawable.ic_clear)
            }
            if(count > 1){
                ivDel.setImageResource(R.drawable.ic_remove)
                tvCount.text = count.toString()
            }
        }
    }
}