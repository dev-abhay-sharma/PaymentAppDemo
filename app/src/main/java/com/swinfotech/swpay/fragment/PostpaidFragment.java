package com.swinfotech.swpay.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import com.swinfotech.swpay.Constant;
import android.util.Log;
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
import com.swinfotech.swpay.activities.PostPaidCustInfo;
import com.swinfotech.swpay.activities.PostpaidConfirmation;
import com.swinfotech.swpay.activities.PostpaidOperatorList;
import com.swinfotech.swpay.databinding.ActivityPostPaidCustInfoBinding;
import com.swinfotech.swpay.model.operator_get_set;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostpaidFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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

    EditText opList1, mobile, amount, tPin;

    public PostpaidFragment() {
        // Required empty public constructor
    }


    public static PostpaidFragment newInstance() {
        PostpaidFragment fragment = new PostpaidFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_postpaid, container, false);
        session = new Session(context);

        ImageView imageView = (ImageView) v.findViewById(R.id.postpaidBack);
        imageView.setOnClickListener(view -> {
            startActivity(new Intent(context, MainActivity.class));
        });

//        spinner = (Spinner) v.findViewById(R.id.postpaid_operator);
//        retrieveJSON();

        mobile = (EditText) v.findViewById(R.id.postpaid_mobile);
        amount = (EditText) v.findViewById(R.id.postpaid_amount);
        tPin = (EditText) v.findViewById(R.id.postpaid_tpin);
        Button btn = (Button) v.findViewById(R.id.postpaid_submit);
        opList1 = (EditText) v.findViewById(R.id.operatorListPostpaid);
//        EditText sTDCode = (EditText) v.findViewById(R.id.postpaid_STD);
//        TextView check = (TextView) v.findViewById(R.id.checkPostpaid);

        opList1.setOnClickListener(view -> {
            Intent intent = new Intent(context, PostpaidOperatorList.class);
            startActivityForResult(intent, 1001);
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mob = mobile.getText().toString();
//                String op = spinner.getSelectedItem().toString();
                String amt = amount.getText().toString();
                String tp = tPin.getText().toString();

                if (mob.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Mobile No", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (getStringFromEditText(mobile).length() != 10) {
//                    Toast.makeText(context, "Please Enter 10 Digit Mobile No", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                else if (getStringFromEditText(opList1) == null) {
                    Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
                    return;
                } else if (amt.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Amount", Toast.LENGTH_SHORT).show();
                    return;
                } else if (tp.equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter TPin", Toast.LENGTH_SHORT).show();
                    return;
                }
//                else if (tp.length() != 4) {
//                    Toast.makeText(context, "Please Enter 4 Digit Pin", Toast.LENGTH_SHORT).show();
//                    return;
//                }

                Intent intent = new Intent(getActivity(), PostpaidConfirmation.class);
                intent.putExtra("keyMobile", mob);
//                intent.putExtra("keyOperator", op);
                intent.putExtra("keyOperator", getStringFromEditText(opList1));
                intent.putExtra("keyOperator_id", opreator_id);
                intent.putExtra("keyAmount", amt);
                intent.putExtra("keyTPin", tp);
                startActivityForResult(intent, 1002);
//                startActivity(intent);
            }
        });


        // check
