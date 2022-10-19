package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.OPCommissionGETSET;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.util.ArrayList;
import java.util.List;

public class OPCommissionAdapter extends RecyclerView.Adapter<OPCommissionAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<OPCommissionGETSET> commission;
    List<OPCommissionGETSET> tempListCommission;
    private Context context;


    public OPCommissionAdapter(Context ctx, List<OPCommissionGETSET> commission) {
        this.inflater = LayoutInflater.from(ctx);
        this.commission = commission;
        this.tempListCommission = commission;
        this.context = ctx;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.commission_list_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // bind the data
        holder.OperatorName.setText(commission.get(position).getOpName());
        holder.CommPer.setText(commission.get(position).getCommPer());
        holder.CommVal.setText(commission.get(position).getCommVal());
        holder.ServiceChPer.setText(commission.get(position).getServiceChargePer());
        holder.ServiceChVal.setText(commission.get(position).getServiceChargeVal());
        String image = commission.get(position).getImage();
        // check if image is svg or not
        if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
            // make uri from url
            Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage());
            // load image from uri using GlideToVector
            GlideToVectorYou
                    .init()
                    .with(context)
                    .withListener(new GlideToVectorYouListener() {
                        @Override
                        public void onLoadFailed() {
                        }

                        @Override
                        public void onResourceReady() {
                        }
                    })
                    .load(uri, holder.image);
        } else if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("png")) {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        } else if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("PNG")) {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        } else if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("jpg")) {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        } else if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("jpeg")) {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        } else if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("webp")) {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        } else {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + commission.get(position).getImage())
                    .into(holder.image);
        }
    }

    @Override
    public int getItemCount() {
        return commission.size();
    }

    @Override
    public Filter getFilter() {
        return new CommisionFilter();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView OperatorName, CommPer, CommVal, ServiceChPer, ServiceChVal;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            OperatorName = itemView.findViewById(R.id.commission_op);
            CommPer = itemView.findViewById(R.id.commission_per);
            CommVal = itemView.findViewById(R.id.commission_val);
            ServiceChPer = itemView.findViewById(R.id.commission_ch_per);
            ServiceChVal = itemView.findViewById(R.id.commission_ch_val);
            image = itemView.findViewById(R.id.image);

        }
    }

    private class CommisionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();
            if (searchQuery.equalsIgnoreCase("")) {
                filterResults.values = tempListCommission;
            } else {
                List<OPCommissionGETSET> list = new ArrayList<>();
                for (int i = 0; i < tempListCommission.size(); i++) {
                    if (tempListCommission.get(i).getOpName().toLowerCase().contains(searchQuery)) {
                        list.add(tempListCommission.get(i));
                    }
                }
                filterResults.values = list;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            commission = (List<OPCommissionGETSET>) filterResults.values;
            notifyDataSetChanged();
        }
    }


}
