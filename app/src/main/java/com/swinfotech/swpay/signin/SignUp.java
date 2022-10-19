package com.swinfotech.swpay.signin;

import static com.swinfotech.swpay.Constant.VOLLEY_TIMEOUT;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivitySignUpBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONObject;

public class SignUp extends AppCompatActivity {

    private ActivitySignUpBinding binding;
    private SignUp activity;
    private ProgressDialog progressDialog;
    private Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);
        onClick();

        binding.signupBackLogin.setOnClickListener(view -> {
            onBackPressed();
        });

        binding.backLogin.setOnClickListener(view -> {
            startActivity(new Intent(activity, Login.class));
        });

    }

    private void onClick() {
        // signupButton click
        binding.signUpButton.setOnClickListener(v -> {
            // check if name is empty or not
            if (binding.nameInput.getText().toString().trim().equalsIgnoreCase("")) {
                binding.nameInput.setError("Please Enter Name");
                binding.nameInput.requestFocus();
            } else if (binding.mobNum.getText().toString().trim().equalsIgnoreCase("")) {
                binding.mobNum.setError("Please Enter Number");
                binding.mobNum.requestFocus();
            }  // check if number length is 10 or not
            else if (binding.mobNum.getText().toString().trim().length() != 10) {
                binding.mobNum.setError("Please Enter 10 digit Number");
                binding.mobNum.requestFocus();
            } else if (binding.emailInput.getText().toString().trim().equalsIgnoreCase("")) {
                binding.emailInput.setError("Please Enter Email");
                binding.emailInput.requestFocus();
            } // check if gmail is valid or not
            else if (!binding.emailInput.getText().toString().trim().contains("@gmail.com")) {
                binding.emailInput.setError("Please Enter valid Email");
                binding.emailInput.requestFocus();
            } else if (binding.pinInput.getText().toString().trim().equalsIgnoreCase("")) {
                binding.pinInput.setError("Please Enter Pin");
                binding.pinInput.requestFocus();
            } else if (binding.pinInput.getText().toString().trim().length() < 6) {
                binding.pinInput.setError("Please Enter at least 6 digit pin");
                binding.pinInput.requestFocus();
            } else if (binding.addressInput.getText().toString().trim().equalsIgnoreCase("")) {
                binding.addressInput.setError("Please Enter Address");
                binding.addressInput.requestFocus();
            } else {
                // now attempt sign up proccess
                attemptSignUp();
            }
        });
    }

    private void attemptSignUp() {
//        showProgressDialog();
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.SIGNUP_API + "?user_name=" + binding.nameInput.getText().toString().trim() + "&mobile=" + binding.mobNum.getText().toString().trim() + "&email=" + binding.emailInput.getText().toString().trim() + "&pin=" + binding.pinInput.getText().toString().trim() + "&address=" + binding.addressInput.getText().toString().trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
//                        hideProgressDialog();
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            // check if response is not null
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                String status = jsonObject.getString("Status");
                                if (status.equalsIgnoreCase("true")) {
                                    //set the user login in SharedPreferences
                                    session.setBoolean(Session.IS_LOGIN, true);
                                    // set username and password into SharedPreferences
                                    Toast.makeText(activity, "Register Successfully We Will Send your password on your number please wait for a minute", Toast.LENGTH_SHORT).show();
                                    session.setString(Session.MOBILE, binding.mobNum.getText().toString().trim());
                                    session.setString(Session.PASSWORD, binding.passwordInput.getText().toString().trim());
                                    startActivity(new Intent(activity, Login.class));
                                    finish();
                                } else if (status.equalsIgnoreCase("false")) {
                                    Toast.makeText(activity, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                hideProgressDialog();
                hideLoadingDialog();
                Log.e("tag", "onErrorResponse: " + error.getLocalizedMessage());
                if (error.networkResponse.statusCode == 400) {
                    Toast.makeText(activity, "Signup Failed" + error.networkResponse.toString(), Toast.LENGTH_SHORT).show();
                }
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

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Getting your details");
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