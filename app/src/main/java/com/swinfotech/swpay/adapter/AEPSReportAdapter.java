package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.model.AEPSReportGetSet;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class AEPSReportAdapter extends RecyclerView.Adapter<AEPSReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<AEPSReportGetSet> viewReport;
    Context context;
    Session session;

    public AEPSReportAdapter(Context context, List<AEPSReportGetSet> viewReport) {
        this.inflater = LayoutInflater.from(context);
        this.viewReport = viewReport;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public AEPSReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.aeps_report_list, parent, false);
        return new AEPSReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AEPSReportAdapter.ViewHolder holder, int position) {

        holder.orderId.setText(getTextFromString(viewReport.get(position).getOrderId()));

        holder.date.setText(getTextFromString(getDateAndMonth(viewReport.get(position).getDate()))
                + " " + getTime(viewReport.get(position).getDate()));

        holder.oldBal.setText(getTextFromString(getAmountFormat(viewReport.get(position).getOldBal())));
        holder.newBal.setText(getTextFromString(getAmountFormat(viewReport.get(position).getNewBal())));

//        holder.txnType.setText(getTextFromString(viewReport.get(position).getTxnType()));

        holder.netAmount.setText(getTextFromString(getAmountFormat(viewReport.get(position).getNetAmount())));
        holder.operator.setText(getTextFromString(viewReport.get(position).getOperator()));

        holder.status.setText(getTextFromString(viewReport.get(position).getStatus()));

        if (getTextFromString(viewReport.get(position).getStatus()).equalsIgnoreCase("SUCCESS")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(viewReport.get(position).getStatus()).equalsIgnoreCase("PENDING")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if (getTextFromString(viewReport.get(position).getStatus()).equalsIgnoreCase("REFUND")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.amount.setText(getTextFromString(getAmountFormat(viewReport.get(position).getAmount())));
        holder.trName.setText(getTextFromString(viewReport.get(position).getTrName()));

//        holder.merchantNo.setText(getTextFromString(viewReport.get(position).getMerchantNo()));

        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("3")) {
            holder.newBalanceLt.setVisibility(View.VISIBLE);
            holder.oldBalanceLyt.setVisibility(View.VISIBLE);
            holder.amountLyt.setVisibility(View.VISIBLE);
        } else {
            holder.newBalanceLt.setVisibility(View.INVISIBLE);
            holder.oldBalanceLyt.setVisibility(View.INVISIBLE);
            holder.amountLyt.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return viewReport.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, date, oldBal, newBal, txnType, netAmount, operator, status, amount, trName, merchantNo;
        LinearLayout newBalanceLt, oldBalanceLyt, amountLyt;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.aepsOrderId);
            date = itemView.findViewById(R.id.aepsDate);
            oldBal = itemView.findViewById(R.id.aepsOldBal);
            newBal = itemView.findViewById(R.id.aepsNewBal);

//            txnType = itemView.findViewById(R.id.aepsTxnType);

            netAmount = itemView.findViewById(R.id.aepsNetAmount);
            operator = itemView.findViewById(R.id.aepsOpName);
            status = itemView.findViewById(R.id.aepsStatus);
            amount = itemView.findViewById(R.id.aepsAmount1);
            trName = itemView.findViewById(R.id.aepsTrName);
            amountLyt = itemView.findViewById(R.id.amountLyt);
            oldBalanceLyt = itemView.findViewById(R.id.oldBalanceLyt);
            newBalanceLt = itemView.findViewById(R.id.newBalanceLt);
//            merchantNo = itemView.findViewById(R.id.aepsMerchantNo);

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

    public static String getDateAndMonth(String fullDate) {
        String day = "", month = "", year = "";
        try {
            // getting day from date
            day = StringUtils.substringBetween(fullDate, "-", "T");
            // split the extra text from date
            day = day.substring(day.lastIndexOf("-") + 1);
            year = fullDate.substring(0, 4);
            String demoMonth = StringUtils.substringBetween(fullDate, "-", "-");
            if (demoMonth.equalsIgnoreCase("01") || demoMonth.equalsIgnoreCase("1")) {
                month = "Jan";
            }
            if (demoMonth.equalsIgnoreCase("02") || demoMonth.equalsIgnoreCase("2")) {
                month = "Feb";
            }
            if (demoMonth.equalsIgnoreCase("03") || demoMonth.equalsIgnoreCase("3")) {
                month = "March";
            }
            if (demoMonth.equalsIgnoreCase("04") || demoMonth.equalsIgnoreCase("4")) {
                month = "April";
            }
            if (demoMonth.equalsIgnoreCase("05") || demoMonth.equalsIgnoreCase("5")) {
                month = "May";
            }
            if (demoMonth.equalsIgnoreCase("06") || demoMonth.equalsIgnoreCase("6")) {
                month = "June";
            }
            if (demoMonth.equalsIgnoreCase("07") || demoMonth.equalsIgnoreCase("7")) {
                month = "July";
            }
            if (demoMonth.equalsIgnoreCase("08") || demoMonth.equalsIgnoreCase("8")) {
                month = "August";
            }
            if (demoMonth.equalsIgnoreCase("09") || demoMonth.equalsIgnoreCase("9")) {
                month = "September";
            }
            if (demoMonth.equalsIgnoreCase("10")) {
                month = "October";
            }
            if (demoMonth.equalsIgnoreCase("11")) {
                month = "November";
            }
            if (demoMonth.equalsIgnoreCase("12")) {
                month = "December";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("TAG", "getDateAndMonth: " + day + " month " + month + " year " + year);
        return day + " " + month + " " + year;
    }

    public String getTime(String startTime) {
        // get the date from time
        String date1 = StringUtils.substringBetween(startTime, "", "T");
        // print the value you can see the actual result
        Log.e("DATE", "onBindViewHolder: " + " date " + date1 + " " + startTime + " time ");
        // get the time from date
        startTime = StringUtils.substringBetween(startTime, "T", ".");
        Log.e("DATE", "onBindViewHolder: " + startTime + " after time ");
        try {
            StringTokenizer tk = new StringTokenizer(date1 + " " + startTime);
            String date = tk.nextToken();
            String time = tk.nextToken();
            Log.e("DATE", "onBindViewHolder: " + date + " time " + time);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
            SimpleDateFormat sdfs = new SimpleDateFormat("hh:mm a");
            Date dt = new Date();
            dt = sdf.parse(time);
            System.out.println("Time Display: " + sdfs.format(dt));
            return sdfs.format(dt);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getAmountFormat(String data) {
        String newInput = "0.0";
        try {
            if (data == null) {
                return "0.0";
            }
            BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
            newInput = String.valueOf(bd.doubleValue());
            return newInput;
        } catch (Exception e) {
            Log.e("TAG", "getAmountFormat: " + e.getLocalizedMessage());
        }
        return newInput;
    }

}
