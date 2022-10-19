package com.swinfotech.swpay;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.example.swpay.activities.AllReports;
//import com.example.swpay.activities.Help;
//import com.example.swpay.activities.KYC;
//import com.example.swpay.activities.MyProfile;
//import com.example.swpay.activities.Notification;
import com.swinfotech.swpay.activities.AllReports;
import com.swinfotech.swpay.activities.Help;
import com.swinfotech.swpay.activities.KYC;
import com.swinfotech.swpay.activities.MyProfile;
import com.swinfotech.swpay.activities.Notification;
import com.swinfotech.swpay.databinding.ActivityChangePasswordBinding;
import com.swinfotech.swpay.databinding.ActivityChangeTpinBinding;
import com.swinfotech.swpay.databinding.ActivityCreateAppLockBinding;
//import com.example.swpay.databinding.ActivityKycpopUpBinding;
import com.swinfotech.swpay.databinding.ActivityKycpopUpBinding;
import com.swinfotech.swpay.databinding.ActivityMainBinding;
import com.swinfotech.swpay.fragment.HomeFragment;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener, NavigationView.OnNavigationItemSelectedListener {

    Session session;
    MainActivity activity;
    ActivityMainBinding binding;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    String imei;

//    private static boolean isFirstLaunch = true;

    private final NavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener = item -> {
        switch (item.getItemId()) {
            case R.id.logout:
                Session session = new Session(MainActivity.this);
                session.logout(MainActivity.this);
                openCloseDrawer();
                return true;

            case R.id.about_us:
                openAboutUsDialog();
                openCloseDrawer();
                return true;

            case R.id.privacy_policy:
                openPrivacyPolicyDialog();
                openCloseDrawer();
                return true;

            case R.id.changePass1:
                openChangePasswordDialog();
                openCloseDrawer();
                return true;

            case R.id.changeTPin1:
                openTPinDialog();
                openCloseDrawer();
                return true;

            case R.id.appLock:
                openCreatePin();
                openCloseDrawer();
                return true;

            default:
                openCloseDrawer();
                return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


//        Log.e("TAG", "onCreate: "+session.getString(Session.TOKEN));
        activity = this;
        session = new Session(MainActivity.this);

        getSupportFragmentManager().beginTransaction().add(R.id.home_view, HomeFragment.newInstance()).commit();

        binding.navigation.setOnItemSelectedListener(this);
        binding.navigationView.setNavigationItemSelectedListener(navigationItemSelectedListener);
        binding.navigationView.setItemIconTintList(null);

        binding.toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.logout:
                        Session session = new Session(MainActivity.this);
                        session.logout(MainActivity.this);
                        return true;
                    case R.id.notification:
                        startActivity(new Intent(MainActivity.this, Notification.class));
//                        Toast.makeText(activity, "Available Soon", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }

            }
        });


        binding.drawerLayout.setOnClickListener(v -> {
            openCloseDrawer();
        });

        setUpNavigationHeaderLayout();

//        showKYCDialog();

//        String kyc_status = session.getString(Session.KYC_STATUS);
//        System.out.println(kyc_status);
//        if (kyc_status.equalsIgnoreCase("1")){
//            System.out.println("KYC Already Complete");
//        } else if (kyc_status.equalsIgnoreCase("0")) {
//            System.out.println("KYC Pending");
//        }
//        else {
//            if (isFirstLaunch){
//                showKYCDialog();
//                isFirstLaunch = false;
//            }
//        }

//        if (kyc_status.equalsIgnoreCase("1")) {
//            System.out.println("KYC Already Complete");
//        } else {
//            showKYCDialog();
//        }

        // KYC PopUp
//        if (isFirstLaunch){
//            showKYCDialog();
//            isFirstLaunch = false;
//        }


//        getIemiNo();

    }

