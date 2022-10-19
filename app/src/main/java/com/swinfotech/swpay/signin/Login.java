package com.swinfotech.swpay.signin;

import static com.swinfotech.swpay.Constant.VOLLEY_TIMEOUT;
import static com.swinfotech.swpay.Session.TOKEN;
import static com.swinfotech.swpay.Session.USER_ID;
import static com.swinfotech.swpay.Session.USER_TYPE;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityLoginBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.databinding.ActivityLoginOtpBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {

    ActivityLoginBinding binding;
    Login activity;
    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);

        String android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Log.d("Android","Android ID : "+android_id);
        System.out.println(android_id);
//        Toast.makeText(activity, "android id : " + android_id, Toast.LENGTH_LONG).show();

        // Animations

        binding.forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ForgotPassword.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(binding.logoImage, "logo_image");
                pairs[1] = new Pair<View, String>(binding.logoName, "logo_text");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, pairs);
                    startActivity(intent, options.toBundle());

                }
            }
        });

        binding.signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, SignUp.class);
                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(binding.logoImage, "logo_image");
                pairs[1] = new Pair<View, String>(binding.logoName, "logo_text");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(activity, pairs);
                    startActivity(intent, options.toBundle());
                }

            }
        });

        // check if user check the remember me checkbox on last login
        if (session.getBoolean(Session.IS_REMEMBER_ME)) {
            setUpView();
        }
        onClick();
    }

    private void setUpView() {
        binding.usernameInput.setText(session.getString(Session.MOBILE));
        binding.pass.setText(session.getString(Session.PASSWORD));
        binding.rememberMe.setChecked(true);
    }

    private void onClick() {
        // signup Button click
//        binding.signup.setOnClickListener(v -> {
//            startActivity(new Intent(activity, SignUp.class));
//        });

        // click on login button
        binding.login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // getting edittext data into string variables
                String username = binding.usernameInput.getText().toString().trim();
                String password = binding.pass.getText().toString().trim();

                // match conditions on editetxt
                // check if username is empty or not
                if (username.equalsIgnoreCase("")) {
                    // if user name is empty
                    binding.usernameInput.setError("Please Enter UserName");
                    // request focus on user name edittext
                    binding.usernameInput.requestFocus();
                } else if (password.equalsIgnoreCase("")) {
                    // if password is empty
                    binding.pass.setError("Please Enter Password");
                    // request focus on user name edittext
                    binding.pass.requestFocus();
                } else {
                    // if both edittext have data then call your login api
                    loginApiCall();
                }
            }
        });
    }


    private void loginApiCall() {
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.LOGIN + "?mobile=" + binding.usernameInput.getText().toString().trim() + "&password=" + binding.pass.getText().toString().trim(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideLoadingDialog();
                        System.out.println(response);
                        Log.e("TAG", "onResponse: loginnnnnn"+response );
                        try {
                            // check if response is not null
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("ip")) {
                                    if (binding.rememberMe.isChecked()) {
                                        // make object of session class where we store our SharedPreferences values
                                        session.setBoolean(Session.IS_REMEMBER_ME, true);
                                    }
                                    //set the user login in SharedPreferences
                                    session.setBoolean(Session.IS_LOGIN, true);
                                    // set username and password into SharedPreferences
                                    session.setString(Session.MOBILE, binding.usernameInput.getText().toString().trim());
                                    session.setString(Session.PASSWORD, binding.pass.getText().toString().trim());
                                    session.setString(USER_ID, jsonObject.getString("identity"));
                                    session.setString(USER_TYPE, jsonObject.getString("user_type"));
//                                    if (jsonObject.has("tokEn")) {
//                                        session.setString(TOKEN, jsonObject.getString("tokEn"));
//                                    }
//                                    session.setString(TOKEN, jsonObject.getString("tokEn"));
                                    String stat = "";
                                    if (jsonObject.has("Stat")) {
                                        stat = jsonObject.getString("Stat");
                                    }

                                    String msg = "";
                                    if (jsonObject.has("Message")) {
                                        msg = jsonObject.getString("Message");
                                    }

                                    if (stat.equalsIgnoreCase("200")) {
                                        if (jsonObject.has("tokEn")) {
                                            session.setString(TOKEN, jsonObject.getString("tokEn"));
                                        }
                                        startActivity(new Intent(activity, MainActivity.class));
                                        finish();
                                    } else {
                                        loginOTPPage(msg, binding.usernameInput.getText().toString(), binding.pass.getText().toString());
                                    }

//                                    startActivity(new Intent(activity, MainActivity.class));
//                                    finish();
                                } else {
                                    if (jsonObject.has("Status")) {
                                        Toast.makeText(activity, jsonObject.getString("Status"), Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } else {
//                                Toast.makeText(activity, "Mobile No OR Password Incorrect !", Toast.LENGTH_SHORT).show();
                                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (
                                Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Log.e("tag", "onErrorResponse: " + error.getMessage());
//                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                Toast.makeText(activity, "Mobile Number or Password Incorrect !", Toast.LENGTH_SHORT).show();
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

    private void loginOTPPage(String msg, String mobileNo, String pass) {
        final Dialog dia = new Dialog(activity);
        //We have added a title in the custom layout. So let's disable the default title.
        dia.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dia.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dia.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dia.setCancelable(false);
        //Mention the name of the layout of your custom dialog.

        ActivityLoginOtpBinding binding1 = ActivityLoginOtpBinding.inflate(getLayoutInflater());
        dia.setContentView(binding1.getRoot());

        binding1.loginMessage.setText(msg);
        binding1.loginOtpBack.setOnClickListener(v -> {
            dia.dismiss();
        });

//        binding1.loginOtpSubmit.setOnClickListener(v -> {
//            if (binding1.loginOtp.getText().toString().equalsIgnoreCase("")) {
//                binding1.loginOtp.setError("Please Enter OTP");
//                binding1.loginOtp.requestFocus();
//            } else if (binding1.loginOtp.getText().length() != 6) {
//                binding1.loginOtp.setError("Please Enter 6 Digit OTP");
//                binding1.loginOtp.requestFocus();
//            } else {
//                loginOTPAPI(dia, mobileNo, pass, binding1.loginOtp, binding1.loginOtpMiss);
//            }
//        });

        binding1.loginOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 6) {
                    loginOTPAPI(dia, mobileNo, pass, binding1.loginOtp, binding1.loginOtpMiss);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        dia.show();
    }

    private void loginOTPAPI(Dialog dia, String mobileNo, String pass, PinView loginOtp, TextView error) {
        showLoadingDialog();
        String otp = String.valueOf(loginOtp.getText());
        String url = "https://mobile.swpay.in/api/AndroidAPI/AndroidLoginOTP";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        Log.e("TAG", "onResponse: loginnnnnn"+response );
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String userType = "";
                            if (object.has("user_type")){
                                userType = object.getString("user_type");
                            }

                            String ip = "";
                            if (object.has("ip")){
                                ip = object.getString("ip");
                            }

                            String stat = "";
                            if (object.has("Stat")){
                                stat = object.getString("Stat");
                            }

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

//                            String token = "";
//                            if (object.has("tokEn")){
//                                token = object.getString("tokEn");
//                            }

                            if (stat.equalsIgnoreCase("200")){
                                if (object.has("tokEn")){
                                    session.setString(TOKEN, object.getString("tokEn"));
                                }
                                startActivity(new Intent(activity, MainActivity.class));
                                dia.dismiss();
                            } else {
                                error.setVisibility(View.VISIBLE);
                                error.setText(status);
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
                params.put("MobileNo", mobileNo);
                params.put("Password", pass);
                params.put("EnterOTP", otp);
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