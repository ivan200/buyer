package app.simple.buyer.util.views

import android.view.View
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Zakharovi on 12.01.2018.
 */

//Вьюхолдер для MultiCellTypeAdapter'a, который сам обрабатывает метод биндингаобъекта на вьюшки
abstract class BindHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView) {

    abstract fun bind(obj: T)
}