//    private void getIemiNo() {
//        TelephonyManager telephonyManager;
//        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
//
//        if (permission == PackageManager.PERMISSION_GRANTED) {
//            telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//            imei = telephonyManager.getDeviceId().toString();
//        } else {
//            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 123);
//        }
//
//        System.out.println(imei);
//    }


    private void showKYCDialog() {
        Dialog dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(false);

        ActivityKycpopUpBinding binding1 = ActivityKycpopUpBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding1.getRoot());


        binding1.kycCancel.setOnClickListener(v -> {
            dialog.dismiss();
        });

        binding1.kycUpdate.setOnClickListener(v -> {
            startActivity(new Intent(activity, KYC.class));
        });


        dialog.show();


    }

    public void setUpNavigationHeaderLayout() {
        Session session = new Session(MainActivity.this);
        View view = binding.navigationView.getHeaderView(0);
        TextView name, mobile, userType, userEmail;
        name = view.findViewById(R.id.userName);
        mobile = view.findViewById(R.id.userMobile);
        userType = view.findViewById(R.id.userType);
        userEmail = view.findViewById(R.id.userEmail);

        String userTypeName = "";

        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("3")) {
            userTypeName = "Retailer";
        }
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("2")) {
            userTypeName = "Distributor";
        }
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("1")) {
            userTypeName = "Master Distributor";
        }

        name.setText("Name: " + session.getString(Session.USER_NAME));
        mobile.setText("Mobile: " + session.getString(Session.MOBILE));
        userEmail.setText("Email: " + session.getString(Session.USER_EMAIL));
        userType.setText(userTypeName);

        Log.e("TAG", "onCreate: " + session.getString(Session.PASSWORD));
    }

    private void openCloseDrawer() {
        if (binding.drawer.isDrawerOpen(GravityCompat.START)) {
            binding.drawer.closeDrawer(GravityCompat.START, true);
        } else {
            binding.drawer.openDrawer(GravityCompat.START, true);
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:

                return true;

            case R.id.navigation_help:
                startActivity(new Intent(MainActivity.this, Help.class));
                return true;
            case R.id.navigation_reports:
                startActivity(new Intent(MainActivity.this, AllReports.class));
                return true;
            case R.id.navigation_profile:
                startActivity(new Intent(MainActivity.this, MyProfile.class));
                return true;
        }
        return false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.navigation.setSelectedItemId(R.id.navigation_home);
    }


    // new things

    private void openAboutUsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this);
        bottomSheetDialog.setContentView(R.layout.activity_about_us);

        bottomSheetDialog.show();
    }


    private void openCreatePin() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);

        ActivityCreateAppLockBinding binding = ActivityCreateAppLockBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.submitButton.setOnClickListener(v -> {
            if (binding.pin.getText().toString().trim().equalsIgnoreCase("")) {
                binding.pin.setError("Enter Pin");
                binding.pin.requestFocus();
            } else if (binding.confirmPin.getText().toString().trim().equalsIgnoreCase("")) {
                binding.confirmPin.setError("Enter Pin");
                binding.confirmPin.requestFocus();
            } else if (binding.pin.getText().toString().trim().length() != 4) {
                binding.pin.setError("Enter 4 digit Pin");
                binding.pin.requestFocus();
            } else if (binding.confirmPin.getText().toString().trim().length() != 4) {
                binding.confirmPin.setError("Enter 4 digit Pin");
                binding.confirmPin.requestFocus();
            } else if (!binding.confirmPin.getText().toString().trim().equalsIgnoreCase(binding.pin.getText().toString().trim())) {
                binding.confirmPin.setError("Pin Not Mated");
                binding.confirmPin.requestFocus();
            } else {
                session.setBoolean(Session.IS_LOCK_SCREEN_ACTIVE, true);
                session.setString(Session.APP_LOCK_PIN, binding.pin.getText().toString().trim());
                Toast.makeText(activity, "Successfully Created Pin", Toast.LENGTH_SHORT).show();
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.show();
    }

    private void openPrivacyPolicyDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.activity_privacy_policy);

        bottomSheetDialog.show();
    }

    private void openTPinDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        ActivityChangeTpinBinding binding = ActivityChangeTpinBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.tpinChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hello");
                if (binding.oldTpin.getText().toString().trim().equalsIgnoreCase("")) {
                    binding.oldTpin.setError("Enter Old TPin");
                    binding.oldTpin.requestFocus();
                } else if (binding.newTpin.getText().toString().trim().equalsIgnoreCase("")) {
                    binding.newTpin.setError("Enter New TPin");
                    binding.newTpin.requestFocus();
                } else {
                    changeTpinAPI(bottomSheetDialog, getDataFromEditText(binding.oldTpin),getDataFromEditText(binding.newTpin));

                }
            }
        });

        System.out.println("Here open change tpin");

        bottomSheetDialog.show();
    }

    private void changeTpinAPI(BottomSheetDialog bottomSheetDialog, String oldTpin, String newTpin) {
        showLoadingDialog();
        String url = Constant.CHANGE_TPIN
                + "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&&oldtpin=" + oldTpin
                + "&&newtpin=" + newTpin
                + "&tok=" + session.getString(Session.TOKEN);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);
                        hideLoadingDialog();
                        try {
                            // get the array
                            JSONObject obj = new JSONObject(response);

                            if (obj.length() == 0) {

                            } else {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                if (obj.has("Status")){
                                    if (obj.getInt("Status")==200){
                                        // set this when api hit successfully and response if TPin is changed
                                        session.setString(Session.TPIN, newTpin);
                                    }
                                }
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        try {
                            bottomSheetDialog.dismiss();
                            hideLoadingDialog();
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void openChangePasswordDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        ActivityChangePasswordBinding binding=ActivityChangePasswordBinding.inflate(getLayoutInflater());
        bottomSheetDialog.setContentView(binding.getRoot());

        binding.changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("hello");
                if (binding.oldPass.getText().toString().trim().equalsIgnoreCase("")) {
                    binding.oldPass.setError("Enter Old Password");
                    binding.oldPass.requestFocus();
                } else if (binding.newPass.getText().toString().trim().equalsIgnoreCase("")) {
                    binding.newPass.setError("Enter New Password");
                    binding.newPass.requestFocus();
                } else {
                    changePasswordAPI(bottomSheetDialog, getDataFromEditText(binding.oldPass),getDataFromEditText(binding.newPass));

                }
            }
        });


        System.out.println("Here open change password");

        bottomSheetDialog.show();
    }


    private void changePasswordAPI(BottomSheetDialog bottomSheetDialog, String oldPassword, String newPassword) {
        showLoadingDialog();
        String url = Constant.CHANGE_PASSWORD
                + "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&oldpassword=" + oldPassword
                + "&newpassword=" + newPassword
                + "&tok=" + session.getString(Session.TOKEN);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);
                        hideLoadingDialog();
                        try {
                            // get the array
                            JSONObject obj = new JSONObject(response);

                            if (obj.length() == 0) {

                            } else {
                                bottomSheetDialog.dismiss();
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                if (obj.has("Status")){
                                    if (obj.getInt("Status")==200){
                                        // set this when api hit sucessfully and response if password is changed
                                        session.setString(Session.PASSWORD, newPassword);
                                    }
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        bottomSheetDialog.dismiss();
                        try {
                            bottomSheetDialog.dismiss();
                            hideLoadingDialog();
                            Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private String getDataFromEditText(EditText editText) {
        if (editText == null) {
            return "";
        }
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            return "";
        }
        return editText.getText().toString().trim();
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