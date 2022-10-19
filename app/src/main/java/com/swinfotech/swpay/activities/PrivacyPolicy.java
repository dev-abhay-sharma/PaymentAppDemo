package com.swinfotech.swpay.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.databinding.ActivityPrivacyPolicyBinding;

public class PrivacyPolicy extends AppCompatActivity {

    ActivityPrivacyPolicyBinding binding;
    PrivacyPolicy activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPrivacyPolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;


    }
}