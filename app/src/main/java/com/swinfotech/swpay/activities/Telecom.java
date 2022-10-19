package com.swinfotech.swpay.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.fragment.DTHFragment;
import com.swinfotech.swpay.fragment.MobileFragment;
//import com.example.swpay.fragment.MyFragmentAdapter;
import com.swinfotech.swpay.fragment.PostpaidFragment;
import com.google.android.material.tabs.TabLayout;

public class Telecom extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
//    private MyFragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_telecom);

        if (getIntent() == null) {
            Toast.makeText(Telecom.this, "Intent Is Null Check in HomeAdapter", Toast.LENGTH_SHORT).show();
        }

        String fragmentName = getIntent().getStringExtra("fragment");
        Fragment fragment = null;
        if (fragmentName.equalsIgnoreCase("Mobile")) {
            fragment = MobileFragment.newInstance();
        } else if (fragmentName.equalsIgnoreCase("DTH")) {
            fragment = DTHFragment.newInstance();
        } else if (fragmentName.equalsIgnoreCase("Postpaid")) {
            fragment = PostpaidFragment.newInstance();
        }

        if (fragment == null) {
            Toast.makeText(Telecom.this, "No Fragment Attached", Toast.LENGTH_SHORT).show();
            return;
        }
        getSupportFragmentManager().beginTransaction().add(R.id.frameLayout, fragment).commit();

    }
}