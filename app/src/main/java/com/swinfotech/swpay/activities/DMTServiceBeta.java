package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import com.swinfotech.swpay.databinding.ActivityDmtserviceBetaBinding;


public class DMTServiceBeta extends AppCompatActivity {

    ActivityDmtserviceBetaBinding binding;
    DMTServiceBeta activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDmtserviceBetaBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

    }

}