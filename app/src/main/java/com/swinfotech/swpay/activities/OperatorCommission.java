package com.swinfotech.swpay.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.SearchView;
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
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.OPCommissionAdapter;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.model.OPCommissionGETSET;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OperatorCommission extends AppCompatActivity {
    RecyclerView recyclerView;
    List<OPCommissionGETSET> commission;
    OPCommissionAdapter opCommissionAdapter;
    private Session session;

    OperatorCommission activity;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    androidx.appcompat.widget.SearchView search;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_commission);

        activity =  this;

        recyclerView = findViewById(R.id.commissionList);
        search = findViewById(R.id.search_view_commission);


        commission = new ArrayList<>();
        session = new Session(this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        opCommissionAdapter = new OPCommissionAdapter(getApplicationContext(), commission);
        recyclerView.setAdapter(opCommissionAdapter);




        search.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                opCommissionAdapter.getFilter().filter(s.toString());
                return false;
            }
        });


        extractAPI();

    }

    private void extractAPI() {
        showLoadingDialog();
        String JSON_URL = "https://mobile.swpay.in/api/AndroidAPI/Commisions?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        try {
                            Log.e("TAG", "onResponse: " + response);
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("Resp")) {
                                Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
//                            JSONObject obj = jsonObject.getJSONObject("Commission");
                            JSONArray jsonArray = jsonObject.getJSONArray("Commision");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String op = object.getString("operator_name");
                                    String comPer = object.getString("comm_per");
                                    String comVal = object.getString("comm_val");
                                    String serChPer = object.getString("service_charge_per");
                                    String serChVal = object.getString("service_charge_val");
                                    String image = object.getString("image");
                                    commission.add(new OPCommissionGETSET(op, comPer, comVal, serChPer, serChVal, image));
                                }
                                opCommissionAdapter = new OPCommissionAdapter(getApplicationContext(), commission);
                                recyclerView.setAdapter(opCommissionAdapter);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

}