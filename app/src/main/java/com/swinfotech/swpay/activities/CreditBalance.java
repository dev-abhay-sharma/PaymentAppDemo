package com.swinfotech.swpay.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
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
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.CreditBalUsersAdapter;
import com.swinfotech.swpay.databinding.ActivityCreditBalanceBinding;
import com.swinfotech.swpay.model.CreditBalUsersListGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CreditBalance extends AppCompatActivity {

    ActivityCreditBalanceBinding binding;
    List<CreditBalUsersListGetSet> CreditList = new ArrayList<>();
    CreditBalUsersAdapter creditBalUsersAdapter;
    Dialog dialog;
    Session session;
    private CreditBalance activity;
    // this is for knowing if api successfully hit or not we will take it later
    private boolean isDataGetSuccessfully = false;

    // make a globule object so we can use in the whole activity
    private CreditBalUsersListGetSet userData;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreditBalanceBinding.inflate(getLayoutInflater());

        // getting our root layout in our view.
        View view = binding.getRoot();

        // below line is to set
        // Content view for our layout.
        setContentView(view);

        binding.gotoMainDASHBO.setOnClickListener(v -> {
            onBackPressed();
        });

        activity = this;
        session = new Session(activity);
        viewUsersAPI();

        binding.selectUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenDialog();
            }
        });
        onClick();
    }


    private void OpenDialog() {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.activity_credit_bal_users);

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // set this to true if you want to cancel the dialog when user click outside of the dialog or set false if you
        // want not cancelable dialog
        dialog.setCancelable(true);


        SearchView searchView;
        searchView = dialog.findViewById(R.id.searchViewUser);


        ///get the dialog view using dialog object
        RecyclerView recyclerView = dialog.findViewById(R.id.crRecView);
        // check if the list empty or not
        if (CreditList.isEmpty()) {
            // check if the data is not coming
            if (!isDataGetSuccessfully) {
                // then call again the api if for any reason the api not gived any data or there is any error in api
                viewUsersAPI();
            }
        }
        creditBalUsersAdapter = new CreditBalUsersAdapter(activity, CreditList);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        recyclerView.setAdapter(creditBalUsersAdapter);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                creditBalUsersAdapter.getFilter().filter(s);
                return false;
            }
        });


        // for showing dailog
        dialog.show();


    }


    private void viewUsersAPI() {
        //here change the username and password with session value
        String url = "https://mobile.swpay.in/api/AndroidAPI/ViewUsers?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        System.out.println(response);
                        Log.e("TAG", "onResponse: " + response);
                        try {
                            CreditList.clear();
                            // check if response is null or not
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(activity, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
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
                                        if (object.has("id")) {
                                            // setting the id
                                            data.setId(object.getString("id"));
                                        }

                                        if (object.has("balance_amt")) {
                                            // setting the id
                                            data.setAmount(object.getString("balance_amt"));
                                        }

                                        CreditList.add(data);
                                    }

                                    // set this variable to true so we know that api hits successfully
                                    isDataGetSuccessfully = true;
                                    //check the list if it is empty or not
                                    if (CreditList.isEmpty()) {

                                    } else {

                                    }
                                } else {

                                }
                            } else {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // you can change here the boolean variable  to true if you don't want to reload data after api not responding
                Log.d("tag", "onErrorResponse: " + error.getMessage());
            }
        }) {

        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    public void setNameToActivity(int position) {
        // here get the object from our list
        userData = CreditList.get(position);

        // set the username to our text view
//        binding.selectUsers.setText(userData.getUserName());
        binding.userName1.setText(userData.getUserName());
        binding.userMobile1.setText(userData.getUserMobile());

        dialog.cancel();
    }


    private void onClick() {

        binding.walletAddBal.setOnClickListener(v -> {
            if (getStringFromEditText(binding.userName1) == null) {
                binding.userName1.setError("Please Enter Name");
                binding.userName1.requestFocus();
            } else if (getStringFromEditText(binding.userMobile1) == null) {
                binding.userMobile1.setError("Please Enter Mobile Number");
                binding.userMobile1.requestFocus();
            } else if (getStringFromEditText(binding.userAmount) == null) {
                binding.userAmount.setError("Please Enter Amount");
                binding.userAmount.requestFocus();
            } else if (getStringFromEditText(binding.userTPIN) == null) {
                binding.userTPIN.setError("Please Enter TPIN");
                binding.userTPIN.requestFocus();
            } else if (getStringFromEditText(binding.userRemark) == null) {
                binding.userRemark.setError("Please Enter Remarks");
                binding.userRemark.requestFocus();
            } else if (userData == null) {
                Toast.makeText(activity, "Select User First", Toast.LENGTH_SHORT).show();
            } else {
                creditAddAPI();
            }
        });

    }

    private void creditAddAPI() {
        showLoadingDialog();
        String user_type = "";
        // check if user is MDT
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("1")) {
            // then usertype is 2 for DT
            user_type = "2";
        } // if user is DT
        else if (session.getString(Session.USER_TYPE).equalsIgnoreCase("2")) {
            // then user type is 3 for RT
            user_type = "3";
        }

        String Url = "https://mobile.swpay.in/api/AndroidAPI/CreditAdd?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&credit_amount=" + getStringFromEditText(binding.userAmount) + "&tpin=" + getStringFromEditText(binding.userTPIN) + "&user_type=" + user_type + "&target_user_id=" + userData.getId() + "&comment=" + getStringFromEditText(binding.userRemark) + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        Log.d("Response", response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("Resp")) {
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                if (obj.getString("Resp").equalsIgnoreCase("Success")) {
                                    binding.userAmount.setText("");
                                    binding.userTPIN.setText("");
                                    binding.userRemark.setText("");
                                    binding.userName1.setText("");
                                    binding.userMobile1.setText("");
                                }
                                return;
                            }
                            Toast.makeText(activity, "Credit Add Successfully", Toast.LENGTH_SHORT).show();

                            // here we get the response when all api is ready then we get response here

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        try {
                            Toast.makeText(activity, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private String getStringFromEditText(EditText editText) {
        // check if edittext is null or not
        if (editText == null) {
            // then return null
            return null;
        }
        // check if edittext is empty
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            // then also return null for better optimization
            return null;
        }
        // else return the value
        return editText.getText().toString().trim();
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