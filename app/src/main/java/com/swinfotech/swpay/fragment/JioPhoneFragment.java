package com.swinfotech.swpay.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import com.swinfotech.swpay.adapter.FULLTTFragmentAdapter;
import com.swinfotech.swpay.model.FULLTTFragmentGetSet;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JioPhoneFragment extends Fragment implements FULLTTFragmentAdapter.OnPlanClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    Context context;
    Session session;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    FULLTTFragmentAdapter fullttFragmentAdapter;
    List<FULLTTFragmentGetSet> fullTT = new ArrayList<>();

    RecyclerView recyclerView;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    public JioPhoneFragment() {
        // Required empty public constructor
    }


    public static JioPhoneFragment newInstance(String param1, String param2) {
        JioPhoneFragment fragment = new JioPhoneFragment();
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
        View v = inflater.inflate(R.layout.fragment_jio_phone, container, false);

        session = new Session(context);

        String mobile = getActivity().getIntent().getStringExtra("mobileNo");
        String operator = getActivity().getIntent().getStringExtra("operator");
        String opId = getActivity().getIntent().getStringExtra("opId");

        recyclerView = v.findViewById(R.id.jioPhoneRecView);
        TextView sms = v.findViewById(R.id.errorJioPhone);

        // set the layout manager on recyclerview
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        fullttFragmentAdapter = new FULLTTFragmentAdapter(context, fullTT, this);
        recyclerView.setAdapter(fullttFragmentAdapter);

//        fullTalkTimeAPI(mobile, operator);

        if (operator.equalsIgnoreCase("Jio")) {
            fullTalkTimeAPI(mobile, operator);
        } else if (operator.equalsIgnoreCase("Bsnl Special")) {
            sms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }  else if (operator.equalsIgnoreCase("Bsnl Recharge")) {
            sms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else if (operator.equalsIgnoreCase("Vodafone-Idea")) {
            sms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            sms.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        return v;

    }

    private void fullTalkTimeAPI(String mobile, String operator) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&op=" + operator
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.PLANS_API + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        fullTT.clear();

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("apiPlans");

                                String status = "";
                                if (obj.has("status")){
                                    status = obj.getString("status");

                                    if (status.equalsIgnoreCase("200")) {

                                        String dataObj = "";
                                        if (obj.has("data")){
                                            dataObj = obj.getString("data");

                                            JSONObject object = new JSONObject(dataObj);
                                            JSONObject object1 = object.getJSONObject("records");

                                            JSONArray jsonArray = object1.getJSONArray("2G");

                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject object2 = jsonArray.getJSONObject(i);
                                                FULLTTFragmentGetSet data = new FULLTTFragmentGetSet();

                                                if (object2.has("rs")) {
                                                    data.setPrice(object2.getString("rs"));
                                                }

                                                if (object2.has("desc")) {
                                                    data.setDetails(object2.getString("desc"));
                                                }

                                                fullTT.add(data);

                                                fullttFragmentAdapter.notifyDataSetChanged();

                                            }

                                        }



                                    } else {
                                        System.out.println("Sorry No Data Available");
                                    }

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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
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

    @Override
    public void onPlanClick(FULLTTFragmentGetSet planList) {
        Log.e("TAG", "onPlanClick: ");
        Constant.RECHARGE_AMOUNT=planList.getPrice();
        getActivity().onBackPressed();
    }
}