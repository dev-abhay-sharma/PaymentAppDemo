package com.swinfotech.swpay.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityPaymentRequestBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Time;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class PaymentRequest extends AppCompatActivity {

    private PaymentRequest activity;
    private ActivityPaymentRequestBinding binding;
    private Session session;
    private String paymentMode = "", cashMode = "", current_date = "";
    private ProgressDialog progressDialog;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentRequestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        activity = this;
        session = new Session(activity);
        setPaymentModes();
        onClick();
    }

    private void onClick() {

        binding.gotoMainDa.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.time.setOnClickListener(v -> {
            showTimePicker();
        });

        binding.paymentDateRequest.setOnClickListener(v -> {
            showDatePicker();
        });
        binding.request.setOnClickListener(v -> {
            if (binding.amountRequest.getText().toString().trim().equalsIgnoreCase("")) {
                binding.amountRequest.setError("Enter Amount");
                binding.amountRequest.requestFocus();
            } else if (current_date.equalsIgnoreCase("")) {
                binding.paymentDateRequest.setError("Select Payment Date");
                binding.paymentDateRequest.requestFocus();
            } else if (paymentMode.equalsIgnoreCase("") || paymentMode.equalsIgnoreCase("Select Payment Mode")) {
                Toast.makeText(activity, "Select Payment Mode", Toast.LENGTH_SHORT).show();
            } else {
                if (paymentMode.equalsIgnoreCase("Cash")) {
                    if (cashMode.equalsIgnoreCase("") || cashMode.equalsIgnoreCase("Select Cash Mode")) {
                        Toast.makeText(activity, "Select Cash Mode", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                attemptPaymentRequest();
            }
        });

    }

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(activity, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                binding.time.setText(getTime(selectedHour, selectedMinute));
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    // get time from 24 hours timing
    private String getTime(int hr, int min) {
        Time tme = new Time(hr, min, 0);//seconds by default set to zero
        Format formatter;
        // this the old date format
//        formatter = new SimpleDateFormat("h:mm a");
        formatter = new SimpleDateFormat("hh:mm ");
        return formatter.format(tme);
    }

    private void attemptPaymentRequest() {
//        showProgressDialog();
        showLoadingDialog();
        String url = Constant.PAYMENT_REQUEST
                + "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&amount=" + getDataFromEditText(binding.amountRequest)
                + "&date=" + current_date
                + "&time=" + binding.time.getText().toString().trim()
                + "&paymentmode=" + binding.paymentModeRequest.getSelectedItem()
                + "&cashtype=" + cashMode
                + "&trno=" + getDataFromEditText(binding.transcation)
                + "&bank=" + getDataFromEditText(binding.depositBankRequest)
                + "&branch=" + getDataFromEditText(binding.branch)
                + "chequeno=" + getDataFromEditText(binding.cheque)
                + "utrno=" + getDataFromEditText(binding.utrName)
                + "&tok=" + session.getString(Session.TOKEN);

        System.out.println(url);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);
//                        hideProgressDialog();
                        hideLoadingDialog();
                        try {
                            // get the array
                            JSONObject obj = new JSONObject(response);

                            if (obj.length() == 0) {

                            } else {
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                onBackPressed();
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
                        try {
//                            hideProgressDialog();
                            hideLoadingDialog();
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);


    }


    private String getDataFromEditText(EditText editText) {
        if (editText == null) {
            return "1";
        }
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            return "1";
        }
        return editText.getText().toString().trim();
    }


    private void setPaymentModes() {
        // use array for this
        String[] paymentModes = {"Select Payment Mode", "Cash", "Cheque", "RTGS", "NEFT", "IMPS", "Credit", "Upi"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, paymentModes);
        binding.paymentModeRequest.setAdapter(arrayAdapter);
        binding.paymentModeRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                paymentMode = paymentModes[i];
                if (paymentMode.equalsIgnoreCase("Cash")) {
                    setCashMode();
                }
                if (paymentMode.equalsIgnoreCase("Cheque")) {
                    setChequeMode();
                }
                if (paymentMode.equalsIgnoreCase("RTGS")) {
                    setRtgsMode();
                }
                if (paymentMode.equalsIgnoreCase("NEFT")) {
                    setRtgsMode();
                }
                if (paymentMode.equalsIgnoreCase("IMPS")) {
                    setRtgsMode();
                }
                if (paymentMode.equalsIgnoreCase("Credit")) {
                    setCreditMode();
                }
                if (paymentMode.equalsIgnoreCase("Upi")) {
                    setRtgsMode();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setCreditMode() {
        binding.bankLyt.setVisibility(View.VISIBLE);
        binding.timeLayout.setVisibility(View.VISIBLE);


        binding.utrLyt.setVisibility(View.GONE);

        binding.cashSpinner.setVisibility(View.GONE);
        binding.branchLyt.setVisibility(View.GONE);
        binding.chequeLyt.setVisibility(View.GONE);
        binding.transcationLayout.setVisibility(View.GONE);
    }

    private void setChequeMode() {
        // hide other view
        binding.cashSpinner.setVisibility(View.GONE);
        binding.timeLayout.setVisibility(View.GONE);
        binding.bankLyt.setVisibility(View.VISIBLE);
        binding.branchLyt.setVisibility(View.VISIBLE);
        binding.chequeLyt.setVisibility(View.VISIBLE);
        binding.transcationLayout.setVisibility(View.GONE);

    }

    private void setCashMode() {
        // set here layout for cash and hide other views
        binding.cashSpinner.setVisibility(View.VISIBLE);
        binding.timeLayout.setVisibility(View.VISIBLE);
        binding.bankLyt.setVisibility(View.GONE);
        binding.branchLyt.setVisibility(View.GONE);
        binding.chequeLyt.setVisibility(View.GONE);
        binding.transcationLayout.setVisibility(View.GONE);


        String cashModes[] = {"Select Cash Mode", "Branch Deposit", "CDM Machine"};

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, cashModes);
        binding.paymentCashRequest.setAdapter(arrayAdapter);
        binding.paymentCashRequest.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                cashMode = cashModes[i];
                if (cashMode.equalsIgnoreCase("Branch Deposit")) {
                    binding.transcationLayout.setVisibility(View.GONE);
                } else if (cashMode.equalsIgnoreCase("CDM Machine")) {
                    binding.transcationLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setRtgsMode() {
        binding.bankLyt.setVisibility(View.VISIBLE);
        binding.timeLayout.setVisibility(View.VISIBLE);
        binding.utrLyt.setVisibility(View.VISIBLE);

        binding.cashSpinner.setVisibility(View.GONE);
        binding.branchLyt.setVisibility(View.GONE);
        binding.chequeLyt.setVisibility(View.GONE);
        binding.transcationLayout.setVisibility(View.GONE);


    }


    private void showDatePicker() {
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
                current_date = sdf.format(calendar.getTime());
                binding.paymentDateRequest.setText(current_date);
            };

            DatePickerDialog datePickerDialog = new DatePickerDialog(activity, date, calendar
                    .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH));
            // this line for restrict your calendar to select future dates
            datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
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