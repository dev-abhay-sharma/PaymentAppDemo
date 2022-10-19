package com.swinfotech.swpay.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.CreditBalUsersAdapter;
import com.swinfotech.swpay.databinding.ActivityCreditBalUsersBinding;
import com.swinfotech.swpay.model.CreditBalUsersListGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CreditBalUsers extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    List<CreditBalUsersListGetSet> CreditList = new ArrayList<>();
    CreditBalUsersAdapter creditBalUsersAdapter;
    ActivityCreditBalUsersBinding binding;
    private CreditBalUsers activity;
    private ProgressDialog progressDialog;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditBalUsersBinding.inflate(getLayoutInflater());


        View view = binding.getRoot();

        setContentView(view);

        activity = this;

        session = new Session(activity);
        this.setFinishOnTouchOutside(false);

        binding.crRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.crRecView.setHasFixedSize(true);
        creditBalUsersAdapter = new CreditBalUsersAdapter(activity, CreditList);
        binding.crRecView.setAdapter(creditBalUsersAdapter);

        viewUsersAPI();

    }

    private void viewUsersAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ViewUsers?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            CreditList.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("FetchUsers");
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        CreditBalUsersListGetSet data = new CreditBalUsersListGetSet();

                                        if (object.has("user_fname")) {
                                            data.setUserName(object.getString("user_fname"));
                                        }

                                        if (object.has("user_mobile")) {
                                            data.setUserMobile(object.getString("user_mobile"));
                                        }

                                        CreditList.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (CreditList.isEmpty()) {
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

    @SuppressLint("NotifyDataSetChanged")
    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
//            binding.noData.setVisibility(View.VISIBLE);
            binding.crRecView.setVisibility(View.GONE);
        } else {
//            binding.noData.setVisibility(View.GONE);
            binding.crRecView.setVisibility(View.VISIBLE);
            creditBalUsersAdapter.notifyDataSetChanged();
        }

    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Fetching View Users");
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