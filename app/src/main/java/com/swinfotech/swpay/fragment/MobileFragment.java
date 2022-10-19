package com.swinfotech.swpay.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import com.swinfotech.swpay.Constant;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.swinfotech.swpay.MainActivity;
import com.swinfotech.swpay.R;
import com.swinfotech.swpay.Session;
import com.swinfotech.swpay.activities.MobileRechargeOffers;
import com.swinfotech.swpay.activities.MobileRechargePlans;
import com.swinfotech.swpay.activities.RechargeConfirmation;
import com.swinfotech.swpay.activities.RechargeOperatorList;
import com.swinfotech.swpay.model.operator_get_set;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class MobileFragment extends Fragment implements AdapterView.OnItemSelectedListener {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private final ArrayList<String> operator = new ArrayList<String>();
    private final ArrayList<String> operator_id = new ArrayList<String>();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ArrayList<operator_get_set> goodModelArrayList;
    private Spinner spinner;
    private Context context;
    private String opreator_id = "";
    private Session session;

    // New Progress Dialog
    SimpleArcDialog mDialog;
    ArcConfiguration configuration;

    // new operator work
    EditText opList1;

    String opName;

    EditText mobile, amount, tPin;



    public MobileFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MobileFragment newInstance() {
        MobileFragment fragment = new MobileFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

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
    public void onResume() {
        super.onResume();
        if(amount!=null){
            amount.setText(Constant.RECHARGE_AMOUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_mobile, container, false);
        session = new Session(context);

        ImageView imageView = (ImageView) v.findViewById(R.id.mobileBack1);
        imageView.setOnClickListener(view -> {
            startActivity(new Intent(context, MainActivity.class));
        });

        // this is current spinner
//        spinner = (Spinner) v.findViewById(R.id.select_operator);


        // old api
//        retrieveJSON();




        // Circle Option In Mobile
//        Spinner circle = (Spinner) v.findViewById(R.id.select_circle);
//        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this.getActivity(), R.array.circle, android.R.layout.simple_spinner_item);
//        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        circle.setAdapter(adapter2);
//        circle.setOnItemSelectedListener(this);

        mobile = (EditText) v.findViewById(R.id.mobile_input);
        amount = (EditText) v.findViewById(R.id.amount_input);
        tPin = (EditText) v.findViewById(R.id.tpin_input);
        Button btn = (Button) v.findViewById(R.id.recharge_submit);
        TextView plans = (TextView) v.findViewById(R.id.sPlan);
        TextView offers = (TextView) v.findViewById(R.id.rOffers);

        opList1 = (EditText) v.findViewById(R.id.operatorList);

        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 10) {
                    String url = "https://mobile.swpay.in/api/AndroidAPI/CheckLiveOp?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD)
                            + "&phoneno=" + charSequence
                            + "&tok=" + session.getString(Session.TOKEN);
                    System.out.println(url);
                    checkOperator(url);
//                    getFirstOpListDataAPI();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        opList1.setOnClickListener(view -> {
            Intent intent = new Intent(context, RechargeOperatorList.class);
            startActivityForResult(intent, 1001);
        });

        amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Constant.RECHARGE_AMOUNT=charSequence.toString();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        plans.setOnClickListener(view -> {
            if (getStringFromEditText(mobile) == null){
                Toast.makeText(context, "Please Enter Mobile No", Toast.LENGTH_SHORT).show();
            } else if(getStringFromEditText(mobile).length() != 10) {
                Toast.makeText(context, "Please Enter 10 Digit Mobile No", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(opList1) == null) {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, MobileRechargePlans.class);
                intent.putExtra("mobileNo", mobile.getText().toString());
                intent.putExtra("operator", opList1.getText().toString());
                intent.putExtra("opId", opreator_id);
                startActivity(intent);

//                startActivityForResult(intent, 1001);
            }

        });

        offers.setOnClickListener(view -> {
            if (getStringFromEditText(mobile) == null){
                Toast.makeText(context, "Please Enter Mobile No", Toast.LENGTH_SHORT).show();
            } else if(getStringFromEditText(mobile).length() != 10) {
                Toast.makeText(context, "Please Enter 10 Digit Mobile No", Toast.LENGTH_SHORT).show();
            } else if (getStringFromEditText(opList1) == null) {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
            } else {
                Intent intent = new Intent(context, MobileRechargeOffers.class);
                intent.putExtra("mobileNo", mobile.getText().toString());
                intent.putExtra("operator", opList1.getText().toString());
                intent.putExtra("opId", opreator_id);
                startActivityForResult(intent, 1002);
//                startActivity(intent);

//                startActivityForResult(intent, 1001);
            }
        });




        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = mobile.getText().toString();
//                String op = spinner.getSelectedItem().toString();
//                String cir = circle.getSelectedItem().toString();
                String amt = amount.getText().toString();
                String tp = tPin.getText().toString();

                String op = opList1.getText().toString();

                if (getStringFromEditText(mobile) == null) {
                    Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show();
                    return;
                } else if(getStringFromEditText(mobile).length() != 10) {
                    Toast.makeText(context, "Please Enter 10 Digit Mobile No", Toast.LENGTH_SHORT).show();
                    return;
                } else if(getStringFromEditText(amount) == null) {
                    Toast.makeText(context, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                    return;
                } else if(getStringFromEditText(opList1) == null) {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
                    return;
                } else if(getStringFromEditText(tPin) == null) {
                    Toast.makeText(context, "Please Enter TPin", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if(getStringFromEditText(tPin).length() != 4) {
//                    Toast.makeText(context, "Please Enter 4 Digit TPin", Toast.LENGTH_SHORT).show();
//                    return;
//                }


                // old data
                //                } else if (opreator_id.equalsIgnoreCase("Select Operator") || (opreator_id.equalsIgnoreCase(""))) {
//                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
//                    return;
//                } else if (op.equalsIgnoreCase("Select Operator")) {
//                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                Intent intent = new Intent(getActivity(), RechargeConfirmation.class);
                intent.putExtra("keyMobile", mob);
//                intent.putExtra("keyOperator", op);
                intent.putExtra("operator", getStringFromEditText(opList1));
                intent.putExtra("keyOperator_id", opreator_id);
//                intent.putExtra("keyCircle", cir);
                intent.putExtra("keyAmount", amt);
                intent.putExtra("keyTPin", tp);
                startActivityForResult(intent, 1003);
//                startActivity(intent);
            }
        });

        return v;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if(resultCode == Activity.RESULT_OK){
                String opreator = data.getStringExtra("opreator");
                opList1.setText(opreator);
                opreator_id=data.getStringExtra("opreator_id");

                String amt = data.getStringExtra("price");
                amount.setText(amt);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        if (requestCode == 1002) {
            if(resultCode == Activity.RESULT_OK){
//                String opreator = data.getStringExtra("opreator");
//                opList1.setText(opreator);
//                opreator_id=data.getStringExtra("opreator_id");

                String amt = data.getStringExtra("price");
                amount.setText(amt);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

        if (requestCode == 1003) {
            if(resultCode == Activity.RESULT_OK){
//                String opreator = data.getStringExtra("opreator");
//                opList1.setText(opreator);
//                opreator_id=data.getStringExtra("opreator_id");

                mobile.setText("");
                opList1.setText("");
                amount.setText("");
                tPin.setText("");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        }

    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void checkOperator(String url) {
        showLoadingDialog();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        System.out.println("Data is here " + response);
                        hideLoadingDialog();

                        try {
                            JSONObject jsonObject = new JSONObject(response);
//                            String opId = "";
                            if (jsonObject.has("operator_id")) {
                                opreator_id = jsonObject.getString("operator_id");
                            }

                            String circle = "";
                            if (jsonObject.has("circle")) {
                                circle = jsonObject.getString("circle");
                            }

                            String operator1 = "";
                            if (jsonObject.has("operator")) {
                                operator1 = jsonObject.getString("operator");
                            }

                            String resp = "";
                            if (jsonObject.has("Resp")) {
                                resp = jsonObject.getString("Resp");
                            }

                            if (jsonObject.has("Resp")) {
                                Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
                                opList1.setText("");
                            } else {
                                System.out.println("Operator Found");
                                opList1.setText(operator1);
                            }
//                            if (resp.equalsIgnoreCase("")) {
////                                Toast.makeText(context, "Operator Not Found", Toast.LENGTH_SHORT).show();
//                                Toast.makeText(context, resp, Toast.LENGTH_SHORT).show();
//                            } else {
//                                System.out.println("Operator Found");
//                                opList1.setText(operator1);
//                            }


                            System.out.println(opreator_id);
                            System.out.println(circle);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideLoadingDialog();
                Log.d("tag", "onErrorResponse: " + error.getMessage());
                System.out.println(error.getMessage());
            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(context);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                Constant.VOLLEY_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);
    }


    // Operator api fetch data from api

    // old api
    private void retrieveJSON() {
        showLoadingDialog();
        String URl = "https://mobile.swpay.in/api/AndroidAPI/GetOperator?username=" + session.getString(Session.MOBILE) + "&password=" + session.getString(Session.PASSWORD);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        Log.d("strrrrr", ">>" + response);

                        try {
                            JSONObject obj = new JSONObject(response);

                            goodModelArrayList = new ArrayList<>();
                            JSONObject obj1 = obj.getJSONObject("MobileOpFetch");
                            JSONArray dataArray = obj1.getJSONArray("data");
                            operator_get_set playerModel1 = new operator_get_set();

                            playerModel1.setOpName("Select Operator");
                            playerModel1.setOpId("0");

                            goodModelArrayList.add(playerModel1);

                            for (int i = 0; i < dataArray.length(); i++) {

                                operator_get_set playerModel = new operator_get_set();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                playerModel.setOpName(dataobj.getString("operator_name"));
                                playerModel.setOpId(dataobj.getString("op_id"));


                                goodModelArrayList.add(playerModel);

                            }

                            for (int i = 0; i < goodModelArrayList.size(); i++) {
                                operator.add(goodModelArrayList.get(i).getOpName().toString());
                                operator_id.add(goodModelArrayList.get(i).getOpId().toString());
                            }

                            ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, operator);
                            spinner.setAdapter(spinnerArrayAdapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    opreator_id = operator_id.get(position);
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        hideLoadingDialog();
                        //displaying the error in toast if occurrs
                        Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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

}