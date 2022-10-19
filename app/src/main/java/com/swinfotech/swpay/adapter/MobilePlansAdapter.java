package com.swinfotech.swpay.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.swinfotech.swpay.fragment.DataFragment;
import com.swinfotech.swpay.fragment.FULLTTFragment;
import com.swinfotech.swpay.fragment.ISDFragment;
import com.swinfotech.swpay.fragment.JioPhoneFragment;
import com.swinfotech.swpay.fragment.PopularFragment;
import com.swinfotech.swpay.fragment.SMSFragment;
import com.swinfotech.swpay.fragment.TOPUPFragment;

public class MobilePlansAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public MobilePlansAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                FULLTTFragment fullttFragment = new FULLTTFragment();
                return fullttFragment;

            case 1:
                TOPUPFragment topupFragment = new TOPUPFragment();
                return topupFragment;

            case 2:
                PopularFragment popularFragment = new PopularFragment();
                return popularFragment;

            case 3:
                DataFragment dataFragment = new DataFragment();
                return dataFragment;

            case 4:
                JioPhoneFragment jioPhoneFragment = new JioPhoneFragment();
                return jioPhoneFragment;

            case 5:
                ISDFragment isdFragment = new ISDFragment();
                return isdFragment;

            case 6:
                SMSFragment smsFragment = new SMSFragment();
                return smsFragment;

            default:
                return null;
        }

    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
