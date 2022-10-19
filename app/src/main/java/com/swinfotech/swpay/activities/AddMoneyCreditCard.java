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
import com.swinfotech.swpay.databinding.ActivityAddMoneyCreditCardBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AddMoneyCreditCard extends AppCompatActivity {

    ActivityAddMoneyCreditCardBinding binding;
    AddMoneyCreditCard activity;
    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddMoneyCreditCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);

        binding.addMoneyBack.setOnClickListener(v -> onBackPressed());

        addMoney();

    }

    private void addMoney() {
        binding.addMoney.setOnClickListener(v -> {
            if (getStringFromEditText(binding.cardAmount) == null) {
                binding.cardAmount.setError("Please Enter Amount");
                binding.cardAmount.requestFocus();
            } else if (getStringFromEditText(binding.cardMessage) == null) {
                binding.cardMessage.setError("Please Enter Message");
                binding.cardMessage.requestFocus();
            } else {
                addMoneyAPI();
            }
        });
    }

    private void addMoneyAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/AddMoneyToWallet";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String addMoneyUrl = "";
                            if (object.has("AddMoneyUrl")){
                                addMoneyUrl = object.getString("AddMoneyUrl");
                            }

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String resp = "";
                            if (object.has("Resp")){
                                resp = object.getString("Resp");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                System.out.println("Successfully");
                                showUrlCode(addMoneyUrl);
                            } else {
//                                Toast.makeText(activity, resp, Toast.LENGTH_SHORT).show();
                                Snackbar snackbar = Snackbar.make(binding.addMoneyLayout, resp, Snackbar.LENGTH_LONG);
                                snackbar.setBackgroundTint(getResources().getColor(R.color.light_red));
                                snackbar.setTextColor(getResources().getColor(R.color.black));
                                snackbar.show();
                                System.out.println("Service Under Maintenance");
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
                params.put("Amount", getStringFromEditText(binding.cardAmount));
                params.put("Message", getStringFromEditText(binding.cardMessage));
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

    private void showUrlCode(String addMoneyUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(addMoneyUrl));
        startActivity(browserIntent);
        binding.cardAmount.setText("");
        binding.cardMessage.setText("");
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