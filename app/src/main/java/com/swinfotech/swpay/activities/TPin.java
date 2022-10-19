package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.swinfotech.swpay.databinding.ActivityTpinBinding;

public class TPin extends AppCompatActivity {

    ActivityTpinBinding binding;
    TPin activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTpinBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

    }


}