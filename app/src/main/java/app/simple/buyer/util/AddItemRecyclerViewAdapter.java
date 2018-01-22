package app.simple.buyer.util;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import app.simple.buyer.R;
import app.simple.buyer.entities.BuyItem;
import io.realm.OrderedRealmCollection;
import io.realm.RealmRecyclerViewAdapter;

/**
 * Created by Zakharovi on 22.01.2018.
 */

public class AddItemRecyclerViewAdapter extends RealmRecyclerViewAdapter<BuyItem, AddItemRecyclerViewAdapter.MyViewHolder> {

    public AddItemRecyclerViewAdapter(OrderedRealmCollection<BuyItem> data) {
        super(data, true);
        setHasStableIds(true);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_add_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final BuyItem obj = getItem(position);
        holder.buyItem = obj;
        //noinspection ConstantConditions
        holder.title.setText(obj.getName());
    }

    @Override
    public long getItemId(int index) {
        //noinspection ConstantConditions
        return getItem(index).getId();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        public BuyItem buyItem;

        MyViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
//            deletedCheckBox = (CheckBox) view.findViewById(R.id.checkBox);
        }
    }
}
