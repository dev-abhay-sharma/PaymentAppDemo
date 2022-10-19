package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.swinfotech.swpay.activities.BillDetails;
import com.swinfotech.swpay.model.OPListElectricityGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.util.ArrayList;
import java.util.List;

public class OPListElectricityAdapter extends RecyclerView.Adapter<OPListElectricityAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<OPListElectricityGetSet> opList;
    List<OPListElectricityGetSet> tempOpList;
    Context context;

    public OPListElectricityAdapter(Context context, List<OPListElectricityGetSet> opList) {
        this.inflater = LayoutInflater.from(context);
        this.opList = opList;
        this.tempOpList = opList;
        this.context = context;
    }

    @NonNull
    @Override
    public OPListElectricityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.op_list_electricity_list, parent, false);
        return new OPListElectricityAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OPListElectricityAdapter.ViewHolder holder, int position) {
        holder.operatorName.setText(getTextFromString(opList.get(position).getOpName()));

        String image = opList.get(position).getImage();
        // check if image is svg or not
        if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
            // make uri from url
            Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + opList.get(position).getImage());
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
        } else {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + opList.get(position).getImage())
                    .into(holder.image);
        }

        // Data goes in BillDetails
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, BillDetails.class);
                intent.putExtra("data", opList.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return opList.size();
    }

    @Override
    public Filter getFilter() {
        return new OPListFilter();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView operatorName;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            operatorName = itemView.findViewById(R.id.electricityOpName);
            image = itemView.findViewById(R.id.opImage);

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

    private class OPListFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();
            if (searchQuery.equalsIgnoreCase("")) {
                filterResults.values = tempOpList;
            } else {
                List<OPListElectricityGetSet> list = new ArrayList<>();
                for (int i = 0; i < tempOpList.size(); i++) {
                    if (tempOpList.get(i).getOpName().toLowerCase().contains(searchQuery)) {
                        list.add(tempOpList.get(i));
                    }
                }
                filterResults.values = list;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            opList = (List<OPListElectricityGetSet>) filterResults.values;
            notifyDataSetChanged();
        }
    }

}
