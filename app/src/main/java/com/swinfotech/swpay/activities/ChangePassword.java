package com.swinfotech.swpay.activities;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.databinding.ActivityChangePasswordBinding;

public class ChangePassword extends AppCompatActivity {

    ActivityChangePasswordBinding binding;
    ChangePassword activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChangePasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;


    }

}