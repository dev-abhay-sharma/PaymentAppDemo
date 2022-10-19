package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.databinding.ActivityEntryBinding;
import com.swinfotech.swpay.signin.Login;
import com.swinfotech.swpay.signin.SignUp;

public class Entry extends AppCompatActivity {

    ActivityEntryBinding binding;
    Entry activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEntryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        binding.goToLogin.setOnClickListener(v -> {
            startActivity(new Intent(activity, Login.class));
        });

        binding.goToSignup.setOnClickListener(v -> {
            startActivity(new Intent(activity, SignUp.class));
        });

    }
}