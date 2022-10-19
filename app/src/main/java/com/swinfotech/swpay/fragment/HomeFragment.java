package com.swinfotech.swpay.fragment;

import static com.swinfotech.swpay.Constant.VOLLEY_TIMEOUT;
import static com.swinfotech.swpay.Session.BALANCE_AEPS;
import static com.swinfotech.swpay.Session.BALANCE_AMOUNT;
import static com.swinfotech.swpay.Session.PASSWORD;
import static com.swinfotech.swpay.Session.USER_TYPE;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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

import com.swinfotech.swpay.activities.AEPSSettlement;
import com.swinfotech.swpay.activities.AddMoneyCreditCard;
import com.swinfotech.swpay.activities.AddUser;
import com.swinfotech.swpay.activities.BBPSCategorie;
import com.swinfotech.swpay.activities.CreditBalance;
import com.swinfotech.swpay.activities.KYC;
import com.swinfotech.swpay.activities.OperatorCommission;
import com.swinfotech.swpay.activities.PaymentLoadReport;
import com.swinfotech.swpay.activities.PaymentRequest;
import com.swinfotech.swpay.activities.Telecom;
import com.swinfotech.swpay.activities.TransferMoney;
import com.swinfotech.swpay.activities.UPIAddMoney;
import com.swinfotech.swpay.activities.ViewUsers;
import com.swinfotech.swpay.adapter.HomeAdapter;
import com.swinfotech.swpay.adapter.HomeAdapter1;
import com.swinfotech.swpay.adapter.SliderAdapter;
import com.swinfotech.swpay.databinding.ActivityKycpopUpBinding;
import com.swinfotech.swpay.databinding.FragmentHomeBinding;
import com.swinfotech.swpay.model.HomeModel;

import com.swinfotech.swpay.model.ServiceModel;
import com.swinfotech.swpay.model.SliderItem;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class HomeFragment extends Fragment implements HomeAdapter.OnClickListenerAeps {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Session session;
    Context context;
    private FragmentHomeBinding binding;
    private ProgressDialog progressDialog;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private HomeAdapter1 homeAdapter1;
    private ArrayList<HomeModel> homeModelArrayList = new ArrayList<>();
    private ArrayList<HomeModel> homeModelArrayList1 = new ArrayList<>();
    private ArrayList<ServiceModel> serviceModel = new ArrayList<>();
    private SliderAdapter sliderAdapter;

    private static boolean isFirstLaunch = true;

    public HomeFragment() {
        // Required empty public constructor
    }

    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    // here attach the context
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
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
        binding = FragmentHomeBinding.inflate(inflater, container, false);
//        Log.e("TAG", "onCreate: "+session.getString(Session.TOKEN));
        homeAdapter = new HomeAdapter(homeModelArrayList, getActivity(), this, this);
        binding.recyclerView.setNestedScrollingEnabled(false);
        binding.recyclerView.setLayoutManager(new GridLayoutManager(context, 3));
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setAdapter(homeAdapter);

        // This is the second adapter for next list
        homeAdapter1 = new HomeAdapter1(homeModelArrayList1, context, this);
        binding.recyclerView1.setNestedScrollingEnabled(false);
        binding.recyclerView1.setLayoutManager(new GridLayoutManager(context, 3));
        binding.recyclerView1.setHasFixedSize(true);
        binding.recyclerView1.setAdapter(homeAdapter1);

        binding.imgmobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), Telecom.class));
            }
        });


        binding.commission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), OperatorCommission.class));
            }
        });

//        binding.reports.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), Reports.class));
//            }
//        });
//
//        binding.statement.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                startActivity(new Intent(getActivity(), StatementsReport.class));
//            }
//        });

//        binding.addUser.setOnClickListener(v -> {
//            startActivity(new Intent(getActivity(), AddUser.class));
//        });

        session = new Session(context);
        // getting profile data

        binding.refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showRefresh();
                getProfileData();
            }
        });


        putDataIntoSlider();


        showRefresh();
        getProfileData();
