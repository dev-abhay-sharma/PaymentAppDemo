package com.swinfotech.swpay.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.ManagePaymentRequestAdapter;
import com.swinfotech.swpay.databinding.ActivityManagePaymentRequestBinding;
import com.swinfotech.swpay.model.ManagePaymentRequestGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ManagePaymentRequest extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    // init your list first so you don't get error anywhere of null
    List<ManagePaymentRequestGetSet> ManageReport = new ArrayList<>();
    ManagePaymentRequestAdapter managePaymentRequestAdapter;
    ActivityManagePaymentRequestBinding binding;
    private ManagePaymentRequest activity;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    private Session session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating our xml layout in our binding
        binding = ActivityManagePaymentRequestBinding.inflate(getLayoutInflater());

        // getting our root layout in our view.
        View view = binding.getRoot();

        // below line is to set
        // Content view for our layout.
        setContentView(view);

        // here pass the current activity context to your activity object
        activity = this;
        session = new Session(activity);
        // back to mainActivity dashboard
        binding.manageGoDash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // get any xml view using the binding object it reduce your code lines and also prevent from null pointer exception if you forgot to get id of that view.

        // set the layout manager on recyclerview
        binding.manageRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.manageRecView.setHasFixedSize(true);
        managePaymentRequestAdapter = new ManagePaymentRequestAdapter(activity, ManageReport);
        binding.manageRecView.setAdapter(managePaymentRequestAdapter);


        binding.manageCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.manageToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(false);
            }
        });

        setCurrentDate();

    }

    private void showDatePicker(boolean isStartDate) {
        // check if activity not null otherwise your app is crashed
        if (activity != null) {
            // create new object of calendar everytime else when you reselect the date it bydefault chose prevoius one.
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
                // TODO Auto-generated method stub
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy/MM/dd"; //Here you can change you desire format of date
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                // here pass the select date to matchDate method
                matchDate(isStartDate, sdf.format(calendar.getTime()));
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            // this line for restrict your calendar to select future dates
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        }
    }

    private void setCurrentDate() {
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);

        SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String formattedDate = df.format(c);
        binding.manageCal1.setText(formattedDate);
        binding.manageToDate.setText(formattedDate);
        managePaymentRequestAPI();
    }

    private void managePaymentRequestAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ManagePaymentReq?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&fromdate=" + binding.manageCal1.getText().toString() + "&todate=" + binding.manageToDate.getText().toString() + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            ManageReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject obj = jsonObject.getJSONObject("GetRequestedList");
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        ManagePaymentRequestGetSet data = new ManagePaymentRequestGetSet();
                                        if (object.has("id")) {
                                            data.setId(object.getString("id"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setTr_date(object.getString("tr_date"));
                                        }

                                        if (object.has("tr_time")) {
                                            data.setTr_time(object.getString("tr_time"));
                                        }

                                        if (object.has("tr_no")) {
                                            data.setTr_no(object.getString("tr_no"));
                                        }

                                        if (object.has("bank_name")) {
                                            data.setBankName(object.getString("bank_name"));
                                        }

                                        if (object.has("branch_name")) {
                                            data.setBranchName(object.getString("branch_name"));
                                        }

                                        if (object.has("cheque_no")) {
                                            data.setChequeNo(object.getString("cheque_no"));
                                        }

                                        if (object.has("utr_no")) {
                                            data.setUtrNo(object.getString("utr_no"));
                                        }

                                        if (object.has("payment_mode")) {
                                            data.setPaymentMode(object.getString("payment_mode"));
                                        }

                                        if (object.has("cash_type")) {
                                            data.setCashType(object.getString("cash_type"));
                                        }

                                        if (object.has("payment_mode")) {
                                            data.setPaymentMode(object.getString("payment_mode"));
                                        }

                                        if (object.has("remarks")) {
                                            data.setRemarks(object.getString("remarks"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }

                                        if (object.has("request_time")) {
                                            data.setRequestTime(object.getString("request_time"));
                                        }

                                        if (object.has("response_time")) {
                                            data.setResponseTime(object.getString("response_time"));
                                        }

                                        if (object.has("req_user_id")) {
                                            data.setUserId(object.getString("req_user_id"));
                                        }

                                        if (object.has("req_user_name")) {
                                            data.setUserName(object.getString("req_user_name"));
                                        }

                                        if (object.has("req_user_balance")) {
                                            data.setUserBal(object.getString("req_user_balance"));
                                        }

                                        if (object.has("req_user_mobile")) {
                                            data.setUserMobile(object.getString("req_user_mobile"));
                                        }

//                                        if (object.has("u_id")) {
//                                            data.setU_Id(object.getString("u_id"));
//                                        }
//
//                                        if (object.has("user_type")) {
//                                            data.setUserType(object.getString("user_type"));
//                                        }

                                        ManageReport.add(data);
                                        managePaymentRequestAdapter.notifyDataSetChanged();
                                    }
                                    //check the list if it is empty or not
                                    if (ManageReport.isEmpty()) {
                                        setData(true);
                                    } else {
                                        setData(false);
                                    }
                                } else {
                                    setData(true);
                                }
                            } else {
                                setData(true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            setData(true);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                setData(true);
            }
        }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
            binding.manageNoData.setVisibility(View.VISIBLE);
            binding.manageRecView.setVisibility(View.GONE);
        } else {
            binding.manageNoData.setVisibility(View.GONE);
            binding.manageRecView.setVisibility(View.VISIBLE);
            managePaymentRequestAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.manageCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.manageCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.manageToDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.manageToDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.manageCal1.getText().toString().equalsIgnoreCase("") && !binding.manageToDate.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            managePaymentRequestAPI();
        }
    }

    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new SimpleArcDialog(activity);
            configuration = new ArcConfiguration(activity);
            mDialog.setConfiguration(configuration);
            configuration.setText("Please Wait.....");
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
            configuration.setColors(new int[]{Color.BLUE, Color.RED, Color.WHITE, Color.MAGENTA});
            configuration.setTextColor(Color.BLUE);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}