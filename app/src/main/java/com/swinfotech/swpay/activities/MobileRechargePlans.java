package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.swinfotech.swpay.adapter.FULLTTFragmentAdapter;
import com.swinfotech.swpay.adapter.MobilePlansAdapter;
import com.swinfotech.swpay.databinding.ActivityMobileRechargePlansBinding;
//import com.example.swpay.fragment.FULLTTFragment;
import com.swinfotech.swpay.model.FULLTTFragmentGetSet;
import com.google.android.material.tabs.TabLayout;

public class MobileRechargePlans extends AppCompatActivity implements FULLTTFragmentAdapter.OnPlanClickListener{

    ActivityMobileRechargePlansBinding binding;
    MobileRechargePlans activity;

    MobilePlansAdapter mobilePlansAdapter;

    String mobile, operator, opId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMobileRechargePlansBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        mobile = getIntent().getStringExtra("mobileNo");
        operator = getIntent().getStringExtra("operator");
        opId = getIntent().getStringExtra("opId");

        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("FULLTT"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("TOPUP"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Popular"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Data"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("Jio Phone"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("ISD"));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setText("SMS"));

        adapterGetTabs();

    }

    private void adapterGetTabs() {
        mobilePlansAdapter = new MobilePlansAdapter(activity, getSupportFragmentManager(), binding.tabLayout.getTabCount());
        binding.viewPager.setAdapter(mobilePlansAdapter);

        binding.viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayout));

        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onPlanClick(FULLTTFragmentGetSet planList) {
        Log.e("OPPLAN", "onPlanClick: " );
    }


}