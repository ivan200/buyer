package app.simple.buyer.util.views

import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.BaseActivity
import io.realm.Realm
import java.util.*

class HolderData<T>(val layoutId: Int, val newInstance: Function1<View, MultiBindHolder<T>>)

class MultiCellObject<T>(val holderData: HolderData<T>, val data: T)

abstract class MultiBindHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    lateinit var mActivity: BaseActivity
    lateinit var realm: Realm
    lateinit var errorHandler: Function1<Throwable, Unit>
    abstract fun bind(data: T)
    open fun onViewCreated(){}
    open fun onDetached(){}
}

class MultiCellTypeAdapter(
    private val mActivity: BaseActivity,
    private val errorHandler: Function1<Throwable, Unit>? = null,
    private val realm: Realm? = null)
    : RecyclerView.Adapter<MultiBindHolder<Any>>() {
    private var listItems: MutableList<MultiCellObject<Any>> = mutableListOf()
    private var itemTypes = SparseArray<Function1<View, MultiBindHolder<Any>>>()


    fun update(items: List<MultiCellObject<*>>?) {
        val oldCount = listItems.count()
        if (items != null) {
            this.listItems = items.filterIsInstance<MultiCellObject<Any>>().toMutableList()
            itemTypes.clear()
            for (listItem in listItems) {
                val holderData = listItem.holderData
                itemTypes.put(holderData.layoutId, holderData.newInstance)
            }
            val newCount = listItems.count()
            when {
                oldCount == 0 && newCount > 0 -> notifyItemRangeInserted(0, newCount)
                newCount == 0 && oldCount > 0 -> notifyItemRangeRemoved(0, oldCount)
                else -> notifyDataSetChanged()
            }
        } else {
            listItems = ArrayList()
            itemTypes = SparseArray()
            if(oldCount>0){
                notifyItemRangeRemoved(0,oldCount)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiBindHolder<Any> {
        val view = LayoutInflater.from(parent.context).inflate(viewType, parent, false)
        val holder = itemTypes.get(viewType).invoke(view)
        holder.mActivity = mActivity
        errorHandler?.let { holder.errorHandler = it }
        realm?.let { holder.realm = it }
        holder.onViewCreated()
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (position < listItems.size && position >= 0) {
            listItems[position].holderData.layoutId
        } else -1
    }

    override fun onBindViewHolder(holder: MultiBindHolder<Any>, position: Int) {
        holder.bind(listItems[position].data)
    }

    override fun onViewDetachedFromWindow(holder: MultiBindHolder<Any>) {
        super.onViewDetachedFromWindow(holder)
        holder.onDetached()
    }

    override fun getItemCount(): Int {
        return listItems.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }
}