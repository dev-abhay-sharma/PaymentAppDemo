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

import com.swinfotech.swpay.activities.OPListElectricity;
import com.swinfotech.swpay.model.BBPsCategorieGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import java.util.ArrayList;
import java.util.List;

public class BBPSCategorieAdapter extends RecyclerView.Adapter<BBPSCategorieAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<BBPsCategorieGetSet> categorieList;
    List<BBPsCategorieGetSet> tempCategorieList;
    Context context;

    public BBPSCategorieAdapter(Context context, List<BBPsCategorieGetSet> categorieList) {
        this.inflater = LayoutInflater.from(context);
        this.categorieList = categorieList;
        this.tempCategorieList = categorieList;
        this.context = context;
    }

    @NonNull
    @Override
    public BBPSCategorieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.bbps_categories_list, parent, false);
        return new BBPSCategorieAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BBPSCategorieAdapter.ViewHolder holder, int position) {
        holder.operatorName.setText(getTextFromString(categorieList.get(position).getOpName()));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, OPListElectricity.class);
                intent.putExtra("services_id", categorieList.get(position).getId());
                intent.putExtra("position", position);
                context.startActivity(intent);
            }
        });
//        if (isServiceId(categorieList.get(position).getId())){
//
//        } else if (isServiceId1(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListGas.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId2(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListWater.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId3(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListCableTV.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId4(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListInsurance.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId5(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListLandlinePostpaid.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId6(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListEMI.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        } else if (isServiceId7(categorieList.get(position).getId())) {
//            holder.itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
////                    Intent intent = new Intent(context, OPListElectricity.class);
//                    Intent intent = new Intent(context, OPListFastTag.class);
//                    intent.putExtra("services_id", categorieList.get(position).getId());
//                    context.startActivity(intent);
//                }
//            });
//
//        }


        String image = categorieList.get(position).getImage();
        // check if image is svg or not
        if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
            // make uri from url
            Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + categorieList.get(position).getImage());
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
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + categorieList.get(position).getImage())
                    .into(holder.image);
        }


    }

    @Override
    public int getItemCount() {
        return categorieList.size();
    }

    @Override
    public Filter getFilter() {
        return new BBPSFilter();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView operatorName, serviceId;
        ImageView image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            operatorName = itemView.findViewById(R.id.bbpsOpName);
            image = itemView.findViewById(R.id.bbpsImage);
            serviceId = itemView.findViewById(R.id.bbpsServiceId);

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

    private class BBPSFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();
            if (searchQuery.equalsIgnoreCase("")) {
                filterResults.values = tempCategorieList;
            } else {
                List<BBPsCategorieGetSet> list = new ArrayList<>();
                for (int i = 0; i < tempCategorieList.size(); i++) {
                    if (tempCategorieList.get(i).getOpName().toLowerCase().contains(searchQuery)) {
                        list.add(tempCategorieList.get(i));
                    }
                }
                filterResults.values = list;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            categorieList = (List<BBPsCategorieGetSet>) filterResults.values;
            notifyDataSetChanged();
        }
    }


    // List Of Operators

    // check he serviceId is 5 or not (Electricity)
    public boolean isServiceId(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("5");
    }

    // check he serviceId is 6 or not (Gas)
    public boolean isServiceId1(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("6");
    }

    // check he serviceId is 7 or not (Water)
    public boolean isServiceId2(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("7");
    }

    // check he serviceId is 15 or not (Cable Tv)
    public boolean isServiceId3(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("15");
    }

    // check he serviceId is 4 or not (Insurance)
    public boolean isServiceId4(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("4");
    }

    // check he serviceId is 12 or not (Landline Postpaid)
    public boolean isServiceId5(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("12");
    }

    // check he serviceId is 14 or not (EMI)
    public boolean isServiceId6(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("14");
    }

    // check he serviceId is 13 or not (Fast Tag)
    public boolean isServiceId7(String id) {
        // return true if status failed else return false if status success
        return id.equalsIgnoreCase("13");
    }

}
