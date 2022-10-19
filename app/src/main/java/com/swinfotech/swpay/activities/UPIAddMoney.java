package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.snackbar.Snackbar;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityUpiaddMoneyBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UPIAddMoney extends AppCompatActivity {

    ActivityUpiaddMoneyBinding binding;
    UPIAddMoney activity;
    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpiaddMoneyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);

        binding.upiBack.setOnClickListener(v -> onBackPressed());

        getQRCode();

    }

    private void getQRCode() {
        binding.getQrCode.setOnClickListener(v -> {
            if (getStringFromEditText(binding.upiAmount) == null) {
                binding.upiAmount.setError("Please Enter Amount");
                binding.upiAmount.requestFocus();
            } else {
                runQRCodeAPI();
            }
        });
    }

    private void runQRCodeAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/UPIAddMoney";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String upiUrl = "";
                            if (object.has("UPIUrl")){
                                upiUrl = object.getString("UPIUrl");
                            }

                            String orderId = "";
                            if (object.has("OrderId")){
                                orderId = object.getString("OrderId");
                            }

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String resp = "";
                            if (object.has("Resp")){
                                resp = object.getString("Resp");
                            }
                            
                            if (status.equalsIgnoreCase("true")) {
                                System.out.println("Successfully");
                                showQRCode(upiUrl);
                            } else {
//                                Toast.makeText(activity, resp, Toast.LENGTH_SHORT).show();
                                Snackbar snackbar = Snackbar.make(binding.addUPILayout, resp, Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(getResources().getColor(R.color.light_red));
                                snackbar.setTextColor(getResources().getColor(R.color.black));
                                snackbar.show();
                                System.out.println("Service under maintenance");
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
                params.put("UPIAmount", getStringFromEditText(binding.upiAmount));
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

    private void showQRCode(String upiUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(upiUrl));
        startActivity(browserIntent);
        binding.upiAmount.setText("");
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