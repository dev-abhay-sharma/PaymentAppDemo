package com.swinfotech.swpay.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.material.textfield.TextInputEditText;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityAepsmerchantFormBinding;
import com.swinfotech.swpay.databinding.ActivityAepsmerchantFormStartBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AEPSMerchantFormStart extends AppCompatActivity {

    ActivityAepsmerchantFormStartBinding binding;
    AEPSMerchantFormStart activity;

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAepsmerchantFormStartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session =  new Session(activity);

        binding.merchantFind.setOnClickListener(v -> {
            if (getStringFromEditText(binding.merchantMobileNo) == null){
                binding.merchantMobileNo.setError("Please Enter Mobile Number");
                binding.merchantMobileNo.requestFocus();
            } else if (getStringFromEditText(binding.merchantMobileNo).length() != 10) {
                binding.merchantMobileNo.setError("Please Enter 10 Digit Mobile Number");
                binding.merchantMobileNo.requestFocus();
            }
            else{
                getMerchantForm();
            }
        });
    }

    private void getMerchantForm() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSMerchantEnquiry";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("Resp")) {
                                Toast.makeText(activity, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }

                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String description = "";
                            if (object.has("Desc")){
                                description = object.getString("Desc");
                            }


                            String merchantId = session.getString(Session.AEPS_MERCHANT_ID);

                            if (merchantId.equalsIgnoreCase("")) {
                                showMerchantForm();
                            } else if (merchantId.equalsIgnoreCase("null")) {
                                showMerchantForm();
                            }
                            else {
                                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
                            }

