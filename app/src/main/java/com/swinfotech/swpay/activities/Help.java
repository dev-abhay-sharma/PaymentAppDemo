package com.swinfotech.swpay.activities;

import static com.swinfotech.swpay.Constant.VOLLEY_TIMEOUT;
import static com.swinfotech.swpay.Session.PASSWORD;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityHelpBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class Help extends AppCompatActivity {

    private Help activity;
    private ActivityHelpBinding binding;
    private ProgressDialog progressDialog;
    Session session;
    private String salesEmail, salesNumber, technicalEmail, technicalNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHelpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarHelp);
        try {
            getSupportActionBar().setTitle("Help Desk");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.toolbarHelp.setNavigationOnClickListener(v -> {
            onBackPressed();
        });


        activity = this;
        session = new Session(activity);
        //binding.navigation.setOnItemSelectedListener(this);
        binding.navigation.setOnNavigationItemSelectedListener(listener);
        onClick();
    }

    private void onClick() {
        binding.helptechnical.setOnClickListener(v -> {
            openEmail(technicalEmail);
        });
        binding.emailOtherSupport.setOnClickListener(v -> {
            openEmail(salesEmail);
        });

        binding.raiseComplaint.setOnClickListener(v -> {
            startActivity(new Intent(activity, RaiseComplaintOutSide.class));
        });
        binding.supportNumberTechnical.setOnClickListener(v -> {
            makeCallToSupportNumber(technicalNumber);
        });
        binding.supportNumberOther.setOnClickListener(v -> {
            makeCallToSupportNumber(salesNumber);
        });

        getHelpDesk();
    }

    private void setData() {
//        binding.numberTechnical.setText(technicalNumber + " 24*7");
        binding.numberTechnical.setText(technicalNumber);
        binding.salesEmail.setText(salesEmail);
        binding.salesNumber.setText(salesNumber);
        binding.technicalEmail.setText(technicalEmail);
    }

    private void getHelpDesk() {
        showProgressDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.HELP_DESK_API + "username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(PASSWORD) + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideProgressDialog();
                        System.out.println(response);
                        try {
                            // check if response is not null
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("GetHelp")) {
                                    JSONObject jb1 = jsonObject.getJSONObject("GetHelp");
                                    if (jb1.has("data")) {
                                        JSONArray jsonArray = jb1.getJSONArray("data");
                                        if (jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jb = jsonArray.getJSONObject(i);
                                                if (jb.has("support_email1")) {
                                                    technicalEmail = jb.getString("support_email1");
                                                }
                                                if (jb.has("support_mobile")) {
                                                    technicalNumber = jb.getString("support_mobile");
                                                }
                                                if (jb.has("sales_email2")) {
                                                    salesEmail = jb.getString("sales_email2");
                                                }
                                                if (jb.has("sales_mobile1")) {
                                                    salesNumber = jb.getString("sales_mobile1");
                                                }
                                            }

                                            setData();
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            hideProgressDialog();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                Log.e("tag", "onErrorResponse: " + error.getMessage());
                Toast.makeText(activity, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }


    private void openEmail(String email) {
        try {
            Session session = new Session(activity);
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{email});
            intent.putExtra(Intent.EXTRA_SUBJECT, session.getString(Session.USER_NAME));
            intent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(intent, "Open Gmail"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Email Not Found Please check your internet connection if already has then ask to app provider", Toast.LENGTH_SHORT).show();
        }
    }

    private void makeCallToSupportNumber(String number) {
        try {
            if (isCallPermission()) {
                Intent intent = new Intent(Intent.ACTION_CALL);
                intent.setData(Uri.parse("tel:" + number));
                startActivity(intent);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Phone Number Not Found Please check your internet connection if already has then ask to app provider", Toast.LENGTH_SHORT).show();
        }
    }


    private boolean isCallPermission() {
        // check if permission is not granted
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // take permission from user
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 101);
            return false;
        } else {
            // permission is granted
            return true;
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // here we get the permission result

        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // here user permission give successfully
            // finally we call our calling function
        }
    }

    private void openAboutUsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.activity_about_us);

        bottomSheetDialog.show();
    }


//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.navigation_home:
//                startActivity(new Intent(activity, MainActivity.class));
//                return true;
//
//            case R.id.navigation_help:
//
//                return true;
//            case R.id.navigation_reports:
//                startActivity(new Intent(activity, AllReports.class));
//                return true;
//            case R.id.navigation_profile:
//                startActivity(new Intent(activity, MyProfile.class));
//                return true;
//        }
//        return false;
//    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    startActivity(new Intent(activity, MainActivity.class));
                    return true;

                case R.id.navigation_help:

                    return true;
                case R.id.navigation_reports:
                    startActivity(new Intent(activity, AllReports.class));
                    return true;
                case R.id.navigation_profile:
                    startActivity(new Intent(activity, MyProfile.class));
                    return true;
            }
            return false;
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        binding.navigation.setSelectedItemId(R.id.navigation_help);
    }


    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Getting Notifications");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }
}