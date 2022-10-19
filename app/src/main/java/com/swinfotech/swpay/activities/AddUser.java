package com.swinfotech.swpay.activities;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.swinfotech.swpay.databinding.ActivityAddUserBinding;
import com.swinfotech.swpay.model.Scheme;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AddUser extends AppCompatActivity {

    ActivityAddUserBinding binding;
    Session session;
    AddUser activity;
    private ProgressDialog progressDialog;
    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    // these things change in array adapter when user select something
    private String scheme_id = "", state = "", district = "", scheme_1 = "", scheme_2 = "", scheme_3 = "";
    // for know in whole activity current user is md or dt
    private boolean isMasterDistributor = false;
    private ArrayList<Scheme> schemeArrayList = new ArrayList<>();
    private ArrayList<Scheme> rtschemeArrayList = new ArrayList<>();
    private ArrayAdapter<Scheme> schemeArrayAdapter;
    private ArrayAdapter<Scheme> rt_scheme1ArrayAdapter;
    private ArrayAdapter<Scheme> rt_scheme2ArrayAdapter;
    private ArrayAdapter<Scheme> rt_scheme3ArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        session = new Session(activity);
        onClick();
        initView();
        getScheme();
    }

    private void initView() {
        schemeArrayAdapter = new ArrayAdapter<Scheme>(activity, android.R.layout.simple_spinner_dropdown_item, schemeArrayList);// here pass your list name
        // here set the adapter on spinner
        binding.selectScheme.setAdapter(schemeArrayAdapter);
        binding.selectScheme.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // here get your selected scheme id or name
                scheme_id = schemeArrayList.get(position).getScheme_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isMasterDistributor) {
            rt_scheme1ArrayAdapter = new ArrayAdapter<Scheme>(activity, android.R.layout.simple_spinner_dropdown_item, rtschemeArrayList);// here pass your list name
            // here set the adapter on spinner
            binding.scheme1Spinner.setAdapter(rt_scheme1ArrayAdapter);
            binding.scheme1Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // here get your selected scheme id or name
                    scheme_1 = rtschemeArrayList.get(position).getScheme_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            rt_scheme2ArrayAdapter = new ArrayAdapter<Scheme>(activity, android.R.layout.simple_spinner_dropdown_item, rtschemeArrayList);// here pass your list name
            // here set the adapter on spinner
            binding.scheme2Spinner.setAdapter(rt_scheme2ArrayAdapter);
            binding.scheme2Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // here get your selected scheme id or name
                    scheme_2 = rtschemeArrayList.get(position).getScheme_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            rt_scheme3ArrayAdapter = new ArrayAdapter<Scheme>(activity, android.R.layout.simple_spinner_dropdown_item, rtschemeArrayList);// here pass your list name
            // here set the adapter on spinner
            binding.scheme3Spinner.setAdapter(rt_scheme3ArrayAdapter);
            binding.scheme3Spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    // here get your selected scheme id or name
                    scheme_3 = rtschemeArrayList.get(position).getScheme_id();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }
    }


    // copy paste this method for get your other spinner data
    private void getScheme() {
        // here put the url of get scheme

        // initialize with dt
        String for_user_type = "3";
        // check if the user is MDT
        if (isMasterDistributor) {
            for_user_type = "2";
        }
        String url = Constant.GET_SCHEME + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD) + "&for_user_type=" + for_user_type + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);

                        try {
                            JSONObject obj = new JSONObject(response);
                            if (obj.has("Resp")) {
                                Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }
                            if (obj.has("custom")) {
                                // here we get scheme object
                                JSONArray scheme_object = obj.getJSONArray("custom");
                                if (scheme_object.length() == 0) {
                                    // array is empty
                                    Toast.makeText(activity, "No Scheme Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < scheme_object.length(); i++) {
                                        JSONObject scheme = scheme_object.getJSONObject(i);

                                        // check if scheme_id is present or not
                                        if (scheme.has("scheme_id")) {
                                            // check if scheme_names is present or not
                                            if (scheme.has("scheme_names")) {
                                                // check both objects if they are not contains null values
                                                if (!scheme.getString("scheme_id").equalsIgnoreCase("null") && !scheme.getString("scheme_names").equalsIgnoreCase("null")) {
                                                    // here load data into list
                                                    schemeArrayList.add(new Scheme(scheme.getString("scheme_names"), scheme.getString("scheme_id")));
                                                }
                                            }
                                        }
                                    }
                                    // notify the adapter data is changed in list
                                    schemeArrayAdapter.notifyDataSetChanged();
                                }
                            }

                            if (obj.has("GetRtScheme")) {
                                JSONArray rt_scheme_object = obj.getJSONArray("GetRtScheme");
                                if (rt_scheme_object.length() == 0) {
                                    // array is empty
                                    Toast.makeText(activity, "No Rt Scheme Found", Toast.LENGTH_SHORT).show();
                                } else {
                                    for (int i = 0; i < rt_scheme_object.length(); i++) {
                                        JSONObject scheme = rt_scheme_object.getJSONObject(i);

                                        // check if scheme_id is present or not
                                        if (scheme.has("scheme_id")) {
                                            // check if scheme_names is present or not
                                            if (scheme.has("scheme_names")) {
                                                // check both objects if they are not contains null values
                                                if (!scheme.getString("scheme_id").equalsIgnoreCase("null") && !scheme.getString("scheme_names").equalsIgnoreCase("null")) {
                                                    rtschemeArrayList.add(new Scheme(scheme.getString("scheme_names"), scheme.getString("scheme_id")));
                                                }
                                            }
                                        }
                                    }
                                    // notify all the adapter
                                    rt_scheme1ArrayAdapter.notifyDataSetChanged();
                                    rt_scheme2ArrayAdapter.notifyDataSetChanged();
                                    rt_scheme3ArrayAdapter.notifyDataSetChanged();
                                }
                            }
                            // when data is come then load into your list


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

    private void onClick() {
        if (session.getString(Session.USER_TYPE).equalsIgnoreCase("1")) {
            isMasterDistributor = true;
        }
        if (isMasterDistributor) {
            binding.mdLayout.setVisibility(View.VISIBLE);
        } else {
            binding.mdLayout.setVisibility(View.GONE);
        }
        binding.pinCodeInput.addTextChangedListener(new TextWatcher() {
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

        binding.addUser.setOnClickListener(v -> {
            if (getStringFromEditText(binding.userInput) == null) {
                binding.userInput.setError("Please Enter First Name");
                binding.userInput.requestFocus();
            } else if (getStringFromEditText(binding.lastInput) == null) {
                binding.lastInput.setError("Please Enter last Name");
                binding.lastInput.requestFocus();
            } else if (getStringFromEditText(binding.firmInput) == null) {
                binding.firmInput.setError("Please Enter Firm Name");
                binding.firmInput.requestFocus();
            } else if (getStringFromEditText(binding.mobileNumInput) == null) {
                binding.mobileNumInput.setError("Please Enter Mobile Number");
                binding.mobileNumInput.requestFocus();
            } else if (getStringFromEditText(binding.mobileNumInput).length() != 10) {
                binding.mobileNumInput.setError("Please Enter 10 Digit Mobile Number");
                binding.mobileNumInput.requestFocus();
            } else if (getStringFromEditText(binding.panNumInput) == null) {
                binding.panNumInput.setError("Please Enter Pan Number");
                binding.panNumInput.requestFocus();
            } // check if pan card is not valid
            else if (!isPanCardValid(getStringFromEditText(binding.panNumInput))) {
                binding.panNumInput.setError("Please Enter Correct Pan Number");
                binding.panNumInput.requestFocus();
            } else if (getStringFromEditText(binding.emailNumInput) == null) {
                binding.emailNumInput.setError("Please Enter Email Address");
                binding.emailNumInput.requestFocus();
            } else if (!getStringFromEditText(binding.emailNumInput).contains("@gmail.com")) {
                binding.emailNumInput.setError("Please Enter Correct Email Address");
                binding.emailNumInput.requestFocus();
            } else if (getStringFromEditText(binding.aadharNumInput) == null) {
                binding.aadharNumInput.setError("Please Enter Aadhaar Number");
                binding.aadharNumInput.requestFocus();
            } else if (getStringFromEditText(binding.aadharNumInput).length() != 12) {
                binding.aadharNumInput.setError("Please Enter 12 Digit Aadhaar Number");
                binding.aadharNumInput.requestFocus();
            } else if (getStringFromEditText(binding.perAddressInput) == null) {
                binding.perAddressInput.setError("Please Enter Permanent Address");
                binding.perAddressInput.requestFocus();
            } else if (getStringFromEditText(binding.pinCodeInput) == null) {
                binding.pinCodeInput.setError("Please Enter Pin Code");
                binding.pinCodeInput.requestFocus();
            } else if (getStringFromEditText(binding.stateInput) == null) {
                binding.stateInput.setError("Please Enter State");
                binding.stateInput.requestFocus();
            } else if (getStringFromEditText(binding.cityInput) == null) {
                binding.cityInput.setError("Please Enter City");
                binding.cityInput.requestFocus();
            } else {
                // check if user select or not any scheme
                if (scheme_id.equalsIgnoreCase("")) {
                    Toast.makeText(activity, "Select Scheme First", Toast.LENGTH_SHORT).show();
                    return;
                }

                // if is MDT then check if he select or not all the schemes
                if (isMasterDistributor) {
                    if (scheme_1.equalsIgnoreCase("")) {
                        Toast.makeText(activity, "Select Scheme 1 First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (scheme_2.equalsIgnoreCase("")) {
                        Toast.makeText(activity, "Select Scheme 2 First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (scheme_3.equalsIgnoreCase("")) {
                        Toast.makeText(activity, "Select Scheme 3 First", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                // finally call the add user method
                addUser();
            }
        });

    }


    private void getPinCodeList() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_PIN_CODE_OUR + "pincode=" + getStringFromEditText(binding.pinCodeInput) + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        Log.d("Response", response);

                        try {
                            // get the array
//                            JSONArray obj = new JSONArray(response); // Old response

                            JSONObject object1 = new JSONObject(response);

                            if (object1.length() == 0) {

                            } else {
                                // get array first element
//                                JSONArray obj = object1.getJSONArray("PinAPI");
//                                JSONObject obj1 = obj.getJSONObject(0);
                                // get PostOffice Array from object
                                JSONArray array = object1.getJSONArray("PinAPI");
                                // get the first element from PostOffice array
                                JSONObject object = array.getJSONObject(0);
                                binding.stateInput.setText(object.getString("Circle"));
                                binding.cityInput.setText(object.getString("District"));
                                state = binding.stateInput.getText().toString().trim();
                                district = binding.cityInput.getText().toString().trim();
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
//                        Toast.makeText(activity, error.getMessage(), Toast.LENGTH_SHORT).show();
                        System.out.println(error.getMessage());
                    }
                });

        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void addUser() {
//        showProgressDialog();
        showLoadingDialog();
        String user_type = "3";
        if (isMasterDistributor) {
            user_type = "2";
        }
        String url = "?username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&user_type=" + user_type
                + "&name=" + getStringFromEditText(binding.userInput) + getStringFromEditText(binding.lastInput)
                + "&mobile=" + getStringFromEditText(binding.mobileNumInput)
                + "&email=" + getStringFromEditText(binding.emailNumInput)
                + "&scheme_id=" + scheme_id
                + "&aadhar=" + getStringFromEditText(binding.aadharNumInput)
                + "&pan=" + getStringFromEditText(binding.panNumInput)
                + "&address=" + getStringFromEditText(binding.perAddressInput)
                + "&pin=" + getStringFromEditText(binding.pinCodeInput)
                + "&state=" + state
                + "&firm_name=" + getStringFromEditText(binding.firmInput)
                + "&district=" + district
                + "&user_id=" + ""
                + "&status=1"
                + "&tok=" + session.getString(Session.TOKEN);

        System.out.println(Constant.ADD_USER + url);

        if (isMasterDistributor) {
            url = url + "&scheme0=" + scheme_1 + "&scheme1=" + scheme_2 + "&scheme2=" + scheme_3;
        }

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.ADD_USER + url,
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
                                    Toast.makeText(activity, "Successfully Create User", Toast.LENGTH_SHORT).show();
                                    finish();
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

    private boolean isPanCardValid(String panCard) {
        Pattern pattern = Pattern.compile("[A-Z]{5}[0-9]{4}[A-Z]{1}");

        Matcher matcher = pattern.matcher(panCard);
        // Check if pattern matches
        // it return true if pan card valid and return false if pan card is not valid
        return matcher.matches();
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