//                            if (status.equalsIgnoreCase("0")){
//                                showMerchantForm();
//                            } else {
//                                Toast.makeText(activity, description, Toast.LENGTH_SHORT).show();
//                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("MerchantMobile", getStringFromEditText(binding.merchantMobileNo));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showMerchantForm() {
        ActivityAepsmerchantFormBinding binding1 = ActivityAepsmerchantFormBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());

        binding1.merchantBack.setOnClickListener(v -> {
            onBackPressed();
        });

        getDocFromKYC(binding1.merchantPanCard, binding1.merchantAadhaarCard, binding1.merchantAadhaarCardBack);

        String kycStatus = session.getString(Session.KYC_STATUS);

        if (kycStatus.equalsIgnoreCase("1")) {
            binding1.merchantFormUpdate.setOnClickListener(v -> {
                if (getStringFromEditText(binding1.merchantName) == null){
                    binding1.merchantName.setError("Please Enter Name");
                    binding1.merchantName.requestFocus();
                } else if (getStringFromEditText(binding1.merchantFirmName) == null) {
                    binding1.merchantFirmName.setError("Please Enter Firm Name");
                    binding1.merchantFirmName.requestFocus();
                } else if (getStringFromEditText(binding1.merchantID) == null) {
                    binding1.merchantID.setError("Please Enter Merchant ID");
                    binding1.merchantID.requestFocus();
                } else if (getStringFromEditText(binding1.merchantPanNo) == null) {
                    binding1.merchantPanNo.setError("Please Enter Pan No");
                    binding1.merchantPanNo.requestFocus();
                } else if (getStringFromEditText(binding1.merchantAadhaarNo) == null) {
                    binding1.merchantAadhaarNo.setError("Please Enter Aadhar No");
                    binding1.merchantAadhaarNo.requestFocus();
                } else if (getStringFromEditText(binding1.merchantAadhaarNo).length() != 12) {
                    binding1.merchantAadhaarNo.setError("Please Enter 12 Digit Aadhar No");
                    binding1.merchantAadhaarNo.requestFocus();
                } else if (getStringFromEditText(binding1.merchantPinCode) == null) {
                    binding1.merchantPinCode.setError("Please Enter Pin Code");
                    binding1.merchantPinCode.requestFocus();
                } else if (getStringFromEditText(binding1.merchantPinCode).length() != 6) {
                    binding1.merchantPinCode.setError("Please Enter 6 Digit Pin Code");
                    binding1.merchantPinCode.requestFocus();
                } else if (getStringFromEditText(binding1.merchantDistrict) == null) {
                    binding1.merchantDistrict.setError("Please Enter District");
                    binding1.merchantDistrict.requestFocus();
                } else if (getStringFromEditText(binding1.merchantState) == null) {
                    binding1.merchantState.setError("Please Enter State");
                    binding1.merchantState.requestFocus();
                } else if (getStringFromEditText(binding1.merchantAddress) == null) {
                    binding1.merchantAddress.setError("Please Enter Address");
                    binding1.merchantAddress.requestFocus();
                }
                else{
                    merchantFormAPI(binding1.merchantName, binding1.merchantFirmName,
                            binding1.merchantID, binding1.merchantPanNo, binding1.merchantAadhaarNo,
                            binding1.merchantPinCode, binding1.merchantDistrict, binding1.merchantState,
                            binding1.merchantAddress);
                }
            });
        } else {
            binding1.merchantFormUpdate.setVisibility(View.GONE);
            binding1.merchantFormButton.setVisibility(View.VISIBLE);
        }


    }

    private void merchantFormAPI(TextInputEditText merchantName, TextInputEditText merchantFirmName, TextInputEditText merchantID, TextInputEditText merchantPanNo, TextInputEditText merchantAadhaarNo, TextInputEditText merchantPinCode, TextInputEditText merchantDistrict, TextInputEditText merchantState, TextInputEditText merchantAddress) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSAddMerchant";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject obj = new JSONObject(response);

                            JSONObject object = obj.getJSONObject("apiMerchantAddEpay");

                            String status = "";
                            if (object.has("status")){
                                status = object.getString("status");
                            }

                            if (status.equalsIgnoreCase("200")){
                                Toast.makeText(activity, "AEPS Merchant Registration Applied Successfully, Please wait for the Merchant approval", Toast.LENGTH_SHORT).show();
                                System.out.println("AEPS Merchant Registration Applied Successfully, Please wait for the Merchant approval");
                                finish();
                            } else {
                                Toast.makeText(activity, "Contact technical support team.", Toast.LENGTH_SHORT).show();
                                System.out.println("Contact technical support team.");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Successfully");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        Toast.makeText(activity,error.toString(),Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("Name", getStringFromEditText(merchantName));
                params.put("ShopName", getStringFromEditText(merchantFirmName));
                params.put("Pan", getStringFromEditText(merchantPanNo));
                params.put("AdharNo", getStringFromEditText(merchantAadhaarNo));
                params.put("MerchantID", getStringFromEditText(merchantID));
                params.put("pincode", getStringFromEditText(merchantPinCode));
                params.put("City", getStringFromEditText(merchantDistrict));
                params.put("State", getStringFromEditText(merchantState));
                params.put("Address", getStringFromEditText(merchantAddress));
                params.put("tok", session.getString(Session.TOKEN));
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void getDocFromKYC(ImageView merchantPanCard, ImageView merchantAadhaarCard, ImageView merchantAadhaarCardBack) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_KYC_DOC + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            JSONObject object = obj.getJSONObject("GetKycDoc");

                            String profileImg = "";
                            if (object.has("profile_pic")) {
                                profileImg = object.getString("profile_pic");
                            }

                            String panCard = "";
                            if (object.has("pan_card")) {
                                panCard = object.getString("pan_card");
                            }

                            String aadharFront = "";
                            if (object.has("aadhar_front")) {
                                aadharFront = object.getString("aadhar_front");
                            }

                            String aadharBack = "";
                            if (object.has("aadhar_back")) {
                                aadharBack = object.getString("aadhar_back");
                            }

                            String profileImage = "https://swpay.in/" + profileImg;
                            String panCardImg = "https://swpay.in/" + panCard;
                            String aadhaarImg = "https://swpay.in/" + aadharFront;
                            String aadhaarBackImg = "https://swpay.in/" + aadharBack;

//                            Glide.with(activity).load(profileImage).into(binding.kycUserImage);
                            Glide.with(activity).load(panCardImg).into(merchantPanCard);
                            Glide.with(activity).load(aadhaarImg).into(merchantAadhaarCard);
                            Glide.with(activity).load(aadhaarBackImg).into(merchantAadhaarCardBack);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Toast.makeText(activity, "Something went wrong !", Toast.LENGTH_SHORT).show();
                System.out.println("Something went wrong !");
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(Objects.requireNonNull(activity));
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private String getStringFromEditText(EditText editText) {
        // check if edittext is null or not
        if (editText == null) {
            // then return null
            return null;
        }
        // check if edittext is empty
        if (editText.getText().toString().trim().equalsIgnoreCase("")) {
            // then also return null for better optimization
            return null;
        }
        // else return the value
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