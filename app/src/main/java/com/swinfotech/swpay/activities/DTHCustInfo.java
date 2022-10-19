package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityDthcustInfoBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

public class DTHCustInfo extends AppCompatActivity {

    ActivityDthcustInfoBinding binding;
    DTHCustInfo activity;
    Session session;

    String id, op, status, bal, name, date, details, monthlyRe;

    String status2, desc;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDthcustInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.goToBack3.setOnClickListener(v -> {
            onBackPressed();
        });

//        mobile = getIntent().getStringExtra("mobileNo");
//        operator = getIntent().getStringExtra("operator");
//        opId = getIntent().getStringExtra("opId");



//        fullTalkTimeAPI(mobile, operator);

        showSuccessCustInfo();

    }

    private void showSuccessCustInfo() {
        id = getIntent().getStringExtra("id");
        op = getIntent().getStringExtra("opDth");
        status = getIntent().getStringExtra("status");
        bal = getIntent().getStringExtra("bal");
        name = getIntent().getStringExtra("name");
        date = getIntent().getStringExtra("date");
        details = getIntent().getStringExtra("details");
        monthlyRe = getIntent().getStringExtra("monthlyRe");




        if (status.equalsIgnoreCase("null")) {
            binding.noCustData.setVisibility(View.GONE);
            binding.rOffersDTHError.setVisibility(View.VISIBLE);
        } else if(status.equalsIgnoreCase("")){
            binding.noCustData.setVisibility(View.GONE);
            binding.rOffersDTHError.setVisibility(View.VISIBLE);
        } else if(status.equalsIgnoreCase("0")){
            binding.noCustData.setVisibility(View.GONE);
            binding.rOffersDTHError.setVisibility(View.VISIBLE);
        } else {
            binding.subscribeID.setText(id);
            binding.subscribeOp.setText(op);
            binding.subscribeName.setText(name);
            binding.subscribeBal.setText(bal);
            binding.subscribeRecharge.setText(monthlyRe);
            binding.subscribeDate.setText(date);
            binding.subscribeDetails.setText(details);
        }

    }


    private void showLoadingDialog() {
        if (mDialog == null) {
            mDialog = new SimpleArcDialog(activity);
            configuration = new ArcConfiguration(activity);
            mDialog.setConfiguration(configuration);
            configuration.setText("Please Wait.....");
            configuration.setLoaderStyle(SimpleArcLoader.STYLE.COMPLETE_ARC);
            configuration.setColors(new int[]{Color.BLUE, Color.RED, Color.WHITE, Color.MAGENTA});
            configuration.setTextColor(Color.BLUE);
            mDialog.setCanceledOnTouchOutside(false);
        }
        if (!mDialog.isShowing()) {
            mDialog.show();
        }
    }

    private void hideLoadingDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

}