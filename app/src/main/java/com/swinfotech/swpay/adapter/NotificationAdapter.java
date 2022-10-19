package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.NotificationGetSet;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<NotificationGetSet> notify;
    Context context;

    public NotificationAdapter(Context context, List<NotificationGetSet> notify) {
        this.inflater = LayoutInflater.from(context);
        this.notify = notify;
        this.context = context;
    }

    @NonNull
    @Override
    public NotificationAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.notification_list, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationAdapter.ViewHolder holder, int position) {
        Glide.with(holder.itemView)
                .load(Constant.COMMISSION_IMAGES_BASE_URL + notify.get(position).getImage())
                .fitCenter()
                .into(holder.image);
    }

    @Override
    public int getItemCount() {
        return notify.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.notImg);

        }
    }

}
