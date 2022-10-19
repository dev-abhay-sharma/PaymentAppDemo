package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.DebitReportGetSet;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class DebitReportAdapter extends RecyclerView.Adapter<DebitReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<DebitReportGetSet> DbReport;

    public DebitReportAdapter(Context context, List<DebitReportGetSet> DbReport) {
        this.inflater = LayoutInflater.from(context);
        this.DbReport = DbReport;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.debit_report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.orderId.setText(getTextFromString(DbReport.get(position).getOrId()));

        holder.oldBalance.setText(getTextFromString(getAmountFormat(DbReport.get(position).getOldBal())));

        holder.netAmount.setText(getTextFromString(getAmountFormat(DbReport.get(position).getNetAmt())));

//        holder.newBalance.setText(getTextFromString(DbReport.get(position).getNewBal()));
        holder.newBalance.setText(getTextFromString(getAmountFormat(DbReport.get(position).getNewBal())));

//        holder.transactionDate.setText(getTextFromString(DbReport.get(position).getTrDate()));
        holder.transactionDate.setText(getTextFromString(getDateAndMonth(DbReport.get(position).getTrDate()))
                                                                        +" " + getTime(DbReport.get(position).getTrDate()));
        holder.trByName.setText(getTextFromString(DbReport.get(position).getNameTrBy()));
        holder.operator_name.setText(getTextFromString(DbReport.get(position).getOperatorName()));
        holder.remarks.setText(getTextFromString(DbReport.get(position).getRemarks()));
        holder.trByMob.setText(getTextFromString(DbReport.get(position).getMobileNo()));
    }

    @Override
    public int getItemCount() {
        return DbReport.size();
    }

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, oldBalance, netAmount, newBalance, transactionDate, trByName, operator_name, remarks, trByMob;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orderId = itemView.findViewById(R.id.dbOrderId);
            oldBalance = itemView.findViewById(R.id.dbOldBal);
            netAmount = itemView.findViewById(R.id.dbNetAmount);
            newBalance = itemView.findViewById(R.id.dbNewBal);
            transactionDate = itemView.findViewById(R.id.dbDate);
            trByName = itemView.findViewById(R.id.dbName);
            operator_name = itemView.findViewById(R.id.dbWallet);
            remarks = itemView.findViewById(R.id.dbRemarks);
            trByMob = itemView.findViewById(R.id.dbMobileNo);

        }
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
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }

}
