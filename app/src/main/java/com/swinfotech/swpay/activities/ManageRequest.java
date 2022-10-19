package com.swinfotech.swpay.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;
import com.swinfotech.swpay.Constant;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.databinding.ActivityManageRequestBinding;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ManageRequest extends AppCompatActivity {

    ActivityManageRequestBinding binding;
    private ManageRequest activity;
    private ProgressDialog progressDialog;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    private String allowStatus = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // inflating our xml layout in our binding
        binding = ActivityManageRequestBinding.inflate(getLayoutInflater());

        // getting our root layout in our view.
        View view = binding.getRoot();

        // below line is to set
        // Content view for our layout.
        setContentView(view);

        // here pass the current activity context to your activity object
        activity = this;


        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(ManageRequest.this, R.array.manageRequest, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.allowStatus.setAdapter(adapter);
        binding.allowStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                allowStatus = ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void ManageRequestAPI() {
//        showProgressDialog();
        showLoadingDialog();
//        String user_type = "3";
//        if (isMasterDistributor) {
//            user_type = "2";
//        }
//        String url = "?username=" + session.getString(Session.MOBILE)
//                + "&password=" + session.getString(Session.PASSWORD)
//                + "&user_type=" + user_type
//                + "&name=" + getStringFromEditText(binding.userInput) + getStringFromEditText(binding.lastInput)
//                + "&mobile=" + getStringFromEditText(binding.mobileNumInput)
//                + "&email=" + getStringFromEditText(binding.emailNumInput)
//                + "&scheme_id=" + scheme_id
//                + "&aadhar=" + getStringFromEditText(binding.aadharNumInput)
//                + "&pan=" + getStringFromEditText(binding.panNumInput)
//                + "&address=" + getStringFromEditText(binding.perAddressInput)
//                + "&pin=" + getStringFromEditText(binding.pinCodeInput)
//                + "&state=" + state
//                + "&firm_name=" + getStringFromEditText(binding.firmInput)
//                + "&district=" + district
//                + "&user_id=" + ""
//                + "&status=1";
//
//        if (isMasterDistributor) {
//            url = url + "&scheme0=" + scheme_1 + "&scheme1=" + scheme_2 + "&scheme2=" + scheme_3;
//        }

//        String url = "https://mobile.swpay.in/api/AndroidAPI/ProcessPayReq?username=9876543210&password=983fc53b5&pay_req_id=14&tpin=1234&allow_status=4&remarks=test";
        String url = "https://mobile.swpay.in/api/AndroidAPI/ProcessPayReq?username=9876543210&password=983fc53b5&pay_req_id=14&tpin=" + getStringFromEditText(binding.mrTpin) + "&allow_status=4&remarks=" + getStringFromEditText(binding.mrRemarks);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);
//                        hideProgressDialog();
                        hideLoadingDialog();
                        try {
                            JSONObject obj = new JSONObject(response);

                            // here we get the response when all api is ready then we get response here
                            if (obj.has("Status")) {
                                String status = obj.getString("Status");
                                if (status.equalsIgnoreCase("Success")) {
                                    Toast.makeText(activity, "Successfully Manage Request", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(activity, status, Toast.LENGTH_SHORT).show();
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
//                        hideProgressDialog();
                        hideLoadingDialog();
                        try {
                            Toast.makeText(activity, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(activity);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Adding User");
        }
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
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