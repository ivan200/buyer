package app.simple.buyer.util.views

import android.support.v7.widget.RecyclerView
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import rx.functions.Func1
import java.util.*

/**
 * Created by Zakharovi on 12.01.2018.
 */

//Адаптер для списка с ячейками разных типов, с которым удобно работать(мне)
class MultiCellTypeAdapter : RecyclerView.Adapter<BindHolder<*>>() {
    private var listItems: List<MultiCellObject<*>> = ArrayList()
    private var itemTypes = SparseArray<Func1<View, out BindHolder<*>>>()

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
        return itemTypes.get(viewType).call(LayoutInflater.from(parent.context).inflate(viewType, parent, false))
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