//        check.setOnClickListener(view -> {
//            if (getStringFromEditText(mobile) == null){
//                Toast.makeText(context, "Please Enter Mobile No", Toast.LENGTH_SHORT).show();
//            } else if(getStringFromEditText(sTDCode) == null) {
//                Toast.makeText(context, "Please Enter STD Code", Toast.LENGTH_SHORT).show();
//            } else if (getStringFromEditText(opList1) == null) {
//                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
//            } else {
//                postPaidCustInfoAPI(mobile, opList1, sTDCode);
//
//            }
//        });

        return v;
    }

    private void postPaidCustInfoAPI(EditText mobile, EditText operator, EditText sTDCode) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&op=" + getStringFromEditText(operator)
                + "&tel=" + getStringFromEditText(mobile)
                + "&stdcode=" + getStringFromEditText(sTDCode);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.POSTPAID_BSNL_INFO_API + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("apiBSNL");

                                String status = "";
                                if (obj.has("status")) {
                                    status = obj.getString("status");
                                }

                                if (status.equalsIgnoreCase("200")) {

                                    String dataObj = "";
                                    if (obj.has("data")) {
                                        dataObj = obj.getString("data");

                                        JSONObject object = new JSONObject(dataObj);

                                        String telNo = "";
                                        if (object.has("tel")) {
                                            telNo= object.getString("tel");
                                        }

                                        String operator = "";
                                        if (object.has("operator")) {
                                            operator = object.getString("operator");
                                        }

                                        if (operator.equalsIgnoreCase("BsnlLL")) {
                                            JSONArray jsonArray = object.getJSONArray("records");

                                            JSONObject object1 = jsonArray.getJSONObject(0);

                                            String status1 = "";
                                            if (object1.has("status")) {
                                                status1 = object1.getString("status");
                                            }

                                            String custName = "";
                                            if (object1.has("CustomerName")) {
                                                custName = object1.getString("CustomerName");
                                            }

                                            String billNo = "";
                                            if (object1.has("BillNumber")) {
                                                billNo = object1.getString("BillNumber");
                                            }

                                            String billDate = "";
                                            if (object1.has("Billdate")) {
                                                billDate = object1.getString("Billdate");
                                            }

                                            String billAmt = "";
                                            if (object1.has("Billamount")) {
                                                billAmt = object1.getString("Billamount");
                                            }

                                            String dueDate = "";
                                            if (object1.has("Duedate")) {
                                                dueDate = object1.getString("Duedate");
                                            }

                                            if (operator.equalsIgnoreCase("BsnlLL")) {
                                                showPostPaidCustInfo (custName, billNo, billDate, billAmt, dueDate, operator, telNo, status1);
                                            } else {
                                                Toast.makeText(context, "Please Select BSNll", Toast.LENGTH_SHORT).show();
                                            }

                                        } else {
                                            Toast.makeText(context, "Only Available For BSNL Landline", Toast.LENGTH_SHORT).show();
                                        }

                                    }

                                } else {
                                    System.out.println("Sorry No Data Available");
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

    private void showPostPaidCustInfo(String custName, String billNo, String billDate, String billAmt, String dueDate, String operator, String telNo, String status1) {
        Intent intent = new Intent(context, PostPaidCustInfo.class);
        intent.putExtra("customerName", custName);
        intent.putExtra("billNo", billNo);
        intent.putExtra("billDate", billDate);
        intent.putExtra("billAmount", billAmt);
        intent.putExtra("dueDate", dueDate);
        intent.putExtra("operator", operator);
        intent.putExtra("tellNo", telNo);
        intent.putExtra("status", status1);
        startActivity(intent);

//        Dialog dialog = new Dialog(context);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
//        dialog.setCancelable(true);
//
//        ActivityPostPaidCustInfoBinding binding = ActivityPostPaidCustInfoBinding.inflate(getLayoutInflater());
//        dialog.setContentView(binding.getRoot());
//
//        if (status1.equalsIgnoreCase("0")) {
//            binding.noCustPostData.setVisibility(View.GONE);
//            binding.bsNlPostPaidError.setVisibility(View.VISIBLE);
//        } else {
//            binding.landLineName.setText(custName);
//            binding.landLineBillNo.setText(billNo);
//            binding.landLineBillDate.setText(billDate);
//            binding.landLineBillAmount.setText(billAmt);
//            binding.landLineDueDate.setText(dueDate);
//            binding.landLineOp.setText(operator);
//            binding.landLineNo.setText(telNo);
//        }
//
//        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1001) {
            if(resultCode == Activity.RESULT_OK){
                String opreator = data.getStringExtra("opreator");
                opList1.setText(opreator);
                opreator_id=data.getStringExtra("opreator_id");
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
//        Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    // Operator api fetch data from api

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
//                            JSONObject obj1 = obj.getJSONObject("DTHOpFetch");
                            JSONArray dataArray = obj.getJSONArray("GetPostPaidOp");
                            operator_get_set playerModel1 = new operator_get_set();

                            playerModel1.setOpName("Select Operator");
                            playerModel1.setOpId("0");

                            goodModelArrayList.add(playerModel1);

                            for (int i = 0; i < dataArray.length(); i++) {

                                operator_get_set playerModel = new operator_get_set();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                playerModel.setOpId(dataobj.getString("op_id"));
                                playerModel.setOpName(dataobj.getString("operator_name"));


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