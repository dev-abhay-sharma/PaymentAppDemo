package com.swinfotech.swpay.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.model.ManagePaymentRequestGetSet;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;

public class ManagePaymentRequestAdapter extends RecyclerView.Adapter<ManagePaymentRequestAdapter.ViewHolder> {

    LayoutInflater inflater;
    List<ManagePaymentRequestGetSet> ManageReport;
    private Context context;
    private ProgressDialog progressDialog;
    private Session session;

    public ManagePaymentRequestAdapter(Context context, List<ManagePaymentRequestGetSet> ManageReport) {
        this.inflater = LayoutInflater.from(context);
        this.ManageReport = ManageReport;
        this.context = context;
        this.session = new Session(context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.manage_payment_request_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        holder.Id.setText(getTextFromString(ManageReport.get(position).getId()));

        holder.Request_Time.setText(getTextFromString(getDateAndMonth(ManageReport.get(position).getRequestTime()))
                                                        +" "+getTime(ManageReport.get(position).getRequestTime()));

        holder.UserName.setText(getTextFromString(ManageReport.get(position).getUserName()));
        holder.UserBal.setText(getTextFromString(ManageReport.get(position).getUserBal()));
        holder.UserMobile.setText(getTextFromString(ManageReport.get(position).getUserMobile()));
        holder.Amount.setText(getTextFromString(ManageReport.get(position).getAmount()));

        if (getTextFromString(ManageReport.get(position).getStatus()).equalsIgnoreCase("1")) {
            holder.Status.setText("Pending");
            holder.manage_request.setVisibility(View.VISIBLE);
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else if (getTextFromString(ManageReport.get(position).getStatus()).equalsIgnoreCase("2")) {
            holder.Status.setText("Approved");
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.manage_request.setVisibility(View.GONE);
        } else if (getTextFromString(ManageReport.get(position).getStatus()).equalsIgnoreCase("3")) {
            holder.Status.setText("Decline");
            holder.manage_request.setVisibility(View.GONE);
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.red));
        } else {
            holder.Status.setText("Hold");
            holder.manage_request.setVisibility(View.VISIBLE);
            holder.Status.setTextColor(ContextCompat.getColor(context, R.color.red));
        }

        holder.BankName.setText(getTextFromString(ManageReport.get(position).getBankName()));
        holder.BranchName.setText(getTextFromString(ManageReport.get(position).getBranchName()));
        holder.PaymentMode.setText(getTextFromString(ManageReport.get(position).getPaymentMode()));
        holder.CashType.setText(getTextFromString(ManageReport.get(position).getCashType()));

        holder.ResponseTime.setText(getTextFromString(getDateAndMonth(ManageReport.get(position).getResponseTime()))
                                        +" "+ getTime(ManageReport.get(position).getResponseTime()));

        holder.UserId.setText(getTextFromString(ManageReport.get(position).getUserId()));
        holder.UtrNO.setText(getTextFromString(ManageReport.get(position).getUtrNo()));
//        holder.u_id.setText(getTextFromString(ManageReport.get(position).getU_Id()));
//        holder.UserType.setText(getTextFromString(ManageReport.get(position).getUserType()));
        holder.Remarks.setText(getTextFromString(ManageReport.get(position).getRemarks()));

        holder.TrDate.setText(getTextFromString(getDateAndMonth(ManageReport.get(position).getTr_date()))
                                                    +" "+ getTime(ManageReport.get(position).getTr_date()));

        holder.ChequeNo.setText(getTextFromString(ManageReport.get(position).getChequeNo()));
        holder.TrNO.setText(getTextFromString(ManageReport.get(position).getTr_no()));
        holder.TrTime.setText(getTextFromString(ManageReport.get(position).getTr_time()));

        holder.manage_request.setOnClickListener(v -> {
            openBottomSheet(position);
        });

    }

