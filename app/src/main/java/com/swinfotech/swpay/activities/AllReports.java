package com.swinfotech.swpay.activities;

import static com.swinfotech.swpay.Session.USER_TYPE;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.AllReportsAdapter;
import com.swinfotech.swpay.databinding.ActivityAllReportsBinding;
import com.swinfotech.swpay.model.HomeModel;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;

import java.util.ArrayList;

public class AllReports extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, AllReportsAdapter.OnClickListenerAeps {

    Session session;
    private ActivityAllReportsBinding binding;
    private AllReports activity;
    private AllReportsAdapter homeAdapter;
    private ArrayList<HomeModel> homeModelArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAllReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar1);
        try {
            getSupportActionBar().setTitle("All Reports");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.toolbar1.setNavigationOnClickListener(v -> {
            onBackPressed();
        });
        activity = this;
        session = new Session(activity);
        initView();

        binding.navigation.setOnItemSelectedListener(this);

    }

    private void initView() {
        homeAdapter = new AllReportsAdapter(homeModelArrayList, activity, null, this);
        binding.recylerViewReports.setLayoutManager(new GridLayoutManager(activity, 3));
        binding.recylerViewReports.setHasFixedSize(true);
        binding.recylerViewReports.setAdapter(homeAdapter);
        homeModelArrayList.add(new HomeModel("AEPS Report", R.drawable.aeps_report_new, AEPSReports.class, true));
        if (session.getString(USER_TYPE).equalsIgnoreCase("3")) {
            homeModelArrayList.add(new HomeModel("Search Transaction", R.drawable.general_search_icon, null, true));
//            homeModelArrayList.add(new HomeModel("Complaints Status", R.drawable.complain_history, ComplaintReport.class));

            homeModelArrayList.add(new HomeModel("Commission Report", R.drawable.commission_new, null, false));
//            homeModelArrayList.add(new HomeModel("Commission Report", R.drawable.commission_new, null));

//            homeModelArrayList.add(new HomeModel("Payment Request", R.drawable.payment_request, PaymentRequest.class));
        } else {
//            homeModelArrayList.add(new HomeModel("Commission Report", R.drawable.commission_new, null));
            homeModelArrayList.add(new HomeModel("Debit Report", R.drawable.debit_report, DebitReport.class, true));
            homeModelArrayList.add(new HomeModel("Manage Payment Request", R.drawable.manage_payment_request, ManagePaymentRequest.class, true));
            homeModelArrayList.add(new HomeModel("Downline Sales Report", R.drawable.sales_report, DownlineSalesReport.class, true));
        }
        // here start
        homeModelArrayList.add(new HomeModel("Complaints Status", R.drawable.complain_history, ComplaintReport.class, true));
        homeModelArrayList.add(new HomeModel("Other Complaints", R.drawable.other_complain, OtherComplaintReport.class, true));
        // here end

//        homeModelArrayList.add(new HomeModel("Payment Request", R.drawable.payment_request, PaymentRequest.class));

        //here start

        homeModelArrayList.add(new HomeModel("Payment Request History", R.drawable.payment_request_history, PayRequestHistory.class, true));
        homeModelArrayList.add(new HomeModel("Statement Report", R.drawable.transaction_history, StatementsReport.class, true));
        homeModelArrayList.add(new HomeModel("Transaction Report", R.drawable.tr_report_image, TransactionReport.class, true));
        homeModelArrayList.add(new HomeModel("Credit Report", R.drawable.credit_report_icon, PaymentLoadReport.class, true));
       // homeModelArrayList.add(new HomeModel("Commission Report", R.drawable.commission_new, null, false));
        // here end
//        homeModelArrayList.add(new HomeModel("Credit Report", R.drawable.credit_report_icon, AEPSAddBeneficiary.class));

        homeAdapter.notifyDataSetChanged();

    }


    public void openTranscationReport() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.fragment_fab__add__bottom__sheet);

        LinearLayout searchByNumber = bottomSheetDialog.findViewById(R.id.search_by_number);
        LinearLayout searchByOrderId = bottomSheetDialog.findViewById(R.id.search_by_orderId);

        LinearLayout debitReport = bottomSheetDialog.findViewById(R.id.search_by_debitReport);

        if (searchByNumber != null) {
            searchByNumber.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                startActivity(new Intent(activity, SearchByNumber.class));
            });
        }
        if (searchByOrderId != null) {
            searchByOrderId.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                startActivity(new Intent(activity, SearchByOrderId.class));
            });
        }
        if (debitReport != null) {
            debitReport.setOnClickListener(v -> {
                bottomSheetDialog.dismiss();
                startActivity(new Intent(activity, DebitReport.class));
            });
        }

        bottomSheetDialog.show();
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                startActivity(new Intent(activity, MainActivity.class));
                return true;

            case R.id.navigation_help:
                startActivity(new Intent(activity, Help.class));
                return true;
            case R.id.navigation_reports:
                // leave blank if user in all report activity then no need to call any activity
                return true;
            case R.id.navigation_profile:
                startActivity(new Intent(activity, MyProfile.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.navigation.setSelectedItemId(R.id.navigation_reports);
    }

    @Override
    public void onContainerClick(int position) {

    }
}