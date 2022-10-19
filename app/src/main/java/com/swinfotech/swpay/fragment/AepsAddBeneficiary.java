package com.swinfotech.swpay.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.AEPSViewBeneficiaryAdapter;
import com.swinfotech.swpay.databinding.ActivityAepsToWalletSuccessBinding;
import com.swinfotech.swpay.databinding.FragmentAepsAddBeneficiaryBinding;
import com.swinfotech.swpay.model.AEPSViewBeneficiaryGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.model.DMTBankListGetSet;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AepsAddBeneficiary extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentAepsAddBeneficiaryBinding binding;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    AEPSViewBeneficiaryAdapter aepsViewBeneficiaryAdapter;
    List<AEPSViewBeneficiaryGetSet> viewList = new ArrayList<>();

    Session session;
    Context context;

    // Bank List Array
    private ArrayList<DMTBankListGetSet> model;
    private final ArrayList<String> bankName = new ArrayList<String>();
    private final ArrayList<String> ifscCode = new ArrayList<String>();
    private String ifsc = "";


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public AepsAddBeneficiary() {
        // Required empty public constructor
    }


    public static AepsAddBeneficiary newInstance(String param1, String param2) {
        AepsAddBeneficiary fragment = new AepsAddBeneficiary();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentAepsAddBeneficiaryBinding.inflate(inflater, container, false);

        session = new Session(context);

        // set the layout manager on recyclerview
        binding.beneListRecView.setLayoutManager(new LinearLayoutManager(context));
        binding.beneListRecView.setHasFixedSize(true);
        aepsViewBeneficiaryAdapter = new AEPSViewBeneficiaryAdapter(context, viewList);
        binding.beneListRecView.setAdapter(aepsViewBeneficiaryAdapter);

        onSelect();


        getBankList(binding.bankListAddBeneAeps);

        binding.aepsAddBeneSubmit.setOnClickListener(v -> {
            String bankListIFSC = binding.bankListAddBeneAeps.getSelectedItem().toString();

            //  Toast.makeText(context, "Temporary out of service", Toast.LENGTH_LONG).show();

            if (getStringFromEditText(binding.aepsAddBeneName) == null) {
                binding.aepsAddBeneName.setError("Please Enter A/C Holder Name");
                binding.aepsAddBeneName.requestFocus();
            } else if (bankListIFSC.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(context, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (ifsc.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(context, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(binding.aepsAddBeneAccNo) == null) {
                binding.aepsAddBeneAccNo.setError("Please Enter Account No");
                binding.aepsAddBeneAccNo.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBeneAddress) == null) {
                binding.aepsAddBeneAddress.setError("Please Enter Address");
                binding.aepsAddBeneAddress.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBenePincode) == null) {
                binding.aepsAddBenePincode.setError("Please Enter Pincode");
                binding.aepsAddBenePincode.requestFocus();
            } else if (getStringFromEditText(binding.aepsAddBeneIfsc) == null) {
                binding.aepsAddBeneIfsc.setError("Please Enter IFSC Code");
                binding.aepsAddBeneIfsc.requestFocus();
            } else {
                if (!Constant.IS_AEPS_LIMIT_EXCEED) {
                    aepsAddBeneficiary(bankListIFSC);
                }else {
                    showLimitExceedDialog();
                }
                //   Toast.makeText(context, "Temporary out of service", Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }

    private void showLimitExceedDialog() {
        Toast.makeText(context, "Add bank limit Exceed!", Toast.LENGTH_SHORT).show();
    }

    private void getBankList(SearchableSpinner spinner) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DTM_BANK_LIST + url,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);

                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            model = new ArrayList<>();
//                                old
//                                JSONArray jsonArray = jsonObject.getJSONArray("apiBankList");
                            JSONArray jsonArray = jsonObject.getJSONArray("GetDBBanks");

                            DMTBankListGetSet model1 = new DMTBankListGetSet();
                            model1.setBankName("Select Bank");
                            model1.setIfscCode("0");

                            model.add(model1);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                DMTBankListGetSet data = new DMTBankListGetSet();
                                JSONObject object = jsonArray.getJSONObject(i);

//                                    old
//                                    data.setBankName(object.getString("BankName"));
                                data.setBankName(object.getString("bank_name"));
//                                    data.setIfscCode(object.getString("IfscCode"));
                                data.setIfscCode(object.getString("ifsc"));

                                model.add(data);

                            }

                            for (int i = 0; i < model.size(); i++) {
                                bankName.add(model.get(i).getBankName().toString());
                                ifscCode.add(model.get(i).getIfscCode().toString());
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, bankName);
                            spinner.setAdapter(spinnerArrayAdapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                    ifsc = ifscCode.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {

                                }
                            });

                        } else {
                            System.out.println("Response is Null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
            }
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void aepsAddBeneficiary(String bankListIFSC) {
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSAddBeneficiary";
        Log.e("TAG", "aepsAddBeneficiary: " + url + "---- " + bankListIFSC);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        Log.e("TAG", "onResponse: " + response);
                        try {
                            JSONObject object = new JSONObject(response);
                            String resp = "";
                            String status = "";
                            if (object.has("Resp")) {
                                resp = object.getString("Resp");
                                status = "Failed!";
                                //  Toast.makeText(context, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }


                            if (object.has("Desc")) {
                                status = "Success!";
                                resp = object.getString("Desc");
                            }

                            aepsAddBeneSuccess(resp, status);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("UserName", getStringFromEditText(binding.aepsAddBeneName));
                params.put("BankAC", getStringFromEditText(binding.aepsAddBeneAccNo));
                params.put("BankIFSC", getStringFromEditText(binding.aepsAddBeneIfsc));
                params.put("AreaPIN", getStringFromEditText(binding.aepsAddBenePincode));
                params.put("Address", getStringFromEditText(binding.aepsAddBeneAddress));
//                params.put("BankName", getStringFromEditText(binding.aepsAddBeneBankName));
                params.put("BankName", bankListIFSC);
                params.put("tok", session.getString(Session.TOKEN));
                //   Log.e("TAG", "getParams: "+params);
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void aepsAddBeneSuccess(String resp, String status) {
        Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);

        ActivityAepsToWalletSuccessBinding binding1 = ActivityAepsToWalletSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding1.getRoot());

        binding1.aepsToWalletDetails.setVisibility(View.GONE);
        binding1.aepsToBankDetails.setVisibility(View.GONE);

        binding1.aepsAddBeneResp.setText(resp);
        binding1.addStatus.setText(status);

        binding1.aepsAddBeneDone.setOnClickListener(v -> {
            dialog.dismiss();
            binding.aepsAddBeneName.setText("");
            binding.aepsAddBeneAccNo.setText("");
            binding.aepsAddBeneIfsc.setText("");
            binding.aepsAddBenePincode.setText("");
            binding.aepsAddBeneAddress.setText("");
            requireActivity().recreate();
        });

        dialog.show();
    }

    private void onSelect() {
        binding.viewBeneText.setOnClickListener(v -> {
            binding.viewBeneText.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.viewBeneText.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.addBene.setVisibility(View.GONE);
            binding.beneListRecView.setVisibility(View.VISIBLE);

            binding.addBeneText.setTextColor(ContextCompat.getColor(context, R.color.blue));
            binding.addBeneText.setBackground(getResources().getDrawable(R.drawable.white_button_background));

            viewBeneListAPI();

        });


        binding.addBeneText.setOnClickListener(v -> {
            binding.addBeneText.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.addBeneText.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.beneListRecView.setVisibility(View.GONE);
            binding.addBene.setVisibility(View.VISIBLE);

            binding.viewBeneText.setTextColor(ContextCompat.getColor(context, R.color.blue));
            binding.viewBeneText.setBackground(getResources().getDrawable(R.drawable.white_button_background));
        });

    }

    private void viewBeneListAPI() {
        showLoadingDialog();
        viewList.clear();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_AEPS_BENE_LIST + url,
                response -> {
                    hideLoadingDialog();
                    Log.e("TAG", "viewBeneListAPI: " + response);
                    try {
                        JSONObject object = new JSONObject(response);
                        if (object.has("Resp")) {
                            Toast.makeText(context, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                        }
                        if (object.has("ViewBenef")) {
                            String aeps_balance = "", AEPSLimit = "";
                            if (object.has("AEPSBalance")) {
                                JSONObject ob = object.getJSONObject("AEPSBalance");
                                if (ob.has("aeps_balance")) {
                                    aeps_balance = ob.getString("aeps_balance");
                                }
                            }
                            if (object.has("AEPSLimit")) {
                                AEPSLimit = object.getString("AEPSLimit");
                            }
                            JSONArray jsonArray = object.getJSONArray("ViewBenef");
                            if (jsonArray.length() > 0) {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    viewList.add(new AEPSViewBeneficiaryGetSet(
                                            obj.getString("id"),
                                            obj.getString("benef_name"),
                                            obj.getString("bank_name"),
                                            obj.getString("bank_ifsc"),
                                            obj.getString("account_no"),
                                            obj.getString("contact"), "",
                                            //obj.getString("created_on"),
                                            obj.getString("benef_id"),
                                            aeps_balance,
                                            AEPSLimit
                                    ));
                                }
                                if (jsonArray.length() >= 5) {
                                    Constant.IS_AEPS_LIMIT_EXCEED = true;
                                }
                            }
                            aepsViewBeneficiaryAdapter.notifyDataSetChanged();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Toast.makeText(context, error.toString(), Toast.LENGTH_LONG).show();
                System.out.println(error);
            }
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    private String getAmountFormat(String data) {
        BigDecimal bd = new BigDecimal(data).setScale(2, RoundingMode.HALF_UP);
//        double newInput = bd.doubleValue();
        String newInput = String.valueOf(bd.doubleValue());
        return newInput;
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
            mDialog = new SimpleArcDialog(context);
            configuration = new ArcConfiguration(context);
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