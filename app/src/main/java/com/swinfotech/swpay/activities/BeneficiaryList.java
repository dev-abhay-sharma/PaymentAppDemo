package com.swinfotech.swpay.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.databinding.ActivityBeneficiaryListBinding;

public class BeneficiaryList extends AppCompatActivity {


    ActivityBeneficiaryListBinding binding;
    BeneficiaryList activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBeneficiaryListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
    }


}