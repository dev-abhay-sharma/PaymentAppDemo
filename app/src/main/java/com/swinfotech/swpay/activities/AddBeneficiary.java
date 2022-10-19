package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.swinfotech.swpay.databinding.ActivityAddBeneficiaryBinding;

public class AddBeneficiary extends AppCompatActivity {

    ActivityAddBeneficiaryBinding binding;
    AddBeneficiary activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBeneficiaryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;

    }
}