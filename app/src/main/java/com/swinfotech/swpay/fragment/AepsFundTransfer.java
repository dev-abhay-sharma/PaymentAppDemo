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
import com.swinfotech.swpay.databinding.ActivityAepsToWalletSuccessBinding;
import com.swinfotech.swpay.databinding.FragmentAepsFundTransferBinding;
import com.swinfotech.swpay.model.AEPSViewBeneficiaryGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AepsFundTransfer extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FragmentAepsFundTransferBinding binding;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;
    String selectBene = "Select";
    List<AEPSViewBeneficiaryGetSet> viewList = new ArrayList<>();
    Session session;
    ArrayAdapter<AEPSViewBeneficiaryGetSet> arrayAdapter;

    Context context;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    public AepsFundTransfer() {
        // Required empty public constructor
    }

    public static AepsFundTransfer newInstance(String param1, String param2) {
        AepsFundTransfer fragment = new AepsFundTransfer();
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
        // Inflate the layout for this fragment

//        View v = inflater.inflate(R.layout.fragment_aeps_fund_transfer, container, false);

        binding = FragmentAepsFundTransferBinding.inflate(inflater, container, false);

        session = new Session(context);
        arrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, viewList);
        binding.selectBeneAeps.setAdapter(arrayAdapter);
        binding.selectBeneAeps.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectBene = viewList.get(i).getBenef_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        onSelect();

        aepsGetBeneBalAPI();
        viewBeneListAPI();


        binding.aepsToWalletSubmit.setOnClickListener(v -> {
//            int aepsLimit = Integer.parseInt(session.getString(Session.AEPS_LIMIT));
//            float aepsLimit = Float.parseFloat(session.getString(Session.AEPS_LIMIT));
            float aepsBal = Float.parseFloat(session.getString(Session.AEPS_BALANCE));

            if (getStringFromEditText(binding.aepsWithdrawalAmt) == null) {
                binding.aepsWithdrawalAmt.setError("Please Enter Amount");
                binding.aepsWithdrawalAmt.requestFocus();
            } else if (Integer.parseInt(getStringFromEditText(binding.aepsWithdrawalAmt)) >= aepsBal) {
                binding.aepsWithdrawalAmt.setError("Please Check Your AEPS Balance");
                binding.aepsWithdrawalAmt.requestFocus();
            } else {
                aepsToWalletAPI();
            }
        });

        binding.aepsToBankSubmit.setOnClickListener(v -> {
            String selectPayMode = binding.selectPayMode.getSelectedItem().toString();
           // int aepsLimit = Integer.parseInt(session.getString(Session.AEPS_LIMIT));
            float aepsLimit = Float.parseFloat(session.getString(Session.AEPS_LIMIT));
            float aepsBal = Float.parseFloat(session.getString(Session.AEPS_BALANCE));

  //          Toast.makeText(context, "Temporary out of service Please use wallet transfer.", Toast.LENGTH_LONG).show();

            if (getStringFromEditText(binding.aepsWithdrawalAmt) == null) {
                binding.aepsWithdrawalAmt.setError("Please Enter Amount");
                binding.aepsWithdrawalAmt.requestFocus();
            } else if (Integer.parseInt(getStringFromEditText(binding.aepsWithdrawalAmt)) >= aepsBal) {
                binding.aepsWithdrawalAmt.setError("Please Check Your AEPS Limit");
                binding.aepsWithdrawalAmt.requestFocus();
            } else if (selectBene.equalsIgnoreCase("Select")) {
                Toast.makeText(context, "Please Select Beneficiary", Toast.LENGTH_SHORT).show();
            } else if (selectPayMode.equalsIgnoreCase("Select")) {
                Toast.makeText(context, "Please Select Payment Mode", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(binding.aepsTPin) == null) {
                binding.aepsTPin.setError("Please Enter TPin");
                binding.aepsTPin.requestFocus();
            } else {
                aepsToBankAPI(selectBene, selectPayMode);
           //     Toast.makeText(context, "Temporary out of service Please use wallet transfer.", Toast.LENGTH_LONG).show();
            }
        });

        return binding.getRoot();
    }


    private void aepsGetBeneBalAPI() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_AEPS_BENE_LIST + url,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);

                    try {
                        JSONObject object = new JSONObject(response);

                        String aepsLimit = "";
                        if (object.has("AEPSLimit")) {
                            aepsLimit = object.getString("AEPSLimit");
                            session.setString(Session.AEPS_LIMIT, object.getString("AEPSLimit"));
                        }

                        JSONObject obj = object.getJSONObject("AEPSBalance");

                        String aepsBal = "";
                        if (obj.has("aeps_balance")) {
                            aepsBal = obj.getString("aeps_balance");
                            session.setString(Session.AEPS_BALANCE, obj.getString("aeps_balance"));
                        }

//                            binding.currentAepsLimit.setText(aepsLimit);
//                            binding.currentAepsLimit.setText(getAmountFormat(aepsLimit));
                        binding.currentAepsLimit.setText(getAmountFormat(aepsBal)); // we put both aeps Bal
//                            binding.currentAepsBal.setText(aepsBal); // this is for without amount format
                        binding.currentAepsBal.setText(getAmountFormat(aepsBal));

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

    private void aepsToBankAPI(String selectBene, String selectPayMode) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSBankTransfer";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    hideLoadingDialog();
                    Log.e("TAG", "aepsToBankAPI: "+response );
                    System.out.println(response);
                    try {
                        JSONObject object = new JSONObject(response);


                        String status = "";
                        if (object.has("Status")) {
                            status = object.getString("Status");
                        }

                        String remarks = "";
                        if (object.has("Remarks")) {
                            remarks = object.getString("Remarks");
                        }

                        String apiRef = "";
                        if (object.has("api_ref")) {
                            apiRef = object.getString("api_ref");
                        }

                        String acNo = "";
                        if (object.has("ac_no")) {
                            acNo = object.getString("ac_no");
                        }

                        String bankName = "";
                        if (object.has("bank_name")) {
                            bankName = object.getString("bank_name");
                        }

                        String beneName = "";
                        if (object.has("benef_name")) {
                            beneName = object.getString("benef_name");
                        }

                        String resp = "";
                        if (object.has("Resp")) {
                            resp = object.getString("Resp");
                        }

                        aepsToBankSuccess(status, remarks, apiRef, acNo, bankName, beneName, resp, getStringFromEditText(binding.aepsWithdrawalAmt));

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
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("WithdrawAmtBank", getStringFromEditText(binding.aepsWithdrawalAmt));
                params.put("BenefIdBank", selectBene);
                params.put("SelectMode", selectPayMode);
                params.put("WithdrawTPIN", getStringFromEditText(binding.aepsTPin));
                params.put("tok", session.getString(Session.TOKEN));
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

    private void aepsToBankSuccess(String status, String remarks, String apiRef, String acNo, String bankName, String beneName, String resp, String amt) {
        Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);

        ActivityAepsToWalletSuccessBinding binding2 = ActivityAepsToWalletSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());

        binding2.aepsToWalletDetails.setVisibility(View.GONE);
        binding2.aepsAddBeneDetails.setVisibility(View.GONE);


        binding2.aepsToBankStatus.setText(status);
        binding2.aepsToBankAmt.setText(amt);
        binding2.aepsToBankRemark.setText(remarks);
        binding2.aepsToBankApiRef.setText(apiRef);
        binding2.aepsToBankAccNo.setText(acNo);
        binding2.aepsToBankBankName.setText(bankName);
        binding2.aepsToBankBeneName.setText(beneName);
        binding2.aepsToBankResp.setText(resp);

        binding2.aepsToBankDone.setOnClickListener(v -> {
            dialog.dismiss();
            getActivity().onBackPressed();
        });

        dialog.show();
    }

    private void aepsToWalletAPI() {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/AEPSWalletTransfer";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            if (object.has("Resp")) {
                                Toast.makeText(context, object.getString("Resp"), Toast.LENGTH_SHORT).show();
                            }

                            String rtCommAmt = "";
                            if (object.has("RTCommAmt")) {
                                rtCommAmt = object.getString("RTCommAmt");
                            }

                            String rtChargeAmt = "";
                            if (object.has("RTChargeAmt")) {
                                rtChargeAmt = object.getString("RTChargeAmt");
                            }

                            String aepsRemaining = "";
                            if (object.has("AEPSRemaining")) {
                                aepsRemaining = object.getString("AEPSRemaining");
                            }

                            String walletNetAmount = "";
                            if (object.has("WalletNetAmt")) {
                                walletNetAmount = object.getString("WalletNetAmt");
                            }

                            String resp = "";
                            if (object.has("Resp")) {
                                resp = object.getString("Resp");
                            }

                            showAepsToWalletSuccess(rtCommAmt, rtChargeAmt, aepsRemaining, walletNetAmount, resp, getStringFromEditText(binding.aepsWithdrawalAmt));

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
                params.put("WithdrawAmt", getStringFromEditText(binding.aepsWithdrawalAmt));
                params.put("tok", session.getString(Session.TOKEN));
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

    private void showAepsToWalletSuccess(String rtCommAmt, String rtChargeAmt, String aepsRemaining, String walletNetAmount, String resp, String amt) {
        Dialog dialog = new Dialog(context);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
        dialog.setCancelable(true);

        ActivityAepsToWalletSuccessBinding binding1 = ActivityAepsToWalletSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding1.getRoot());

        binding1.aepsToBankDetails.setVisibility(View.GONE);
        binding1.aepsAddBeneDetails.setVisibility(View.GONE);

        binding1.rtCommAmount.setText(rtCommAmt);
        binding1.rtChargeAmount.setText(rtChargeAmt);
        binding1.aepsRemaining.setText(aepsRemaining);
