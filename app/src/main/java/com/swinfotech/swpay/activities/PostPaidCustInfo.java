package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;

import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityPostPaidCustInfoBinding;

import java.util.Objects;

public class PostPaidCustInfo extends AppCompatActivity {

    ActivityPostPaidCustInfoBinding binding;
    PostPaidCustInfo activity;

    Session session;

    String customerName, billNo, billDate, billAmount, dueDate, operator, telNo, status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPostPaidCustInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.goToBack4.setOnClickListener(v -> {
            onBackPressed();
        });

        showSuccessPostPaidInfo();

    }

    private void showSuccessPostPaidInfo() {
        customerName = getIntent().getStringExtra("customerName");
        billNo = getIntent().getStringExtra("billNo");
        billDate = getIntent().getStringExtra("billDate");
        billAmount = getIntent().getStringExtra("billAmount");
        dueDate = getIntent().getStringExtra("dueDate");
        operator = getIntent().getStringExtra("operator");
        telNo = getIntent().getStringExtra("tellNo");
        status = getIntent().getStringExtra("status");


        if (status.equalsIgnoreCase("0")) {
            binding.noCustPostData.setVisibility(View.GONE);
            binding.bsNlPostPaidError.setVisibility(View.VISIBLE);
        } else {
            binding.landLineName.setText(customerName);
            binding.landLineBillNo.setText(billNo);
            binding.landLineBillDate.setText(billDate);
            binding.landLineBillAmount.setText(billAmount);
            binding.landLineDueDate.setText(dueDate);
            binding.landLineOp.setText(operator);
            binding.landLineNo.setText(telNo);
        }

    }

}