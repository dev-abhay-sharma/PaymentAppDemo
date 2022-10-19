package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.AEPSViewBeneficiaryAdapter;
import com.swinfotech.swpay.databinding.ActivityAepsToWalletSuccessBinding;
import com.swinfotech.swpay.databinding.ActivityAepsaddBeneficiaryBinding;
import com.swinfotech.swpay.model.AEPSViewBeneficiaryGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AEPSAddBeneficiary extends AppCompatActivity {

    ActivityAepsaddBeneficiaryBinding binding;
    AEPSAddBeneficiary activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    AEPSViewBeneficiaryAdapter aepsViewBeneficiaryAdapter;
    List<AEPSViewBeneficiaryGetSet> viewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAepsaddBeneficiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.gotoMainBack1.setOnClickListener(v -> {
            onBackPressed();
        });

        // set the layout manager on recyclerview
        binding.beneListRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.beneListRecView.setHasFixedSize(true);
        aepsViewBeneficiaryAdapter = new AEPSViewBeneficiaryAdapter(activity, viewList);
        binding.beneListRecView.setAdapter(aepsViewBeneficiaryAdapter);

        onSelect();

        binding.aepsAddBeneSubmit.setOnClickListener(v -> {
            if (getStringFromEditText(binding.aepsAddBeneName) == null) {
                binding.aepsAddBeneName.setError("Please Enter A/C Holder Name");
                binding.aepsAddBeneName.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBeneBankName) == null){
                binding.aepsAddBeneBankName.setError("Please Enter Bank Name");
                binding.aepsAddBeneBankName.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBeneAccNo) == null){
                binding.aepsAddBeneAccNo.setError("Please Enter Account No");
                binding.aepsAddBeneAccNo.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBeneIfsc) == null){
                binding.aepsAddBeneIfsc.setError("Please Enter IFSC Code");
                binding.aepsAddBeneIfsc.requestFocus();
            } else {
                aepsAddBeneficiary();
            }
        });

    }

    private void aepsAddBeneficiary() {
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSAddBeneficiary";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String resp = "";
                            if (object.has("Resp")) {
                                resp = object.getString("Resp");
                            }

                            aepsAddBeneSuccess(resp);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
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
                params.put("UserName", getStringFromEditText(binding.aepsAddBeneName));
                params.put("BankAC", getStringFromEditText(binding.aepsAddBeneAccNo));
                params.put("BankIFSC", getStringFromEditText(binding.aepsAddBeneIfsc));
                params.put("BankName", getStringFromEditText(binding.aepsAddBeneBankName));
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

    private void aepsAddBeneSuccess(String resp) {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);

        ActivityAepsToWalletSuccessBinding binding1 = ActivityAepsToWalletSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding1.getRoot());

        binding1.aepsToWalletDetails.setVisibility(View.GONE);
        binding1.aepsToBankDetails.setVisibility(View.GONE);

        binding1.aepsAddBeneResp.setText(resp);

        binding1.aepsAddBeneDone.setOnClickListener(v -> {
            dialog.dismiss();
            onBackPressed();
        });

        dialog.show();
    }

    private void onSelect() {
        binding.viewBeneText.setOnClickListener(v -> {
            binding.viewBeneText.setTextColor(ContextCompat.getColor(activity, R.color.white));
            binding.viewBeneText.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.addBene.setVisibility(View.GONE);
            binding.beneListRecView.setVisibility(View.VISIBLE);

            binding.addBeneText.setTextColor(ContextCompat.getColor(activity, R.color.blue));
            binding.addBeneText.setBackground(getResources().getDrawable(R.drawable.white_button_background));

            viewBeneListAPI();

        });


        binding.addBeneText.setOnClickListener(v -> {
            binding.addBeneText.setTextColor(ContextCompat.getColor(activity, R.color.white));
            binding.addBeneText.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.beneListRecView.setVisibility(View.GONE);
            binding.addBene.setVisibility(View.VISIBLE);

            binding.viewBeneText.setTextColor(ContextCompat.getColor(activity, R.color.blue));
            binding.viewBeneText.setBackground(getResources().getDrawable(R.drawable.white_button_background));
        });

    }

    private void viewBeneListAPI() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_AEPS_BENE_LIST + url,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);

                    try {
                        JSONObject object = new JSONObject(response);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, error -> {
                    hideLoadingDialog();
                    Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                    System.out.println(error);
                });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
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