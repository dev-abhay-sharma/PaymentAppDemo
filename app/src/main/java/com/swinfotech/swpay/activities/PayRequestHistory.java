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
import com.swinfotech.swpay.adapter.PayRequestHistoryAdapter;
import com.swinfotech.swpay.databinding.ActivityPayRequestHistoryBinding;
import com.swinfotech.swpay.model.PayRequestHistoryGetSet;
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

public class PayRequestHistory extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    List<PayRequestHistoryGetSet> PayReport = new ArrayList<>();
    PayRequestHistoryAdapter payRequestHistoryAdapter;
    ActivityPayRequestHistoryBinding binding;
    private PayRequestHistory activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPayRequestHistoryBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        activity = this;
        session = new Session(activity);

        binding.Dashboard.setOnClickListener(v -> {
            onBackPressed();
        });


        // set the layout manager on recyclerview
        binding.payRequestRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.payRequestRecView.setHasFixedSize(true);
        payRequestHistoryAdapter = new PayRequestHistoryAdapter(activity, PayReport);
        binding.payRequestRecView.setAdapter(payRequestHistoryAdapter);


        binding.payCal1Request.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.payToDate.setOnClickListener(new View.OnClickListener() {
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
        binding.payCal1Request.setText(formattedDate);
        binding.payToDate.setText(formattedDate);
        payRequestHistoryAPI();
    }

    private void payRequestHistoryAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/PayReqFetch?username="+ session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)+"&fromdate=" + binding.payCal1Request.getText().toString() + "&todate=" + binding.payToDate.getText().toString() + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            PayReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("GetRequests");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        PayRequestHistoryGetSet data = new PayRequestHistoryGetSet();
                                        if (object.has("id")) {
                                            data.setId(object.getString("id"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setTrDate(object.getString("tr_date"));
                                        }

                                        if (object.has("tr_no")) {
                                            data.setTrNo(object.getString("tr_no"));
                                        }

                                        if (object.has("bank_name")) {
                                            data.setBankName(object.getString("bank_name"));
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

                                        if (object.has("parent_name")) {
                                            data.setParentName(object.getString("parent_name"));
                                        }

                                        if (object.has("payment_mode")) {
                                            data.setPaymentMode(object.getString("payment_mode"));
                                        }

                                        if (object.has("parent_mobile")) {
                                            data.setParentMobile(object.getString("parent_mobile"));
                                        }

                                        PayReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (PayReport.isEmpty()) {
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
            binding.payNoData.setVisibility(View.VISIBLE);
            binding.payRequestRecView.setVisibility(View.GONE);
        } else {
            binding.payNoData.setVisibility(View.GONE);
            binding.payRequestRecView.setVisibility(View.VISIBLE);
            payRequestHistoryAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select different date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.payCal1Request.getText().toString().equalsIgnoreCase(date)) {
                    binding.payCal1Request.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.payToDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.payToDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.payCal1Request.getText().toString().equalsIgnoreCase("") && !binding.payToDate.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            payRequestHistoryAPI();
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