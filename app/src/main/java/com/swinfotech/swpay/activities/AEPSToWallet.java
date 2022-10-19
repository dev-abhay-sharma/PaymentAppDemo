package com.swinfotech.swpay.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityAepstoWalletBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;

public class AEPSToWallet extends AppCompatActivity {

    ActivityAepstoWalletBinding binding;
    AEPSToWallet activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAepstoWalletBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

    }

}