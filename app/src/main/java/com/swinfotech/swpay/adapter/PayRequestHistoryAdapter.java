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
import com.swinfotech.swpay.model.PayRequestHistoryGetSet;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class PayRequestHistoryAdapter extends RecyclerView.Adapter<PayRequestHistoryAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<PayRequestHistoryGetSet> PayReport;
    private Context context;

    public PayRequestHistoryAdapter(Context context, List<PayRequestHistoryGetSet> PayReport) {
        this.inflater = LayoutInflater.from(context);
        this.PayReport = PayReport;
        this.context = context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.pay_request_history_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txnNo.setText(getTextFromString(PayReport.get(position).getId()));

//        holder.payRTime.setText(getTextFromString(PayReport.get(position).getRequestTime()));
        holder.payRTime.setText(getTextFromString(getDateAndMonth(PayReport.get(position).getRequestTime()))
                                                    +" "+ getTime(PayReport.get(position).getRequestTime()));

        if (getTextFromString(PayReport.get(position).getStatus()).equalsIgnoreCase("1")) {
            holder.payRStatus.setText("Pending");
            holder.payRStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (getTextFromString(PayReport.get(position).getStatus()).equalsIgnoreCase("2")) {
            holder.payRStatus.setText("Approved");
            holder.payRStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(PayReport.get(position).getStatus()).equalsIgnoreCase("3")) {
            holder.payRStatus.setText("Decline");
            holder.payRStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.payRStatus.setText("Hold");
            holder.payRStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.payRAmount.setText(getTextFromString(PayReport.get(position).getAmount()));
        holder.payRBankName.setText(getTextFromString(PayReport.get(position).getBankName()));
        holder.payRName.setText(getTextFromString(PayReport.get(position).getParentName()));
        holder.payRMobile.setText(getTextFromString(PayReport.get(position).getParentMobile()));

//        holder.payTrDate.setText(getTextFromString(PayReport.get(position).getTrDate()));
        holder.payTrDate.setText(getTextFromString(getDateAndMonth(PayReport.get(position).getTrDate()))
                                                    +" "+ getTime(PayReport.get(position).getTrDate()));

//        holder.payRResponseTime.setText(getTextFromString(PayReport.get(position).getResponseTime()));
        holder.payRResponseTime.setText(getTextFromString(getDateAndMonth(PayReport.get(position).getResponseTime()))
                                                            +" "+ getTime(PayReport.get(position).getResponseTime()));


        holder.payTrNo.setText(getTextFromString(PayReport.get(position).getTrNo()));
        holder.payMode.setText(getTextFromString(PayReport.get(position).getPaymentMode()));
        holder.payRemarks.setText(getTextFromString(PayReport.get(position).getRemarks()));
    }

    @Override
    public int getItemCount() {
        return PayReport.size();
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
        TextView txnNo, payRTime, payRStatus, payRAmount, payRBankName, payRName, payRMobile, payTrDate, payRResponseTime, payTrNo, payMode, payRemarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txnNo = itemView.findViewById(R.id.payRequestId);
            payRTime = itemView.findViewById(R.id.payRequestTime);
            payRStatus = itemView.findViewById(R.id.payStatus);
            payRAmount = itemView.findViewById(R.id.payAmount);
            payRBankName = itemView.findViewById(R.id.payBankName);
            payRName = itemView.findViewById(R.id.payParentName);
            payRMobile = itemView.findViewById(R.id.payParentMobile);
            payTrDate = itemView.findViewById(R.id.payTrDate);
            payRResponseTime = itemView.findViewById(R.id.payResponseTime);
            payTrNo = itemView.findViewById(R.id.payRefNo);
            payMode = itemView.findViewById(R.id.payMode);
            payRemarks = itemView.findViewById(R.id.payRemarks);
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

}
