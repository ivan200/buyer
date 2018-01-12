package app.simple.buyer.util.views;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Zakharovi on 12.01.2018.
 */

//Вьюхолдер для MultiCellTypeAdapter'a, который сам обрабатывает метод биндингаобъекта на вьюшки
public abstract class BindHolder<T> extends RecyclerView.ViewHolder {
    public BindHolder(View itemView) {
        super(itemView);
    }

    public abstract void bind(T object);
}