//        getKYCdata();
        return binding.getRoot();
    }


    private void getKYCdata() {
        String kyc_status = session.getString(Session.KYC_STATUS);
        System.out.println(kyc_status);
        if (kyc_status.equalsIgnoreCase("1")){
            System.out.println("KYC Already Complete");
        } else if (kyc_status.equalsIgnoreCase("0")) {
            System.out.println("KYC Pending");
        }
        else {
            if (isFirstLaunch){
                showKYCDialog();
                isFirstLaunch = false;
            }
        }
    }

    private void putDataIntoSlider() {
        // setting up our slider
        sliderAdapter = new SliderAdapter(context);
        binding.imageSlider.setSliderAdapter(sliderAdapter);
        binding.imageSlider.startAutoCycle();
        getSliderDataFromApi();
    }

    private void getSliderDataFromApi() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.SLIDER_IMAGE_API + "username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(PASSWORD) + "&tok=" + session.getString(Session.TOKEN),
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
                                if (jsonObject.has("GetSlidingImages")) {
                                    JSONObject jb1=jsonObject.getJSONObject("GetSlidingImages");
                                    if (jb1.has("data")) {
                                        JSONArray jsonArray = jb1.getJSONArray("data");
                                        if (jsonArray.length() > 0) {
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject jb = jsonArray.getJSONObject(i);
                                                if (jb.has("sliding_image")) {
                                                    sliderAdapter.addItem(new SliderItem("", jb.getString("sliding_image")));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (
                                Exception e) {
                            hideRefresh();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                hideRefresh();
                Log.e("tag", "onErrorResponse: " + error.getMessage());
                Toast.makeText(context, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void getProfileData() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DASHBOARD_USER_INFO + "?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(PASSWORD) + "&tok=" + session.getString(Session.TOKEN),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // check the response
                        hideProgressDialog();
                        System.out.println(response);
                        try {
                            hideRefresh();
                            // check if response is not null
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                if (jsonObject.has("Resp")) {
                                    Toast.makeText(context, jsonObject.getString("Resp"), Toast.LENGTH_SHORT).show();
                                }
                                JSONObject loginInfo = jsonObject.getJSONObject("LoginUserInfo");
                                if (jsonObject.has("ActiveDMTApi")) {
                                    JSONObject activeDMTApi = jsonObject.getJSONObject("ActiveDMTApi");

                                    if (activeDMTApi.has("api_code")) {

                                        String api  = activeDMTApi.getString("api_code");
                                        if (api.equals("DMEP")){
                                            Constant.ISBETA = true;
                                        }else  if(api.equals("VFDMT")){
                                            Constant.ISALPHA = true;
                                        }else {
                                            Constant.ISCF = true;
                                        }

                                    }
                                }
                                if (loginInfo.has("balance_amt")) {
                                    session.setString(Session.BALANCE_AMOUNT, loginInfo.getString("balance_amt"));
                                    Log.e("TAG", "onResponse: balance");
                                }
                                if (loginInfo.has("balance_outstanding")) {
                                    session.setString(Session.BALANCE_OUTSTANDING, loginInfo.getString("balance_outstanding"));
                                }
                                if (loginInfo.has("aeps_balance")) {
                                    session.setString(Session.BALANCE_AEPS, loginInfo.getString("aeps_balance"));
                                }

                                if (loginInfo.has("user_type")) {
                                    session.setString(Session.USER_TYPE, loginInfo.getString("user_type"));
                                }
                                if (loginInfo.has("user_mobile")) {
                                    session.setString(Session.MOBILE, loginInfo.getString("user_mobile"));
                                }
                                if (loginInfo.has("user_email")) {
                                    session.setString(Session.USER_EMAIL, loginInfo.getString("user_email"));
                                }
                                if (loginInfo.has("user_fname")) {
                                    session.setString(Session.USER_NAME, loginInfo.getString("user_fname"));
                                }

                                // this is for edit profile
                                if (loginInfo.has("aadhar_no")) {
                                    session.setString(Session.AADHAAR_NO, loginInfo.getString("aadhar_no"));
                                }
                                if (loginInfo.has("pan_no")) {
                                    session.setString(Session.PAN_NO, loginInfo.getString("pan_no"));
                                }
                                if (loginInfo.has("pin")) {
                                    session.setString(Session.PIN_CODE, loginInfo.getString("pin"));
                                }
                                if (loginInfo.has("state")) {
                                    session.setString(Session.STATES, loginInfo.getString("state"));
                                }
                                if (loginInfo.has("district")) {
                                    session.setString(Session.DISTRICT, loginInfo.getString("district"));
                                }
                                if (loginInfo.has("address")) {
                                    session.setString(Session.ADDRESS, loginInfo.getString("address"));
                                }
                                if (loginInfo.has("kyc_status")) {
                                    session.setString(Session.KYC_STATUS, loginInfo.getString("kyc_status"));
                                }
                                if (loginInfo.has("aeps_merchant_id")) {
                                    session.setString(Session.AEPS_MERCHANT_ID, loginInfo.getString("aeps_merchant_id"));
                                }
                                if (loginInfo.has("user_id")) {
                                    session.setString(Session.USER_ID_TWO, loginInfo.getString("user_id"));
                                }

                                if (loginInfo.has("profile_pic")) {
                                    session.setString(Session.PROFILE_IMG, loginInfo.getString("profile_pic"));
                                }

                                if (jsonObject.has("ActiveServices")) {
                                    JSONObject object = jsonObject.getJSONObject("ActiveServices");
                                    JSONArray jsonArray = object.getJSONArray("data");

                                    JSONObject obj1 = jsonArray.getJSONObject(0);
                                    JSONObject obj2 = jsonArray.getJSONObject(1);
                                    JSONObject obj3 = jsonArray.getJSONObject(2);
                                    JSONObject obj4 = jsonArray.getJSONObject(3);
                                    JSONObject obj5 = jsonArray.getJSONObject(4);
                                    JSONObject obj6 = jsonArray.getJSONObject(5);
                                    JSONObject obj7 = jsonArray.getJSONObject(6);
                                    JSONObject obj8 = jsonArray.getJSONObject(7);
                                    JSONObject obj9 = jsonArray.getJSONObject(8);

                                    if (obj1.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_RECHARGE, obj1.getString("service_id"));
                                    }
                                    if (obj1.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_RECHARGE, obj1.getString("status"));
                                    }

                                    if (obj2.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_BBPS, obj2.getString("service_id"));
                                    }
                                    if (obj2.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_BBPS, obj2.getString("status"));
                                    }

                                    if (obj3.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_AEPS, obj3.getString("service_id"));
                                    }
                                    if (obj3.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_AEPS, obj3.getString("status"));
                                    }

                                    if (obj4.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_DMT, obj4.getString("service_id"));
                                    }
                                    if (obj4.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_DMT, obj4.getString("status"));
                                    }

                                    if (obj5.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_WALLET, obj5.getString("service_id"));
                                    }
                                    if (obj5.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_WALLET, obj5.getString("status"));
                                    }

                                    if (obj6.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_UPI, obj6.getString("service_id"));
                                    }
                                    if (obj6.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_UPI, obj6.getString("status"));
                                    }

                                    if (obj7.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_PAYOUT, obj7.getString("service_id"));
                                    }
                                    if (obj7.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_PAYOUT, obj7.getString("status"));
                                    }

                                    if (obj8.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_MPOS, obj8.getString("service_id"));
                                    }
                                    if (obj8.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_MPOS, obj8.getString("status"));
                                    }

                                    if (obj9.has("service_id")) {
                                        session.setString(Session.SERVICE_ID_CARD, obj9.getString("service_id"));
                                    }
                                    if (obj9.has("status")) {
                                        session.setString(Session.SERVICE_STATUS_CARD, obj9.getString("status"));
                                    }

                                } else {
                                    Toast.makeText(context, "Active Services Not Working", Toast.LENGTH_SHORT).show();
                                    System.out.println("Active Services Not Working");
                                }


                                setUserProfile();
