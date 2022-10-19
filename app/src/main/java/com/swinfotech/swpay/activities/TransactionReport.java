package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.TransactionReportAdapter;
import com.swinfotech.swpay.databinding.ActivityTransactionReportBinding;
import com.swinfotech.swpay.model.TransactionReportGetSet;
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

public class TransactionReport extends AppCompatActivity {

    ActivityTransactionReportBinding binding;
    private TransactionReport activity;
    private ProgressDialog progressDialog;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;
    private static int VOLLEY_TIMEOUT = 1000 * 30;
    TransactionReportAdapter transactionReportAdapter;
    List<TransactionReportGetSet> trReport = new ArrayList<>();

    // for Selection
    String id ="Select";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(this);

        binding.baMain.setOnClickListener(v -> {
            onBackPressed();
        });

        // set the layout manager on recyclerview
        binding.traRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.traRecView.setHasFixedSize(true);
        transactionReportAdapter = new TransactionReportAdapter(activity, trReport);
        binding.traRecView.setAdapter(transactionReportAdapter);


        getSpinnerData();


        binding.traCal1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(true);
            }
        });
        binding.traToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker(false);
            }
        });


        setCurrentDate();

    }

    private void getSpinnerData() {
        String[] itemsId = new String[]{ "0", "SUCCESS", "FAILED"};
        String[] items = new String[]{ "All", "Success", "Failure"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, items);
        Spinner select = (Spinner) findViewById(R.id.trSpinner);
        select.setAdapter(adapter);

        select.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                id = itemsId[select.getSelectedItemPosition()];
                transactionReportAPI();
                System.out.println(id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void showDatePicker(boolean isStartDate) {
        // check if activity not null otherwise your app is crashed
        if (activity != null) {
            // create new object of calendar everytime else when you reselect the date it by Default chose previous one.
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
        binding.traCal1.setText(formattedDate);
        binding.traToDate.setText(formattedDate);
        transactionReportAPI();
    }

    private void transactionReportAPI() {
        showLoadingDialog();

        // old api
//        String url = "https://mobile.swpay.in/api/AndroidAPI/RechTransReport?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
//                "&fromdate=" + binding.traCal1.getText().toString()+"00:00:00" + "&todate=" + binding.traToDate.getText().toString()+"23:59:59.99"+ "&selectstatus=" + "&limit=100000" + "&tok=" + session.getString(Session.TOKEN);

        // new api
        String url = "https://mobile.swpay.in/api/AndroidAPI/RechTransReportNew?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) +
                "&fromdate=" + binding.traCal1.getText().toString()+" 00:00:00" + "&todate=" + binding.traToDate.getText().toString()+" 23:59:59.99"+ "&selectstatus=" + id + "&limit=100000" + "&tok=" + session.getString(Session.TOKEN);
        Log.e("TAG", "statementReportAPI: " + url);
        System.out.println("Transaction API------- " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            trReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject obj = jsonObject.getJSONObject("Recharge");
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        TransactionReportGetSet data = new TransactionReportGetSet();

                                        if (object.has("pay_ref_id")) {
                                            data.setTrId(object.getString("pay_ref_id"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setTrDate(object.getString("tr_date"));
                                        }

                                        if (object.has("old_balance")) {
                                            data.setOldBal(object.getString("old_balance"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("netamt")) {
                                            data.setNetAmount(object.getString("netamt"));
                                        }

                                        if (object.has("new_balance")) {
                                            data.setNewBal(object.getString("new_balance"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }

                                        if (object.has("operator_ref")) {
                                            data.setOpRef(object.getString("operator_ref"));
                                        }

                                        if (object.has("recharge_number")) {
                                            data.setRechargeNum(object.getString("recharge_number"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOpName(object.getString("operator_name"));
                                        }

                                        if (object.has("service_name")) {
                                            data.setServiceName(object.getString("service_name"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }

                                        if (object.has("transaction_type")) {
                                            data.setTrType(object.getString("transaction_type"));
                                        }

                                        if (object.has("remarks")) {
                                            data.setRemark(object.getString("remarks"));
                                        }

                                        if (object.has("image")) {
                                            data.setOpImage(object.getString("image"));
                                        }

                                        if (object.has("api_ref")) {
                                            data.setApiRef(object.getString("api_ref"));
                                        }

                                        trReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (trReport.isEmpty()) {
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
            binding.traNoData.setVisibility(View.VISIBLE);
            binding.traRecView.setVisibility(View.GONE);
        } else {
            binding.traNoData.setVisibility(View.GONE);
            binding.traRecView.setVisibility(View.VISIBLE);
            transactionReportAdapter.notifyDataSetChanged();
        }

    }


    // this is for if user select diffrent date then call the api otherwise no need to call the api.
    private void matchDate(boolean isFromDate, String date) {
        // this is for from date
        if (isFromDate) {
            if (date != null) {
                if (!binding.traCal1.getText().toString().equalsIgnoreCase(date)) {
                    binding.traCal1.setText(date);
                    getData();
                }
            }
        }
        // this is for to date
        else {
            // match the date is not null
            if (date != null) {
                // match if previous date is not same with current date
                if (!binding.traToDate.getText().toString().equalsIgnoreCase(date)) {
                    // after that set the date to textview
                    binding.traToDate.setText(date);
                    getData();
                }
            }
        }
    }


    private void getData() {
        //check if both start or end date is selected or not you can remove this condition if you want
        if (!binding.traCal1.getText().toString().equalsIgnoreCase("") && !binding.traToDate.getText().toString().equalsIgnoreCase("")) {
            // getting the data
            transactionReportAPI();
        }
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Fetching Statements");
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