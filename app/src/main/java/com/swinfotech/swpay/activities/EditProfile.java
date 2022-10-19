package com.swinfotech.swpay.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.databinding.ActivityEditProfileBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class EditProfile extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    EditProfile activity;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    Session session;

    // this for automatic district and state
    String state, district;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);

        binding.editBack.setOnClickListener(v -> {
            onBackPressed();
        });


        initViews();

        onClick();

        System.out.println(state);
        System.out.println(district);




        binding.editProfileUpdate.setOnClickListener(v -> {

//            if (getStringFromEditText(binding.editName) == null) {
//                binding.editName.setError("Please Enter Name");
//                binding.editName.requestFocus();
//            }
            if (getStringFromEditText(binding.editEmail) == null) {
                binding.editEmail.setError("Please Enter Email");
                binding.editEmail.requestFocus();
            } else if (getStringFromEditText(binding.editAadhaarNo) == null) {
                binding.editAadhaarNo.setError("Please Enter Aadhaar No");
                binding.editAadhaarNo.requestFocus();
            } else if (getStringFromEditText(binding.editPanNo) == null) {
                binding.editPanNo.setError("Please Enter Pan No");
                binding.editPanNo.requestFocus();
            } else if (getStringFromEditText(binding.editPinCode) == null) {
                binding.editPinCode.setError("Please Enter Pin Code");
                binding.editPinCode.requestFocus();
            } else if (getStringFromEditText(binding.editPinCode).length() != 6) {
                binding.editPinCode.setError("Please Enter 6 Digit Pin Code");
                binding.editPinCode.requestFocus();
            } else if (getStringFromEditText(binding.editState) == null) {
                binding.editState.setError("Please Enter Your State");
                binding.editState.requestFocus();
            } else if (getStringFromEditText(binding.editDistrict) == null) {
                binding.editDistrict.setError("Please Enter Your District");
                binding.editDistrict.requestFocus();
            } else if (getStringFromEditText(binding.editAddress) == null) {
                binding.editAddress.setError("Please Enter Your Address");
                binding.editAddress.requestFocus();
            } else if (getStringFromEditText(binding.editFirmName) == null) {
                binding.editFirmName.setError("Please Enter Your Firm Name");
                binding.editFirmName.requestFocus();
            } else {
                editProfileAPI();
            }

        });

    }

    private void onClick() {
        binding.editPinCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.e("TAG", "onTextChanged: " + charSequence);
                if (charSequence.length() == 6) {
                    getPinCodeList();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void getPinCodeList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_PIN_CODE + getStringFromEditText(binding.editPinCode) + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);

                        try {
                            // get the array
                            JSONArray obj = new JSONArray(response);

                            if (obj.length() == 0) {

                            } else {
                                // get array first element
                                JSONObject obj1 = obj.getJSONObject(0);
                                // get PostOffice Array from object
                                JSONArray array = obj1.getJSONArray("PostOffice");
                                // get the first element from PostOffice array
                                JSONObject object = array.getJSONObject(0);
                                binding.editState.setText(object.getString("Circle"));
                                binding.editDistrict.setText(object.getString("District"));
                                state = binding.editState.getText().toString().trim();
                                district = binding.editDistrict.getText().toString().trim();
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
                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void editProfileAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/EditProfile";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("Resp")){
                                Toast.makeText(activity, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                            String status = "";
                            if (object.has("Status")){
                                status = object.getString("Status");
                            }

                            String msg = "";
                            if (object.has("Resp")){
                                msg = object.getString("Resp");
                            }

                            if (status.equalsIgnoreCase("200")) {
                                System.out.println("Done");
                                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(activity, "Sorry Wrong Information", Toast.LENGTH_SHORT).show();
                                System.out.println("Sorry Wrong Information");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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
//                params.put("user_fname", getStringFromEditText(binding.editName));
                params.put("user_email", getStringFromEditText(binding.editEmail));
                params.put("aadhar_no", getStringFromEditText(binding.editAadhaarNo));
                params.put("pan_no", getStringFromEditText(binding.editPanNo));
                params.put("pin", getStringFromEditText(binding.editPinCode));
                params.put("state", getStringFromEditText(binding.editState));
                params.put("district", getStringFromEditText(binding.editDistrict));
//                params.put("state", state);
//                params.put("district", district);
                params.put("address", getStringFromEditText(binding.editAddress));
                params.put("firmname", getStringFromEditText(binding.editFirmName));
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

    private void initViews() {
        // set the user details from session
        binding.editName.setText(session.getString(Session.USER_NAME));
        binding.editName.setEnabled(false);
        binding.editMobNo.setText(session.getString(Session.MOBILE));
        binding.editMobNo.setEnabled(false);
        binding.editEmail.setText(session.getString(Session.USER_EMAIL));
        binding.editAadhaarNo.setText(session.getString(Session.AADHAAR_NO));
        binding.editPanNo.setText(session.getString(Session.PAN_NO));
        binding.editPinCode.setText(session.getString(Session.PIN_CODE));
        binding.editState.setText(session.getString(Session.STATES));
        binding.editDistrict.setText(session.getString(Session.DISTRICT));
        binding.editAddress.setText(session.getString(Session.ADDRESS));
//        binding.editFirmName.setText(session.getString(Session.FIRM_NAME));
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