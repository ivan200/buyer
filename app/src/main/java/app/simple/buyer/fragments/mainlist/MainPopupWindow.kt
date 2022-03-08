package app.simple.buyer.fragments.mainlist

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.ViewGroup
import android.widget.PopupWindow
import androidx.lifecycle.LifecycleOwner
import app.simple.buyer.R
import app.simple.buyer.databinding.ViewMainSortBinding
import app.simple.buyer.entities.enums.CheckedPosition
import app.simple.buyer.entities.enums.OrderType
import app.simple.buyer.entities.enums.SortType
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.invisibleIf
import app.simple.buyer.util.showIf


/**
 * @author ivan200
 * @since 13.02.2022
 */
class MainPopupWindow(val binding: ViewMainSortBinding, val model: MainListViewModel, owner: LifecycleOwner) : PopupWindow(
    binding.root,
    ViewGroup.LayoutParams.WRAP_CONTENT,
    ViewGroup.LayoutParams.WRAP_CONTENT
) {
    init {
        isOutsideTouchable = true
        isFocusable = true
        setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        bind()

        model.listOrderChanged.observe(owner) {
            bind()
        }
        binding.checkboxContainer.setOnClickListener {
            model.toggleCheckedItems()
        }
        binding.ibOrder01.setOnClickListener { model.updateListItems(CheckedPosition.BOTTOM) }
        binding.ibOrder02.setOnClickListener { model.updateListItems(CheckedPosition.TOP) }
        binding.ibOrder03.setOnClickListener { model.updateListItems(CheckedPosition.BETWEEN) }

        binding.ibOrderCreate.setOnClickListener        { model.updateListItems(OrderType.CREATED) }
        binding.ibOrderModify.setOnClickListener        { model.updateListItems(OrderType.MODIFIED) }
        binding.ibOrderAlphabet.setOnClickListener      { model.updateListItems(OrderType.ALPHABET) }
        binding.ibOrderPopularity.setOnClickListener    { model.updateListItems(OrderType.POPULARITY) }
        binding.ibOrderSize.setOnClickListener          { model.updateListItems(OrderType.SIZE) }
        binding.ibOrderPrice.setOnClickListener         { model.updateListItems(OrderType.PRICE) }

        binding.checkbox.apply {
            setColors(
                context.getColorResCompat(R.attr.colorToolbarIcon),
                context.getColorResCompat(R.attr.colorPrimary),
                context.getColorResCompat(R.attr.colorDropdownBackground)
            )
        }
    }

    fun bind() {
        binding.apply {
            val checkedPosition = model.getItemsCheck()
            val isCheckedVisible = model.getShowCheckedItems()
            checkbox.setChecked(isCheckedVisible, true)

            listOf(rowChecked, rowDivider, rowCheckedSelection).showIf { isCheckedVisible }

            ibOrder01Selection.invisibleIf { checkedPosition != CheckedPosition.BOTTOM }
            ibOrder02Selection.invisibleIf { checkedPosition != CheckedPosition.TOP }
            ibOrder03Selection.invisibleIf { checkedPosition != CheckedPosition.BETWEEN }

            val itemsOrder = model.getItemsOrder()

            ibOrderCreateAsc        .invisibleIf { itemsOrder != OrderType.CREATED }
            ibOrderModifyAsc        .invisibleIf { itemsOrder != OrderType.MODIFIED }
            ibOrderAlphabetAsc      .invisibleIf { itemsOrder != OrderType.ALPHABET }
            ibOrderPopularityAsc    .invisibleIf { itemsOrder != OrderType.POPULARITY }
            ibOrderSizeAsc          .invisibleIf { itemsOrder != OrderType.SIZE }
            ibOrderPriceAsc         .invisibleIf { itemsOrder != OrderType.PRICE }

            val image = when(model.getItemsSort()){
                SortType.ASCENDING -> R.drawable.ic_arrow_order_asc
                SortType.DESCENDING -> R.drawable.ic_arrow_order_desc
            }
            val view = when(itemsOrder){
                OrderType.CREATED   -> ibOrderCreateAsc
                OrderType.MODIFIED  -> ibOrderModifyAsc
                OrderType.ALPHABET  -> ibOrderAlphabetAsc
                OrderType.POPULARITY-> ibOrderPopularityAsc
                OrderType.SIZE      -> ibOrderSizeAsc
                OrderType.PRICE     -> ibOrderPriceAsc
                OrderType.HAND ->null
            }
            view?.setImageResource(image)
        }
    }

}
