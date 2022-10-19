package com.swinfotech.swpay.activities;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.chaos.view.PinView;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;
import com.swinfotech.swpay.Constant;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.adapter.BeneficiaryListAdapter;
import com.swinfotech.swpay.adapter.BeneficiaryListAdapterBeta;
import com.swinfotech.swpay.adapter.BeneficiaryListAdapterCF;
import com.swinfotech.swpay.databinding.ActivityAddBeneSuccessDialogBinding;
import com.swinfotech.swpay.databinding.ActivityAddBeneficiaryBetaBinding;
import com.swinfotech.swpay.databinding.ActivityAddBeneficiaryBinding;
import com.swinfotech.swpay.databinding.ActivityAddNewRemitterBetaBinding;
import com.swinfotech.swpay.databinding.ActivityAddNewRemitterOtpBinding;
import com.swinfotech.swpay.databinding.ActivityAddNewRemitterOtpbetaBinding;
import com.swinfotech.swpay.databinding.ActivityAepsToWalletSuccessBinding;
import com.swinfotech.swpay.databinding.ActivityBeneficiaryListBetaBinding;
import com.swinfotech.swpay.databinding.ActivityBeneficiaryListBinding;
import com.swinfotech.swpay.databinding.ActivityRemitterAddSuccessBinding;
import com.swinfotech.swpay.databinding.ActivityTransferMoneyBinding;
import com.swinfotech.swpay.databinding.AddCfBenefLytBinding;
import com.swinfotech.swpay.databinding.BeneficiaryMainCfLytBinding;
import com.swinfotech.swpay.model.BenefListCFModel;
import com.swinfotech.swpay.model.BeneficiaryListBetaGetSet;
import com.swinfotech.swpay.model.BeneficiaryListGetSet;
import com.swinfotech.swpay.model.DMTBankListGetSet;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TransferMoney extends AppCompatActivity {

    ActivityTransferMoneyBinding binding;
    TransferMoney activity;

    Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    // Recycler View details
    BeneficiaryListAdapter beneficiaryListAdapter;
    List<BeneficiaryListGetSet> beneList = new ArrayList<>();
    List<String> spinnerArray = new ArrayList<String>();

    // Recycler View details for DMT Beta
    BeneficiaryListAdapterBeta beneficiaryListAdapterBeta;
    List<BeneficiaryListBetaGetSet> beneList1 = new ArrayList<>();
    List<BeneficiaryListBetaGetSet> beneList2 = new ArrayList<>();

    // Bank List Array
    private ArrayList<DMTBankListGetSet> model;

    private final ArrayList<String> bankName = new ArrayList<String>();
    private final ArrayList<String> ifscCode = new ArrayList<String>();
    private String ifsc = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransferMoneyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        activity = this;

        session = new Session(activity);


        binding.transferMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getStringFromEditText(binding.mobTransfer) == null) {
                    binding.mobTransfer.setError("Please Enter Mobile Number");
                    binding.mobTransfer.requestFocus();
                } else if (getStringFromEditText(binding.mobTransfer).length() != 10) {
                    binding.mobTransfer.setError("Please Enter 10 Digit Mobile Number");
                    binding.mobTransfer.requestFocus();
                } else {

                    getDMTBeneAPI(false);
                    // Toast.makeText(activity, "Use Beta Version !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.transferMoneyBeta1.setOnClickListener(v -> {
            if (getStringFromEditText(binding.mobTransfer) == null) {
                binding.mobTransfer.setError("Please Enter Mobile Number");
                binding.mobTransfer.requestFocus();
            } else if (getStringFromEditText(binding.mobTransfer).length() != 10) {
                binding.mobTransfer.setError("Please Enter 10 Digit Mobile Number");
                binding.mobTransfer.requestFocus();
            } else {
                getDMTBeneAPIBeta();
                getDMTVerifyAPI();
            }
        });

        binding.transferMoneyCf.setOnClickListener(v -> {
            getDMTBeneAPICF();
        });
        getDMTUser();


    }


    public void getDMTBeneAPICF() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.GET_DMTCF_BENF_LIST + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            if (response != null) {
                                JSONObject obj = new JSONObject(response);
                                ArrayList<BenefListCFModel> list = new ArrayList<>();
                                if (obj.has("GetBeneficiary")) {
                                    JSONArray GetBeneficiaryArr = obj.getJSONArray("GetBeneficiary");
                                    if (GetBeneficiaryArr.length() > 0) {
                                        for (int i = 0; i < GetBeneficiaryArr.length(); i++) {
                                            BenefListCFModel model = new BenefListCFModel();
                                            JSONObject benefObj = GetBeneficiaryArr.getJSONObject(i);
                                            model.setBenef_id(benefObj.getString("benef_id"));
                                            model.setBenef_name(benefObj.getString("benef_name"));
                                            model.setBenef_mobile(benefObj.getString("benef_mobile"));
                                            model.setBank_ac(benefObj.getString("bank_ac"));
                                            model.setIfsc(benefObj.getString("ifsc"));
                                            model.setAddress1(benefObj.getString("address1"));
                                            model.setPincode(benefObj.getString("pincode"));
                                            model.setBank_name(benefObj.getString("bank_name"));
                                            model.setVerified(!benefObj.getString("verified").equals("0"));

                                            list.add(model);
                                        }
                                    }
                                }

                                showBeneficiaryListCF(list);

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    private void getDMTUser() {
        if (Constant.ISBETA) {
            binding.transferMoney.setVisibility(View.GONE);
            binding.transferMoneyBeta1.setVisibility(View.VISIBLE);
            binding.llDMT.setVisibility(View.GONE);
            binding.llDMTBeta.setVisibility(View.VISIBLE);
            binding.llDMTCF.setVisibility(View.GONE);
            binding.transferMoneyCf.setVisibility(View.GONE);
        }
        if (Constant.ISALPHA) {
            binding.transferMoney.setVisibility(View.VISIBLE);
            binding.transferMoneyBeta1.setVisibility(View.GONE);
            binding.llDMT.setVisibility(View.VISIBLE);
            binding.llDMTBeta.setVisibility(View.GONE);
            binding.llDMTCF.setVisibility(View.GONE);
            binding.transferMoneyCf.setVisibility(View.GONE);
        }
        if (Constant.ISCF) {
            binding.transferMoney.setVisibility(View.GONE);
            binding.transferMoneyBeta1.setVisibility(View.GONE);
            binding.llDMT.setVisibility(View.GONE);
            binding.llDMTBeta.setVisibility(View.GONE);
            binding.mobTransfer.setVisibility(View.GONE);
            binding.llmobile.setVisibility(View.GONE);
            binding.tvDetails.setVisibility(View.GONE);
            binding.tvTransfer.setVisibility(View.GONE);
            binding.llPay.setVisibility(View.GONE);
            binding.icPay.setVisibility(View.GONE);
            binding.llDMTCF.setVisibility(View.GONE);
            binding.transferMoneyCf.setVisibility(View.GONE);
            binding.transferMoneyCf.performClick();
        }
    }

    private void getDMTVerifyAPI() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&rem_id=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_VERIFY_BETA + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        System.out.println("Second API Also Run");
                        try {
                            if (response != null) {
                                JSONObject obj = new JSONObject(response);
                                JSONArray jsonArray = obj.getJSONArray("DbBen");

                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject object2 = jsonArray.getJSONObject(i);
                                        BeneficiaryListBetaGetSet data = new BeneficiaryListBetaGetSet();

                                        String beneId12 = "";
                                        if (object2.has("ben_id")) {
                                            data.setBeneId1(object2.getString("ben_id"));
//                                            beneId12 = object2.getString("ben_id");
                                        }

                                        String beneVerify12 = "";
                                        if (object2.has("ben_verify")) {
                                            data.setVerify(object2.getString("ben_verify"));
//                                            beneVerify12 = object2.getString("ben_verify");
                                        }

                                        String aadharNo12 = "";
                                        if (object2.has("aadhar_no")) {
                                            data.setAadharNo(object2.getString("aadhar_no"));
//                                            aadharNo12 = object2.getString("aadhar_no");
                                        }

                                        String panNo12 = "";
                                        if (object2.has("pan_no")) {
                                            data.setPanNo(object2.getString("pan_no"));
//                                            panNo12 = object2.getString("pan_no");
                                        }

//                                        data.setBeneId1(beneId12);
//                                        data.setBeneId1(beneVerify12);
//                                        data.setBeneId1(aadharNo12);
//                                        data.setBeneId1(panNo12);

//                                        for (int j=0; j < beneList1.toArray().length; j++) {
//                                            if (beneList1.get(j).getCode().equals(beneId12)) {
//                                                System.out.println("Your code is here : " + beneId12 + "and" + beneList1.get(j).getCode());
////                                                data.setBeneId1(beneId12);
//                                                beneList1.add(data);
//                                            }
//                                        }


                                        beneList2.add(data);
                                        System.out.println(data.getVerify());


                                    }

//                                  check the list if it is empty or not
                                    if (beneList2.isEmpty()) {
                                        System.out.println("List is Empty");
                                        Toast.makeText(activity, "Data is Empty", Toast.LENGTH_SHORT).show();
                                    } else {
                                        System.out.println("Data is Full");
                                    }

                                } else {
                                    System.out.println("Json Array is not Responding");
//                                        Toast.makeText(activity, "Json Array is not Responding", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(activity, "First Add Beneficiary", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    // Here Start DMT BETA
    private void getDMTBeneAPIBeta() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_GET_BEN_BETA + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            if (response != null) {
                                JSONObject obj = new JSONObject(response);

                                if (obj.has("Resp")) {
                                    Toast.makeText(activity, obj.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }

                                if (obj.has("StatusCode")) {
                                    String statusCode = "";
                                    statusCode = obj.getString("StatusCode");

                                    String mobileNo1 = "";
                                    if (obj.has("MobileNo")) {
                                        mobileNo1 = obj.getString("MobileNo");
                                    }

                                    String beneFMSG = "";
                                    if (obj.has("BenefMSG")) {
                                        beneFMSG = obj.getString("BenefMSG");
                                    }


                                    if (statusCode.equalsIgnoreCase("1")) {

                                        System.out.println(obj);

                                        String mobileNo = "";
                                        if (obj.has("MobileNo")) {
                                            mobileNo = obj.getString("MobileNo");
                                        }

                                        String name = "";
                                        if (obj.has("Name")) {
                                            name = obj.getString("Name");
                                        }

                                        String monthlyLimit = "";
                                        if (obj.has("MonthlyLimit")) {
                                            monthlyLimit = obj.getString("MonthlyLimit");
                                        }

                                        String availableLimit = "";
                                        if (obj.has("AvailableLimit")) {
                                            availableLimit = obj.getString("AvailableLimit");
                                        }

                                        String beneStat = "";
                                        if (obj.has("BenefStat")) {
                                            beneStat = obj.getString("BenefStat");
                                        }

                                        showBeneficiaryListBeta(mobileNo, name, monthlyLimit, availableLimit, beneStat, beneFMSG);

                                    } else if (statusCode.equalsIgnoreCase("2")) {

                                        showAddNewRemitterDialogBeta(statusCode, getStringFromEditText(binding.mobTransfer), mobileNo1);

                                    } else if (statusCode.equalsIgnoreCase("3")) {
                                        Toast.makeText(activity, "DMT Beta Under Maintenance", Toast.LENGTH_SHORT).show();
                                        System.out.println("DMT Beta Under Maintenance");
                                    } else {
                                        Toast.makeText(activity, "Something went wrong try again..!", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    private void showBeneficiaryListBeta(String mobileNo, String name, String monthlyLimit, String availableLimit, String beneStat, String beneFMSG) {
        ActivityBeneficiaryListBetaBinding binding1 = ActivityBeneficiaryListBetaBinding.inflate(getLayoutInflater());
        setContentView(binding1.getRoot());

        binding1.beneBackBeta.setOnClickListener(v -> {
            onBackPressed();
        });

        binding1.addBeneficiaryBeta.setOnClickListener(v -> {
            showAddBeneficiaryBeta(mobileNo);
        });

        binding1.beneNameBeta.setText(name);
        binding1.beneMobNumBeta.setText(mobileNo);
//        binding1.beneRemitterId.setText(remitterId);
        binding1.monthlyLimitBeta.setText(monthlyLimit);
        binding1.availableLimitBeta.setText(availableLimit);

        // set the layout manager on recyclerview
        binding1.beneRecViewBeta.setLayoutManager(new LinearLayoutManager(activity));
        binding1.beneRecViewBeta.setHasFixedSize(true);
        beneficiaryListAdapterBeta = new BeneficiaryListAdapterBeta(activity, beneList1, beneList2);
        binding1.beneRecViewBeta.setAdapter(beneficiaryListAdapterBeta);

        if (beneStat.equalsIgnoreCase("0")) {
            Toast.makeText(activity, "You Need To Add ADD Beneficiary First", Toast.LENGTH_SHORT).show();
            binding1.errorBeta.setVisibility(View.VISIBLE);
            binding1.errorBeta.setText(beneFMSG);
        } else {
            getBeneficiaryListAPIBeta();
        }
//        getBeneficiaryListAPIBeta();
    }

    private void showBeneficiaryListCF(ArrayList<BenefListCFModel> list) {
        BeneficiaryMainCfLytBinding beneficiaryListCfLytBinding = BeneficiaryMainCfLytBinding.inflate(getLayoutInflater());
        setContentView(beneficiaryListCfLytBinding.getRoot());

        beneficiaryListCfLytBinding.beneBackCF.setOnClickListener(v -> {
            onBackPressed();
        });

        beneficiaryListCfLytBinding.addBeneficiaryCF.setOnClickListener(v -> {
            showAddBeneficiaryCF();
        });


        // set the layout manager on recyclerview
        beneficiaryListCfLytBinding.beneRecViewCF.setLayoutManager(new LinearLayoutManager(activity));
        beneficiaryListCfLytBinding.beneRecViewCF.setHasFixedSize(true);

        BeneficiaryListAdapterCF adapterCF = new BeneficiaryListAdapterCF(this, list);
        beneficiaryListCfLytBinding.beneRecViewCF.setAdapter(adapterCF);

    }

    private void showAddBeneficiaryCF() {

        Dialog dialog = new Dialog(activity);
        AddCfBenefLytBinding addCfBenefLytBinding = AddCfBenefLytBinding.inflate(getLayoutInflater());
        dialog.setContentView(addCfBenefLytBinding.getRoot());
        dialog.getWindow().setLayout(90, 90);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);
        getBankListCF(addCfBenefLytBinding.bankListAddBenecf, addCfBenefLytBinding);
        dialog.setCancelable(true);

        addCfBenefLytBinding.cfAddBeneSubmit.setOnClickListener(v -> {
            String bankListIFSC = addCfBenefLytBinding.bankListAddBenecf.getSelectedItem().toString();
            //  Toast.makeText(context, "Temporary out of service", Toast.LENGTH_LONG).show();

            if (getStringFromEditText(addCfBenefLytBinding.cfAddBeneName) == null) {
                addCfBenefLytBinding.cfAddBeneName.setError("Please Enter A/C Holder Name");
                addCfBenefLytBinding.cfAddBeneName.requestFocus();
            } else if (bankListIFSC.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(this, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (ifsc.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(this, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(addCfBenefLytBinding.cfAddBeneAccNo) == null) {
                addCfBenefLytBinding.cfAddBeneAccNo.setError("Please Enter Account No");
                addCfBenefLytBinding.cfAddBeneAccNo.requestFocus();
            } else if (getStringFromEditText(addCfBenefLytBinding.cfAddBeneAddress) == null) {
                addCfBenefLytBinding.cfAddBeneAddress.setError("Please Enter Address");
                addCfBenefLytBinding.cfAddBeneAddress.requestFocus();
            } else if (getStringFromEditText(addCfBenefLytBinding.cfAddBenePincode) == null) {
                addCfBenefLytBinding.cfAddBenePincode.setError("Please Enter Pincode");
                addCfBenefLytBinding.cfAddBenePincode.requestFocus();
            } else if (getStringFromEditText(addCfBenefLytBinding.cfAddBeneIfsc) == null) {
                addCfBenefLytBinding.cfAddBeneIfsc.setError("Please Enter IFSC Code");
                addCfBenefLytBinding.cfAddBeneIfsc.requestFocus();
            } else {

                aepsAddBeneficiary(bankListIFSC, addCfBenefLytBinding, dialog);

                //   Toast.makeText(context, "Temporary out of service", Toast.LENGTH_LONG).show();
            }
        });
        dialog.show();

    }

    private void aepsAddBeneficiary(String bankListIFSC, AddCfBenefLytBinding addCfBenefLytBinding, Dialog dialog) {
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

                            aepsAddBeneSuccess(resp, status, dialog);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Toast.makeText(TransferMoney.this, error.toString(), Toast.LENGTH_LONG).show();
                System.out.println(error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("UserName", getStringFromEditText(addCfBenefLytBinding.cfAddBeneName));
                params.put("BankAC", getStringFromEditText(addCfBenefLytBinding.cfAddBeneAccNo));
                params.put("BankIFSC", getStringFromEditText(addCfBenefLytBinding.cfAddBeneIfsc));
                params.put("AreaPIN", getStringFromEditText(addCfBenefLytBinding.cfAddBenePincode));
                params.put("Address", getStringFromEditText(addCfBenefLytBinding.cfAddBeneAddress));
//                params.put("BankName", getStringFromEditText(binding.aepsAddBeneBankName));
                params.put("BankName", bankListIFSC);
                params.put("tok", session.getString(Session.TOKEN));
                //   Log.e("TAG", "getParams: "+params);
                return params;
            }

        };
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(TransferMoney.this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void aepsAddBeneSuccess(String resp, String status, Dialog mainDialog) {
        Dialog dialog = new Dialog(this);

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
        mainDialog.dismiss();
        binding1.aepsAddBeneDone.setOnClickListener(v -> {
            dialog.dismiss();
            this.recreate();
        });

        dialog.show();
    }

    private void getBankListCF(SearchableSpinner spinner, AddCfBenefLytBinding addCfBenefLytBinding) {
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

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, bankName);
                            addCfBenefLytBinding.bankListAddBenecf.setAdapter(spinnerArrayAdapter);
                            addCfBenefLytBinding.bankListAddBenecf.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showAddBeneficiaryBeta(String mobileNo) {
        Dialog dialog = new Dialog(activity);
        ActivityAddBeneficiaryBetaBinding binding6 = ActivityAddBeneficiaryBetaBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding6.getRoot());
        dialog.getWindow().setLayout(90, 90);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(false);

        binding6.cancelCrossBeta.setOnClickListener(v -> {
            dialog.dismiss();
        });

        binding6.addMobNoBeneBeta.setText(mobileNo);

//        String bankListIFSC = binding2.bankList.getSelectedItem().toString();

        getBankList(binding6.bankListBeta);

        Log.e("TAG", "showAddBeneficiaryBeta:---------_LIST ");

        binding6.addBeneDetailsBeta.setOnClickListener(v -> {
            String bankListIFSC = binding6.bankListBeta.getSelectedItem().toString();

            // old ifsc
//            else if (getStringFromEditText(binding2.addIfscBene) == null) {
//                binding2.addIfscBene.setError("Please Enter IFSC Code");
//                binding2.addIfscBene.requestFocus();
//            }

            if (getStringFromEditText(binding6.addNameBeneBeta) == null) {
                binding6.addNameBeneBeta.setError("Please Enter Name");
                binding6.addNameBeneBeta.requestFocus();
            } else if (getStringFromEditText(binding6.addMobNoBeneBeta) == null) {
                binding6.addMobNoBeneBeta.setError("Please Enter Mobile No");
                binding6.addMobNoBeneBeta.requestFocus();
            } else if (getStringFromEditText(binding6.addMobNoBeneBeta).length() != 10) {
                binding6.addMobNoBeneBeta.setError("Please Enter 10 Digit Mobile No");
                binding6.addMobNoBeneBeta.requestFocus();
            } else if (getStringFromEditText(binding6.addAccNoBeneBeta) == null) {
                binding6.addAccNoBeneBeta.setError("Please Enter Account Number");
                binding6.addAccNoBeneBeta.requestFocus();
            } else if (bankListIFSC.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(activity, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (ifsc.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(activity, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(binding6.addIFSCBeta) == null) {
                binding6.addIFSCBeta.setError("Please Enter Account Number");
                binding6.addIFSCBeta.requestFocus();
            } else {
                // old
//                addBeneficiaryAPI(dialog, remitterId, getStringFromEditText(binding2.addNameBene), getStringFromEditText(binding2.addMobNoBene),
//                        getStringFromEditText(binding2.addAccNoBene), getStringFromEditText(binding2.addIfscBene), bankListIFSC);
                // new
                addBeneficiaryAPIBeta(dialog, getStringFromEditText(binding6.addNameBeneBeta), getStringFromEditText(binding6.addMobNoBeneBeta),
                        getStringFromEditText(binding6.addAccNoBeneBeta), getStringFromEditText(binding6.addIFSCBeta), bankListIFSC, ifsc);
            }

        });

        dialog.show();
    }

    private void addBeneficiaryAPIBeta(Dialog dialog, String name, String mobileNo, String accNo, String beneIfsc, String bankListIFSC, String ifsc) {
        showLoadingDialog();
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTAddBenBeta";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);
                            Log.e("TAG", "onResponse:6868686868 " + response);

                            String BeneName = "";
                            if (object.has("BenName")) {
                                BeneName = object.getString("BenName");
                            }

                            String BeneId = "";
                            if (object.has("BenefId")) {
                                BeneId = object.getString("BenefId");
                            }

                            String message = "";
                            if (object.has("Message")) {
                                message = object.getString("Message");
                            }

                            String mobNo = "";
                            if (object.has("Mobile")) {
                                mobNo = object.getString("Mobile");
                            }

                            String status = "";
                            if (object.has("StatusCode")) {
                                status = object.getString("StatusCode");
                            }


                            Log.e("TAG", "onResponse: 9999999999 " + mobileNo + name);

                            if (status.equalsIgnoreCase("FAILED")) {
                                dialog.dismiss();


                                showSuccessAddBeneficiaryBeta(status, message, BeneName, BeneId, mobNo);
                            } else {
                                dialog.dismiss();
                                showSuccessAddBeneficiaryBeta(status, message, BeneName, BeneId, mobNo);
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
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("BenMobileAdd", mobileNo);
                params.put("BenNameAdd", name);
                params.put("BenACAdd", accNo);
                params.put("BenAddIFSC", beneIfsc);
                params.put("BenAddBank", bankListIFSC);
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

    private void showSuccessAddBeneficiaryBeta(String status, String message, String beneName, String beneId, String mobNo) {
        Dialog dialog = new Dialog(activity);
        ActivityAddBeneSuccessDialogBinding binding7 = ActivityAddBeneSuccessDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding7.getRoot());
        dialog.setCancelable(false);

        binding7.beneAddBetaDetails.setVisibility(View.VISIBLE);

        if (status.equalsIgnoreCase("FAILED")) {
            binding7.gifBeneSuccess.setVisibility(View.GONE);
            binding7.gifBeneFailed.setVisibility(View.VISIBLE);
            binding7.addBeneBetaMobNoDone.setText(mobNo);
            binding7.addBeneBetaBeneNameDone.setText(beneName);
            binding7.addBeneBetaBeneIdDone.setText(beneId);
            binding7.addResultBene.setText(message);
            binding7.addResultBene.setTextColor(ContextCompat.getColor(activity, R.color.red));
//            binding7.addMessageBene.setText("Try Again...");
//            binding7.addMessageBene.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding7.addMessageBene.setVisibility(View.GONE);
        } else {
            binding7.gifBeneFailed.setVisibility(View.GONE);
            binding7.gifBeneSuccess.setVisibility(View.VISIBLE);
            binding7.addBeneBetaMobNoDone.setText(mobNo);
            binding7.addBeneBetaBeneNameDone.setText(beneName);
            binding7.addBeneBetaBeneIdDone.setText(beneId);
            binding7.addResultBene.setText(message);
            binding7.addResultBene.setTextColor(ContextCompat.getColor(activity, R.color.green));
//            binding7.addMessageBene.setText("You Added Beneficiary Successfully");
//            binding7.addMessageBene.setTextColor(ContextCompat.getColor(activity, R.color.green));
            binding7.addMessageBene.setVisibility(View.GONE);
        }

        binding7.addDoneBene.setOnClickListener(v -> {
            dialog.dismiss();

            // This is for refreshing API
            beneList1.clear();
            beneList2.clear();
            getDMTBeneAPIBeta();
            getDMTVerifyAPI();


        });

        dialog.show();
    }

    private void getBeneficiaryListAPIBeta() {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_GET_BEN_BETA + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);

                                String mobileNo = "";
                                if (jsonObject.has("MobileNo")) {
                                    mobileNo = jsonObject.getString("MobileNo");
                                }

                                if (jsonObject.has("StatusCode")) {

                                    JSONArray jsonArray = jsonObject.getJSONArray("Recipients");

                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object2 = jsonArray.getJSONObject(i);
                                            //check the object has parameter or not then add into pojo class
                                            BeneficiaryListBetaGetSet data = new BeneficiaryListBetaGetSet();

                                            if (object2.has("BeneficiaryCode")) {
                                                data.setCode(object2.getString("BeneficiaryCode"));
                                            }

                                            if (object2.has("BeneficiaryName")) {
                                                data.setName(object2.getString("BeneficiaryName"));
                                            }

                                            if (object2.has("AccountNumber")) {
                                                data.setAccNo(object2.getString("AccountNumber"));
                                            }

                                            if (object2.has("AccountType")) {
                                                data.setAccType(object2.getString("AccountType"));
                                            }

                                            if (object2.has("IFSC")) {
                                                data.setIfsc(object2.getString("IFSC"));
                                            }

                                            if (object2.has("Bankname")) {
                                                data.setBankName(object2.getString("Bankname"));
                                            }

                                            data.setMobileNo(mobileNo);

//                                            if (object2.has("Status")) {
//                                                data.setStatus(object2.getString("Status"));
//                                            }
//
//                                            if (object2.has("IsValidate")) {
//                                                data.setIsValidate(object2.getString("IsValidate"));
//                                            }

//                                            data.setRemitterID(remitterId);
                                            beneList1.add(data);

                                            beneficiaryListAdapterBeta.notifyDataSetChanged();

                                        }

                                        //check the list if it is empty or not
                                        if (beneList1.isEmpty()) {
                                            System.out.println("List is Empty");
                                            Toast.makeText(activity, "Data is Empty", Toast.LENGTH_SHORT).show();
                                        } else {
                                            System.out.println("Data is Full");
                                        }

                                    } else {
                                        System.out.println("Json Array is not Responding");
//                                        Toast.makeText(activity, "Json Array is not Responding", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(activity, "First Add Beneficiary", Toast.LENGTH_SHORT).show();
                                    }


                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    private void showAddNewRemitterDialogBeta(String statusCode, String mobileNo, String mobileNo1) {
        Dialog dialog = new Dialog(activity);
        ActivityAddNewRemitterBetaBinding binding3 = ActivityAddNewRemitterBetaBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding3.getRoot());
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        binding3.dismissBackBeta.setOnClickListener(v -> {
            dialog.dismiss();
        });

        binding3.newMobNoBeta.setText(mobileNo);
        binding3.newMobNoBeta.setEnabled(false);

        binding3.submitMobBeta.setOnClickListener(v -> {
            if (getStringFromEditText(binding3.newMobNoBeta) == null) {
                binding3.newMobNoBeta.setError("Please Enter Mobile No");
                binding3.newMobNoBeta.requestFocus();
            } else if (getStringFromEditText(binding3.newMobNoBeta).length() != 10) {
                binding3.newMobNoBeta.setError("Please Enter 10 Digit Mobile No");
                binding3.newMobNoBeta.requestFocus();
            } else if (getStringFromEditText(binding3.newRemitterNameBeta) == null) {
                binding3.newRemitterNameBeta.setError("Please Enter Name");
                binding3.newRemitterNameBeta.requestFocus();
            } else {
                getOTP(dialog, mobileNo, binding3.newRemitterNameBeta);
            }

        });


        dialog.show();
    }

    private void getOTP(Dialog dialog, String mobileNo, EditText newRemitterNameBeta) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobileno=" + mobileNo
                + "&name=" + getStringFromEditText(newRemitterNameBeta);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_GET_OTP_BETA + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject obj = new JSONObject(response);

                                if (obj.has("RemAddStatusCode")) {
                                    String statusCode = "";
                                    statusCode = obj.getString("RemAddStatusCode");

                                    String remName = "";
                                    if (obj.has("RemName")) {
                                        remName = obj.getString("RemName");
                                    }

                                    String refNo = "";
                                    if (obj.has("RefNo")) {
                                        refNo = obj.getString("RefNo");
                                    }

                                    String message = "";
                                    if (obj.has("RemAddMessage")) {
                                        message = obj.getString("RemAddMessage");
                                    }

                                    // here we go
                                    if (statusCode.equalsIgnoreCase("1")) {
                                        dialog.dismiss();
                                        sentOTPAddRemitter(statusCode, message, remName, refNo);

                                    } else {
                                        Toast.makeText(activity, "Something went wrong try again..", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    private void sentOTPAddRemitter(String statusCode, String message, String remName, String refNo) {
        Dialog dialog = new Dialog(activity);
        ActivityAddNewRemitterOtpbetaBinding binding4 = ActivityAddNewRemitterOtpbetaBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding4.getRoot());
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        binding4.dismissBack1Beta.setOnClickListener(v -> {
            dialog.dismiss();
        });

        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();

        binding4.newRefNoBeta.setText(refNo);
        binding4.newRefNoBeta.setEnabled(false);


        PinView otp = dialog.findViewById(R.id.newRemitterOTPBeta);

        binding4.submitRemitterOTPBeta.setOnClickListener(v -> {
            if (getStringFromEditText(binding4.newRefNoBeta) == null) {
                binding4.newRefNoBeta.setError("Please Enter Ref No");
                binding4.newRefNoBeta.requestFocus();
            } else if (otp == null) {
                otp.setError("Please Enter OTP");
                otp.requestFocus();
            } else if (otp.length() != 6) {
                otp.setError("Please Enter 6 Digit OTP");
                otp.requestFocus();
            } else {
                submitOTPAPIBeta(dialog, refNo, otp, remName);
            }

        });

        dialog.show();
    }

    private void submitOTPAPIBeta(Dialog dialog, String refNo, PinView otp, String remName) {
        showLoadingDialog();
        String OTP = String.valueOf(otp.getText());
        String url = "https://mobile.swpay.in/api/AndroidAPI/DMTAddRemOTP";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {
                            JSONObject object = new JSONObject(response);

                            String apiVerify = "";
                            if (object.has("apiVerifyOTP")) {
                                apiVerify = object.getString("apiVerifyOTP");
                            }

                            String statusCode = "";
                            if (object.has("OTPStatusCode")) {
                                statusCode = object.getString("OTPStatusCode");
                            }

                            String message = "";
                            if (object.has("OTPMessageCode")) {
                                message = object.getString("OTPMessageCode");
                            }

                            String mobileOTP = "";
                            if (object.has("OTPMobile")) {
                                mobileOTP = object.getString("OTPMobile");
                            }


                            if (statusCode.equalsIgnoreCase("FAILED")) {
                                dialog.dismiss();
                                showSuccessAddRemitterBeta(statusCode, message);
                            } else {
                                dialog.dismiss();
                                showSuccessAddRemitterBeta(statusCode, message);
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
                        Toast.makeText(activity, error.toString(), Toast.LENGTH_LONG).show();
                        System.out.println(error);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("RemOTPRefNo", refNo);
                params.put("RemAddOTP", OTP);
                params.put("RemName", remName);
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

    private void showSuccessAddRemitterBeta(String status, String message) {
        Dialog dialog = new Dialog(activity);
        ActivityRemitterAddSuccessBinding binding5 = ActivityRemitterAddSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding5.getRoot());
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        binding5.otpSuccessDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (status.equalsIgnoreCase("SUCCESS")) {
            binding5.gifOTPFailed.setVisibility(View.GONE);
            binding5.gifOTPSuccess.setVisibility(View.VISIBLE);
            binding5.otpMessage.setVisibility(View.VISIBLE);
            binding5.otpRemarks.setText(message);
            binding5.otpRemarks.setTextColor(ContextCompat.getColor(activity, R.color.green));
        } else {
            binding5.gifOTPSuccess.setVisibility(View.GONE);
            binding5.gifOTPFailed.setVisibility(View.VISIBLE);
            binding5.otpMessage.setVisibility(View.GONE);
            binding5.otpRemarks.setText(message);
            binding5.otpRemarks.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }

        dialog.show();
    }

    // Here Start DMT ALPHA
    private void getDMTBeneAPI(boolean sts) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_GET_BEN + url,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);
                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);

                            Log.e("TAG", "onResponse: Recipients " + response);
                            JSONObject obj = jsonObject.getJSONObject("Recipients");
                            if (obj.has("Status")) {
                                if (obj.getString("Status").equals("SUCCESS")) {
                                    // Toast.makeText(activity, obj.getString("Status"), Toast.LENGTH_SHORT).show();
                                    String statusCode = "";
                                    statusCode = obj.getString("Status");


                                    System.out.println(obj);

                                    String mobileNo = "";
                                    if (obj.has("phone_no")) {
                                        mobileNo = obj.getString("phone_no");
                                    }

                                    String name = "";
                                    if (obj.has("first_name")) {
                                        name = obj.getString("first_name");
                                    }

                                    String remitterId = "";
                                    if (obj.has("RemitterID")) {
                                        remitterId = obj.getString("RemitterID");
                                    }

                                    String pinCode = "";
                                    if (obj.has("PinCode")) {
                                        pinCode = obj.getString("PinCode");
                                    }

                                    String monthlyLimit = "";
                                    if (obj.has("MonthlyLimit")) {
                                        monthlyLimit = obj.getString("MonthlyLimit");
                                    }

                                    String availableLimit = "";
                                    if (obj.has("AvailableLimit")) {
                                        availableLimit = obj.getString("AvailableLimit");
                                    }


                                    showBeneficiaryList(statusCode, mobileNo, name, remitterId, pinCode, monthlyLimit, availableLimit, sts);

                                    /*} else {*/
                                            /*JSONObject obj1 = jsonObject.getJSONObject("apiAddRemOTP");

                                            String status1 = "";
                                            if (obj1.has("StatusCode")) {
                                                status1 = obj1.getString("StatusCode");
                                            }

                                            String message = "";
                                            if (obj1.has("Message")) {
                                                message = obj1.getString("Message");
                                            }

                                            if (status1.equalsIgnoreCase("1")) {*/

                                           /* } else {
                                                showAddNewRemitterDialog(status1, getStringFromEditText(binding.mobTransfer), message);
                                            }*/
                                } else if (obj.getString("Status").equals("FAILED")) {
                                    //    Toast.makeText(activity, "list is empty", Toast.LENGTH_SHORT).show();
                                    showAddNewRemitterDialog("status1", getStringFromEditText(binding.mobTransfer), "Add Remitter");
                                    //showAddNewRemitterDialogBeta("statusCode", getStringFromEditText(binding.mobTransfer), "mobileNo1");

                                }
                            }


                                /*}

                                else {
                                    showAddNewRemitterDialogBeta("statusCode", getStringFromEditText(binding.mobTransfer), "mobileNo1");
                                    return;
                                }*/
                        }

                              /*else {
                                System.out.println("Data Not Going in Another Activity");
                            }

                        } else {
                            System.out.println("Response is Null");
                        }*/
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
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    // new Remitter
    private void showAddNewRemitterDialog(String status1, String mobileNo, String message) {
        Dialog dialog = new Dialog(activity);
        ActivityAddNewRemitterOtpBinding activityAddNewRemitterOtpBinding = ActivityAddNewRemitterOtpBinding.inflate(getLayoutInflater());
        dialog.setContentView(activityAddNewRemitterOtpBinding.getRoot());
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        activityAddNewRemitterOtpBinding.dismissBack1.setOnClickListener(v -> {
            dialog.dismiss();
        });

        activityAddNewRemitterOtpBinding.newMobNo1.setText(mobileNo);
        activityAddNewRemitterOtpBinding.newMobNo1.setEnabled(false);

        activityAddNewRemitterOtpBinding.otpMessage.setText(message);
        activityAddNewRemitterOtpBinding.otpMessage.setTextColor(ContextCompat.getColor(activity, R.color.green));

        PinView otp = dialog.findViewById(R.id.newRemitterOTP);

        activityAddNewRemitterOtpBinding.submitRemitterOTP.setOnClickListener(v -> {
            if (getStringFromEditText(activityAddNewRemitterOtpBinding.newRemitterName) == null) {
                activityAddNewRemitterOtpBinding.newRemitterName.setError("Please Enter Name");
            } else if (getStringFromEditText(activityAddNewRemitterOtpBinding.lastName) == null) {
                activityAddNewRemitterOtpBinding.lastName.setError("Please Enter Last Name");

            } else if (activityAddNewRemitterOtpBinding.remAddress.getText().toString().equals("")) {
                activityAddNewRemitterOtpBinding.remAddress.setError("Please Enter Address");

            } /*else if (otp == null) {
                otp.setError("Please Enter OTP");
                otp.requestFocus();
            } else if (otp.length() != 6) {
                otp.setError("Please Enter 6 Digit OTP");
                otp.requestFocus();
            } */ else {
                submitOTPAPI(
                        dialog, mobileNo, activityAddNewRemitterOtpBinding.newRemitterName,
                        activityAddNewRemitterOtpBinding.lastName,
                        otp,
                        activityAddNewRemitterOtpBinding.remAddress.getText().toString());
            }

        });

        dialog.show();

    }

    private void submitOTPAPI(Dialog dialog, String newMobNo, EditText newRemitterName, EditText last_name, PinView otp, String address) {
        showLoadingDialog();
        String OTP = String.valueOf(otp.getText());
        //  + "&otp=" + OTP;
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DTM_OTP_REMITTER,
                response -> {
                    hideLoadingDialog();
                    System.out.println(response);
                    Log.e("TAG", "onResponse: ");

                    try {
                        if (response != null) {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject obj = jsonObject.getJSONObject("RemitterAdd");

                            if (obj.has("status")) {
                               /* String statusCode = "";
                                statusCode = obj.getString("StatusCode");
*/
                                String message = "";
                                if (obj.has("Message")) {
                                    message = obj.getString("msg");
                                }

                                // here we go
                                if (obj.getBoolean("status")) {
                                    dialog.dismiss();
                                    showSuccessAddRemitter(true, message);
                                } else {
//                                        expireOtp.setText(message);
                                    dialog.dismiss();
                                    Toast.makeText(activity, "Something went wrong!", Toast.LENGTH_SHORT).show();
                                    showSuccessAddRemitter(false, message);
                                }

                            } else {
                                System.out.println("Data Not Going in Another Activity");
                            }

                        } else {
                            System.out.println("Response is Null");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, error -> {
            hideLoadingDialog();
            System.out.println(error);
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("RemAddress", address);
                params.put("RemMobileAdd", newMobNo);
                params.put("RemFName", getStringFromEditText(newRemitterName));
                params.put("RemLName", getStringFromEditText(last_name));
                Log.e("PARAMS", "getParams: " + params);
                return params;
            }
        };
        ;
        // request queue
        RequestQueue requestQueue = Volley.newRequestQueue(activity);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void showSuccessAddRemitter(boolean statusCode, String message) {
        Dialog dialog = new Dialog(activity);
        ActivityRemitterAddSuccessBinding binding4 = ActivityRemitterAddSuccessBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding4.getRoot());
        int width = (int) (activity.getResources().getDisplayMetrics().widthPixels * 0.98f);
        dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false);

        binding4.otpSuccessDone.setOnClickListener(v -> {
            dialog.dismiss();
        });

        if (statusCode) {
            binding4.gifOTPFailed.setVisibility(View.GONE);
            binding4.gifOTPSuccess.setVisibility(View.VISIBLE);
            binding4.otpMessage.setVisibility(View.VISIBLE);
            binding4.otpRemarks.setText(message);
            binding4.otpRemarks.setTextColor(ContextCompat.getColor(activity, R.color.green));
        } else {
            binding4.gifOTPSuccess.setVisibility(View.GONE);
            binding4.gifOTPFailed.setVisibility(View.VISIBLE);
            binding4.otpMessage.setVisibility(View.GONE);
            binding4.otpRemarks.setText(message);
            binding4.otpRemarks.setTextColor(ContextCompat.getColor(activity, R.color.red));
        }

        dialog.show();

    }

    private void showBeneficiaryList(String statusCode, String mobileNo, String name, String remitterId, String pinCode, String monthlyLimit, String availableLimit, boolean sts) {
        ActivityBeneficiaryListBinding activityBeneficiaryListBinding = ActivityBeneficiaryListBinding.inflate(getLayoutInflater());
        setContentView(activityBeneficiaryListBinding.getRoot());

        Log.e("TAG", "showBeneficiaryList: ");
        activityBeneficiaryListBinding.beneBack.setOnClickListener(v -> {
            onBackPressed();
        });

        activityBeneficiaryListBinding.addBeneficiary.setOnClickListener(v -> {
            showAddBeneficiary(remitterId);
        });

        activityBeneficiaryListBinding.beneName.setText(name);
        activityBeneficiaryListBinding.beneMobNum.setText(mobileNo);
        activityBeneficiaryListBinding.beneRemitterId.setText(remitterId);
        activityBeneficiaryListBinding.monthlyLimit.setText(monthlyLimit);
        activityBeneficiaryListBinding.availableLimit.setText(availableLimit);

        // set the layout manager on recyclerview
        activityBeneficiaryListBinding.beneRecView.setLayoutManager(new LinearLayoutManager(activity));
        activityBeneficiaryListBinding.beneRecView.setHasFixedSize(true);
        beneficiaryListAdapter = new BeneficiaryListAdapter(activity, beneList);
        activityBeneficiaryListBinding.beneRecView.setAdapter(beneficiaryListAdapter);

        if (!sts) {
            getBeneficiaryListAPI(remitterId);
        }
    }

    private void getBeneficiaryListAPI(String remitterId) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&mobile=" + getStringFromEditText(binding.mobTransfer);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DMT_GET_BEN + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        try {

                            if (response != null) {

                                JSONObject jsonObject = new JSONObject(response);
                                //  JSONObject obj = jsonObject.getJSONObject("apiBeneficiary");

                                if (jsonObject.has("apiBeneficiary")) {
                                    //  JSONObject object1 = obj.getJSONObject("Data");

                                    JSONArray jsonArray = jsonObject.getJSONArray("apiBeneficiary");

                                    if (jsonArray.length() > 0) {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject object2 = jsonArray.getJSONObject(i);
                                            //check the object has parameter or not then add into pojo class
                                            BeneficiaryListGetSet data = new BeneficiaryListGetSet();

                                            if (object2.has("first_name")) {
                                                data.setName(object2.getString("beneficiary_name"));
                                            }

                                            if (object2.has("customer_mobile")) {
                                                data.setMobileNo(object2.getString("customer_mobile"));
                                            }

                                            if (object2.has("RPTID")) {
                                                data.setrPTid(object2.getString("RPTID"));
                                            }

                                            if (object2.has("beneficiary_account_number")) {
                                                data.setAccountNum(object2.getString("beneficiary_account_number"));
                                            }

                                            if (object2.has("beneficiary_ifsc")) {
                                                data.setIfsc(object2.getString("beneficiary_ifsc"));
                                            }

                                            if (object2.has("beneficiary_bank_name")) {
                                                data.setBankName(object2.getString("beneficiary_bank_name"));
                                            }

                                            if (object2.has("Status")) {
                                                data.setStatus(object2.getString("Status"));
                                            }

                                            if (object2.has("IsValidate")) {
                                                data.setIsValidate(object2.getString("IsValidate"));
                                            }

                                            data.setRemitterID(remitterId);
                                            beneList.add(data);

                                            beneficiaryListAdapter.notifyDataSetChanged();

                                        }

                                        //check the list if it is empty or not
                                        if (beneList.isEmpty()) {
                                            System.out.println("List is Empty");
                                            Toast.makeText(activity, "Data is Empty", Toast.LENGTH_SHORT).show();
                                        } else {
                                            System.out.println("Data is Full");
                                        }

                                    } else {
                                        System.out.println("Json Array is not Responding");
//                                        Toast.makeText(activity, "Json Array is not Responding", Toast.LENGTH_SHORT).show();
                                        Toast.makeText(activity, "First Add Beneficiary", Toast.LENGTH_SHORT).show();
                                    }

                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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


    private void showAddBeneficiary(String remitterId) {
        Dialog dialog = new Dialog(activity);
        ActivityAddBeneficiaryBinding activityAddBeneficiaryBinding = ActivityAddBeneficiaryBinding.inflate(getLayoutInflater());
        dialog.setContentView(activityAddBeneficiaryBinding.getRoot());
        dialog.getWindow().setLayout(90, 90);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialog.getWindow().setAttributes(lp);

        dialog.setCancelable(false);

        activityAddBeneficiaryBinding.cancelCross.setOnClickListener(v -> {
            dialog.dismiss();
        });

//        String bankListIFSC = binding2.bankList.getSelectedItem().toString();

        getBankList(activityAddBeneficiaryBinding.bankList);


        activityAddBeneficiaryBinding.addBeneDetails.setOnClickListener(v -> {
            String bankListIFSC = activityAddBeneficiaryBinding.bankList.getSelectedItem().toString();

            // old ifsc
//            else if (getStringFromEditText(binding2.addIfscBene) == null) {
//                binding2.addIfscBene.setError("Please Enter IFSC Code");
//                binding2.addIfscBene.requestFocus();
//            }

            activityAddBeneficiaryBinding.addMobNoBene.setText(binding.mobTransfer.getText());
            activityAddBeneficiaryBinding.addMobNoBene.setEnabled(false);

            if (getStringFromEditText(activityAddBeneficiaryBinding.addNameBene) == null) {
                activityAddBeneficiaryBinding.addNameBene.setError("Please Enter Name");
                activityAddBeneficiaryBinding.addNameBene.requestFocus();
            } else if (getStringFromEditText(activityAddBeneficiaryBinding.addMobNoBene) == null) {
                activityAddBeneficiaryBinding.addMobNoBene.setError("Please Enter Mobile No");
                activityAddBeneficiaryBinding.addMobNoBene.requestFocus();
            } else if (getStringFromEditText(activityAddBeneficiaryBinding.addMobNoBene).length() != 10) {
                activityAddBeneficiaryBinding.addMobNoBene.setError("Please Enter 10 Digit Mobile No");
                activityAddBeneficiaryBinding.addMobNoBene.requestFocus();
            } else if (getStringFromEditText(activityAddBeneficiaryBinding.addAccNoBene) == null) {
                activityAddBeneficiaryBinding.addAccNoBene.setError("Please Enter Account Number");
                activityAddBeneficiaryBinding.addAccNoBene.requestFocus();
            } else if (bankListIFSC.equalsIgnoreCase("Select Bank")) {
                Toast.makeText(activity, "Please Select Bank", Toast.LENGTH_SHORT).show();
            } else if (activityAddBeneficiaryBinding.addIFSCCode.getText().toString().equals("")) {
                Toast.makeText(activity, "Please Ifsc Code", Toast.LENGTH_SHORT).show();
            } else {
                // old
//                addBeneficiaryAPI(dialog, remitterId, getStringFromEditText(binding2.addNameBene), getStringFromEditText(binding2.addMobNoBene),
//                        getStringFromEditText(binding2.addAccNoBene), getStringFromEditText(binding2.addIfscBene), bankListIFSC);
                // new

                ifsc = activityAddBeneficiaryBinding.addIFSCCode.getText().toString();

                addBeneficiaryAPI(dialog, remitterId, getStringFromEditText(activityAddBeneficiaryBinding.addNameBene), getStringFromEditText(activityAddBeneficiaryBinding.addMobNoBene),
                        getStringFromEditText(activityAddBeneficiaryBinding.addAccNoBene), bankListIFSC, ifsc);
            }

        });

        dialog.show();
    }

    private void getBankList(SearchableSpinner spinner) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DTM_BANK_LIST + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);
                        Log.e("TAG", "onResponse: -----------------------" + response);
                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                model = new ArrayList<>();
                                JSONArray jsonArray = jsonObject.getJSONArray("GetDBBanks");

                                DMTBankListGetSet model1 = new DMTBankListGetSet();
                                model1.setBankName("Select Bank");
                                model1.setIfscCode("0");

                                model.add(model1);

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    DMTBankListGetSet data = new DMTBankListGetSet();
                                    JSONObject object = jsonArray.getJSONObject(i);

                                    data.setBankName(object.getString("bank_name"));
                                    //   data.setIfscCode(object.getString("IfscCode"));

                                    model.add(data);

                                }

                                for (int i = 0; i < model.size(); i++) {
                                    bankName.add(model.get(i).getBankName().toString());
//                                    ifscCode.add(model.get(i).getIfscCode().toString());
                                }

                                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(activity, android.R.layout.simple_spinner_dropdown_item, bankName);
                                spinner.setAdapter(spinnerArrayAdapter);
                                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                                        //    ifsc = ifscCode.get(position);
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

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
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

    private void addBeneficiaryAPI(Dialog dialog, String remitterId, String name, String mobileNo, String accountNo, String bankListIFSC, String ifsc) {


        dialog.dismiss();
        showLoadingDialog();
//                + "&ben_ifsc=" + bankListIFSC;
//                + "&ben_ifsc=" + ifscCode;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constant.DMT_ADD_BENE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                // JSONObject obj = jsonObject.getJSONObject("apiAddBen");

                                if (jsonObject.has("Status")) {
                                    // JSONObject obj1 = jsonObject.getJSONObject("Status");

                                    String status = "";
                                    /*if (obj1.has("StatusCode")) {
                                        status = obj1.getString("StatusCode");
                                    }*/

                                    String message = "";
                                    if (jsonObject.has("Msg")) {
                                        message = jsonObject.getString("Msg");
                                    }
/*

                                    String data = "";
                                    if (obj1.has("Data")) {
                                        data = obj1.getString("Data");
                                    }
*/

                                    if (message.equalsIgnoreCase("Beneficiary Add Success")) {
                                        showSuccessDialog(true, message, "data", name, mobileNo);
                                        Log.e("TAG", "onResponse: sucess ");
                                    } else {
                                        showSuccessDialog(false, message, "data", "", "");
                                    }

                                } else {
                                    System.out.println("Data Not Going in Another Activity");
                                }

                            } else {
                                System.out.println("Response is Null");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                System.out.println(error);
            }
        }) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                params.put("username", session.getString(Session.MOBILE));
                params.put("password", session.getString(Session.PASSWORD));
                params.put("BenNameAdd", name);
                params.put("BenACAdd", accountNo);
                params.put("BenIFSCAdd", ifsc);
                params.put("BenBankAdd", bankListIFSC);
                params.put("BenMobileAdd", mobileNo);
                Log.e("TAG", "getParams: " + params);
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

    private void showSuccessDialog(boolean status, String message, String data, String name, String number) {
        Dialog dialog = new Dialog(activity);
        ActivityAddBeneSuccessDialogBinding binding2 = ActivityAddBeneSuccessDialogBinding.inflate(getLayoutInflater());
        dialog.setContentView(binding2.getRoot());
        dialog.setCancelable(false);

        if (!status) {
            binding2.gifBeneSuccess.setVisibility(View.GONE);
            binding2.gifBeneFailed.setVisibility(View.VISIBLE);
            binding2.addResultBene.setText(message);
            binding2.addResultBene.setTextColor(ContextCompat.getColor(activity, R.color.red));
            binding2.addMessageBene.setText("Sorry Something Went Wrong");
            binding2.addMessageBene.setTextColor(ContextCompat.getColor(activity, R.color.red));

        } else {
            binding2.gifBeneFailed.setVisibility(View.GONE);
            binding2.gifBeneSuccess.setVisibility(View.VISIBLE);
            binding2.addResultBene.setText(message);
            binding2.beneAddBetaDetails.setVisibility(View.VISIBLE);
            binding2.addBeneBetaMobNoDone.setText(number);
            binding2.addBeneBetaBeneNameDone.setText(name);
            binding2.addResultBene.setTextColor(ContextCompat.getColor(activity, R.color.green));
            binding2.addMessageBene.setText("You Added Beneficiary Successfully");
            binding2.addMessageBene.setTextColor(ContextCompat.getColor(activity, R.color.green));
        }

        binding2.addDoneBene.setOnClickListener(v -> {
            dialog.dismiss();

            // This is for refreshing API
            beneList.clear();
            getBeneficiaryListAPI("remitterId");
            getDMTBeneAPI(true);

        });

        dialog.show();

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