package app.simple.buyer.fragments.mainlist

import android.content.Context
import android.util.TypedValue
import android.view.Gravity
import android.view.Gravity.CENTER_VERTICAL
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.LinearLayout.HORIZONTAL
import android.widget.LinearLayout.LayoutParams
import android.widget.LinearLayout.VERTICAL
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import app.simple.buyer.R
import app.simple.buyer.entities.BuyListItem
import app.simple.buyer.entities.enums.ActionModeType
import app.simple.buyer.fragments.mainlist.MainListAdapter.MainListItemMaker.Companion.id_checkBoxSquare
import app.simple.buyer.fragments.mainlist.MainListAdapter.MainListItemMaker.Companion.id_tvCount
import app.simple.buyer.fragments.mainlist.MainListAdapter.MainListItemMaker.Companion.id_tvSubtitle
import app.simple.buyer.fragments.mainlist.MainListAdapter.MainListItemMaker.Companion.id_tvTitle
import app.simple.buyer.util.getColorResCompat
import app.simple.buyer.util.getDimensionPx
import app.simple.buyer.util.getDrawableCompat
import app.simple.buyer.util.getResCompat
import app.simple.buyer.util.hide
import app.simple.buyer.util.show
import app.simple.buyer.util.views.CheckBoxSquare
import app.simple.buyer.util.views.RealmRecyclerViewAdapter2
import io.realm.OrderedRealmCollection

