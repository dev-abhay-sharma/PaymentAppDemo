package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.FULLTTFragmentGetSet;

import java.util.List;

public class FULLTTFragmentAdapter extends RecyclerView.Adapter<FULLTTFragmentAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<FULLTTFragmentGetSet> fullTT;
    private Context context;

    OnPlanClickListener listener;
    boolean isCheck= true;


    public FULLTTFragmentAdapter(Context ctx, List<FULLTTFragmentGetSet> fullTT, OnPlanClickListener listener) {
        this.inflater = LayoutInflater.from(ctx);
        this.fullTT = fullTT;
        this.context = ctx;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FULLTTFragmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.full_talk_time_list, parent, false);
        return new FULLTTFragmentAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull FULLTTFragmentAdapter.ViewHolder holder, int position) {
        holder.price.setText(getTextFromString(fullTT.get(position).getPrice()));
        holder.detail.setText(getTextFromString(fullTT.get(position).getDetails())+" ");

//        holder.detail.setOnClickListener(v -> {
//            if (isCheck) {
//                holder.detail.setMaxLines(10);
//                isCheck = false;
//            } else {
//                holder.detail.setMaxLines(2);
//                isCheck = true;
//            }
//        });

//        holder.detail.setText(Html.fromHtml(getTextFromString(fullTT.get(position).getDetails()) + "<font color='red'> <u>View More</u></font>"));

        holder.itemView.setOnClickListener(v -> {
            FULLTTFragmentGetSet data = fullTT.get(holder.getAdapterPosition());
            listener.onPlanClick(data);

//            context.startActivity(new Intent(context, Notification.class));

        });

    }

    @Override
    public int getItemCount() {
        return fullTT.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView price, detail;
        OnPlanClickListener listener;
        public ViewHolder(@NonNull View itemView, OnPlanClickListener listener) {
            super(itemView);
            this.listener = listener;
            price = itemView.findViewById(R.id.fullTTPrice);
            detail = itemView.findViewById(R.id.fullTTDetails);

        }
    }

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
    }

    public interface OnPlanClickListener {
        void onPlanClick(FULLTTFragmentGetSet planList);
    }

}
