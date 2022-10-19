package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.GridLayoutManager;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.BBPSCategorieAdapter;
import com.swinfotech.swpay.databinding.ActivityBbpscategorieBinding;
import com.swinfotech.swpay.model.BBPsCategorieGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BBPSCategorie extends AppCompatActivity {

    ActivityBbpscategorieBinding binding;
    BBPSCategorie activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    BBPSCategorieAdapter bbpsCategorieAdapter;
    List<BBPsCategorieGetSet> categorieList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBbpscategorieBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(this);

        binding.bToMain.setOnClickListener(v -> {
            onBackPressed();
        });

        // set the layout manager on recyclerview

        binding.bbpsRecView.setLayoutManager(new GridLayoutManager(activity, 2));

        binding.bbpsRecView.setHasFixedSize(true);
        bbpsCategorieAdapter = new BBPSCategorieAdapter(activity, categorieList);
        binding.bbpsRecView.setAdapter(bbpsCategorieAdapter);

        binding.searchCategories.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                bbpsCategorieAdapter.getFilter().filter(newText.toString());
                return false;
            }
        });

        BBPSCategorieAPI();

    }

    private void BBPSCategorieAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/BbpsServices?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            categorieList.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONArray jsonArray = jsonObject.getJSONArray("StatementReport");
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object = jsonArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        BBPsCategorieGetSet data = new BBPsCategorieGetSet();

                                        if (object.has("services_id")) {
                                            data.setId(object.getString("services_id"));
                                        }

                                        if (object.has("service_name")) {
                                            data.setOpName(object.getString("service_name"));
                                        }

                                        if (object.has("service_type")) {
                                            data.setImage(object.getString("service_type"));
                                        }

                                        categorieList.add(data);
                                        bbpsCategorieAdapter.notifyDataSetChanged();
                                    }
                                    //check the list if it is empty or not
                                    if (categorieList.isEmpty()) {
                                        System.out.println("List is Empty");
                                        Toast.makeText(activity, "List is Empty", Toast.LENGTH_SHORT).show();
                                    } else {
                                        System.out.println("List is Full");
                                    }

                                } else {
                                    System.out.println("Json Array is not Responding");
                                    Toast.makeText(activity, "Json Array is not Responding", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                System.out.println("Response is Null");
                                Toast.makeText(activity, "Response is Null", Toast.LENGTH_SHORT).show();
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
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
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