package com.swinfotech.swpay.activities;

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
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.AEPSReportAdapter;
import com.swinfotech.swpay.databinding.ActivityAepsreportsBinding;
import com.swinfotech.swpay.model.AEPSReportGetSet;
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

public class AEPSReports extends AppCompatActivity {

    ActivityAepsreportsBinding binding;
    AEPSReports activity;
    Session session;

    AEPSReportAdapter aepsReportAdapter;
    List<AEPSReportGetSet> viewReport = new ArrayList<>();

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAepsreportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        session = new Session(activity);

        binding.gotoMainAc1.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.aepsReportRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.aepsReportRecView.setHasFixedSize(true);
        aepsReportAdapter = new AEPSReportAdapter(activity, viewReport);
        binding.aepsReportRecView.setAdapter(aepsReportAdapter);

        binding.aepsCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.aepsToDate.setOnClickListener(new View.OnClickListener() {
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
        binding.aepsCal1.setText(formattedDate);
        binding.aepsToDate.setText(formattedDate);
        statementReportAPI();
    }

    private void statementReportAPI() {
        showLoadingDialog();
        String BASE_URL = "https://mobile.swpay.in/api/AndroidAPI/AEPSCommReport";
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("3")) {
            BASE_URL = "https://mobile.swpay.in/api/AndroidAPI/AEPSSettlement";
        }
        String url = BASE_URL + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.aepsCal1.getText().toString() + " 00:00:00" + "&todate=" + binding.aepsToDate.getText().toString() + " 23:59:59.99"+"&limit=1000" + "&tok=" + session.getString(Session.TOKEN);
        Log.e("TAG", "statementReportAPI: " + url);
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            viewReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject obj = null;
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                if (jsonObject.has("GetAEPSRechTrans")) {
                                    obj = jsonObject.getJSONObject("GetAEPSRechTrans");
                                }
                                if (jsonObject.has("ParentComm")) {
                                    obj = jsonObject.getJSONObject("ParentComm");
                                }
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        AEPSReportGetSet data = new AEPSReportGetSet();
                                        if (object.has("id")) {
                                            data.setOrderId(object.getString("id"));
                                        }

                                        if (object.has("pay_ref_id")) {
                                            data.setOrderId(object.getString("pay_ref_id"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setDate(object.getString("tr_date"));
                                        }

                                        if (object.has("op_bal")) {
                                            data.setOldBal(object.getString("op_bal"));
                                        }

                                        if (object.has("closing_bal")) {
                                            data.setNewBal(object.getString("closing_bal"));
                                        }

                                        if (object.has("tr_type")) {
                                            data.setTxnType(object.getString("tr_type"));
                                        }

                                        if (object.has("net_amt")) {
                                            data.setNetAmount(object.getString("net_amt"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOperator(object.getString("operator_name"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }
                                        if (object.has("crdr_type")) {
                                            data.setStatus(object.getString("crdr_type"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("merchant_id")) {
                                            data.setMerchantNo(object.getString("merchant_id"));
                                        }

                                        if (object.has("reason")) {
                                            data.setTrName(object.getString("reason"));
                                        }
                                        if (object.has("remarks")) {
                                            data.setTrName(object.getString("remarks"));
                                        }

                                        viewReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (viewReport.isEmpty()) {
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
            binding.aepsNoData.setVisibility(View.VISIBLE);
            binding.aepsReportRecView.setVisibility(View.GONE);
        } else {
            binding.aepsNoData.setVisibility(View.GONE);
            binding.aepsReportRecView.setVisibility(View.VISIBLE);
            aepsReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.aepsCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.aepsCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.aepsToDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.aepsToDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.aepsCal1.getText().toString().equalsIgnoreCase("") && !binding.aepsToDate.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            statementReportAPI();
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