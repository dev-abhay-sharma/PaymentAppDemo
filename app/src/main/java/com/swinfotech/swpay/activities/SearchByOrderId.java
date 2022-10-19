package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
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
import com.swinfotech.swpay.adapter.SearchByOrderIdAdapter;
import com.swinfotech.swpay.databinding.ActivitySearchByOrderIdBinding;
import com.swinfotech.swpay.model.SearchByOrderIdGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchByOrderId extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    ActivitySearchByOrderIdBinding binding;
    SearchByOrderId activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    List<SearchByOrderIdGetSet> searchOrIdReport = new ArrayList<>();
    SearchByOrderIdAdapter searchByOrderIdAdapter;
    Session session;
    int type = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchByOrderIdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(this);

        binding.backToReports1.setOnClickListener(v -> {
            onBackPressed();
        });


        binding.searchRecOrId.setLayoutManager(new LinearLayoutManager(activity));
        binding.searchRecOrId.setHasFixedSize(true);
        searchByOrderIdAdapter = new SearchByOrderIdAdapter(activity, searchOrIdReport);
        binding.searchRecOrId.setAdapter(searchByOrderIdAdapter);

        // Search View

        binding.searchId.setSubmitButtonEnabled(true);
        binding.searchId.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                String userId = query;
                String url = "https://mobile.swpay.in/api/AndroidAPI/SearchTransReport?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&";
                url = url + "inpsearch=";
                url = url + userId;
                url = url + "&type=";
                url = url + type + "&tok=" + session.getString(Session.TOKEN);

                System.out.println(url);
//                SearchTReport = new ArrayList<>();

                extractAPI(url);

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }

    private void extractAPI(String url) {
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            searchOrIdReport.clear();

                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("SearchTrans");
                                JSONArray jsonArray = obj.getJSONArray("data");

                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        SearchByOrderIdGetSet data = new SearchByOrderIdGetSet();
                                        if (object.has("user_number")) {
                                            data.setPhNum(object.getString("user_number"));
                                        }

                                        if (object.has("amount")) {
                                            data.setAmount(object.getString("amount"));
                                        }

                                        if (object.has("status")) {
                                            data.setStatus(object.getString("status"));
                                        }

                                        if (object.has("tr_date")) {
                                            data.setDate(object.getString("tr_date"));
                                        }

                                        if (object.has("id")) {
                                            data.setTrId(object.getString("id"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOpName(object.getString("operator_name"));
                                        }

                                        if (object.has("id")) {
                                            data.setId(object.getString("id"));
                                        }

                                        if (object.has("image")) {
                                            data.setImage(object.getString("image"));
                                        }

                                        searchOrIdReport.add(data);
                                        searchByOrderIdAdapter.notifyDataSetChanged();
                                    }
                                    if (searchOrIdReport.isEmpty()) {
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

    private void setData(boolean isEmptyData) {
        // check the data if it is empty or not
        if (isEmptyData) {
            binding.dataNotFound1.setVisibility(View.VISIBLE);
            binding.searchRecOrId.setVisibility(View.GONE);
        } else {
            binding.dataNotFound1.setVisibility(View.GONE);
            binding.searchRecOrId.setVisibility(View.VISIBLE);
            searchByOrderIdAdapter.notifyDataSetChanged();
        }

    }

}