//                                setNextList();

                            } else {
                                Toast.makeText(context, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
                            }
                        } catch (
                                Exception e) {
                            hideRefresh();
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                hideRefresh();
                Log.e("tag", "onErrorResponse: " + error.getMessage());
                Toast.makeText(context, "Something Went Wrong Try Again...", Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        // set the response time to volley if you not set it the default time is 10 second
        stringRequest.setRetryPolicy(new
                DefaultRetryPolicy(
                VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }

    private void setUserProfile() {
        // for formatting points value we can restrict to 2 points value
        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        binding.aepsBalance.setText(context.getResources().getString(R.string.rupee_sysmble) + " " + df.format(Float.parseFloat(session.getString(BALANCE_AEPS))));
        binding.walletBalance.setText(context.getResources().getString(R.string.rupee_sysmble) + " " + df.format(Float.parseFloat(session.getString(BALANCE_AMOUNT))));

        if (context instanceof MainActivity) {
            ((MainActivity) context).setUpNavigationHeaderLayout();
        }

        homeModelArrayList.clear();
        homeModelArrayList1.clear();

        // we put here because Kyc_status get late
        getKYCdata();

        if (session.getString(USER_TYPE).equalsIgnoreCase("3")) {
//            binding.distributerLyt.setVisibility(View.GONE);
//            binding.mobileRechargeLn.setVisibility(View.VISIBLE);


            // here start

            if (session.getString(Session.SERVICE_ID_RECHARGE).equalsIgnoreCase("1")) {
                if (session.getString(Session.SERVICE_STATUS_RECHARGE).equalsIgnoreCase("1")) {
                    homeModelArrayList.add(new HomeModel("Mobile", R.drawable.mobile_recharge, Telecom.class,false));
                    homeModelArrayList.add(new HomeModel("DTH", R.drawable.dth_image, Telecom.class,false));
                    homeModelArrayList.add(new HomeModel("Postpaid", R.drawable.mpostpaid, Telecom.class,false));
                } else {
                    homeModelArrayList.add(new HomeModel("Mobile", R.drawable.mobile_recharge, null,false));
                    homeModelArrayList.add(new HomeModel("DTH", R.drawable.dth_image, null,false));
                    homeModelArrayList.add(new HomeModel("Postpaid", R.drawable.mpostpaid, null,false));
                    System.out.println("Services Not Working");
                }
            }

            if (session.getString(Session.SERVICE_ID_BBPS).equalsIgnoreCase("2")) {
                if (session.getString(Session.SERVICE_STATUS_BBPS).equalsIgnoreCase("1")) {
                    homeModelArrayList.add(new HomeModel("BBPS", R.drawable.bbps_logo, BBPSCategorie.class,false));
                } else {
                    homeModelArrayList.add(new HomeModel("BBPS", R.drawable.bbps_logo, null,false));
                    System.out.println("Services Not Working");
                }
            }

            if (session.getString(Session.SERVICE_ID_DMT).equalsIgnoreCase("4")) {
                if (session.getString(Session.SERVICE_STATUS_DMT).equalsIgnoreCase("1")) {
                    homeModelArrayList.add(new HomeModel("Money Transfer", R.drawable.dtm, TransferMoney.class,false));
                } else {
                    homeModelArrayList.add(new HomeModel("Money Transfer", R.drawable.dtm, null,false));
                    System.out.println("Services Not Working");
                }
            }

            if (session.getString(Session.SERVICE_ID_AEPS).equalsIgnoreCase("3")) {
                if (session.getString(Session.SERVICE_STATUS_AEPS).equalsIgnoreCase("1")) {
                    homeModelArrayList.add(new HomeModel("AEPS", R.drawable.aeps_image, null,false));
                } else {
                    homeModelArrayList.add(new HomeModel("AEPS", R.drawable.aeps_image, null,false));
                    System.out.println("Services Not Working");
                }
            }


            // old
//            homeModelArrayList.add(new HomeModel("Mobile", R.drawable.mobile_recharge, Telecom.class,false));
//            homeModelArrayList.add(new HomeModel("DTH", R.drawable.dth_image, Telecom.class,false));
//            homeModelArrayList.add(new HomeModel("Postpaid", R.drawable.mpostpaid, Telecom.class,false));
//            homeModelArrayList.add(new HomeModel("BBPS", R.drawable.bbps_logo, BBPSCategorie.class,false));
//            homeModelArrayList.add(new HomeModel("Money Transfer", R.drawable.dtm, TransferMoney.class,false));
//            homeModelArrayList.add(new HomeModel("AEPS", R.drawable.aeps_image, null,false));

        } else {
//            binding.distributerLyt.setVisibility(View.VISIBLE);
//            binding.mobileRechargeLn.setVisibility(View.GONE);

            // adding items to list

            homeModelArrayList.add(new HomeModel("Add User", R.drawable.add_user_icon, AddUser.class,false));
            homeModelArrayList.add(new HomeModel("View User", R.drawable.view_users, ViewUsers.class,false));

            if (session.getString(Session.SERVICE_ID_WALLET).equalsIgnoreCase("5")) {
                if (session.getString(Session.SERVICE_STATUS_WALLET).equalsIgnoreCase("1")) {
                    homeModelArrayList.add(new HomeModel("Credit", R.drawable.credit_image_new, CreditBalance.class,false));
                } else {
                    homeModelArrayList.add(new HomeModel("Credit", R.drawable.credit_image_new, null,false));
                    System.out.println("Services Not Working");
                }
            }

//            homeModelArrayList.add(new HomeModel("Credit", R.drawable.credit_image_new, CreditBalance.class,false));
        }

        homeModelArrayList1.add(new HomeModel("AEPS Settlement", R.drawable.aeps, AEPSSettlement.class,false));
        homeModelArrayList1.add(new HomeModel("Commission Report", R.drawable.commission_new, null,false));

        if (session.getString(Session.SERVICE_ID_UPI).equalsIgnoreCase("6")) {
            if (session.getString(Session.SERVICE_STATUS_UPI).equalsIgnoreCase("1")) {
                homeModelArrayList1.add(new HomeModel("UPI ADD Money", R.drawable.upi, UPIAddMoney.class,false));
            } else {
                homeModelArrayList1.add(new HomeModel("UPI ADD Money", R.drawable.upi, null,false));
                System.out.println("Services Not Working");
            }
        }

//        homeModelArrayList1.add(new HomeModel("UPI ADD Money", R.drawable.upi, UPIAddMoney.class,false));

        if (session.getString(Session.SERVICE_ID_CARD).equalsIgnoreCase("9")) {
            if (session.getString(Session.SERVICE_STATUS_CARD).equalsIgnoreCase("1")) {
                homeModelArrayList1.add(new HomeModel("Add Money", R.drawable.credit_card_icon1, AddMoneyCreditCard.class,false));
            } else {
                homeModelArrayList1.add(new HomeModel("Add Money", R.drawable.credit_card_icon1, null,false));
                System.out.println("Services Not Working");
            }
        }
//        homeModelArrayList1.add(new HomeModel("Add Money", R.drawable.credit_card_icon1, AddMoneyCreditCard.class,false));
        homeModelArrayList1.add(new HomeModel("Payment Request", R.drawable.payment_request, PaymentRequest.class,false));


        homeAdapter.notifyDataSetChanged();
        homeAdapter1.notifyDataSetChanged();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        homeAdapter.onActivityResult(requestCode, resultCode, data);

        if(requestCode==111){
            String msg=data.getStringExtra("ezypay_response_inner");
            session.setString(Session.MESSAGE_AEPS, data.getStringExtra("ezypay_response_inner"));
            session.setString(Session.AADHAAR_NO_AEPS, data.getStringExtra("Aadhar-Number"));
            session.setString(Session.MOBILE_NO_AEPS, data.getStringExtra("Mobile_Number"));
            session.setString(Session.BANK_ACCOUNT_AEPS, data.getStringExtra("Bank_Account"));
            session.setString(Session.AMOUNT_AEPS, data.getStringExtra("Amount"));
            if (!msg.equals("")){
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(context, "No Transaction Done Yet!!", Toast.LENGTH_SHORT).show();
            }

            if (msg.equalsIgnoreCase("")) {
                Toast.makeText(context, "Data is not coming From SDK", Toast.LENGTH_SHORT).show();
            } else {
                // method
            }
        }


    }

    private void setNextList() {
//        homeModelArrayList1.clear();
//        homeModelArrayList1.add(new HomeModel("Complaints Status", R.drawable.complain_history, ComplaintReport.class));
//        homeModelArrayList1.add(new HomeModel("Payment Request", R.drawable.payment_request, PaymentRequest.class));
//        homeModelArrayList1.add(new HomeModel("UPI", R.drawable.upi, null));
//        homeAdapter1.notifyDataSetChanged();
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Please Wait");
            progressDialog.setMessage("Getting your details");
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

    // show the payment dialog
    public void showPaymentDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context);
        bottomSheetDialog.setContentView(R.layout.payment_bottom_sheet_dialog);


        TextView payment_request;
        TextView payment_history;
        final TextView[] selectedText = {null};
        MaterialButton nextButton;
        payment_history = bottomSheetDialog.findViewById(R.id.payment_history);
        payment_request = bottomSheetDialog.findViewById(R.id.payment_request);
        nextButton = bottomSheetDialog.findViewById(R.id.nextButton);


        if (payment_history != null) {
            payment_history.setOnClickListener(v -> {
                // here we store selected text to object
                selectedText[0] = payment_history;
                payment_history.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_fill__rounded_color));
                payment_request.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_border_rounded_cornwe));

                payment_history.setTextColor(ContextCompat.getColor(context, R.color.white));
                payment_request.setTextColor(ContextCompat.getColor(context, R.color.black));
            });
        }
        if (payment_request != null) {
            payment_request.setOnClickListener(v -> {
                selectedText[0] = payment_request;
                payment_history.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_border_rounded_cornwe));
                payment_request.setBackground(ContextCompat.getDrawable(context, R.drawable.blue_fill__rounded_color));


                payment_history.setTextColor(ContextCompat.getColor(context, R.color.black));
                payment_request.setTextColor(ContextCompat.getColor(context, R.color.white));
            });
        }

        if (nextButton != null) {
            nextButton.setOnClickListener(v -> {
                // check if user not selected any option
                if (selectedText[0] == null) {
                    Toast.makeText(context, "Please Selected Any Option", Toast.LENGTH_SHORT).show();
                    return;
                }
                // check which option is selected
                if (selectedText[0] == payment_history) {
                    bottomSheetDialog.dismiss();
                    startActivity(new Intent(context, PaymentLoadReport.class));
                }
                if (selectedText[0] == payment_request) {
                    bottomSheetDialog.dismiss();
                    startActivity(new Intent(context, PaymentRequest.class));
                }
            });
        }

        // show the botom sheet dialog
        bottomSheetDialog.show();
    }

    private void hideRefresh() {
        if (binding.refresh.isRefreshing()) {
            binding.refresh.setRefreshing(false);
        }
    }

    private void showRefresh() {
        if (!binding.refresh.isRefreshing()) {
            binding.refresh.setRefreshing(true);
        }
    }

    @Override
    public void onContainerClick(int position) {

    }

    private void showKYCDialog() {
        Dialog dialog = new Dialog(context);

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
            startActivity(new Intent(context, KYC.class));
        });


        dialog.show();


    }

}