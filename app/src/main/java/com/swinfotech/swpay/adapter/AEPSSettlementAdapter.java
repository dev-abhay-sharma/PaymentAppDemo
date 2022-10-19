package com.swinfotech.swpay.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.swinfotech.swpay.fragment.AepsAddBeneficiary;
import com.swinfotech.swpay.fragment.AepsFundTransfer;

public class AEPSSettlementAdapter extends FragmentPagerAdapter {

    private Context myContext;
    int totalTabs;

    public AEPSSettlementAdapter(Context context, FragmentManager fm, int totalTabs) {
        super(fm);
        myContext = context;
        this.totalTabs = totalTabs;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                AepsAddBeneficiary aepsAddBeneficiary = new AepsAddBeneficiary();
                return aepsAddBeneficiary;

            case 1:
                AepsFundTransfer aepsFundTransfer = new AepsFundTransfer();
                return aepsFundTransfer;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }

}
