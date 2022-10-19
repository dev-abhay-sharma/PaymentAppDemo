package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.SalesReportGetSet;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class DownlineSalesReportAdapter extends RecyclerView.Adapter<DownlineSalesReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<SalesReportGetSet> salesReport;
    Context context;

    public DownlineSalesReportAdapter(Context context, List<SalesReportGetSet> salesReport) {
        this.inflater = LayoutInflater.from(context);
        this.salesReport = salesReport;
        this.context = context;
    }

    @NonNull
    @Override
    public DownlineSalesReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.sales_report_list, parent, false);
        return new DownlineSalesReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DownlineSalesReportAdapter.ViewHolder holder, int position) {
        holder.id.setText(getTextFromString(salesReport.get(position).getId()));
        holder.successAmt.setText(getTextFromString(getAmountFormat(salesReport.get(position).getAmtSuccess())));
        holder.name.setText(getTextFromString(salesReport.get(position).getuName()));
        holder.mobile.setText(getTextFromString(salesReport.get(position).getMobile()));
        holder.bal.setText(getTextFromString(getAmountFormat(salesReport.get(position).getBalAmt())));
    }

    @Override
    public int getItemCount() {
        return salesReport.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView id, successAmt, name, mobile, bal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.salesId);
            successAmt = itemView.findViewById(R.id.salesSuccessAmt);
            name = itemView.findViewById(R.id.salesUserName);
            mobile = itemView.findViewById(R.id.salesMobNo);
            bal = itemView.findViewById(R.id.salesBalAmt);
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

    private String getAmountFormat(String data) {
        if (data == null) {
            return "0.0";
        }
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }

}
