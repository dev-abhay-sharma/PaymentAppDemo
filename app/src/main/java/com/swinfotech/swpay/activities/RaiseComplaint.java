package com.swinfotech.swpay.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
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
import com.swinfotech.swpay.databinding.ActivityCompalintSuccessBinding;
import com.swinfotech.swpay.databinding.ActivityRaiseComplaintBinding;
import com.swinfotech.swpay.model.RaiseComplaintGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class RaiseComplaint extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ActivityRaiseComplaintBinding binding;
    private RaiseComplaint activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    private Session session;


    private String subject_id = "", tr_id = "";
    private ArrayList<RaiseComplaintGetSet> raiseArrayList = new ArrayList<>();
    private ArrayAdapter<RaiseComplaintGetSet> raiseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRaiseComplaintBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        activity = this;

        binding.bMain.setOnClickListener(v -> {
            onBackPressed();
        });

        session = new Session(activity);



        onClick();
        initView();
        raiseComplaint();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void onClick() {

        binding.sentComplain.setOnClickListener(v -> {
            if (getStringFromEditText(binding.rComplain) == null) {
                binding.rComplain.setError("Please Write Message");
                binding.rComplain.requestFocus();
                return;
            } else {
                if (subject_id.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Select Subject First", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            sentComplainAPI();
        });

    }

    private void showRaiseComplain() {
        Dialog dialog = new Dialog(RaiseComplaint.this);
        ActivityCompalintSuccessBinding binding = ActivityCompalintSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding.getRoot());


        try {
            binding.transcationId.setText("Transcation Id : " + getIntent().getStringExtra("tr_id"));
            binding.rupees.setText("Amount : " + getIntent().getStringExtra("amount"));
            binding.mobile.setText("Mobile no : " + session.getString(Session.MOBILE));
            binding.time.setText("Date " + Constant.getDate());
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.done.setOnClickListener(v -> {
//            onBackPressed();
            finish();
        });

        dialog.show();
    }

    private void sentComplainAPI() {
        showLoadingDialog();
//        String url = "https://mobile.swpay.in/api/AndroidAPI/ComplainRaise?username=0123456789&password=b49e3ccae&transactionid=143&complainreason=abc&subject=pqr"
        String url = "https://mobile.swpay.in/api/AndroidAPI/ComplainRaise?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&transactionid=" + tr_id + "&complainreason=" + getStringFromEditText(binding.rComplain) + "&subject=" + subject_id + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Response", response);
                        hideLoadingDialog();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("Resp")) {
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            showRaiseComplain();
                            Toast.makeText(activity, "Raise Complaint Successfully", Toast.LENGTH_SHORT).show();

                            // here we get the response when all api is ready then we get response here

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        Log.e("TAG", "onErrorResponse: " + error);
                        //Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
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


    private String getStringFromEditText(EditText editText) {
        // check if edittext is null or not
        if (editText == null) {
            // then return null
            return null;
        }
        // check if edittext is empty
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            // then also return null for better optimization
            return null;
        }
        // else return the value
        return editText.getText().toString().trim();

    }

    private void initView() {
        // get tr id here
        if (getIntent() != null) {
            tr_id = getIntent().getStringExtra("tr_id");
        }

        raiseAdapter = new ArrayAdapter<RaiseComplaintGetSet>(activity, android.R.layout.simple_spinner_dropdown_item, raiseArrayList);// here pass your list name
        // here set the adapter on spinner
        binding.raiseSubject.setAdapter(raiseAdapter);
        binding.raiseSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // here get your selected complain subject
                subject_id = raiseArrayList.get(position).getSubject();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    // copy paste this method for get your other spinner data
    private void raiseComplaint() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ComplainSubject?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);

                        hideLoadingDialog();
                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONArray sub = obj.getJSONArray("SubList");
                            if (sub.length() == 0) {
                                // array is empty
                                Toast.makeText(activity, "No Complaint Found", Toast.LENGTH_SHORT).show();
                            } else {
                                for (int i = 0; i < sub.length(); i++) {
                                    JSONObject subject = sub.getJSONObject(i);

                                    if (subject.has("subject")) {
                                        // check objects if they are not contains null values
                                        if (!subject.getString("subject").equalsIgnoreCase("null")) {
                                            // here load data into list
                                            raiseArrayList.add(new RaiseComplaintGetSet(subject.getString("subject")));
                                        }
                                    }
                                }
                                // notify the adapter data is changed in list
                                raiseAdapter.notifyDataSetChanged();
                            }

                            // when data is come then load into your list


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        requestQueue.add(stringRequest);
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