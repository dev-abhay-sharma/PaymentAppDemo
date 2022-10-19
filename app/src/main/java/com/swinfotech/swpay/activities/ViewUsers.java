package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.ViewUsersAdapter;
import com.swinfotech.swpay.databinding.ActivityViewUsersBinding;
import com.swinfotech.swpay.model.ViewUsersGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewUsers extends AppCompatActivity {

    // init your list first so you don't get error anywhere of null
    List<ViewUsersGetSet> VuReport = new ArrayList<>();
    ViewUsersAdapter viewUsersAdapter;

    Session session;

    ActivityViewUsersBinding binding;
    private ViewUsers activity;

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityViewUsersBinding.inflate(getLayoutInflater());

        View view = binding.getRoot();
        setContentView(view);

        activity = this;

        session = new Session(activity);

        binding.goBackMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        // set the layout manager on recyclerview
        binding.vuRecView.setLayoutManager(new LinearLayoutManager(activity));
        binding.vuRecView.setHasFixedSize(true);
        viewUsersAdapter = new ViewUsersAdapter(activity, VuReport);
        binding.vuRecView.setAdapter(viewUsersAdapter);

        viewUsersAPI();

        binding.vuSearch.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                viewUsersAdapter.getFilter().filter(s);
                return false;
            }
        });

    }


    private void viewUsersAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/ViewUsers?username=" + session.getString(Session.MOBILE) + "&password="+ session.getString(Session.PASSWORD)+"&limit=100000" + "&tok=" + session.getString(Session.TOKEN);
        System.out.println(url);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            VuReport.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject obj = jsonObject.getJSONObject("FetchUsers");
                                // for total Users Count
                                if (obj.has("total")) {
                                    binding.totalUsers.setText(obj.getString("total"));
                                }
                                JSONArray jsonArray = obj.getJSONArray("data");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        ViewUsersGetSet data = new ViewUsersGetSet();
                                        if (object.has("id")) {
                                            data.setUserId(object.getString("id"));
                                        }

                                        if (object.has("user_fname")) {
                                            data.setUserName(object.getString("user_fname"));
                                        }

                                        if (object.has("u_status")) {
                                            data.setUserStatus(object.getString("u_status"));
                                        }

                                        if (object.has("user_mobile")) {
                                            data.setUserMobile(object.getString("user_mobile"));
                                        }

                                        if (object.has("user_email")) {
                                            data.setUserEmail(object.getString("user_email"));
                                        }

                                        if (object.has("firm_name")) {
                                            data.setUserFirmName(object.getString("firm_name"));
                                        }

                                        if (object.has("balance_amt")) {
                                            data.setBalanceAmt(object.getString("balance_amt"));
                                        }


                                        VuReport.add(data);
                                    }
                                    //check the list if it is empty or not
                                    if (VuReport.isEmpty()) {
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

    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
//            binding.noData.setVisibility(View.VISIBLE);
            binding.vuRecView.setVisibility(View.GONE);
        } else {
//            binding.noData.setVisibility(View.GONE);
            binding.vuRecView.setVisibility(View.VISIBLE);
            viewUsersAdapter.notifyDataSetChanged();
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