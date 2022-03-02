package app.simple.buyer.fragments.additem

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.base.ItemAction
import app.simple.buyer.databinding.CellAddItemBinding
import app.simple.buyer.entities.BuyItem
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.util.getColorCompat
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class AddItemAdapter(
    data: OrderedRealmCollection<BuyItem>,
    private val onItemAction: Function2<ItemAction, Long, Unit>
) : RealmRecyclerViewAdapter2<BuyItem, AddItemAdapter.FoodHolder>(data, true) {
    init {
        setHasStableIds(true)
    }

    var itemsData: OrderedRealmCollection<BuyListItem>? = null
    fun itemsUpdated(itemsData: OrderedRealmCollection<BuyListItem>) {
        this.itemsData = itemsData
        notifyDataSetChanged()
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHolder {
        val binding = CellAddItemBinding.inflate(LayoutInflater.from(parent.context),  parent, false)
        return FoodHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class FoodHolder(val binding: CellAddItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var currentItemId: Long = 0

        init {
            binding.root.setOnClickListener {
                onItemAction.invoke(ItemAction.CLICK, currentItemId)
            }
            binding.root.setOnLongClickListener {
                onItemAction.invoke(ItemAction.LONG_CLICK, currentItemId)
                return@setOnLongClickListener true
            }
            binding.btnDetete.setOnClickListener {
                onItemAction.invoke(ItemAction.OPTIONAL_CLICK, currentItemId)
            }
        }

        fun bind(data: BuyItem) {
            currentItemId = data.id
            val context = binding.root.context

            val listItem = itemsData?.firstOrNull { it.buyItemId == data.id }

            binding.apply {
                val count = listItem?.count ?: 0
                tvTitle.text = data.name

                if (listItem?.isBuyed == true) {
                    tvTitle.setTextColor(context.getColorResCompat(R.attr.colorTextDisabled))
                    btnDetete.show().setImageResource(R.drawable.ic_clear)
                    if (count > 1) {
                        tvCount.show().setTextColor(context.getColorResCompat(R.attr.colorTextDisabled))
                        tvCount.text = count.toString()
                    } else {
                        tvCount.hide()
                    }
                    btnDetete.hide()
                } else {
                    when (count) {
                        0L -> {
                            tvTitle.setTextColor(context.getColorResCompat(R.attr.colorText))
                            btnDetete.hide()
                            tvCount.hide()
                        }
                        1L -> {
                            tvTitle.setTextColor(context.getColorCompat(R.color.colorCheckboxGreen))
                            btnDetete.show().setImageResource(R.drawable.ic_clear)
                            tvCount.hide()
                        }
                        else -> {
                            tvTitle.setTextColor(context.getColorCompat(R.color.colorCheckboxGreen))
                            btnDetete.show().setImageResource(R.drawable.ic_minus)
                            tvCount.show().setTextColor(context.getColorResCompat(R.attr.colorText))
                            tvCount.show().text = count.toString()
                        }
                    }
                }
            }
        }
    }
}