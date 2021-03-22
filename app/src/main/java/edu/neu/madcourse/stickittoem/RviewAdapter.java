package edu.neu.madcourse.stickittoem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RviewAdapter extends RecyclerView.Adapter<RviewHolder> {
    public List<UserItem> itemList;
    private ItemClickListener listener;

    public RviewAdapter(List<UserItem> itemList) {
        this.itemList = itemList;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new RviewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(RviewHolder holder, int position) {
        UserItem currentItem = itemList.get(position);

        holder.itemIcon.setImageResource(currentItem.getImageSource());
        holder.itemName.setText(currentItem.getUsername());
        holder.itemDesc.setText(currentItem.getDescription());
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public List<UserItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<UserItem> itemList) {
        this.itemList = itemList;
    }

    public ItemClickListener getListener() {
        return listener;
    }

    public void setListener(ItemClickListener listener) {
        this.listener = listener;
    }
}
