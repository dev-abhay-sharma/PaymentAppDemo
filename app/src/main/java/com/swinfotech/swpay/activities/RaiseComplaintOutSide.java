package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityRaiseComplaintOutSideBinding;
import com.swinfotech.swpay.model.RaiseComplaintGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RaiseComplaintOutSide extends AppCompatActivity {

    ActivityRaiseComplaintOutSideBinding binding;
    RaiseComplaintOutSide activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    private Session session;


    private String subject = "", tr_id = "";
    private String subjectMode = "";
    private ArrayList<RaiseComplaintGetSet> raiseArrayList = new ArrayList<>();
    private ArrayAdapter<RaiseComplaintGetSet> raiseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRaiseComplaintOutSideBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.bMain1.setOnClickListener(v -> {
            onBackPressed();
        });

        onClick();
        initView();
        raiseComplaintSubject();

    }

    private void onClick() {

        binding.sentComplain1.setOnClickListener(v -> {
            if (getStringFromEditText(binding.rComplain1) == null) {
                binding.rComplain1.setError("Please Write Message");
                binding.rComplain1.requestFocus();
                return;
            } else {
                if (subject.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Select Subject First", Toast.LENGTH_SHORT).show();
                    return;
                } else if (subject.equalsIgnoreCase("Select")){
                    Toast.makeText(activity, "Select Subject First", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            sentComplainAPI();
        });

    }

    private void sentComplainAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ComplainRaiseOutside";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("Resp")) {
                                Toast.makeText(activity, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String msg = "";
                            if (object.has("Resp")){
                                msg = object.getString("Resp");
                            }

                            if (status.equalsIgnoreCase("200")){
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                System.out.println(msg);
                                finish();
                            } else {
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                System.out.println(msg);
                                finish();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("subject", subject);
                params.put("complainReason", getStringFromEditText(binding.rComplain1));
                if (getStringFromEditText(binding.raiseTrID) != null)
                    params.put("ComplainId", getStringFromEditText(binding.raiseTrID));
                    params.put("tok", session.getString(Session.TOKEN));

                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void raiseComplaintSubject() {
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

                            RaiseComplaintGetSet raiseComplaintGetSet = new RaiseComplaintGetSet();
                            raiseComplaintGetSet.setSubject("Select");
                            raiseArrayList.add(raiseComplaintGetSet);

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

    private void initView() {
        // get tr id here
//        if (getIntent() != null) {
//            tr_id = getIntent().getStringExtra("tr_id");
//        }

        raiseAdapter = new ArrayAdapter<RaiseComplaintGetSet>(activity, android.R.layout.simple_spinner_dropdown_item, raiseArrayList);// here pass your list name
        // here set the adapter on spinner
        binding.raiseSubject1.setAdapter(raiseAdapter);
        binding.raiseSubject1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // here get your selected complain subject
                subject = raiseArrayList.get(position).getSubject();

                subjectMode = String.valueOf(raiseArrayList.get(position));

                if (subjectMode.equalsIgnoreCase("Mobile Recharge Dispute")){
                    setTrId();
                }

                if (subjectMode.equalsIgnoreCase("Billing Inquiry")){
                    setTrIdInvisible();
                }

                if (subjectMode.equalsIgnoreCase("Sales Inquiry")){
                    setTrIdInvisible();
                }

                if (subjectMode.equalsIgnoreCase("Other Technical Support")){
                    setTrIdInvisible();
                }

                if (subjectMode.equalsIgnoreCase("DMT & AEPS Dispute")){
                    setTrId();
                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setTrIdInvisible() {
        binding.raiseSection.setVisibility(View.GONE);
    }

    private void setTrId() {
        binding.raiseSection.setVisibility(View.VISIBLE);
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