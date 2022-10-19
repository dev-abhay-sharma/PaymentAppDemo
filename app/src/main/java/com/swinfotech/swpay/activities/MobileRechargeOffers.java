package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.FULLTTFragmentAdapter;
import com.swinfotech.swpay.databinding.ActivityMobileRechargeOffersBinding;
import com.swinfotech.swpay.model.FULLTTFragmentGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MobileRechargeOffers extends AppCompatActivity implements FULLTTFragmentAdapter.OnPlanClickListener{

    ActivityMobileRechargeOffersBinding binding;
    MobileRechargeOffers activity;

    String mobile, operator, opId;

    // we use same adapter for mobileRechargeOffers !! this adapter belong to mobile recharge plans
    FULLTTFragmentAdapter fullttFragmentAdapter;
    List<FULLTTFragmentGetSet> fullTT = new ArrayList<>();
    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMobileRechargeOffersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.goToBack2.setOnClickListener(v -> {
            onBackPressed();
        });

        mobile = getIntent().getStringExtra("mobileNo");
        operator = getIntent().getStringExtra("operator");
        opId = getIntent().getStringExtra("opId");

        binding.rOffersRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.rOffersRecView.setHasFixedSize(true);
        fullttFragmentAdapter = new FULLTTFragmentAdapter(activity, fullTT, this);
        binding.rOffersRecView.setAdapter(fullttFragmentAdapter);

        fullTalkTimeAPI(mobile, operator);

//        if (operator.equalsIgnoreCase(""))

    }


    private void fullTalkTimeAPI(String mobile, String operator) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&op=" + operator
                + "&mobile=" + mobile
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.OFFER_API + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        fullTT.clear();

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("apiROffer");

                                String dataObj = "";
                                if (obj.has("data")){
                                    dataObj = obj.getString("data");
                                }

                                JSONObject object = new JSONObject(dataObj);

                                String status = "";
                                if (object.has("status")) {
                                    status = object.getString("status");
                                }

                                if (status.equalsIgnoreCase("1")) {
                                    if (object.has("records")) {
                                        JSONArray jsonArray = object.getJSONArray("records");
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object2 = jsonArray.getJSONObject(i);
                                            FULLTTFragmentGetSet data = new FULLTTFragmentGetSet();
                                            if (object2.has("rs")) {
                                                data.setPrice(object2.getString("rs"));
                                            }

                                            if (object2.has("desc")) {
                                                data.setDetails(object2.getString("desc"));
                                            }

                                            fullTT.add(data);

                                            fullttFragmentAdapter.notifyDataSetChanged();

                                        }
                                    } else {
                                        binding.rOffersRecView.setVisibility(View.GONE);
                                        binding.rOffersError.setVisibility(View.VISIBLE);
                                        System.out.println("Data bhaag gya");
                                    }
                                } else {
                                    binding.rOffersRecView.setVisibility(View.GONE);
                                    binding.rOffersError.setVisibility(View.VISIBLE);
                                    System.out.println("Data bhaag gya status ki vajh se");
                                }




                            } else {
                                System.out.println("Response is Null");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
            }
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private String getTextFromString(String data) {

        // check the data is null or not
        // second condition is optional you can remove this
        if (data == null || data.equalsIgnoreCase("null")) {
            return "";
        }
        return data;
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

    @Override
    public void onPlanClick(FULLTTFragmentGetSet planList) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("price", planList.getPrice());
        setResult(Activity.RESULT_OK,returnIntent);
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent returnIntent = new Intent();
        setResult(Activity.RESULT_CANCELED, returnIntent);
        finish();
    }

}