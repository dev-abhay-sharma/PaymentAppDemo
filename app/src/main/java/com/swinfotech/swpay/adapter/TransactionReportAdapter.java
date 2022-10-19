package com.swinfotech.swpay.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.activities.TransactionReportSuccess;
import com.swinfotech.swpay.model.TransactionReportGetSet;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYou;
import com.github.twocoffeesoneteam.glidetovectoryou.GlideToVectorYouListener;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

public class TransactionReportAdapter extends RecyclerView.Adapter<TransactionReportAdapter.ViewHolder>{

    LayoutInflater inflater;
    List<TransactionReportGetSet> trReport;
    List<TransactionReportGetSet> trReportFilter;
    Context context;

    public TransactionReportAdapter(Context context, List<TransactionReportGetSet> trReport) {
        this.inflater = LayoutInflater.from(context);
        this.trReport = trReport;
        this.trReportFilter = trReport;
        this.context = context;
    }

    @NonNull
    @Override
    public TransactionReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.transaction_report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionReportAdapter.ViewHolder holder, int position) {
        holder.trId.setText(getTextFromString(trReport.get(position).getTrId()));

        holder.trDate.setText(getTextFromString(trReport.get(position).getTrDate()));
//        holder.trDate.setText(getTextFromString(getDateAndMonth(trReport.get(position).getTrDate()))
//                                                    +" "+ getTime(trReport.get(position).getTrDate()));

//        holder.oldBal.setText(getTextFromString(getAmountFormat(trReport.get(position).getOldBal())));

        holder.amount.setText(getTextFromString(getAmountFormat(trReport.get(position).getAmount())));

//        holder.netAmount.setText(getTextFromString(getAmountFormat(trReport.get(position).getNetAmount())));

        holder.newBal.setText(getTextFromString(getAmountFormat(trReport.get(position).getNewBal())));

//        holder.opRef.setText(getTextFromString(trReport.get(position).getOpRef()));
//        holder.apiRef.setText(getTextFromString(trReport.get(position).getApiRef()));

        holder.rechargeNo.setText(getTextFromString(trReport.get(position).getRechargeNum()));
        holder.opName.setText(getTextFromString(trReport.get(position).getOpName()));

//        holder.serviceName.setText(getTextFromString(trReport.get(position).getServiceName()));

        // Status
        holder.status.setText(getTextFromString(trReport.get(position).getStatus()));

        if (getTextFromString(trReport.get(position).getStatus()).equalsIgnoreCase("SUCCESS")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(trReport.get(position).getStatus()).equalsIgnoreCase("PENDING")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
        }else if (getTextFromString(trReport.get(position).getStatus()).equalsIgnoreCase("REFUND")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

//        holder.trType.setText(getTextFromString(trReport.get(position).getTrType()));
//        holder.remark.setText(getTextFromString(trReport.get(position).getRemark()));

        String image = trReport.get(position).getOpImage();
        // check if image is svg or not
        if (image.substring(image.lastIndexOf(".") + 1).equalsIgnoreCase("svg")) {
            // make uri from url
            Uri uri = Uri.parse(Constant.COMMISSION_IMAGES_BASE_URL + trReport.get(position).getOpImage());
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
                    .load(uri, holder.opImage);
        } else {
            Glide.with(holder.itemView)
                    .load(Constant.COMMISSION_IMAGES_BASE_URL + trReport.get(position).getOpImage())
                    .into(holder.opImage);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TransactionReportSuccess.class);
//                intent.putExtra("data", trReport.get(position));
                intent.putExtra("data", trReport.get(holder.getAdapterPosition()));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return trReport.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView trId, trDate, oldBal, amount, netAmount, newBal, opRef, apiRef, rechargeNo, opName, serviceName, status, trType, remark;
        ImageView opImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            trId = itemView.findViewById(R.id.tranId);
            trDate = itemView.findViewById(R.id.tranDate);

//            oldBal = itemView.findViewById(R.id.tranOldBal);

            amount = itemView.findViewById(R.id.tranAmount);

//            netAmount = itemView.findViewById(R.id.tranNetAmt);

            newBal = itemView.findViewById(R.id.tranNewBal);

//            opRef = itemView.findViewById(R.id.tranOpRef);
//            apiRef = itemView.findViewById(R.id.tranApiRef);

            rechargeNo = itemView.findViewById(R.id.tranMobNum);
            opName = itemView.findViewById(R.id.tranOpName);

//            serviceName = itemView.findViewById(R.id.tranSerName);

            status = itemView.findViewById(R.id.tranStatus);

//            trType = itemView.findViewById(R.id.tranTrType);
//            remark = itemView.findViewById(R.id.tranRemark);

            opImage = itemView.findViewById(R.id.tranImage);

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
            day = StringUtils.substringBetween(fullDate, "-", " ");
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
        String date1 = StringUtils.substringBetween(startTime, "", " ");
        // print the value you can see the actual result
        Log.e("DATE", "onBindViewHolder: " + " date " + date1 + " " + startTime + " time ");
        // get the time from date
        startTime = StringUtils.substringBetween(startTime, " ", "");
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