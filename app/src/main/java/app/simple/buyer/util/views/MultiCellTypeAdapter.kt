package app.simple.buyer.util.views

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.functions.Function
import java.util.*

/**
 * Created by Zakharovi on 12.01.2018.
 */

//Адаптер для списка с ячейками разных типов, с которым удобно работать(мне)
class MultiCellTypeAdapter : RecyclerView.Adapter<BindHolder<*>>() {
    private var listItems: List<MultiCellObject<*>> = ArrayList()
    private var itemTypes = SparseArray<Function<View, out BindHolder<*>>>()

    fun update(items: List<MultiCellObject<*>>?) {
        if (items != null) {
            this.listItems = items
            for (listItem in listItems) {
                itemTypes.put(listItem.resource, listItem.onCreateHolder )
            }
        } else {
            listItems = ArrayList()
            itemTypes = SparseArray()
        }
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindHolder<*> {
        return itemTypes.get(viewType).apply(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listItems.size) {
            listItems[position].resource
        } else -1
    }

    override fun onBindViewHolder(holder: BindHolder<*>, position: Int) {
        @Suppress("UNCHECKED_CAST")
        (holder as BindHolder<Any?>).bind(listItems[position].obj)
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}

