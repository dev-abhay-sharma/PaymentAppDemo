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
import com.swinfotech.swpay.model.OtherComplaintReportGetSet;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class OtherComplaintReportAdapter extends RecyclerView.Adapter<OtherComplaintReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<OtherComplaintReportGetSet> otherCaList;
    Context context;

    public OtherComplaintReportAdapter(Context context, List<OtherComplaintReportGetSet> otherCaList) {
        this.inflater = LayoutInflater.from(context);
        this.otherCaList = otherCaList;
        this.context = context;
    }

    @NonNull
    @Override
    public OtherComplaintReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.other_complaint_report_list, parent, false);
        return new OtherComplaintReportAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OtherComplaintReportAdapter.ViewHolder holder, int position) {
        holder.orId.setText(getTextFromString(otherCaList.get(position).getId()));
        holder.supportType.setText(getTextFromString(otherCaList.get(position).getSupportType()));
        holder.msg.setText(getTextFromString(otherCaList.get(position).getMessage()));

//        holder.status.setText(getTextFromString(otherCaList.get(position).getStatus()));
        if (getTextFromString(otherCaList.get(position).getStatus()).equalsIgnoreCase("1")) {
            holder.status.setText("Open");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else {
            holder.status.setText("Closed");
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

//        holder.addDate.setText(getTextFromString(otherCaList.get(position).getAddDate()));
        holder.addDate.setText(getTextFromString(getDateAndMonth(otherCaList.get(position).getAddDate()))
                +" "+ getTime(otherCaList.get(position).getAddDate()));

        holder.response.setText(getTextFromString(otherCaList.get(position).getResponse()));
        holder.replyDate.setText(getTextFromString(otherCaList.get(position).getReplyDate()));


    }

    @Override
    public int getItemCount() {
        return otherCaList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView orId, supportType, msg, status, addDate, response, replyDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            orId = itemView.findViewById(R.id.caOtherOrderId);
            supportType = itemView.findViewById(R.id.caOtherSupport);
            msg = itemView.findViewById(R.id.caOtherMessage);
            status = itemView.findViewById(R.id.caOtherStatus);
            addDate = itemView.findViewById(R.id.caOtherAddDate);
            response = itemView.findViewById(R.id.caOtherResponse);
            replyDate = itemView.findViewById(R.id.caOtherReplyDate);

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

}
