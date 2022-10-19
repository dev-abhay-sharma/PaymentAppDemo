package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.activities.CreditBalance;
import com.swinfotech.swpay.model.CreditBalUsersListGetSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class CreditBalUsersAdapter extends RecyclerView.Adapter<CreditBalUsersAdapter.ViewHolder> implements Filterable {

    LayoutInflater inflater;
    List<CreditBalUsersListGetSet> CreditList;
    List<CreditBalUsersListGetSet> tempCreditList;
    private Context context;

    public CreditBalUsersAdapter(Context context, List<CreditBalUsersListGetSet> CreditList) {
        this.inflater = LayoutInflater.from(context);
        this.CreditList = CreditList;
        this.tempCreditList = CreditList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.credit_bal_users_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.uName.setText(getTextFromString(CreditList.get(position).getUserName()));
        holder.uMobile.setText(getTextFromString(CreditList.get(position).getUserMobile()));
        holder.amount.setText(getTextFromString(getAmountFormat(CreditList.get(position).getAmount())));

        // when user click on any name or phone
        holder.itemView.setOnClickListener(v -> {
            // then we paas this data to our activity so we can set data to textview

            // pass whole object to our activity so we can use all the data of it or paas position to this method because we have
            // same list in our activity so we can get from our list

            // so i decided to pass the position for now

            // call method of activity from adapter like this type cast the context
            ((CreditBalance) context).setNameToActivity(position);

        });

    }


    @Override
    public int getItemCount() {
        return CreditList.size();
    }

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
    }

    @Override
    public Filter getFilter() {
        return new CommisionFilter();
    }

    private class CommisionFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence charSequence) {
            String searchQuery = charSequence.toString().toLowerCase();
            FilterResults filterResults = new FilterResults();
            if (searchQuery.equalsIgnoreCase("")) {
                filterResults.values = tempCreditList;
            } else {
                List<CreditBalUsersListGetSet> list = new ArrayList<>();
                for (int i = 0; i < tempCreditList.size(); i++) {
                    if (tempCreditList.get(i).getUserMobile().toLowerCase().contains(searchQuery)) {
                        list.add(tempCreditList.get(i));
                    }
                }
                filterResults.values = list;
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
            CreditList = (List<CreditBalUsersListGetSet>) filterResults.values;
            notifyDataSetChanged();
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView uName, uMobile, amount;
        LinearLayout Data;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            uName = itemView.findViewById(R.id.nameVu);
            uMobile = itemView.findViewById(R.id.mobileVu);

            Data = itemView.findViewById(R.id.data);
            amount = itemView.findViewById(R.id.amountVu);

        }
    }

    private String getAmountFormat(String data) {
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }


}
