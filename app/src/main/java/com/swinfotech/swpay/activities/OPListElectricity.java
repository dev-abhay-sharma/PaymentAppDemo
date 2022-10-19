package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import com.swinfotech.swpay.adapter.OPListElectricityAdapter;
import com.swinfotech.swpay.databinding.ActivityOplistElectricityBinding;
import com.swinfotech.swpay.model.OPListElectricityGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class OPListElectricity extends AppCompatActivity {

    ActivityOplistElectricityBinding binding;
    OPListElectricity activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    private Session session;

    private String services_id;

    private static int VOLLEY_TIMEOUT = 1000 * 30;
    OPListElectricityAdapter opListElectricityAdapter;
    List<OPListElectricityGetSet> opList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOplistElectricityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.bToMain1.setOnClickListener(v -> {
            onBackPressed();
        });

        // set the layout manager on recyclerview
        binding.electricityOpRec.setLayoutManager(new LinearLayoutManager(activity));
        binding.electricityOpRec.setHasFixedSize(true);
        opListElectricityAdapter = new OPListElectricityAdapter(activity, opList);
        binding.electricityOpRec.setAdapter(opListElectricityAdapter);

        binding.searchElectricity.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                opListElectricityAdapter.getFilter().filter(newText.toString());
                return false;
            }
        });

        initView();

        opListElectricityAPI();


    }

    private void initView() {
        // get tr id here
        if (getIntent() != null) {
            services_id = getIntent().getStringExtra("services_id");
        }
    }

    private void opListElectricityAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/BbpsServices?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            opList.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("StatementReport");

                                JSONObject obj = jsonArray.getJSONObject(getIntent().getIntExtra("position",0));

                                JSONArray jArray = obj.getJSONArray("op");

                                if (jArray.length() > 0) {
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject object = jArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        OPListElectricityGetSet data = new OPListElectricityGetSet();

                                        if (object.has("op_id")) {
                                            data.setId(object.getString("op_id"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOpName(object.getString("operator_name"));
                                        }

                                        if (object.has("op_image")) {
                                            data.setImage(object.getString("op_image"));
                                        }

                                        if (object.has("our_op_code")) {
                                            data.setOpCode(object.getString("our_op_code"));
                                        }

                                        opList.add(data);
                                        opListElectricityAdapter.notifyDataSetChanged();
                                    }
                                    //check the list if it is empty or not
                                    if (opList.isEmpty()) {
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


    private void opListGasAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/BbpsServices?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            opList.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("StatementReport");



                                JSONObject obj = jsonArray.getJSONObject(1);
                                JSONArray jArray = obj.getJSONArray("op");

                                if (jArray.length() > 0) {
                                    for (int i = 0; i < jArray.length(); i++) {
                                        JSONObject object = jArray.getJSONObject(i);
                                        //check the object has parameter or not then add into pojo class
                                        OPListElectricityGetSet data = new OPListElectricityGetSet();

                                        if (object.has("op_id")) {
                                            data.setId(object.getString("op_id"));
                                        }

                                        if (object.has("operator_name")) {
                                            data.setOpName(object.getString("operator_name"));
                                        }

                                        if (object.has("op_image")) {
                                            data.setImage(object.getString("op_image"));
                                        }

                                        if (object.has("our_op_code")) {
                                            data.setOpCode(object.getString("our_op_code"));
                                        }

                                        opList.add(data);
                                        opListElectricityAdapter.notifyDataSetChanged();
                                    }
                                    //check the list if it is empty or not
                                    if (opList.isEmpty()) {
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