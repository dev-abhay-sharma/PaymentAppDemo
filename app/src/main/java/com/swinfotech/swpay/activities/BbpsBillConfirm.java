package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.swinfotech.swpay.databinding.ActivityBbpsBillConfirmBinding;

public class BbpsBillConfirm extends AppCompatActivity {

    ActivityBbpsBillConfirmBinding binding;
    BbpsBillConfirm activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBbpsBillConfirmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

    }

}