package com.swinfotech.swpay.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import com.swinfotech.swpay.Constant;

import android.view.View;
import android.widget.EditText;
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
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.SearchByNumberAdapter;
import com.swinfotech.swpay.databinding.ActivitySearchByNumberBinding;
import com.swinfotech.swpay.model.SearchByNumberGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchByNumber extends AppCompatActivity {

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    ActivitySearchByNumberBinding binding;
    SearchByNumber activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    // New Search
    EditText editSearch;

    List<SearchByNumberGetSet> SearchTReport = new ArrayList<>();
    SearchByNumberAdapter searchByNumberAdapter;
    Session session;

    int type = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchByNumberBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(this);

        binding.backToReports.setOnClickListener(view -> {
            onBackPressed();
        });


        binding.searchNumRec.setLayoutManager(new LinearLayoutManager(activity));
        binding.searchNumRec.setHasFixedSize(true);
        searchByNumberAdapter = new SearchByNumberAdapter(activity, SearchTReport);
        binding.searchNumRec.setAdapter(searchByNumberAdapter);


        // Search Start Here

        editSearch = (EditText) findViewById(R.id.searchEdit);
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (charSequence.toString().length() == 10) {
                    String url = "https://mobile.swpay.in/api/AndroidAPI/SearchTransReport?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&";
                    url = url + "inpsearch=";
                    url = url + charSequence;
                    url = url + "&type=";
                    url = url + type + "&tok=" + session.getString(Session.TOKEN);

                    System.out.println(url);
                    SearchTReport = new ArrayList<>();

                    extractAPI(url);
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void extractAPI(String url) {
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("data is here " + response);
                        hideLoadingDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.has("Resp")) {
                                Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                            JSONObject obj = jsonObject.getJSONObject("SearchTrans");
                            JSONArray jsonArray = obj.getJSONArray("data");

                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject object = jsonArray.getJSONObject(i);
                                    String mobNum = object.getString("user_number");
                                    String amt = object.getString("amount");
                                    String state = object.getString("status");
                                    String DandT = object.getString("tr_date");
                                    String traId = object.getString("id");
                                    String operaName = object.getString("operator_name");
                                    String image = object.getString("image");

                                    SearchTReport.add(new SearchByNumberGetSet(mobNum, amt, state, DandT, traId, operaName, object.getString("id"), image));
                                }
//                                recyclerView.setHasFixedSize(true);
//                                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
//                                searchByNumberAdapter = new SearchByNumberAdapter(SearchByNumber.this, SearchTReport);
//                                recyclerView.setAdapter(searchByNumberAdapter);

                                binding.searchNumRec.setHasFixedSize(true);
                                binding.searchNumRec.setLayoutManager(new LinearLayoutManager(activity));
                                searchByNumberAdapter = new SearchByNumberAdapter(activity, SearchTReport);
                                binding.searchNumRec.setAdapter(searchByNumberAdapter);

                                if (SearchTReport.isEmpty()){
                                    setData(true);
                                } else{
                                    setData(false);
                                }
                            }
                            else{
//                                Toast.makeText(activity, "sorry Wrong number", Toast.LENGTH_SHORT).show();
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
            binding.dataNotFound.setVisibility(View.VISIBLE);
            binding.searchNumRec.setVisibility(View.GONE);
        } else {
            binding.dataNotFound.setVisibility(View.GONE);
            binding.searchNumRec.setVisibility(View.VISIBLE);
            searchByNumberAdapter.notifyDataSetChanged();
        }

    }


}
