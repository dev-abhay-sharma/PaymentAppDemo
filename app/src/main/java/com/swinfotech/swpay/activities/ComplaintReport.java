package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.ComplaintReportAdapter;
import com.swinfotech.swpay.databinding.ActivityComplaintReportBinding;
import com.swinfotech.swpay.model.ComplaintReportGetSet;
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

public class ComplaintReport extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;

    ActivityComplaintReportBinding binding;
    ComplaintReport activity;

    ComplaintReportAdapter complaintReportAdapter;
    List<ComplaintReportGetSet> CaReport = new ArrayList<>();

    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityComplaintReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.back3.setOnClickListener(v -> {
            onBackPressed();
        });


        // set the layout manager on recyclerview
        binding.caRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.caRecView.setHasFixedSize(true);
        complaintReportAdapter = new ComplaintReportAdapter(activity, CaReport);
        binding.caRecView.setAdapter(complaintReportAdapter);


        binding.caCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.toDateCa.setOnClickListener(new View.OnClickListener() {
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
        binding.caCal1.setText(formattedDate);
        binding.toDateCa.setText(formattedDate);
        statementReportAPI();
    }

    private void statementReportAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ComplainStatus?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.caCal1.getText().toString() + "&todate=" + binding.toDateCa.getText().toString() + "&tok=" + session.getString(Session.TOKEN);
        Log.e("TAG", "statementReportAPI: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            CaReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject obj = jsonObject.getJSONObject("ComplainTr");
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        ComplaintReportGetSet data = new ComplaintReportGetSet();
                                        if (object.has("id")) {
                                            data.setOrID(object.getString("id"));
                                        }

                                        if (object.has("user_number")) {
                                            data.setMobileNo(object.getString("user_number"));
                                        }

                                        if (object.has("details")) {
                                            data.setDetails(object.getString("details"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatusSuccess(object.getString("status"));
                                        }

                                        if (object.has("bank_ac_no")) {
                                            data.setAccountNo(object.getString("bank_ac_no"));
                                        }

                                        if (object.has("remitter_id")) {
                                            data.setRemitter(object.getString("remitter_id"));
                                        }

                                        if (object.has("complain_statius")) {
                                            data.setStatus(object.getString("complain_statius"));
                                        }

                                        if (object.has("support_type")) {
                                            data.setSupportType(object.getString("support_type"));
                                        }

                                        if (object.has("message")) {
                                            data.setMessage(object.getString("message"));
                                        }

                                        if (object.has("adddate")) {
                                            data.setDate(object.getString("adddate"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOperator(object.getString("operator_name"));
                                        }

                                        CaReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (CaReport.isEmpty()) {
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
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
            binding.noDataCa.setVisibility(View.VISIBLE);
            binding.caRecView.setVisibility(View.GONE);
        } else {
            binding.noDataCa.setVisibility(View.GONE);
            binding.caRecView.setVisibility(View.VISIBLE);
            complaintReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.caCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.caCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.toDateCa.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.toDateCa.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.caCal1.getText().toString().equalsIgnoreCase("") && !binding.toDateCa.getText().toString().equalsIgnoreCase("")) {
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