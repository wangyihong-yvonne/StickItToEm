package edu.neu.madcourse.stickittoem;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class RviewHolder extends RecyclerView.ViewHolder {
    public ImageView itemIcon;
    public TextView itemName;
    public TextView itemDesc;

    public RviewHolder(View itemView, final ItemClickListener listener) {
        super(itemView);
        itemIcon = itemView.findViewById(R.id.user_icon);
        itemName = itemView.findViewById(R.id.user_name);
        itemDesc = itemView.findViewById(R.id.user_desc);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    int position = getLayoutPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(position);
                    }
                }
            }
        });
    }
}