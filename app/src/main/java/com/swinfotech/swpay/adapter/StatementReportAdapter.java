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
import com.swinfotech.swpay.model.StatementReportGetSet;

import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class StatementReportAdapter extends RecyclerView.Adapter<StatementReportAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<StatementReportGetSet> StReport;
    Context context;
    Session session;

    public StatementReportAdapter(Context context, List<StatementReportGetSet> StReport) {
        this.inflater = LayoutInflater.from(context);
        this.StReport = StReport;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public StatementReportAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.statements_report_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StatementReportAdapter.ViewHolder holder, int position) {
        holder.oldBalance.setText(getTextFromString(getAmountFormat(StReport.get(position).getOldBal())));

        holder.netAmount.setText(getTextFromString(getAmountFormat(StReport.get(position).getNetAmt())));

        holder.newBalance.setText(getTextFromString(getAmountFormat(StReport.get(position).getNewBal())));

//        holder.transactionDate.setText(getTextFromString(getDateAndMonth(StReport.get(position).getTrDate()))+ " "+ getTime(StReport.get(position).getTrDate()));
        holder.transactionDate.setText(getTextFromString(StReport.get(position).getTrDate()));

        holder.txnType.setText(getTextFromString(StReport.get(position).getTxnType()));
        holder.orderId.setText(getTextFromString(StReport.get(position).getOrId()));
        holder.remarks.setText(getTextFromString(StReport.get(position).getRemarks()));
        holder.status.setText(getTextFromString(StReport.get(position).getStatus()));

        holder.operatorName.setText(getTextFromString(StReport.get(position).getOperator_name()));
//        holder.serviceName.setText(getTextFromString(StReport.get(position).getService_name()));


        if (getTextFromString(StReport.get(position).getStatus()).equalsIgnoreCase("SUCCESS")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(StReport.get(position).getStatus()).equalsIgnoreCase("PENDING")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if (getTextFromString(StReport.get(position).getStatus()).equalsIgnoreCase("REFUND")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (getTextFromString(StReport.get(position).getStatus()).equalsIgnoreCase("ADDREFUND")) {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.logo_blue));
        } else {
            holder.status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.retailerId.setText(getTextFromString(StReport.get(position).getRetailerId()));
        holder.retailerName.setText(getTextFromString(StReport.get(position).getRetailerName()));

        holder.retailerStatus.setText(getTextFromString(StReport.get(position).getRetailerStatus()));

        if (getTextFromString(StReport.get(position).getRetailerStatus()).equalsIgnoreCase("SUCCESS")) {
            holder.retailerStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        } else if (getTextFromString(StReport.get(position).getRetailerStatus()).equalsIgnoreCase("PENDING")) {
            holder.retailerStatus.setTextColor(ContextCompat.getColor(context, R.color.blue));
        } else if (getTextFromString(StReport.get(position).getRetailerStatus()).equalsIgnoreCase("REFUND")) {
            holder.retailerStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (getTextFromString(StReport.get(position).getRetailerStatus()).equalsIgnoreCase("ADDREFUND")) {
            holder.retailerStatus.setTextColor(ContextCompat.getColor(context, R.color.logo_blue));
        } else {
            holder.retailerStatus.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.retailerRechargeNo.setText(getTextFromString(StReport.get(position).getRetailerRechargeNo()));

        holder.dtAmount.setText(getTextFromString(StReport.get(position).getRetailerAmount()));
        holder.dtCommAmt.setText(getTextFromString(getAmountFormat(StReport.get(position).getDtCommAmt())));
        holder.dtChargeAmt.setText(getTextFromString(getAmountFormat(StReport.get(position).getDtChargeAmt())));
        holder.mdtCommAmt.setText(getTextFromString(getAmountFormat(StReport.get(position).getMdtCommAmt())));
        holder.mdtChargeAmt.setText(getTextFromString(getAmountFormat(StReport.get(position).getMdtChargeAmt())));

        holder.dtMdtOp.setText(getTextFromString(StReport.get(position).getDtMDTOp()));



        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("3")) {
            holder.newBalanceLyt.setVisibility(View.VISIBLE);
            holder.oldBalanceLyt.setVisibility(View.VISIBLE);
        } else if (session.getString(Session.USER_TYPE).equalsIgnoreCase("2")){
            holder.newBalanceLyt.setVisibility(View.INVISIBLE);
            holder.oldBalanceLyt.setVisibility(View.INVISIBLE);
            holder.retailerIdLyt.setVisibility(View.VISIBLE);
            holder.retailerNameLyt.setVisibility(View.VISIBLE);
            holder.retailerStatusLyt.setVisibility(View.VISIBLE);
            holder.retailerRechargeNoLyt.setVisibility(View.VISIBLE);
            holder.stNetAmountLyt.setVisibility(View.GONE);
            holder.dtAmountLyt.setVisibility(View.VISIBLE);
            holder.dtCommAmtLyt.setVisibility(View.VISIBLE);
            holder.dtChargeAmtLyt.setVisibility(View.VISIBLE);
//            holder.dtOperatorLyt.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
        } else {
            holder.newBalanceLyt.setVisibility(View.INVISIBLE);
            holder.oldBalanceLyt.setVisibility(View.INVISIBLE);
            holder.retailerIdLyt.setVisibility(View.VISIBLE);
            holder.retailerNameLyt.setVisibility(View.VISIBLE);
            holder.retailerStatusLyt.setVisibility(View.VISIBLE);
            holder.retailerRechargeNoLyt.setVisibility(View.VISIBLE);
            holder.stNetAmountLyt.setVisibility(View.GONE);
            holder.dtAmountLyt.setVisibility(View.VISIBLE);
            holder.mdtCommAmtLyt.setVisibility(View.VISIBLE);
            holder.mdtChargeAmtLyt.setVisibility(View.VISIBLE);
//            holder.dtOperatorLyt.setVisibility(View.VISIBLE);
            holder.status.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return StReport.size();
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

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView oldBalance, netAmount, newBalance, transactionDate, txnType, orderId, remarks, status, operatorName, serviceName;
        LinearLayout oldBalanceLyt, newBalanceLyt, retailerIdLyt, retailerNameLyt, retailerStatusLyt, retailerRechargeNoLyt, stNetAmountLyt, dtAmountLyt, dtCommAmtLyt, dtChargeAmtLyt, mdtCommAmtLyt, mdtChargeAmtLyt, dtOperatorLyt;
        TextView retailerId, retailerName, retailerStatus, retailerRechargeNo, dtAmount, dtCommAmt, dtChargeAmt, mdtCommAmt, mdtChargeAmt, dtMdtOp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            oldBalance = itemView.findViewById(R.id.stOldBal);

            netAmount = itemView.findViewById(R.id.stNetAmount);

            newBalance = itemView.findViewById(R.id.stNewBal);
            transactionDate = itemView.findViewById(R.id.stDate);
            txnType = itemView.findViewById(R.id.stTxnType);
            orderId = itemView.findViewById(R.id.storderId);
            remarks = itemView.findViewById(R.id.stRemarks);
            status = itemView.findViewById(R.id.stStatus);
            oldBalanceLyt = itemView.findViewById(R.id.oldBalanceLyt);
            newBalanceLyt = itemView.findViewById(R.id.newBalanceLyt);

            operatorName = itemView.findViewById(R.id.stOpName);
//            serviceName = itemView.findViewById(R.id.stServiceName);

            // retailer
            retailerId = itemView.findViewById(R.id.stRetailerId);
            retailerName = itemView.findViewById(R.id.stRetailerName);
            retailerStatus = itemView.findViewById(R.id.stStatus1);
            retailerRechargeNo = itemView.findViewById(R.id.stRechargeNo);
            dtAmount = itemView.findViewById(R.id.stDtAmount);
            dtCommAmt = itemView.findViewById(R.id.stCommAmt);
            dtChargeAmt = itemView.findViewById(R.id.stChargeAmt);
            mdtCommAmt = itemView.findViewById(R.id.stMDTCommAmt);
            mdtChargeAmt = itemView.findViewById(R.id.stMDTChargeAmt);
            dtMdtOp = itemView.findViewById(R.id.stDtMDTOperator);

            retailerIdLyt = itemView.findViewById(R.id.dtId);
            retailerNameLyt = itemView.findViewById(R.id.dtName);
            retailerStatusLyt = itemView.findViewById(R.id.dtStatus);
            retailerRechargeNoLyt = itemView.findViewById(R.id.dtRecharge);
            stNetAmountLyt = itemView.findViewById(R.id.stNetAmountLyt);
            dtAmountLyt = itemView.findViewById(R.id.dtAmountLyt);
            dtCommAmtLyt = itemView.findViewById(R.id.dtCommAmtLyt);
            dtChargeAmtLyt = itemView.findViewById(R.id.dtChargeAmtLyt);
            mdtCommAmtLyt = itemView.findViewById(R.id.mdtCommAmtLyt);
            mdtChargeAmtLyt = itemView.findViewById(R.id.mdtChargeAmtLyt);
            dtOperatorLyt = itemView.findViewById(R.id.dtOperatorLyt);



        }
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
