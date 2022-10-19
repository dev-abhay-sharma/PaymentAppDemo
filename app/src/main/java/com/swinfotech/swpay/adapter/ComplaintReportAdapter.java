package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.model.ComplaintReportGetSet;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class ComplaintReportAdapter extends RecyclerView.Adapter<ComplaintReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<ComplaintReportGetSet> CaReport;
    Context context;

    public ComplaintReportAdapter(Context context, List<ComplaintReportGetSet> CaReport) {
        this.inflater = LayoutInflater.from(context);
        this.CaReport = CaReport;
        this.context = context;
    }

    @NonNull
    @Override
    public ComplaintReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.complaint_report_list, parent, false);
        return new ComplaintReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComplaintReportAdapter.ViewHolder holder, int position) {
        holder.orId.setText(getTextFromString(CaReport.get(position).getOrID()));

//        holder.date.setText(getTextFromString(CaReport.get(position).getDate()));

        holder.date.setText(getTextFromString(getDateAndMonth(CaReport.get(position).getDate()))
                                                    +" "+ getTime(CaReport.get(position).getDate()));

        holder.mobNo.setText(getTextFromString(CaReport.get(position).getMobileNo()));

        holder.amount.setText(getTextFromString(getAmountFormat(CaReport.get(position).getAmount())));

//        holder.detail.setText(getTextFromString(CaReport.get(position).getDetails()));

        holder.operator.setText(getTextFromString(CaReport.get(position).getOperator()));
        holder.accountNo.setText(getTextFromString(CaReport.get(position).getAccountNo()));
        holder.remitter.setText(getTextFromString(CaReport.get(position).getRemitter()));

//        holder.support.setText(getTextFromString(CaReport.get(position).getSupportType()));

        holder.message.setText(getTextFromString(CaReport.get(position).getMessage()));

//        holder.status.setText(getTextFromString(CaReport.get(position).getStatus()));

        if (getTextFromString(CaReport.get(position).getStatus()).equalsIgnoreCase("1")) {
            holder.status.setText("Open");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.status.setText("Closed");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.statusSuccess.setText(getTextFromString(CaReport.get(position).getStatusSuccess()));

        if (getTextFromString(CaReport.get(position).getStatusSuccess()).equalsIgnoreCase("SUCCESS")) {
            holder.statusSuccess.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(CaReport.get(position).getStatusSuccess()).equalsIgnoreCase("PENDING")) {
            holder.statusSuccess.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }else if (getTextFromString(CaReport.get(position).getStatusSuccess()).equalsIgnoreCase("REFUND")) {
            holder.statusSuccess.setTextColor(ContextCompat.getColor(context, R.color.logo_blue));
        }else {
            holder.statusSuccess.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

    }

    @Override
    public int getItemCount() {
        return CaReport.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orId, date, mobNo, amount, detail, operator, accountNo, remitter, support, status, message, statusSuccess;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orId = itemView.findViewById(R.id.caOrderId);
            date = itemView.findViewById(R.id.caDate);
            mobNo = itemView.findViewById(R.id.caMobNo);
            amount = itemView.findViewById(R.id.caAmount);

//            detail = itemView.findViewById(R.id.caDetails);

            operator = itemView.findViewById(R.id.caOperator);
            accountNo = itemView.findViewById(R.id.caBankAcc);
            remitter = itemView.findViewById(R.id.caRemitter);

//            support = itemView.findViewById(R.id.caSupportType);

            status = itemView.findViewById(R.id.caStatus);
            message = itemView.findViewById(R.id.caMessage);
            statusSuccess = itemView.findViewById(R.id.caStatusSuccess);

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
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
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

}
