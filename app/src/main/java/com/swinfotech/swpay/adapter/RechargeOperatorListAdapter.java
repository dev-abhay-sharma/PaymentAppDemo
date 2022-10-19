package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.NewOPListGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.util.List;

public class RechargeOperatorListAdapter extends RecyclerView.Adapter<RechargeOperatorListAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<NewOPListGetSet> opList;
    Context context;

    OnTextClickListener listener;

    public RechargeOperatorListAdapter(Context context, List<NewOPListGetSet> opList, OnTextClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.opList = opList;
        this.listener = listener;
    }


    @NonNull
    @Override
    public RechargeOperatorListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.new_operator_list, parent, false);
        return new RechargeOperatorListAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RechargeOperatorListAdapter.ViewHolder holder, int position) {
        holder.operator.setText(getTextFromString(opList.get(position).getOpName()));

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
                    .load(uri, holder.imageView);
        } else {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + opList.get(position).getImage())
                    .into(holder.imageView);
        }

//        NewOPListGetSet data = opList.get(position);
//        listener.onTextClick(data);

//        NewOPListGetSet data = opList.get(position);


        holder.itemView.setOnClickListener(v -> {
            NewOPListGetSet data = opList.get(holder.getAdapterPosition());
            listener.onTextClick(data);

        });

    }

    @Override
    public int getItemCount() {
        return opList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView operator;
        OnTextClickListener listener;
        public ViewHolder(@NonNull View itemView, OnTextClickListener listener) {
            super(itemView);
            this.listener = listener;
            imageView = itemView.findViewById(R.id.opImageList);
            operator = itemView.findViewById(R.id.opNameList);

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


    public interface OnTextClickListener {
        void onTextClick(NewOPListGetSet opList);
    }

}
