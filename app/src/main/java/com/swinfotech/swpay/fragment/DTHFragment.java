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
import com.swinfotech.swpay.activities.DTHConfirmation;
import com.swinfotech.swpay.activities.DTHCustInfo;
import com.swinfotech.swpay.activities.DTHOperatorList;
import com.swinfotech.swpay.model.operator_get_set;
import com.leo.simplearcloader.ArcConfiguration;
import com.leo.simplearcloader.SimpleArcDialog;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class DTHFragment extends Fragment implements AdapterView.OnItemSelectedListener {

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

    EditText opList1;
    EditText mobile, amount, tPin;

    public DTHFragment() {
        // Required empty public constructor
    }


    public static DTHFragment newInstance() {
        DTHFragment fragment = new DTHFragment();
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
        View v = inflater.inflate(R.layout.fragment_d_t_h, container, false);
        session = new Session(context);

        ImageView imageView = (ImageView) v.findViewById(R.id.dthBack);
        imageView.setOnClickListener(view -> {
            startActivity(new Intent(context, MainActivity.class));
        });

//        spinner = (Spinner) v.findViewById(R.id.dth_operator);
//        retrieveJSON();

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this.getActivity(), R.array.operator, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinner.setAdapter(adapter);
//        spinner.setOnItemSelectedListener(this);

        mobile = (EditText) v.findViewById(R.id.dth_mobile);
        amount = (EditText) v.findViewById(R.id.dth_amount);
        tPin = (EditText) v.findViewById(R.id.dth_tpin);
        Button btn = (Button) v.findViewById(R.id.dth_submit);
        opList1 = (EditText) v.findViewById(R.id.operatorListDTH);
        TextView check = (TextView) v.findViewById(R.id.checkDTH);

        opList1.setOnClickListener(view -> {
            Intent intent = new Intent(context, DTHOperatorList.class);
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
                    Toast.makeText(context, "Please Enter Number", Toast.LENGTH_SHORT).show();
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

                Intent intent = new Intent(getActivity(), DTHConfirmation.class);
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

        check.setOnClickListener(view -> {
            if (getStringFromEditText(mobile) == null){
                Toast.makeText(context, "Please Enter DTH Number", Toast.LENGTH_SHORT).show();
            }
//            else if(getStringFromEditText(mobile).length() != 10) {
//                Toast.makeText(context, "Please Enter 10 Digit Mobile No", Toast.LENGTH_SHORT).show();
//            }
            else if (getStringFromEditText(opList1) == null) {
                Toast.makeText(context, "Please Select Operator", Toast.LENGTH_SHORT).show();
            } else {
//                Intent intent = new Intent(context, DTHCustInfo.class);
//                intent.putExtra("mobileNo", mobile.getText().toString());
//                intent.putExtra("operator", opList1.getText().toString());
//                intent.putExtra("opId", opreator_id);
//                startActivity(intent);

                dTHCustInfoAPI(mobile, opList1);

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
                            JSONArray dataArray = obj.getJSONArray("DTHOpFetch");
                            operator_get_set playerModel1 = new operator_get_set();

                            playerModel1.setOpName("Select Operator");
                            playerModel1.setOpId("0");

                            goodModelArrayList.add(playerModel1);

                            for (int i = 0; i < dataArray.length(); i++) {

                                operator_get_set playerModel = new operator_get_set();
                                JSONObject dataobj = dataArray.getJSONObject(i);

                                playerModel.setOpId(dataobj.getString("id"));
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



    private void dTHCustInfoAPI(EditText mobile, EditText operator) {
        showLoadingDialog();
        String url = "username=" + session.getString(Session.MOBILE)
                + "&password=" + session.getString(Session.PASSWORD)
                + "&op=" + getStringFromEditText(operator)
                + "&tel=" + getStringFromEditText(mobile)
                + "&tok=" + session.getString(Session.TOKEN);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constant.DTH_OFFER_API + url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        hideLoadingDialog();
                        System.out.println(response);

                        try {
                            if (response != null) {
                                JSONObject jsonObject = new JSONObject(response);
                                JSONObject obj = jsonObject.getJSONObject("DTHCustomerInfo");

                                String id = "";
                                if (obj.has("tel")){
                                    id = obj.getString("tel");
                                }

                                String opDTH = "";
                                if (obj.has("operator")){
                                    opDTH = obj.getString("operator");
                                }

                                String status1 = "";
                                if (obj.has("status")){
                                    status1 = obj.getString("status");
                                }

                                try {
                                    JSONArray jsonArray = obj.getJSONArray("records");
                                    JSONObject object = jsonArray.getJSONObject(0);

                                    String status = "";
                                    if (object.has("status")) {
                                        status = object.getString("status");
                                    }

                                    String bal = "";
                                    if (object.has("Balance")){
                                        bal = object.getString("Balance");
                                    }

                                    String name = "";
                                    if (object.has("customerName")){
                                        name = object.getString("customerName");
                                    }

                                    String date = "";
                                    if (object.has("NextRechargeDate")){
                                        date = object.getString("NextRechargeDate");
                                    }

                                    String details = "";
                                    if (object.has("planname")){
                                        details = object.getString("planname");
                                    }

                                    String monthlyRe = "";
                                    if (object.has("MonthlyRecharge")){
                                        monthlyRe = object.getString("MonthlyRecharge");
                                    }

                                    if (status1.equalsIgnoreCase("1")) {
                                        showDTHCustInfo(id, opDTH, status, bal, name, date, details, monthlyRe);

                                    } else {
//                                        showDTHCustInfo(id, opDTH, status, bal, name, date, details, monthlyRe);
                                        Toast.makeText(context, "Data Not Found!", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    System.out.println("Data lost because json object");
                                    JSONObject object = obj.getJSONObject("records");

                                    String status = "";
                                    if (object.has("status")) {
                                        status = object.getString("status");
                                    }

                                    String bal = "";
                                    if (object.has("Balance")){
                                        bal = object.getString("Balance");
                                    }

                                    String name = "";
                                    if (object.has("customerName")){
                                        name = object.getString("customerName");
                                    }

                                    String date = "";
                                    if (object.has("NextRechargeDate")){
                                        date = object.getString("NextRechargeDate");
                                    }

                                    String details = "";
                                    if (object.has("planname")){
                                        details = object.getString("planname");
                                    }

                                    String monthlyRe = "";
                                    if (object.has("MonthlyRecharge")){
                                        monthlyRe = object.getString("MonthlyRecharge");
                                    }

                                    if (status1.equalsIgnoreCase("1")) {
//                                        showDTHCustInfo(id, opDTH, status, bal, name, date, details, monthlyRe);
                                        Toast.makeText(context, "Data Not Found!", Toast.LENGTH_SHORT).show();
                                    } else {
//                                        showDTHCustInfo(id, opDTH, status, bal, name, date, details, monthlyRe);
                                        Toast.makeText(context, "Data Not Found", Toast.LENGTH_SHORT).show();
                                    }

                                    return;
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

    private void showDTHCustInfo(String id, String opDTH, String status, String bal, String name, String date, String details, String monthlyRe) {
        Intent intent = new Intent(context, DTHCustInfo.class);
        intent.putExtra("id", id);
        intent.putExtra("opDth", opDTH);
        intent.putExtra("status", status);
        intent.putExtra("bal", bal);
        intent.putExtra("name", name);
        intent.putExtra("date", date);
        intent.putExtra("details", details);
        intent.putExtra("monthlyRe", monthlyRe);
        startActivity(intent);

//        Dialog dialog = new Dialog(context);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        //The user will be able to cancel the dialog bu clicking anywhere outside the dialog.
//        dialog.setCancelable(true);
//
//        ActivityDthcustInfoBinding binding = ActivityDthcustInfoBinding.inflate(getLayoutInflater());
//        dialog.setContentView(binding.getRoot());
//
//        if (status.equalsIgnoreCase("null")) {
//            binding.noCustData.setVisibility(View.GONE);
//            binding.rOffersDTHError.setVisibility(View.VISIBLE);
//        } else if(status.equalsIgnoreCase("")){
//            binding.noCustData.setVisibility(View.GONE);
//            binding.rOffersDTHError.setVisibility(View.VISIBLE);
//        } else if(status.equalsIgnoreCase("0")){
//            binding.noCustData.setVisibility(View.GONE);
//            binding.rOffersDTHError.setVisibility(View.VISIBLE);
//        } else {
//            binding.subscribeID.setText(id);
//            binding.subscribeOp.setText(opDTH);
//            binding.subscribeName.setText(name);
//            binding.subscribeBal.setText(bal);
//            binding.subscribeRecharge.setText(monthlyRe);
//            binding.subscribeDate.setText(date);
//            binding.subscribeDetails.setText(details);
//        }
//
//        dialog.show();

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