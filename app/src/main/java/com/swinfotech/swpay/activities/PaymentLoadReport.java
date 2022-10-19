package com.swinfotech.swpay.activities;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import com.swinfotech.swpay.adapter.PaymentLoadReportAdapter;
import com.swinfotech.swpay.databinding.ActivityPaymentLoadReportBinding;
import com.swinfotech.swpay.model.PaymentLoadReportGetSet;
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

public class PaymentLoadReport extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    List<PaymentLoadReportGetSet> PyReport = new ArrayList<>();
    PaymentLoadReportAdapter paymentLoadReportAdapter;

    ActivityPaymentLoadReportBinding binding;
    private PaymentLoadReport activity; //make object of current activity which can you use in your whole class everytime you write the activityname.this it reduce that thing.
    private ProgressDialog progressDialog;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating our xml layout in our binding
        binding = ActivityPaymentLoadReportBinding.inflate(getLayoutInflater());

        // getting our root layout in our view.
        View view = binding.getRoot();

        // below line is to set
        // Content view for our layout.
        setContentView(view);

        // here pass the current activity context to your activity object
        activity = this;
        session = new Session(this);
        // back to mainActivity dashboard
        binding.gotoMain.setOnClickListener(v -> {
            onBackPressed();
        });

        // get any xml view using the binding object it reduce your code lines and also prevent from null pointer execption if you forgot to get id of that view.

        // set the layout manager on recyclerview
        binding.pyRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.pyRecView.setHasFixedSize(true);
        paymentLoadReportAdapter = new PaymentLoadReportAdapter(activity, PyReport);
        binding.pyRecView.setAdapter(paymentLoadReportAdapter);


        binding.pyCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.pyToDate.setOnClickListener(new View.OnClickListener() {
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
        binding.pyCal1.setText(formattedDate);
        binding.pyToDate.setText(formattedDate);
        paymentLoadReportAPI();
    }

    private void paymentLoadReportAPI() {
        showLoadingDialog();
//        String url = "https://mobile.swpay.in/api/AndroidAPI/ACStatement?username=0123456789&password=b49e3ccae&fromdate=" + binding.pyCal1.getText().toString() + "&todate=" + binding.pyToDate.getText().toString();
        String url = "https://mobile.swpay.in/api/AndroidAPI/CreditLoadReport?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.pyCal1.getText().toString() + " 00:00:00" + "&todate=" + binding.pyToDate.getText().toString() + " 23:59:59.99"+"&limit=1000" + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            PyReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject obj = jsonObject.getJSONObject("PaymentLoad");
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        PaymentLoadReportGetSet data = new PaymentLoadReportGetSet();
                                        if (object.has("id")) {
                                            data.setOrId(object.getString("id"));
                                        }

                                        if (object.has("opening_balance")) {
                                            data.setOldBal(object.getString("opening_balance"));
                                        }

                                        if (object.has("amount")) {
                                            data.setNetAmt(object.getString("amount"));
                                        }

                                        if (object.has("closing_balance")) {
                                            data.setNewBal(object.getString("closing_balance"));
                                        }

                                        if (object.has("reason")) {
                                            data.setRemarks(object.getString("reason"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setTrDate(object.getString("tr_date"));
                                        }

                                        if (object.has("tr_by_mobile")) {
                                            data.setDebit(object.getString("tr_by_mobile"));
                                        }

                                        if (object.has("tr_by")) {
                                            data.setDebit1(object.getString("tr_by"));
                                        }

//                                        if (object.has("operator_name")) {
//                                            data.setOperator_name(object.getString("operator_name"));
//                                        }
//
//                                        if (object.has("service_name")) {
//                                            data.setService_name(object.getString("service_name"));
//                                        }

                                        PyReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (PyReport.isEmpty()) {
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

            // don't send params when you Request is GET type
//            @Override
//            protected Map<String, String> getParams() {
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("username", "0123456789");
//                params.put("password", "b49e3ccae");
//                params.put("fromdate", binding.stCal1.getText().toString().trim());
//                params.put("todate", binding.toDate.getText().toString().trim());
//                return params;
//            }
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
            binding.pyNoData.setVisibility(View.VISIBLE);
            binding.pyRecView.setVisibility(View.GONE);
        } else {
            binding.pyNoData.setVisibility(View.GONE);
            binding.pyRecView.setVisibility(View.VISIBLE);
            paymentLoadReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.pyCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.pyCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.pyToDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.pyToDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.pyCal1.getText().toString().equalsIgnoreCase("") && !binding.pyToDate.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            paymentLoadReportAPI();
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
//            configuration.setColors(new int[]{Color.parseColor("#0513F9"), Color.parseColor("#05B1F4"), Color.parseColor("#FF7F50"), Color.parseColor("#FFFFFF")});
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