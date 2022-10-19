package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.DownlineSalesReportAdapter;
import com.swinfotech.swpay.databinding.ActivityDownlineSalesReportBinding;
import com.swinfotech.swpay.model.SalesReportGetSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DownlineSalesReport extends AppCompatActivity {

    ActivityDownlineSalesReportBinding binding;
    DownlineSalesReport activity;

    List<SalesReportGetSet> salesReport = new ArrayList<>();
    DownlineSalesReportAdapter salesReportAdapter;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDownlineSalesReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.backToMainTo.setOnClickListener(v -> onBackPressed());

        // set the layout manager on recyclerview
        binding.salesRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.salesRecView.setHasFixedSize(true);
        salesReportAdapter = new DownlineSalesReportAdapter(activity, salesReport);
        binding.salesRecView.setAdapter(salesReportAdapter);


        binding.salesCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.toDateSales.setOnClickListener(new View.OnClickListener() {
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
        binding.salesCal1.setText(formattedDate);
        binding.toDateSales.setText(formattedDate);
        downlineSalesReportAPI();
    }

    private void downlineSalesReportAPI() {
        showLoadingDialog();

        String url = "https://mobile.swpay.in/api/AndroidAPI/ChildSalesReport?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.salesCal1.getText().toString() + " 00:00:00" + "&todate=" + binding.toDateSales.getText().toString() + " 23:59:59.99"+"&limit=1000" + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            salesReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
//                                if (jsonObject.has("Resp")) {
//                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
//                                }
//                                JSONObject obj = jsonObject.getJSONObject("PaymentLoad");
                                JSONArray jsonArray = jsonObject.getJSONArray("GetChildSales");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        SalesReportGetSet data = new SalesReportGetSet();
                                        if (object.has("success_amt")) {
                                            data.setAmtSuccess(object.getString("success_amt"));
                                        }

                                        if (object.has("user_fname")) {
                                            data.setuName(object.getString("user_fname"));
                                        }

                                        if (object.has("user_id")) {
                                            data.setId(object.getString("user_id"));
                                        }

                                        if (object.has("user_mobile")) {
                                            data.setMobile(object.getString("user_mobile"));
                                        }

                                        if (object.has("balance_amt")) {
                                            data.setBalAmt(object.getString("balance_amt"));
                                        }

//                                        if (object.has("operator_name")) {
//                                            data.setOperator_name(object.getString("operator_name"));
//                                        }
//
//                                        if (object.has("service_name")) {
//                                            data.setService_name(object.getString("service_name"));
//                                        }

                                        salesReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (salesReport.isEmpty()) {
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
//                hideProgressDialog();
                hideLoadingDialog();
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                setData(true);
            }
        }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
            binding.noDataSales.setVisibility(View.VISIBLE);
            binding.salesRecView.setVisibility(View.GONE);
        } else {
            binding.noDataSales.setVisibility(View.GONE);
            binding.salesRecView.setVisibility(View.VISIBLE);
            salesReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.salesCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.salesCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.toDateSales.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.toDateSales.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.salesCal1.getText().toString().equalsIgnoreCase("") && !binding.toDateSales.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            downlineSalesReportAPI();
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