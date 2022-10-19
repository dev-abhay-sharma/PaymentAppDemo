package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

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
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
//import com.example.swpay.adapter.PaymentLoadReportAdapter;
import com.swinfotech.swpay.adapter.RechargeOperatorListAdapter;
//import com.example.swpay.databinding.ActivityAddBeneficiaryBinding;
import com.swinfotech.swpay.databinding.ActivityRechargeOperatorListBinding;
import com.swinfotech.swpay.model.NewOPListGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RechargeOperatorList extends AppCompatActivity implements RechargeOperatorListAdapter.OnTextClickListener{

    ActivityRechargeOperatorListBinding binding;
    RechargeOperatorList activity;

    RechargeOperatorListAdapter rechargeOperatorListAdapter;
    List<NewOPListGetSet> opList = new ArrayList<>();

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRechargeOperatorListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.back2.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.newOpList.setLayoutManager(new GridLayoutManager(activity, 2));
//        binding.operatorList.setLayoutManager(new LinearLayoutManager(activity));
        binding.newOpList.setHasFixedSize(true);
        rechargeOperatorListAdapter = new RechargeOperatorListAdapter(activity, opList, this);
        binding.newOpList.setAdapter(rechargeOperatorListAdapter);

        getOperatorList();

    }

    private void getOperatorList() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/GetOperator?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            opList.clear();

                            if (response != null) {
                                JSONObject obj = new JSONObject(response);
                                JSONObject object = obj.getJSONObject("MobileOpFetch");
                                JSONArray jsonArray = object.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        NewOPListGetSet data = new NewOPListGetSet();
                                        JSONObject obj1 = jsonArray.getJSONObject(i);

                                        if (obj1.has("op_id")) {
                                            data.setOpId(obj1.getString("op_id"));
                                        }

                                        if (obj1.has("operator_name")) {
                                            data.setOpName(obj1.getString("operator_name"));
                                        }

                                        if (obj1.has("image")) {
                                            data.setImage(obj1.getString("image"));
                                        }

                                        opList.add(data);

                                        rechargeOperatorListAdapter.notifyDataSetChanged();

//                                        String opId = data.getOpId();
//                                        System.out.println(opId);

                                    }
                                } else {
                                    System.out.println("Json Array is Not Coming");
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
    public void onTextClick(NewOPListGetSet opList) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("opreator",opList.getOpName());
        returnIntent.putExtra("opreator_id",opList.getOpId());
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