    private void openBottomSheet(int position) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.activity_manage_request);

        Spinner allow_status;
        EditText mrTpin, mrRemarks;
        Button submit;

        allow_status = bottomSheetDialog.findViewById(R.id.allow_status);
        mrTpin = bottomSheetDialog.findViewById(R.id.mrTpin);
        mrRemarks = bottomSheetDialog.findViewById(R.id.mrRemarks);
        submit = bottomSheetDialog.findViewById(R.id.submit);


        if (submit != null) {
            submit.setOnClickListener(v -> {
                if (mrTpin.getText().toString().trim().equalsIgnoreCase("")) {
                    mrTpin.setError("Enter TPin");
                    mrTpin.requestFocus();
                } else if (mrRemarks.getText().toString().trim().equalsIgnoreCase("")) {
                    mrRemarks.setError("Enter Remark");
                    mrRemarks.requestFocus();
                } else if (allow_status.getSelectedItem().toString().trim().equalsIgnoreCase("Allow Status")) {
                    Toast.makeText(context, "Select Allow Status First", Toast.LENGTH_SHORT).show();
                } else {
                    submitStatus(mrTpin.getText().toString().trim(), mrRemarks.getText().toString().trim(), allow_status.getSelectedItem().toString(), ManageReport.get(position).getId(), bottomSheetDialog, position);
                }
            });
        }

        bottomSheetDialog.show();
    }

    private void submitStatus(String mrTpin, String mrRemarks, String statusPrev, String reId, BottomSheetDialog bottomSheetDialog, int position) {

        if (statusPrev.equalsIgnoreCase("Approved")) {
            statusPrev = "2";
        } else if (statusPrev.equalsIgnoreCase("Declined")) {
            statusPrev = "3";
        } else if (statusPrev.equalsIgnoreCase("Hold")) {
            statusPrev = "4";
        }

        String url = "https://mobile.swpay.in/api/AndroidAPI/ProcessPayReq?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&pay_req_id=" + reId + "&tpin=" + mrTpin + "&allow_status=" + statusPrev + "&remarks=" + mrRemarks + "&tok=" + session.getString(Session.TOKEN);

        showProgressDialog();

        String finalStatusPrev = statusPrev;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);
                        hideProgressDialog();
                        try {
                            JSONObject obj = new JSONObject(response);

                            // here we get the response when all api is ready then we get response here
                            if (obj.has("Status")) {

                                String status = obj.getString("Status");
                                if (status.equalsIgnoreCase("200")) {
//                                    Toast.makeText(context, "Successfully Manage Request", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(context, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog.dismiss();

                                    ManagePaymentRequestGetSet status1 = ManageReport.get(position);
                                    status1.setStatus(finalStatusPrev);
                                    ManageReport.set(position,status1);
                                    notifyItemChanged(position);

                                } else {
                                    Toast.makeText(context, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                            } else if (obj.has("Resp")) {
                                // abhi ye yha p aaya
                                bottomSheetDialog.dismiss();
                                Toast.makeText(context, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        hideProgressDialog();
                        try {
                            Toast.makeText(context, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(context));

        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return ManageReport.size();
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
        TextView Id, Request_Time, UserName, UserBal, UserMobile, Amount, Status, BankName, BranchName, PaymentMode, CashType, ResponseTime, UserId, UtrNO, u_id, UserType, Remarks, TrDate, ChequeNo, TrNO, TrTime;
        Button manage_request;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

//            Id = itemView.findViewById(R.id.manageId);
            Request_Time = itemView.findViewById(R.id.manageRequestTime);
            manage_request = itemView.findViewById(R.id.change_status);
            UserName = itemView.findViewById(R.id.manageUserName);
            UserBal = itemView.findViewById(R.id.manageUserBal);
            UserMobile = itemView.findViewById(R.id.manageUserMobile);
            Amount = itemView.findViewById(R.id.manageAmount);
            Status = itemView.findViewById(R.id.manageStatus);
            BankName = itemView.findViewById(R.id.manageBankName);
            BranchName = itemView.findViewById(R.id.manageBranchName);
            PaymentMode = itemView.findViewById(R.id.managePaymentMode);
            CashType = itemView.findViewById(R.id.manageCashType);
            ResponseTime = itemView.findViewById(R.id.manageResponseTime);
            UserId = itemView.findViewById(R.id.manageReqUserId);
            UtrNO = itemView.findViewById(R.id.manageUtrNo);
//            u_id = itemView.findViewById(R.id.manageUId);
//            UserType = itemView.findViewById(R.id.manageUserType);
            Remarks = itemView.findViewById(R.id.manageRemarks);
            TrDate = itemView.findViewById(R.id.manageTrDate);
            ChequeNo = itemView.findViewById(R.id.manageChequeNO);
            TrNO = itemView.findViewById(R.id.manageTrNo);
            TrTime = itemView.findViewById(R.id.manageTrTime);
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


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Adding User");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }
}
