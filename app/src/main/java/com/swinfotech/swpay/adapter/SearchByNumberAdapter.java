package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.activities.RaiseComplaint;
import com.swinfotech.swpay.activities.TransactionSuccess;
import com.swinfotech.swpay.model.SearchByNumberGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class SearchByNumberAdapter extends RecyclerView.Adapter<SearchByNumberAdapter.ViewHolder> {


    LayoutInflater inflater;
    List<SearchByNumberGetSet> SearchTReport;
    Context context;


    public SearchByNumberAdapter(Context context, List<SearchByNumberGetSet> SearchTReport) {
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.SearchTReport = SearchTReport;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.search_by_number_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchByNumberAdapter.ViewHolder holder, int position) {
        // bind the data
        holder.MobNum.setText(SearchTReport.get(position).getPhNum());
        holder.amt.setText(getAmountFormat(SearchTReport.get(position).getAmount()));
        if (isFailed(SearchTReport.get(position).getStatus())) {
            holder.status.setText(SearchTReport.get(position).getStatus());
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
            holder.raise_btn.setVisibility(View.GONE);
        } else {
            if (SearchTReport.get(position).getOpName().equalsIgnoreCase("Wallet Credit") || SearchTReport.get(position).getOpName().equalsIgnoreCase("Wallet Debit")) {
                holder.raise_btn.setVisibility(View.GONE);
            } else {
                holder.raise_btn.setVisibility(View.VISIBLE);
            }
            if (SearchTReport.get(position).getStatus().equalsIgnoreCase("Pending")){
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
            }else {
                holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
            }
            holder.status.setText(SearchTReport.get(position).getStatus());
        }
//        holder.date.setText(SearchTReport.get(position).getDate());
        holder.date.setText(getDateAndMonth(SearchTReport.get(position).getDate())
                                +" "+ getTime(SearchTReport.get(position).getDate()));
        holder.odNo.setText(SearchTReport.get(position).getTrId());
        holder.operator.setText(SearchTReport.get(position).getOpName());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransactionSuccess.class);
                intent.putExtra("data", SearchTReport.get(position));
                context.startActivity(intent);
            }
        });

        holder.raise_btn.setOnClickListener(v -> {
            Intent intent = new Intent(context, RaiseComplaint.class);
            intent.putExtra("tr_id", SearchTReport.get(position).getId());
            intent.putExtra("amount", SearchTReport.get(position).getAmount());
            context.startActivity(intent);
        });


        String image = SearchTReport.get(position).getImage();
        if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
            // make uri from url
            Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + SearchTReport.get(position).getImage());
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
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + SearchTReport.get(position).getImage())
                    .into(holder.image);
        }

    }

    // check he status is failed or not
    public boolean isFailed(String status) {
        // return true if status failed else return false if status success
        return status.equalsIgnoreCase("FAILED");
    }


    @Override
    public int getItemCount() {
        return SearchTReport.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView odNo, MobNum, amt, operator, date, status;
        Button raise_btn;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            odNo = itemView.findViewById(R.id.reOrder_no);
            MobNum = itemView.findViewById(R.id.reMobNum);
            status = itemView.findViewById(R.id.reStatus);
            amt = itemView.findViewById(R.id.reAmount);
            operator = itemView.findViewById(R.id.reOperator);
            date = itemView.findViewById(R.id.reDateTime);
            raise_btn = itemView.findViewById(R.id.raise_btn);
            image = itemView.findViewById(R.id.image_search);


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
        if (data == null) {
            return "0.0";
        }
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
    }

}










