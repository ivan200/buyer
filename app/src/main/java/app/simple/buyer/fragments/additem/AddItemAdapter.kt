package app.simple.buyer.fragments.additem

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.databinding.CellAddItemBinding
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.getColorCompat
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection
import io.realm.RealmResults

class AddItemAdapter(
    data: OrderedRealmCollection<BuyItem>,
    private val onItemClicked: Function1<Long, Unit>,
    private val onDelClicked: Function1<Long, Unit>
) : RealmRecyclerViewAdapter2<BuyItem, AddItemAdapter.FoodHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    var itemsData: RealmResults<BuyListItem>? = null
    fun itemsUpdated(itemsData: RealmResults<BuyListItem>) {
        this.itemsData = itemsData
        notifyDataSetChanged()
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

    inner class FoodHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val binding = CellAddItemBinding.bind(view)
        var currentItemId: Long = 0

        init {
            view.setOnClickListener {
                onItemClicked.invoke(currentItemId)
            }
            binding.btnDetete.setOnClickListener {
                onDelClicked.invoke(currentItemId)
            }
        }

        fun bind(data: BuyItem) {
            currentItemId = data.id

            val listItem = itemsData?.firstOrNull { it.buyItemId == data.id }

            binding.apply {
                val count = listItem?.count ?: 0
                tvTitle.text = data.name

                if (listItem?.isBuyed == true) {
                    tvTitle.setTextColor(view.context.getColorResCompat(R.attr.colorTextDisabled))
                    btnDetete.show().setImageResource(R.drawable.ic_clear)
                    if (count > 1) {
                        tvCount.show().setTextColor(view.context.getColorResCompat(R.attr.colorTextDisabled))
                        tvCount.text = count.toString()
                    } else {
                        tvCount.hide()
                    }
                    btnDetete.hide()
                } else {
                    when (count) {
                        0L -> {
                            tvTitle.setTextColor(view.context.getColorResCompat(R.attr.colorText))
                            btnDetete.hide()
                            tvCount.hide()
                        }
                        1L -> {
                            tvTitle.setTextColor(view.context.getColorCompat(R.color.colorCheckboxGreen))
                            btnDetete.show().setImageResource(R.drawable.ic_clear)
                            tvCount.hide()
                        }
                        else -> {
                            tvTitle.setTextColor(view.context.getColorCompat(R.color.colorCheckboxGreen))
                            btnDetete.show().setImageResource(R.drawable.ic_remove)
                            tvCount.show().setTextColor(view.context.getColorResCompat(R.attr.colorText))
                            tvCount.show().text = count.toString()
                        }
                    }
                }
            }
        }
    }
}