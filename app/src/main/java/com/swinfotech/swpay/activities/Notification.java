package com.swinfotech.swpay.activities;

import static com.swinfotech.swpay.Constant.VOLLEY_TIMEOUT;
import static com.swinfotech.swpay.Session.PASSWORD;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.NotificationAdapter;
import com.swinfotech.swpay.databinding.ActivityNotificationBinding;
import com.swinfotech.swpay.model.NotificationGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Notification extends AppCompatActivity {

    ActivityNotificationBinding binding;
    Notification activity;

    NotificationAdapter notificationAdapter;
    List<NotificationGetSet> notify = new ArrayList<>();
//    private ProgressDialog progressDialog;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;
        session = new Session(activity);

        binding.back4.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.notificationImg.setLayoutManager(new LinearLayoutManager(activity));
        binding.notificationImg.setHasFixedSize(true);
        notificationAdapter = new NotificationAdapter(activity, notify);
        binding.notificationImg.setAdapter(notificationAdapter);
        getNotificationDataFromApi();
    }


    private void getNotificationDataFromApi() {
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.SLIDER_IMAGE_API + "username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(PASSWORD) + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            // check if response is not null
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("GetNotifImages")) {
                                    JSONObject jb1 = jsonObject.getJSONObject("GetNotifImages");
                                    if (jb1.has("data")) {
                                        JSONArray jsonArray = jb1.getJSONArray("data");
                                        if (jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jb = jsonArray.getJSONObject(i);
                                                if (jb.has("sliding_image")) {
                                                    notify.add(new NotificationGetSet(jb.getString("sliding_image")));
                                                }
                                            }
                                            notificationAdapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            hideLoadingDialog();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Log.e("tag", "onErrorResponse: " + error.getMessage());
                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
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