class MainListAdapter(
    context: Context,
    data: OrderedRealmCollection<BuyListItem>,
    val onItemSelected: Function2<Long, ActionModeType, Unit>,
    val onItemLongClick: Function2<Long, ActionModeType, Unit>,
    var actionModeType: ActionModeType
) : RealmRecyclerViewAdapter2<BuyListItem, MainListAdapter.MainListHolder>(data, true) {

    val mainListItemMaker = MainListItemMaker(context)

    init {
        setHasStableIds(true)
    }

    override fun getItemId(index: Int): Long {
        return getItem(index)!!.id
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        val view = mainListItemMaker.make()
        return MainListHolder(view)
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    inner class MainListHolder(val rootView: View) :
        RecyclerView.ViewHolder(rootView),
        View.OnClickListener,
        View.OnLongClickListener {
        private var itemListId: Long = 0

        init {
            rootView.setOnClickListener(this)
            rootView.setOnLongClickListener(this)
        }

        fun bind(data: BuyListItem) {
            itemListId = data.id

            val tvTitle = rootView.findViewById<TextView>(id_tvTitle)
            val tvSubtitle = rootView.findViewById<TextView>(id_tvSubtitle)
            val tvCount = rootView.findViewById<TextView>(id_tvCount)
            val checkbox = rootView.findViewById<CheckBoxSquare>(id_checkBoxSquare)

            tvTitle.text = data.buyItem?.name

            if (data.count <= 1L) {
                tvCount.hide()
            } else {
                tvCount.show()
                tvCount.text = data.count.toString()
            }

            checkbox.setChecked(data.isBuyed, true)

            rootView.isSelected = actionModeType != ActionModeType.NO && data.isSelected

            if (data.comment.isNullOrEmpty()) {
                tvSubtitle.hide()
            } else {
                tvSubtitle.text = data.comment
                tvSubtitle.show()
            }
        }

        override fun onClick(v: View?) {
            onItemSelected.invoke(itemListId, actionModeType)
        }

        override fun onLongClick(v: View?): Boolean {
            onItemLongClick.invoke(itemListId, actionModeType)
            return true
        }
    }

    /**
     * Программное создание элементов главного списка
     * то же самое, что и cell_main_list, но работает намного быстрее.
     * главный список перестал лагать при перевыборе другого списка.
     */
    class MainListItemMaker(val context: Context) {
        val preferredHeight = TypedValue.complexToDimension(
            context.getResCompat(android.R.attr.listPreferredItemHeightSmall),
            context.resources.displayMetrics
        ).toInt()
        val clickableArea = context.getDimensionPx(R.dimen.size_icon_clickable_area).toInt()
        val sizeCheckbox = context.getDimensionPx(R.dimen.size_checkbox).toInt()
        val marginIconBounding = context.getDimensionPx(R.dimen.margin_icon_bounding).toInt()
        val marginHalf = context.getDimensionPx(R.dimen.margin_half).toInt()
        val noMargin = context.getDimensionPx(R.dimen.no_margin).toInt()
        val marginDefault = context.getDimensionPx(R.dimen.margin_default).toInt()
        val colorText = context.getColorResCompat(R.attr.colorText)
        val colorTextSecondary = context.getColorResCompat(R.attr.colorTextSecondary)
        val textListItemDefault = context.getDimensionPx(R.dimen.text_list_item_default)
        val selectableBgAttr = context.getResCompat(R.attr.bgSelectable)

        companion object {
            val id_root = ViewCompat.generateViewId()
            val id_checkBoxSquare = ViewCompat.generateViewId()
            val id_tvTitle = ViewCompat.generateViewId()
            val id_tvSubtitle = ViewCompat.generateViewId()
            val id_tvCount = ViewCompat.generateViewId()
        }

        fun make(): View = LinearLayout(context).apply {
            id = id_root
            // Нельзя сразу получить drawable и сохранять его в классе 8/,
            // при одинаковом drawable для всех ячеек, почему то ломается их выделение и клики
            // Приходится переполучать его каждый раз
            background = context.getDrawableCompat(selectableBgAttr)
            isBaselineAligned = false
            isClickable = true
            isFocusable = true
            minimumHeight = preferredHeight
            orientation = HORIZONTAL
            layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)

            val checkboxContainer = LinearLayout(context).apply {
                gravity = Gravity.CENTER
                visibility = View.VISIBLE
                layoutParams = LayoutParams(clickableArea, clickableArea).apply {
                    marginStart = marginIconBounding
                    marginEnd = marginIconBounding
                    leftMargin = marginIconBounding
                    rightMargin = marginIconBounding
                }

                val checkBoxSquare = CheckBoxSquare(context).apply {
                    id = id_checkBoxSquare
                    layoutParams = LayoutParams(sizeCheckbox, sizeCheckbox)
                }
                addView(checkBoxSquare)
            }
            addView(checkboxContainer)

            val tvTitleContainer = LinearLayout(context).apply {
                orientation = VERTICAL
                setPaddingRelative(marginHalf, marginHalf, marginDefault, marginHalf)
                setPadding(marginHalf, marginHalf, marginDefault, marginHalf)
                layoutParams = LayoutParams(0, LayoutParams.WRAP_CONTENT).apply {
                    gravity = CENTER_VERTICAL
                    weight = 1f
                }

                val tvTitle = TextView(context).apply {
                    id = id_tvTitle
                    gravity = CENTER_VERTICAL
                    setTextColor(colorText)
                    setTextSize(TypedValue.COMPLEX_UNIT_PX, textListItemDefault)
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                        gravity = CENTER_VERTICAL
                    }
                }
                addView(tvTitle)

                val tvSubtitle = TextView(context).apply {
                    id = id_tvSubtitle
                    gravity = CENTER_VERTICAL
                    setTextColor(colorTextSecondary)
                    visibility = GONE
                    layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                        gravity = CENTER_VERTICAL
                    }
                }
                addView(tvSubtitle)
            }
            addView(tvTitleContainer)

            val tvCount = TextView(context).apply {
                id = id_tvCount
                gravity = CENTER_VERTICAL
                setTextColor(colorTextSecondary)
                setPaddingRelative(noMargin, marginHalf, marginDefault, marginHalf)
                setPadding(noMargin, marginHalf, marginDefault, marginHalf)
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textListItemDefault)
                visibility = GONE
                layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, clickableArea).apply {
                    gravity = CENTER_VERTICAL
                }
            }
            addView(tvCount)
        }
    }
}
