package com.swinfotech.swpay.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityChangePasswordBinding;
import com.swinfotech.swpay.databinding.ActivityChangeTpinBinding;
import com.swinfotech.swpay.databinding.ActivityCreateAppLockBinding;
import com.swinfotech.swpay.databinding.ActivityMyProfileBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationBarView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class MyProfile extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {


    private ActivityMyProfileBinding binding;
    private MyProfile activity;
    private Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);
        binding.navigation.setOnItemSelectedListener(this);
        initViews();

        setPinLock();

        onClick();
    }

    private void setPinLock() {
        if (!session.getString(Session.APP_LOCK_PIN).equalsIgnoreCase("")) {
            binding.openClosePin.setVisibility(View.VISIBLE);
            if (session.getBoolean(Session.IS_LOCK_SCREEN_ACTIVE)) {
                binding.enablePin.setChecked(true);
            } else {
                binding.enablePin.setChecked(false);
            }
        }

        binding.enablePin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                session.setBoolean(Session.IS_LOCK_SCREEN_ACTIVE, b);
            }
        });
    }

    private void onClick() {
        // Profile image
        String img = session.getString(Session.PROFILE_IMG);
        String profileImage = "https://swpay.in/" + img;
//        Glide.with(activity).load(profileImage).into(binding.profileImg);

        if (img.equalsIgnoreCase("")){
            binding.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.profile1));
        } else if (img.equalsIgnoreCase("null")) {
            binding.profileImg.setImageDrawable(getResources().getDrawable(R.drawable.profile1));
        } else {
            Glide.with(activity).load(profileImage).into(binding.profileImg);
        }

        binding.merchantFormStart.setOnClickListener(v -> {
            startActivity(new Intent(activity, AEPSMerchantFormStart.class));
        });

        binding.changePassword.setOnClickListener(v -> {
            openChangePasswordDialog();
        });

        binding.changeTPin.setOnClickListener(v -> {
            // call change tPin dialog
            openTPinDialog();
        });
        binding.logout.setOnClickListener(v -> {
            // logout the user
            session.logout(activity);
        });
        binding.aboutUs.setOnClickListener(v -> {
            // about us dialog
            openAboutUsDialog();
        });

        binding.viewProfile.setOnClickListener(v -> {
            // goto edit profile activity
            startActivity(new Intent(activity, EditProfile.class));
        });

        binding.privacyPolicy.setOnClickListener(v -> {
            // open privacy policy dialog
            openPrivacyPolicyDialog();
        });


        binding.createPin.setOnClickListener(v -> {
            openCreatePin();
        });

        binding.faq.setOnClickListener(v -> {
            openTermsAndConditions();
        });

    }


    private void openTermsAndConditions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.activity_terms_and_conditions);

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

    private void openAboutUsDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(activity);
        bottomSheetDialog.setContentView(R.layout.activity_about_us);

        bottomSheetDialog.show();
    }

    // Change TPin Start Here

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
                    changeTpinAPI(bottomSheetDialog,getDataFromEditText(binding.oldTpin),getDataFromEditText(binding.newTpin));

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
                                System.out.println(obj.getString("Resp"));
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

    // Change Password Start Here

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
//                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                if (obj.has("Status")){
                                    if (obj.getInt("Status")==200){
                                        Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                        // set this when api hit sucessfully and response if password is changed
                                        session.setString(Session.PASSWORD, newPassword);
                                    } else {
                                        Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
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
//                            hideProgressDialog();
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

    private void initViews() {
        // set the user details from session
        binding.userName.setText(session.getString(Session.USER_NAME));
        binding.userEmail.setText(session.getString(Session.USER_EMAIL));
        binding.userNumber.setText(session.getString(Session.MOBILE));
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
                startActivity(new Intent(activity, AllReports.class));
                return true;
            case R.id.navigation_profile:

                return true;
        }
        return false;
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


    @Override
    protected void onResume() {
        super.onResume();
        binding.navigation.setSelectedItemId(R.id.navigation_profile);
    }

}