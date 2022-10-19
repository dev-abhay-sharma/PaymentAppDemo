package com.swinfotech.swpay.activities;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.StatementReportAdapter;
import com.swinfotech.swpay.databinding.ActivityStatementsReportBinding;
import com.swinfotech.swpay.model.StatementReportGetSet;
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

public class StatementsReport extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    private final String JSON_URL = "https://mobile.swpay.in/api/AndroidAPI/ACStatement";
    RecyclerView recyclerView;
    ImageView back;
    // init your list first so you don't get error anywhere of null
    List<StatementReportGetSet> StReport = new ArrayList<>();
    StatementReportAdapter statementReportAdapter;

    ActivityStatementsReportBinding binding;
    private StatementsReport activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating our xml layout in our binding
        binding = ActivityStatementsReportBinding.inflate(getLayoutInflater());

        // getting our root layout in our view.
        View view = binding.getRoot();

        // below line is to set
        // Content view for our layout.
        setContentView(view);

        // here pass the current activity context to your activity object
        activity = this;
        // recyclerView = (RecyclerView) findViewById(R.id.stRecView);
        //back = (ImageView) findViewById(R.id.back_to_main);
        session = new Session(this);
        // back to mainActivity dashboard
        binding.backToMain.setOnClickListener(v -> {
            onBackPressed();
        });

        // get any xml view using the binding object it reduce your code lines and also prevent from null pointer execption if you forgot to get id of that view.

        // set the layout manager on recyclerview
        binding.stRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.stRecView.setHasFixedSize(true);
        statementReportAdapter = new StatementReportAdapter(activity, StReport);
        binding.stRecView.setAdapter(statementReportAdapter);


        binding.stCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.toDate.setOnClickListener(new View.OnClickListener() {
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
        binding.stCal1.setText(formattedDate);
        binding.toDate.setText(formattedDate);
        statementReportAPI();
    }

    private void statementReportAPI() {
        showLoadingDialog();
        String BASE_URL = "https://mobile.swpay.in/api/AndroidAPI/RechBBPSCommReport";
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("3")) {
            BASE_URL = "https://mobile.swpay.in/api/AndroidAPI/ACStatement";
        }
        String url = BASE_URL + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.stCal1.getText().toString() + " 00:00:00" + "&todate=" + binding.toDate.getText().toString() + " 23:59:59.99" + "&tok=" + session.getString(Session.TOKEN);
        Log.e("TAG", "statementReportAPI: " + url);
        System.out.println("hello " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            StReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONArray jsonArray = null;
                                if (jsonObject.has("StatementReport")) {
                                    jsonArray = jsonObject.getJSONArray("StatementReport");
                                }
                                if (jsonObject.has("ParentComm")) {
                                    jsonArray = jsonObject.getJSONArray("ParentComm");
                                }

                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        StatementReportGetSet data = new StatementReportGetSet();
                                        if (object.has("old_balance")) {
                                            data.setOldBal(object.getString("old_balance"));
                                        }

                                        if (object.has("netamt")) {
                                            data.setNetAmt(object.getString("netamt"));
                                        }

                                        if (object.has("new_balance")) {
                                            data.setNewBal(object.getString("new_balance"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setTrDate(object.getString("tr_date"));
                                        }

                                        if (object.has("crdr_type")) {
                                            data.setTxnType(object.getString("crdr_type"));
                                        }

                                        if (object.has("pay_ref_id")) {
                                            data.setOrId(object.getString("pay_ref_id"));
                                        }
                                        if (object.has("id")) {
                                            data.setOrId(object.getString("id"));
                                        }

                                        if (object.has("remarks")) {
                                            data.setRemarks(object.getString("remarks"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOperator_name(object.getString("operator_name"));
                                        }

                                        if (object.has("service_name")) {
                                            data.setService_name(object.getString("service_name"));
                                        }

                                        // dt data come

                                        if (object.has("rt_id")) {
                                            data.setRetailerId(object.getString("rt_id"));
                                        }

                                        if (object.has("rt_name")) {
                                            data.setRetailerName(object.getString("rt_name"));
                                        }

                                        if (object.has("status")) {
                                            data.setRetailerStatus(object.getString("status"));
                                        }

                                        if (object.has("recharge_number")) {
                                            data.setRetailerRechargeNo(object.getString("recharge_number"));
                                        }

                                        if (object.has("amount")) {
                                            data.setRetailerAmount(object.getString("amount"));
                                        }

                                        if (object.has("dtcommamt")) {
                                            data.setDtCommAmt(object.getString("dtcommamt"));
                                        }

                                        if (object.has("dtchargeamt")) {
                                            data.setDtChargeAmt(object.getString("dtchargeamt"));
                                        }

                                        if (object.has("mdtcommamt")) {
                                            data.setMdtCommAmt(object.getString("mdtcommamt"));
                                        }

                                        if (object.has("mdtchargeamt")) {
                                            data.setMdtChargeAmt(object.getString("mdtchargeamt"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setDtMDTOp(object.getString("operator_name"));
                                        }


                                        StReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (StReport.isEmpty()) {
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
            binding.noData.setVisibility(View.VISIBLE);
            binding.stRecView.setVisibility(View.GONE);
        } else {
            binding.noData.setVisibility(View.GONE);
            binding.stRecView.setVisibility(View.VISIBLE);
            statementReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.stCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.stCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.toDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.toDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.stCal1.getText().toString().equalsIgnoreCase("") && !binding.toDate.getText().toString().equalsIgnoreCase("")) {
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