package app.simple.buyer.util.views;

import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

/**
 * Created by Zakharovi on 12.01.2018.
 */

//Адаптер для списка с ячейками разных типов, с которым удобно работать(мне)
@SuppressWarnings("unchecked")
public class MultiCellTypeAdapter extends RecyclerView.Adapter<BindHolder> {
    private List<MultiCellObject> listItems = new ArrayList<>();
    private SparseArray<Func1<View, BindHolder>> itemTypes = new SparseArray<>();

    public MultiCellTypeAdapter(){
        super();
    }

    public void update(List<MultiCellObject> items)
    {
        if(items!= null) {
            this.listItems = items;
            for (MultiCellObject listItem : listItems) {
                itemTypes.put(listItem.getResource(), listItem.getOnCreateHolder());
            }
        } else {
            listItems = new ArrayList<>();
            itemTypes = new SparseArray<>();
        }
        notifyDataSetChanged();
    }

    @Override
    public BindHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return itemTypes.get(viewType).call(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
    }

    @Override
    public int getItemViewType(int position) {
        if(position<listItems.size()) {
            return listItems.get(position).getResource();
        }
        return -1;
    }

    @Override
    public void onBindViewHolder(BindHolder holder, int position) {
        holder.bind(listItems.get(position).getObject());
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}

