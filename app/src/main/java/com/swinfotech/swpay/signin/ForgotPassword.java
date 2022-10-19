package com.swinfotech.swpay.signin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
//import com.example.swpay.activities.BeneficiaryList;
//import com.example.swpay.databinding.ActivityBbpsBillConfirmBinding;
import com.swinfotech.swpay.databinding.ActivityForgetSuccessMessageBinding;
import com.swinfotech.swpay.databinding.ActivityForgotPasswordBinding;
import com.swinfotech.swpay.databinding.ActivitySetNewPasswordBinding;
import com.swinfotech.swpay.databinding.ActivitySignUpBinding;
import com.google.android.material.textfield.TextInputEditText;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ForgotPassword extends AppCompatActivity {

    ActivityForgotPasswordBinding binding;
    ForgotPassword activity;

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    //Declare timer
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

        session = new Session(activity);

        binding.forgotBackLogin.setOnClickListener(view -> {
            onBackPressed();
        });


        binding.forgotNext.setOnClickListener(view -> {
            if (getStringFromEditText(binding.forgotMob) == null){
                binding.forgotMob.setError("Please Enter Mobile Number");
                binding.forgotMob.requestFocus();
            } else if(getStringFromEditText(binding.forgotMob).length() < 10) {
                binding.forgotMob.setError("Please Enter 10 Digit Mobile Number");
                binding.forgotMob.requestFocus();
            }
            else{
                forgotPassAPI1();
            }

        });

    }


    private void forgotPassAPI1() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/Users/PassSendOTP";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(response);
                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String msg = "";
                            if (object.has("Message")){
                                msg = object.getString("Message");
                            }

                            if (status.equalsIgnoreCase("200")){
                                showVerifyOTP(msg);
                            } else {
                                showVerifyOTP(msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("forgot_mobile", getStringFromEditText(binding.forgotMob));
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }




    private void showVerifyOTP(String msg) {
        ActivitySetNewPasswordBinding binding1  = ActivitySetNewPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());

        binding1.back3.setOnClickListener(v -> {
            onBackPressed();
        });

        binding1.forOtp.setText(msg);
        binding1.forOtp.setTextColor(ContextCompat.getColor(activity, R.color.green));

        binding1.newPassDone.setOnClickListener(v -> {
            if (binding1.forgotOTP.getText().toString().equalsIgnoreCase("")){
                binding1.forgotOTP.setError("Please Enter OTP");
                binding1.forgotOTP.requestFocus();
            } else if (binding1.forgotOTP.getText().length() < 6){
                binding1.forgotOTP.setError("Please Enter 6 Digit OTP");
                binding1.forgotOTP.requestFocus();
            } else if (getStringFromEditText(binding1.setNewPassword) == null) {
                binding1.setNewPassword.setError("Please Enter New Password");
                binding1.setNewPassword.requestFocus();
            } else {
                verifyApi(binding1.forgotOTP, binding1.setNewPassword);
            }

        });

        binding1.reSend.setOnClickListener(v -> {
            binding1.reSend.setTextColor(ContextCompat.getColor(activity, R.color.grey));
            binding1.timer.setVisibility(View.VISIBLE);
            binding1.timer.setTextColor(ContextCompat.getColor(activity, R.color.grey_400));
            startTimer(binding1.reSend, binding1.timer);

            // Now Call The API Again For OTP
            forgotPassAPIAgain();

        });

    }

    private void verifyApi(PinView forgotOTP, TextInputEditText setNewPassword) {
        cancelTimer();
        showLoadingDialog();
        String otp = String.valueOf(forgotOTP.getText());
        String url = "https://mobile.swpay.in/api/Users/OTPBasedResetPass";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String status1 = "";
                            if (object.has("Status")){
                                status1 = object.getString("Status");
                            }

                            String msg1 = "";
                            if (object.has("Message")){
                                msg1 = object.getString("Message");
                            }

                            if (status1.equalsIgnoreCase("403")){
                                showForgotSuccess(status1, msg1);
                            } else {
                                showForgotSuccess(status1, msg1);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("otp", otp);
                params.put("newpassword", getStringFromEditText(setNewPassword));
                params.put("mobile", getStringFromEditText(binding.forgotMob));
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showForgotSuccess(String status, String msg) {
        ActivityForgetSuccessMessageBinding bind = ActivityForgetSuccessMessageBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        if (status.equalsIgnoreCase("403")){
            bind.successMessageDescription.setText(msg);
            bind.successMessageDescription.setTextColor(ContextCompat.getColor(activity, R.color.red));
            bind.successMessageTitle.setText("password not Updated");
            bind.successMessageIcon.setImageDrawable(getResources().getDrawable(R.drawable.cross_red1));
            bind.backToLogin.setVisibility(View.GONE);
        } else {
            bind.successMessageDescription.setText(msg);
            bind.successMessageDescription.setTextColor(ContextCompat.getColor(activity, R.color.green));
        }

        bind.backToLogin.setOnClickListener(v -> {
            startActivity(new Intent(activity, Login.class));
            finish();
        });

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

    private void forgotPassAPIAgain() {
//        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/Users/PassSendOTP";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        hideLoadingDialog();
                        System.out.println(response);

//                        try {
//                            JSONObject object = new JSONObject(response);
//                            String status = "";
//                            if (object.has("Status")){
//                                status = object.getString("Status");
//                            }
//
//                            String msg = "";
//                            if (object.has("Message")){
//                                msg = object.getString("Message");
//                            }
//
//                            if (status.equalsIgnoreCase("200")){
//                                showVerifyOTP(msg);
//                            } else {
//                                showVerifyOTP(msg);
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("forgot_mobile", getStringFromEditText(binding.forgotMob));
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    //start timer function
    void startTimer(AppCompatButton reSend, TextView timer) {
        reSend.setEnabled(false);
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                timer.setText("Seconds Remaining : " + String.valueOf(millisUntilFinished/500));
            }
            public void onFinish() {
                timer.setTextColor(ContextCompat.getColor(activity, R.color.red));
                timer.setText("Re send OTP!");
                reSend.setEnabled(true);
                reSend.setTextColor(ContextCompat.getColor(activity, R.color.red));
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
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