package com.swinfotech.swpay.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.swinfotech.swpay.adapter.AEPSSettlementAdapter;
import com.swinfotech.swpay.databinding.ActivityAepssettlementBinding;
import com.google.android.material.tabs.TabLayout;

public class AEPSSettlement extends AppCompatActivity {

    ActivityAepssettlementBinding binding;
    AEPSSettlement activity;

    AEPSSettlementAdapter aepsSettlementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAepssettlementBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

//        onClick();

        adapterGetTabs();

    }

    private void adapterGetTabs() {
        aepsSettlementAdapter = new AEPSSettlementAdapter(activity, getSupportFragmentManager(), binding.tabLayoutAEPS.getTabCount());
        binding.viewPager1.setAdapter(aepsSettlementAdapter);

        binding.viewPager1.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(binding.tabLayoutAEPS));

        binding.tabLayoutAEPS.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager1.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

}