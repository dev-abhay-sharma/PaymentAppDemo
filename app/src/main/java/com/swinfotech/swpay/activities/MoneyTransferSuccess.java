package com.swinfotech.swpay.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.swinfotech.swpay.R;
import com.swinfotech.swpay.databinding.ActivityMoneyTransferSuccessBinding;

public class MoneyTransferSuccess extends AppCompatActivity {

    ActivityMoneyTransferSuccessBinding binding;
    MoneyTransferSuccess activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMoneyTransferSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//            SaveImage();
            Button share = (Button) findViewById(R.id.shareMoneyTr);
//            binding.shareMobile.performClick();
            share.performClick();
        } else {
            Toast.makeText(activity, "Permission Denied", Toast.LENGTH_SHORT).show();
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}