//        binding1.walletNetAmount.setText(getAmountFormat(walletNetAmount));
        binding1.walletNetAmount.setText(walletNetAmount);
        binding1.aepsRespMessage.setText(resp);

        binding1.walletTransferDone.setOnClickListener(v -> {
            dialog.dismiss();
            getActivity().onBackPressed();
        });

        dialog.show();
    }


    private void viewBeneListAPI() {
        showLoadingDialog();
        viewList.clear();
        viewList.add(new AEPSViewBeneficiaryGetSet(
                "",
                "Select",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                ""
        ));
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_AEPS_BENE_LIST + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        Log.e("TAG", "onResponse:listtttt "+response );

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
                                                obj.getString("benef_name")+" || ",
                                                obj.getString("bank_name")+" || ",
                                                obj.getString("bank_ifsc")+" || ",
                                                obj.getString("account_no")+" || ",
                                                obj.getString("contact")+" || ","",
//                                                obj.getString("created_on"),
                                                obj.getString("benef_id"),
                                                aeps_balance,
                                                AEPSLimit
                                        ));
                                    }
                                }
                            }
                            arrayAdapter.notifyDataSetChanged();
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
        });
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    private void onSelect() {
        binding.bankTransferAeps.setOnClickListener(v -> {
            binding.bankTransferAeps.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.bankTransferAeps.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.selectBeneAeps.setVisibility(View.VISIBLE);
            binding.selectPayMode.setVisibility(View.VISIBLE);
            binding.aepsToWalletSubmit.setVisibility(View.GONE);
            binding.aepsToBankSubmit.setVisibility(View.VISIBLE);

            binding.aepsTPinBank.setVisibility(View.VISIBLE);

            binding.walletTransferAeps.setTextColor(ContextCompat.getColor(context, R.color.blue));
            binding.walletTransferAeps.setBackground(getResources().getDrawable(R.drawable.white_button_background));
        });


        binding.walletTransferAeps.setOnClickListener(v -> {
            binding.walletTransferAeps.setTextColor(ContextCompat.getColor(context, R.color.white));
            binding.walletTransferAeps.setBackground(getResources().getDrawable(R.drawable.blue_dark_rounded_color));
            binding.selectBeneAeps.setVisibility(View.GONE);
            binding.selectPayMode.setVisibility(View.GONE);
            binding.aepsToBankSubmit.setVisibility(View.GONE);
            binding.aepsToWalletSubmit.setVisibility(View.VISIBLE);

            binding.aepsTPinBank.setVisibility(View.GONE);

            binding.bankTransferAeps.setTextColor(ContextCompat.getColor(context, R.color.blue));
            binding.bankTransferAeps.setBackground(getResources().getDrawable(R.drawable.white_button_background));
        });

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