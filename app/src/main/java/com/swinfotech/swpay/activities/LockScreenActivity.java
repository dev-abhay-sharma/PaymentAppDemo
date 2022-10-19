package com.swinfotech.swpay.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityLockScreenBinding;
import com.swinfotech.swpay.signin.Login;

import in.aabhasjindal.otptextview.OTPListener;

public class LockScreenActivity extends AppCompatActivity {

    private ActivityLockScreenBinding binding;
    private LockScreenActivity activity;
    private Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLockScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);
        Intent intent = null;
        if (getIntent() != null) {
            // check which activity comes in intent login or main activity
            if (getIntent().getStringExtra("activity_name").equalsIgnoreCase("Login")) {
                intent = new Intent(activity, Login.class);
            } else {
                intent = new Intent(activity, MainActivity.class);
            }
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showKeyboard();
            }
        }, 100);

        // convert to a temp varible so we can access into inner method
        Intent finalIntent = intent;

        binding.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
                // fired when user types something in the Otpbox
                binding.wrongOtp.setVisibility(View.GONE);
            }

            @Override
            public void onOTPComplete(String otp) {
                // fired when user has entered the OTP fully.

                if (otp.equalsIgnoreCase(session.getString(Session.APP_LOCK_PIN))) {
                    hideKeyboard();
//                    binding.wrongOtp.setText("Right Pin");
//                    binding.wrongOtp.setVisibility(View.VISIBLE);
//                    binding.wrongOtp.setTextColor(ContextCompat.getColor(activity, R.color.green));
                    // pin is correct and sen duser to next activity
                    startActivity(finalIntent);
                    finish();
                } else {
                    binding.wrongOtp.setText("Wrong Pin Try Again...");
                    binding.wrongOtp.setVisibility(View.VISIBLE);
                    binding.wrongOtp.setTextColor(ContextCompat.getColor(activity, R.color.red));
                }
            }
        });
    }


    // for hiding keyboard
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    // for showing Keyboard
    public void showKeyboard() {
        binding.otpView.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(binding.otpView, InputMethodManager.SHOW_IMPLICIT);
        inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.SHOW_IMPLICIT);
    }
}